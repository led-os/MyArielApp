package com.qinggan.app.arielapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joooonho.SelectableRoundedImageView;
import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.minor.controller.MusicController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PlayListAdpter;
import com.qinggan.app.arielapp.minor.music.ImitateIphoneSwitch;
import com.qinggan.app.arielapp.minor.music.MySeekBar;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageLoader;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.minor.utils.BitmapUtis;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.voiceapi.control.UIControlMgr;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.tencent.qqmusic.third.api.contract.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.ERROR_CODE_NO_PERMISSION;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTURI;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.MODEGETFOLDERLIST;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.RANK;

public class BKMusicActivity extends UIControlBaseActivity implements IntegrationCore.MusicListener {

    private final String TAG = BKMusicActivity.class.getSimpleName() + "_tag";
    private IntegrationCore integrationCore;
    private ImageLoader imageLoader;
    private VelocityTracker mVelocityTracker;
    private AudioManager audioManager;
    private int mMaxVelocity;
    private int mMinVelocity;
    private boolean isLoading = false;
    private boolean netWorkConnected = false;
    private String folderType;

    private int mHashCode;
    private int curPosition = -1;


    private static final int PLAY_STATUS = 4;


    private int mCurrentIndex = -1;
    private boolean mNoNeedSwitchFirst = false;

    private int listSize;


    /**
     * 当前播放列表
     */
    private ArrayList<Data.Song> curPlayList = new ArrayList<>();

    /**
     * QQ Music API需要集合实现了序列化接口(playSongIdAtIndex). Data.Song没有序列化
     */
    private ArrayList<String> curplaySongIdList = new ArrayList<>();

    private static int defaultMode = -1;

    ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> elementItems = new ArrayList<>();
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;
    private static final String MODULE_NAME = "bkmusic";

    private MusicControlListener musicControlListener;
    private WheelControl wheelControl;

    private int item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, intentFilter);
        integrationCore = IntegrationCore.getIntergrationCore(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        imageLoader = ImageLoader.getInstance();
        mVelocityTracker = VelocityTracker.obtain();
        mMaxVelocity = ViewConfiguration.get(this).getScaledMaximumFlingVelocity();
        mMinVelocity = ViewConfiguration.get(this).getScaledMinimumFlingVelocity();
        mHashCode = hashCode();
        setMusicListener();
        getSwipeBackLayout().setEnableGesture(false);

        initVoiceWords();
        mHotwordManager = HotwordManager.getInstance(mContext, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    addWakeupElements();
                } else {
                    if (null != mHotwordManager) {
                        mHotwordManager.clearElementUCWords(MODULE_NAME);
                    }
                }
            }
        });

        ArrayList<Data.Song> songList = integrationCore.getPlayListCache();
        if (songList != null && songList.size() != 0) {
            updateMusicDatas(songList, false);
            mNoNeedSwitchFirst = false;
        } else {
            Log.i("Brian_music", "enter music activity, find no playing list, " +
                    "so trigger default song list");
            integrationCore.getPlayList();
            mNoNeedSwitchFirst = true;
        }
    }

    private void updateMusicDatas(ArrayList<Data.Song> songList, boolean playFirst) {
        ArrayList<Data.Song> staticList = new ArrayList<>();
        staticList.addAll(songList);
        Log.d(TAG, "onBack list : " + songList.toString());
        isLoading = false;
        curPlayList = staticList;
        if (songList.size() == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (MusicController.getInstance().getDefaultMode()) {
                        case MusicController.LOCALMODE:
                            ToastUtil.show(R.string.bk_no_localmusic, BKMusicActivity.this);
                        case MusicController.FAVOURITEMODE:
                            ToastUtil.show(R.string.bk_no_favmusic, BKMusicActivity.this);
                    }
                }
            });
            return;
        }
        switch (MusicController.getInstance().getDefaultMode()) {
            case MusicController.LOCALMODE:
                MusicController.getInstance().setLocalList(curPlayList);
                MusicController.getInstance().setDefaultMode(MusicController.LOCALMODE);
                Log.d(TAG, "local mode");
                break;
            case MusicController.NEWSONGMODE:
                MusicController.getInstance().setNewSongList(curPlayList);
                MusicController.getInstance().setDefaultMode(MusicController.NEWSONGMODE);
                Log.d(TAG, "new song mode");
                break;
            case MusicController.TOPPOPMODE:
                MusicController.getInstance().setTopPopsList(curPlayList);
                MusicController.getInstance().setDefaultMode(MusicController.TOPPOPMODE);
                Log.d(TAG, "top pop mode");
                break;
            case MusicController.FAVOURITEMODE:
                MusicController.getInstance().setFavouriteList(curPlayList);
                MusicController.getInstance().setDefaultMode(MusicController.FAVOURITEMODE);
                Log.d(TAG, "favourite mode");
                break;
            case MusicController.SEARCHMODE:
                MusicController.getInstance().setSearchList(curPlayList);
                MusicController.getInstance().setDefaultMode(MusicController.SEARCHMODE);
                Log.d(TAG, "search mode");
                break;
            default:
                break;
        }
        updatelist(playFirst);
        mHandler.sendEmptyMessage(UPDATELIST);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initMusicWheelControllerListener();
        initVolumeUI();
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(UPDATE_UI);
        Log.d("sasuke", "onResume");
        if (integrationCore.getMusicStatus() != PLAY_STATUS) {
            integrationCore.playMusic();
            /*pauseImg.setSelected(true);
            if (!pauseImg.isShown()) {
                pauseImg.setVisibility(View.VISIBLE);
            }*/
        } /*else {
            pauseImg.setSelected(false);
            pauseImg.setVisibility(View.INVISIBLE);
        }*/
        isOnpause = false;
        addWakeupElements();
    }


    @Override
    protected void onPause() {
        super.onPause();
        isOnpause = true;
        if (wheelControl != null) {
            wheelControl.setMusicListener(null);
        }
        mHandler.removeMessages(UPDATE_PROGRESS);
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }


    private ImageView backImg;
    private SelectableRoundedImageView albumImg;
    private ImageView musicBg;
    private ImageView songListImg;
    private ImageView songCategory;
    private ImageView pauseImg;
    private TextView songName;
    private TextView songAuthor;
    private RelativeLayout rlBackGround;
    private RelativeLayout volumeRl;
    private MySeekBar seekBar;
    private ImitateIphoneSwitch imitateIphoneSwitch;
    private LinePulseView voiceWakeup;
    private TextView wakeupWords;
    private RelativeLayout backRl;

    @Override
    protected void initView() {
        backImg = (ImageView) findViewById(R.id.music_back);
        rlBackGround = (RelativeLayout) findViewById(R.id.music_main_bg);
        albumImg = (SelectableRoundedImageView) findViewById(R.id.music_album_cover);
        musicBg = (ImageView) findViewById(R.id.music_song_bg);
        songName = (TextView) findViewById(R.id.song_name);
        songAuthor = (TextView) findViewById(R.id.song_author);
        songListImg = (ImageView) findViewById(R.id.song_list);
        songCategory = (ImageView) findViewById(R.id.song_category);
        seekBar = (MySeekBar) findViewById(R.id.my_seekbar);
        pauseImg = (ImageView) findViewById(R.id.music_status_pause);
        volumeRl = (RelativeLayout) findViewById(R.id.volume_linear);
        imitateIphoneSwitch = (ImitateIphoneSwitch) findViewById(R.id.volume_profile);
        voiceWakeup = (LinePulseView) findViewById(R.id.wakeup);
        wakeupWords = (TextView) findViewById(R.id.wakeup_words);
        backRl = (RelativeLayout) findViewById(R.id.music_rl_title);
    }


    private void addWakeupElements() {
        if (mHotwordManager == null) {
            return;
        }
        elementItems.clear();
        mHotwordManager.clearElementUCWords(MODULE_NAME);

        com.qinggan.qinglink.bean.UIControlElementItem openplaylistItem1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        openplaylistItem1.setWord(getString(R.string.bk_playlist));
        openplaylistItem1.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_PLAYLIST);
        elementItems.add(openplaylistItem1);

        com.qinggan.qinglink.bean.UIControlElementItem openplaylistItem2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        openplaylistItem2.setWord(getString(R.string.bk_openplaylist));
        openplaylistItem2.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_PLAYLIST);
        elementItems.add(openplaylistItem2);

        com.qinggan.qinglink.bean.UIControlElementItem openplaylistItem3 = new com.qinggan.qinglink.bean.UIControlElementItem();
        openplaylistItem3.setWord(getString(R.string.bk_openplaylist1));
        openplaylistItem3.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_PLAYLIST);
        elementItems.add(openplaylistItem3);

        com.qinggan.qinglink.bean.UIControlElementItem openmenuItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        openmenuItem.setWord(getString(R.string.bk_music_menu));
        openmenuItem.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_MENU);
        elementItems.add(openmenuItem);

        com.qinggan.qinglink.bean.UIControlElementItem openmenuItem2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        openmenuItem2.setWord(getString(R.string.bk_music_menu1));
        openmenuItem2.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_MENU);
        elementItems.add(openmenuItem2);

        com.qinggan.qinglink.bean.UIControlElementItem openmenuItem3 = new com.qinggan.qinglink.bean.UIControlElementItem();
        openmenuItem3.setWord(getString(R.string.bk_music_menu2));
        openmenuItem3.setIdentify(ConstantNavUc.BK_MUSIC_OPEN_MENU);
        elementItems.add(openmenuItem3);

        com.qinggan.qinglink.bean.UIControlElementItem switchNext = new com.qinggan.qinglink.bean.UIControlElementItem();
        switchNext.setWord(getString(R.string.bk_music_next));
        switchNext.setIdentify(ConstantNavUc.BK_MUSIC_NEXT);
        elementItems.add(switchNext);


        com.qinggan.qinglink.bean.UIControlElementItem switchPrev1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        switchPrev1.setWord(getString(R.string.bk_music_prev));
        switchPrev1.setIdentify(ConstantNavUc.BK_MUSIC_PREV);
        elementItems.add(switchPrev1);

        com.qinggan.qinglink.bean.UIControlElementItem switchPrev2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        switchPrev2.setWord(getString(R.string.bk_music_switch));
        switchPrev2.setIdentify(ConstantNavUc.BK_MUSIC_PREV);
        elementItems.add(switchPrev2);

        com.qinggan.qinglink.bean.UIControlElementItem pauseItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        pauseItem.setWord(getString(R.string.bk_music_pause));
        pauseItem.setIdentify(ConstantNavUc.BK_MUSIC_STOP_PLAY);
        elementItems.add(pauseItem);

        com.qinggan.qinglink.bean.UIControlElementItem playItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        playItem.setWord(getString(R.string.bk_music_play));
        playItem.setIdentify(ConstantNavUc.MUSIC_PLAY);
        elementItems.add(playItem);

        com.qinggan.qinglink.bean.UIControlElementItem exitMusic1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        exitMusic1.setWord(getString(R.string.bk_music_exit));
        exitMusic1.setIdentify(ConstantNavUc.BK_MUSIC_EXIT);
        elementItems.add(exitMusic1);

        com.qinggan.qinglink.bean.UIControlElementItem exitMusic2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        exitMusic2.setWord(getString(R.string.bk_music_close));
        exitMusic2.setIdentify(ConstantNavUc.BK_MUSIC_EXIT);
        elementItems.add(exitMusic2);

        com.qinggan.qinglink.bean.UIControlElementItem closePlaylist1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closePlaylist1.setWord(getString(R.string.bk_music_close_playlist));
        closePlaylist1.setIdentify(ConstantNavUc.BK_MUSIC_CLOSE_PLAYLIST);
        elementItems.add(closePlaylist1);

        com.qinggan.qinglink.bean.UIControlElementItem closePlaylist2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closePlaylist2.setWord(getString(R.string.bk_music_exit_playlist));
        closePlaylist2.setIdentify(ConstantNavUc.BK_MUSIC_CLOSE_PLAYLIST);
        elementItems.add(closePlaylist2);

        com.qinggan.qinglink.bean.UIControlElementItem nextPageItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        nextPageItem.setWord("下一页");
        nextPageItem.setIdentify(ConstantNavUc.MUSIC_PAGE_NEXT);
        elementItems.add(nextPageItem);


        com.qinggan.qinglink.bean.UIControlElementItem prePageItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        prePageItem.setWord("上一页");
        prePageItem.setIdentify(ConstantNavUc.MUSIC_PAGE_PRE);
        elementItems.add(prePageItem);

        com.qinggan.qinglink.bean.UIControlElementItem closeMenulist = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeMenulist.setWord(getString(R.string.bk_music_close_menulist));
        closeMenulist.setIdentify(ConstantNavUc.BK_MUSIC_CLOSE_MENU);
        elementItems.add(closeMenulist);


        com.qinggan.qinglink.bean.UIControlElementItem closeMenulist2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeMenulist2.setWord(getString(R.string.bk_music_exit_menulist));
        closeMenulist2.setIdentify(ConstantNavUc.BK_MUSIC_CLOSE_MENU);
        elementItems.add(closeMenulist2);

        com.qinggan.qinglink.bean.UIControlElementItem closeMenulist3 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeMenulist3.setWord(getString(R.string.bk_music_exit_menulist1));
        closeMenulist3.setIdentify(ConstantNavUc.BK_MUSIC_CLOSE_MENU);
        elementItems.add(closeMenulist3);


        com.qinggan.qinglink.bean.UIControlElementItem localMusicItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        localMusicItem.setWord("本地歌曲");
        localMusicItem.setIdentify(ConstantNavUc.MUSIC_LOCAL);
        elementItems.add(localMusicItem);


        com.qinggan.qinglink.bean.UIControlElementItem newMusicItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        newMusicItem.setWord("新歌榜");
        newMusicItem.setIdentify(ConstantNavUc.MUSIC_NEW);
        elementItems.add(newMusicItem);

        com.qinggan.qinglink.bean.UIControlElementItem topMusicItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        topMusicItem.setWord("流行巅峰榜");
        topMusicItem.setIdentify(ConstantNavUc.MUSIC_TOP);
        elementItems.add(topMusicItem);

        com.qinggan.qinglink.bean.UIControlElementItem favItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        favItem.setWord("我喜欢");
        favItem.setIdentify(ConstantNavUc.MUSIC_FAV);
        elementItems.add(favItem);


        if (songListPopWindow != null && songListPopWindow.isShowing() && curPlayList.size() >= 1) {
            firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
            endPosition = linearLayoutManager.findLastVisibleItemPosition();
            Log.d(TAG, "register index hotword");
            if (firstPosition >= 0 && endPosition >= 0 && firstPosition < curPlayList.size() && endPosition < curPlayList.size()) {
                for (int i = firstPosition; i < endPosition + 1; i++) {
                    com.qinggan.qinglink.bean.UIControlElementItem uiItem = new com.qinggan.qinglink.bean.UIControlElementItem();
                    Log.d(TAG, "register index hotword");
                    uiItem.setWord(curPlayList.get(i).getTitle());
                    uiItem.setIdentify(ConstantNavUc.BK_MUSIC_INDEX + ":" + i);
                    elementItems.add(uiItem);
                }


                for (int i = firstPosition; i < endPosition + 1; i++) {
                    com.qinggan.qinglink.bean.UIControlElementItem uiItem = new com.qinggan.qinglink.bean.UIControlElementItem();
                    Log.d(TAG, "register index hotword");
                    uiItem.setWord("第" + (i - firstPosition) + "个");
                    uiItem.setIdentify(ConstantNavUc.BK_MUSIC_INDEX + ":" + (i - 1));
                    elementItems.add(uiItem);
                }
            }
        }

        mHotwordManager.setElementUCWords(MODULE_NAME, elementItems);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {
            @Override
            public void onItemSelected(String identify) {
                Log.d(TAG, "hot manager control");
                onSelectOtherOC(identify);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }

    private void initVoiceWords() {

        UIControlElementItem openplaylistItem = new UIControlElementItem();
        openplaylistItem.addWord(getString(R.string.bk_playlist));
        openplaylistItem.addWord(getString(R.string.bk_openplaylist));
        openplaylistItem.addWord(getString(R.string.bk_openplaylist1));
        openplaylistItem.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_OPEN_PLAYLIST);
        mUIControlElements.add(openplaylistItem);

        UIControlElementItem openmenuItem = new UIControlElementItem();
        openmenuItem.addWord(getString(R.string.bk_music_menu));
        openmenuItem.addWord(getString(R.string.bk_music_menu1));
        openmenuItem.addWord(getString(R.string.bk_music_menu2));
        openmenuItem.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_OPEN_MENU);
        mUIControlElements.add(openmenuItem);


        UIControlElementItem switchNext = new UIControlElementItem();
        switchNext.addWord(getString(R.string.bk_music_next));
        switchNext.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_NEXT);
        mUIControlElements.add(switchNext);

        UIControlElementItem switchPrev = new UIControlElementItem();
        switchPrev.addWord(getString(R.string.bk_music_prev));
        switchPrev.addWord(getString(R.string.bk_music_switch));
        switchPrev.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_PREV);
        mUIControlElements.add(switchPrev);

        UIControlElementItem pauseItem = new UIControlElementItem();
        pauseItem.addWord(getString(R.string.bk_music_pause));
        pauseItem.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_STOP_PLAY);
        mUIControlElements.add(pauseItem);

        UIControlElementItem playItem = new UIControlElementItem();
        playItem.addWord(getString(R.string.bk_music_play));
        playItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_PLAY);
        mUIControlElements.add(playItem);

        UIControlElementItem exitMusic = new UIControlElementItem();
        exitMusic.addWord(getString(R.string.bk_music_exit));
        exitMusic.addWord(getString(R.string.bk_music_close));
        exitMusic.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_EXIT);
        mUIControlElements.add(exitMusic);


        UIControlElementItem closePlaylist = new UIControlElementItem();
        closePlaylist.addWord(getString(R.string.bk_music_close_playlist));
        closePlaylist.addWord(getString(R.string.bk_music_exit_playlist));
        closePlaylist.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_CLOSE_PLAYLIST);
        mUIControlElements.add(closePlaylist);


        UIControlElementItem nextPageItem = new UIControlElementItem();
        nextPageItem.addWord("下一页");
        nextPageItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_PAGE_NEXT);
        mUIControlElements.add(nextPageItem);


        UIControlElementItem prePageItem = new UIControlElementItem();
        prePageItem.addWord("上一页");
        prePageItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_PAGE_PRE);
        mUIControlElements.add(prePageItem);


        UIControlElementItem closeMenulist = new UIControlElementItem();
        closeMenulist.addWord(getString(R.string.bk_music_close_menulist));
        closeMenulist.addWord(getString(R.string.bk_music_exit_menulist));
        closeMenulist.addWord(getString(R.string.bk_music_exit_menulist1));
        closeMenulist.setIdentify(mHashCode + "-" + ConstantNavUc.BK_MUSIC_CLOSE_MENU);
        mUIControlElements.add(closeMenulist);


        UIControlElementItem localMusicItem = new UIControlElementItem();
        localMusicItem.addWord("本地歌曲");
        localMusicItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_LOCAL);
        mUIControlElements.add(localMusicItem);


        UIControlElementItem newMusicItem = new UIControlElementItem();
        newMusicItem.addWord("新歌榜");
        newMusicItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_NEW);
        mUIControlElements.add(newMusicItem);


        UIControlElementItem topMusicItem = new UIControlElementItem();
        topMusicItem.addWord("流行巅峰榜");
        topMusicItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_TOP);
        mUIControlElements.add(topMusicItem);


        UIControlElementItem favMusicItem = new UIControlElementItem();
        favMusicItem.addWord("我喜欢");
        favMusicItem.setIdentify(mHashCode + "-" + ConstantNavUc.MUSIC_FAV);
        mUIControlElements.add(favMusicItem);


        addElementAndListContent();

    }


    private void addContentItemList() {
        Log.d(TAG, "addContentItemList");
        if (curPlayList == null || curPlayList.size() < 1) {
            Log.d(TAG, "addContentItemList return");
            return;
        }

        firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
        endPosition = linearLayoutManager.findLastVisibleItemPosition();
        listSize = curPlayList.size();
        Log.d(TAG, "addContentItemList firstPositon : " + firstPosition + "endPositon : " + endPosition + "listSize : " + listSize);

        if (firstPosition >= 0 && endPosition >= 0 && listSize > 0 && firstPosition < listSize && endPosition < listSize) {
        } else {
            Log.d(TAG, "addContentItemList size return");
            return;
        }

        mUiControlItems.clear();
        mUIControlElements.clear();


        for (int i = firstPosition; i < endPosition + 1; i++) {
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(curPlayList.get(i).getTitle());
            uiItem.setIndex(i - firstPosition);
            String url = mHashCode + "-" + ConstantNavUc.BK_MUSIC_INDEX + ":" + i;
            uiItem.setUrl(url);
            Log.d(TAG, "url : " + url);
            mUiControlItems.add(uiItem);
        }

        initVoiceWords();

    }


    private void initAdapter() {
        playListAdpter = new PlayListAdpter(this);
        int lastMode = MusicController.getInstance().getDefaultMode();
        Log.d(TAG, "lastMode " + lastMode);
        if (lastMode == -1) {
            if (curPlayList != null) {
                playListAdpter.setList(curPlayList);
            }
        } else {
            ArrayList<Data.Song> lastList = getLastPlayList(lastMode);
            curplaySongIdList.clear();
            if (lastList != null && lastList.size() >= 1) {
                for (Data.Song song : lastList) {
                    curplaySongIdList.add(song.getId());
                }
            }
            curPlayList = lastList;
            playListAdpter.setList(curPlayList);
        }
    }

    @Override
    protected void initData() {
        initAdapter();
    }

    @Override
    protected void initListener() {
        backImg.setOnClickListener(clickListener);
        songListImg.setOnClickListener(clickListener);
        songCategory.setOnClickListener(clickListener);
        pauseImg.setOnClickListener(clickListener);
        voiceWakeup.setOnClickListener(clickListener);
        rlBackGround.setOnTouchListener(onTouchListener);
        backRl.setOnClickListener(clickListener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.bk_music_main_layout;
    }


    private ArrayList<Data.Song> getLastPlayList(final int mode) {
        switch (mode) {
            case MusicController.TOPPOPMODE:
                return MusicController.getInstance().getTopPopsList();
            case MusicController.NEWSONGMODE:
                return MusicController.getInstance().getNewSongList();
            case MusicController.LOCALMODE:
                return MusicController.getInstance().getLocalList();
            case MusicController.FAVOURITEMODE:
                return MusicController.getInstance().getFavouriteList();
            case MusicController.SEARCHMODE:
                return MusicController.getInstance().getSearchList();
        }
        return curPlayList;
    }


    private float lastX, lastY;
    private float curX, curY;
    private float velocityX, velocityY;
    private float distanceX, distanceY;
    private boolean isMove = false;
    private boolean everVolume = false;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mVelocityTracker.addMovement(motionEvent);
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mHandler.removeMessages(HIDE_VOLUME);
                    Log.d(TAG, "****************ACTION_DOWN**********************");
                    lastX = motionEvent.getX();
                    lastY = motionEvent.getY();
                    isMove = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    Log.d(TAG, "****************ACTION_MOVE**********************");
                    mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                    velocityX = mVelocityTracker.getXVelocity();
                    velocityY = mVelocityTracker.getYVelocity();
                    Log.d(TAG, "velocityX " + velocityX);
                    Log.d(TAG, "velocityY " + velocityY);
                    curX = motionEvent.getX();
                    curY = motionEvent.getY();
                    distanceX = curX - lastX;
                    distanceY = curY - lastY;
                    if (Math.abs(distanceX) > ViewConfiguration.get(BKMusicActivity.this).getScaledTouchSlop() ||
                            Math.abs(distanceY) > ViewConfiguration.get(BKMusicActivity.this).getScaledTouchSlop()) {
                        isMove = true;
                    } else {
                        isMove = false;
                    }
                    boolean isAdjustVolume = Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) >= 200 && isMove;
                    if (isAdjustVolume || everVolume) {
                        Log.d(TAG, "**************adjust volume********************");
                        isMove = true;
                        everVolume = true;
                        //initVolumeUI();
                        //ToastUtil.show("显示音量调节",BKMusicActivity.this);
                        volumeRl.setVisibility(View.VISIBLE);
                        updateVolumeUI(-distanceY);
                    }
                    Log.d(TAG, "isAdjustVolume " + isAdjustVolume);
                    Log.d(TAG, "distanceX " + distanceX);
                    Log.d(TAG, "distanceY " + distanceY);
                    int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    if (isAdjustVolume || everVolume) {
                        lastY = motionEvent.getY();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "****************ACTION_UP**********************");
                    Log.d(TAG, "isMoving : " + isMove);
                    boolean isSwitchMusic = Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) >= 200 && Math.abs(velocityX) >= 80 && isMove;
                    Log.d(TAG, "isSwitchMusic " + isSwitchMusic);
                    if (isSwitchMusic && !volumeRl.isShown()) {
                        if (distanceX > 0) {
                            /**if (integrationCore != null) {
                             integrationCore.setMusicNext();
                             mHandler.sendEmptyMessage(UPDATE_UI);
                             Log.d(TAG, "切換下一首歌曲");
                             break;
                             }*/
                            if (integrationCore != null) {
                                int currentPosition = getCurrentPosition();
                                int targetPosition = 0;
                                int code = integrationCore.setMusicPrevious();
                                if (code == ERROR_CODE_NO_PERMISSION) {
                                    if (currentPosition == 0) {
                                        targetPosition = curplaySongIdList.size() - 2;
                                    } else if (currentPosition == 1) {
                                        targetPosition = curplaySongIdList.size() - 1;
                                    } else {
                                        targetPosition = currentPosition - 2;
                                    }
                                    integrationCore.playSongIdAtIndex(curplaySongIdList, targetPosition);
                                }
                                Log.d(TAG, "切換上一首歌曲");
                                mHandler.sendEmptyMessage(UPDATE_UI);
                                break;
                            }
                        } else {
                            if (integrationCore != null) {
                                integrationCore.setMusicNext();
                                mHandler.sendEmptyMessage(UPDATE_UI);
                                Log.d(TAG, "切換下一首歌曲");
                                break;
                            }
                            /**if (integrationCore != null) {
                             int currentPosition = getCurrentPosition();
                             int targetPosition = 0;
                             int code = integrationCore.setMusicPrevious();
                             if (code == ERROR_CODE_NO_PERMISSION) {
                             if (currentPosition == 0) {
                             targetPosition = curplaySongIdList.size() - 2;
                             } else if (currentPosition == 1) {
                             targetPosition = curplaySongIdList.size() - 1;
                             } else {
                             targetPosition = currentPosition - 2;
                             }
                             integrationCore.playSongIdAtIndex(curplaySongIdList, targetPosition);
                             }
                             Log.d(TAG, "切換上一首歌曲");
                             mHandler.sendEmptyMessage(UPDATE_UI);
                             break;
                             }*/
                        }
                    }

                    if (!isMove && !volumeRl.isShown()) {
                        pauseImg.performClick();
                        break;
                    }
                    mHandler.sendEmptyMessageDelayed(HIDE_VOLUME, 500);
                    break;
            }
            return true;
        }
    };

    private int getCurrentPosition() {
        int index = -1;
        if (curplaySongIdList != null && curplaySongIdList.size() >= 1) {
            String curSong = integrationCore.getCurrentSong();
            if (!TextUtils.isEmpty(curSong)) {
                for (int i = 0; i < curplaySongIdList.size(); i++) {
                    if (curplaySongIdList.get(i).equals(curSong)) {
                        index = i;
                        break;
                    }
                }
            }
        }
        return index;
    }

    private void initVolumeUI() {
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float percent = curVolume / (float) maxVolume;
        float value = getImitateIphoneSwitchHeight() * percent;
        Log.d("sasuke", "percent : " + percent);
        Log.d("sasuke", "value : " + value);
        imitateIphoneSwitch.setCurrentValue((int) value);
    }

    private void updateVolumeUI(float value) {
        Log.d(TAG, "updateVolumeUI value : " + value);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int step = getImitateIphoneSwitchHeight() / maxVolume;
        imitateIphoneSwitch.resetValues(value);
        int v = imitateIphoneSwitch.getCurrentValue();
        Log.d(TAG, "cur v : " + v);
        double percent = v / (double) getImitateIphoneSwitchHeight();
        BigDecimal bigDecimal = new BigDecimal(percent * maxVolume);
        double volumeIndex = bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
        volumeIndex = Math.round(volumeIndex);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) volumeIndex, 0);
        Log.d(TAG, "percent : " + percent);
        Log.d(TAG, "volumeIndex : " + volumeIndex);
        if (volumeIndex == 0) {
            imitateIphoneSwitch.setCurrentValue(0);
        }
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.music_back:
                    finish();
                    break;
                case R.id.song_list:
                    showSongList();
                    break;
                case R.id.song_category:
                    showFolder();
                    break;
                case R.id.music_status_pause:
                    mHandler.removeMessages(HIDE_PAUSEIMG);
                    if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                        integrationCore.setMusicPause();
                        //pauseImg.setSelected(true);
                        //mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG, 1000);
                    } else {
                        integrationCore.playMusic();
                        //pauseImg.setSelected(false);

                    }

                    break;
                case R.id.wakeup:
                    VoicePolicyManage.getInstance().record(true);
                    break;
            }
        }
    };

    public static Bitmap captureScreen(Activity activity) {
        /*允许生成对当前view的一个bitmap形式的复制*/
        activity.getWindow().getDecorView().setDrawingCacheEnabled(true);
        Bitmap bmp = activity.getWindow().getDecorView().getDrawingCache();
        return bmp;
    }


    /**
     * 获取activiy有效区域高度
     *
     * @return
     */
    private int getActivityUsefulHeight() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.height();
    }

    /**
     * 获取当前屏幕快照
     *
     * @param activity
     * @return
     */
    private Bitmap getCaptureScreen(Activity activity) {
        Bitmap bmp = captureScreen(activity);
        int height = getUselessHeight();
        Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, height, bmp.getWidth(), getActivityUsefulHeight());
        return bmp2;
    }

    /**
     * 状态栏和标题栏高度，背景模糊时不计算此部分
     *
     * @return
     */
    private int getUselessHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentTop - statusBarHeight;
        return statusBarHeight + titleBarHeight;
    }


    private int getImitateIphoneSwitchHeight() {
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

    /**
     * recyclew view item 点击回调
     */
    private ClickViewInterface clickViewInterface = new ClickViewInterface() {
        @Override
        public void OnClickPositionListener(View view, int... position) {
            if (curplaySongIdList != null && curplaySongIdList.size() >= 1) {
                Log.d(TAG, "playSongIdAtIndex position[0] " + position[0]);
                Log.d(TAG, "name is " + curPlayList.get(position[0]).getTitle());
                Log.d(TAG, "curPlayList id " + curPlayList.get(position[0]).getId());
                Log.d(TAG, "curplaySongIdList id " + curplaySongIdList.get(position[0]));
                integrationCore.playSongIdAtIndex(curplaySongIdList, position[0]);
            }
            if (position[1] == 1) {
                playListAdpter.selectPosition(position[0], true);
                playListAdpter.notifyDataSetChanged();
            }
            songListPopWindow.dismiss();
        }

        @Override
        public void OnClickContentListener(View view, String... content) {

        }
    };

    private boolean isVoiceScroll = false;
    private boolean mIsUp = false;
    private int firstPosition;
    private int endPosition;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isVoiceScroll = false;
                addWakeupElements();
                addContentItemList();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isVoiceScroll) {
                return;
            }

            int distance = 0;

            if (linearLayoutManager == null || songListRecyclerView == null) {
                return;
            }

            if (mIsUp) {
                int n = linearLayoutManager.findLastVisibleItemPosition() - firstPosition;
                if (0 <= n && n < songListRecyclerView.getChildCount()) {
                    distance = -songListRecyclerView.getChildAt(0).getBottom();

                }
            } else {
                int n = endPosition - linearLayoutManager.findFirstVisibleItemPosition();
                if (0 <= n && n < songListRecyclerView.getChildCount()) {
                    distance = songListRecyclerView.getChildAt(n).getTop();
                    songListRecyclerView.scrollBy(0, distance);
                }
            }


        }
    };

    private PopupWindow songListPopWindow;
    private RecyclerView songListRecyclerView;
    private TextView emptyView;
    private PlayListAdpter playListAdpter;
    private Timer timer;
    private TimerTask timerTask;
    private LinearLayoutManager linearLayoutManager;
    private View songView;

    private void showSongList() {
        //先获得当前屏幕虚化背景图
        BitmapDrawable bitmapDrawable = getScreenBlur();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        songView = inflater.inflate(R.layout.music_pop, null);
        songListPopWindow = new PopupWindow(songView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        songListPopWindow.setFocusable(false);
        songListPopWindow.setOutsideTouchable(false);
        songListPopWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
        songListPopWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        RelativeLayout close = songView.findViewById(R.id.closd_music);
        songListRecyclerView = songView.findViewById(R.id.recycle_pop);
        emptyView = songView.findViewById(R.id.empty_view);
        songView.setBackground(bitmapDrawable);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        songListRecyclerView.setLayoutManager(linearLayoutManager);
        songListRecyclerView.setAdapter(playListAdpter);
        playListAdpter.setmClickViewInterface(clickViewInterface);
        if (isLoading) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("正在加载歌曲列表");
            songListRecyclerView.setVisibility(View.INVISIBLE);
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (curPlayList.size() >= 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                songListRecyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.INVISIBLE);
                            }
                        });
                        timer.cancel();
                        timerTask = null;
                        timer = null;
                        Log.d(TAG, "loading finished .update music list.");
                    }
                }
            };
            timer.schedule(timerTask, 0, 1000);
        } else if (curPlayList.size() == 0) {
            emptyView.setText("没有歌曲列表");
            emptyView.setVisibility(View.VISIBLE);
            songListRecyclerView.setVisibility(View.INVISIBLE);
            //语音控制刷新UI
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (curPlayList.size() >= 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                songListRecyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.INVISIBLE);
                            }
                        });
                        timer.cancel();
                        timerTask = null;
                        timer = null;
                        Log.d(TAG, "loading finished .update music list.");
                    }
                }
            };
            timer.schedule(timerTask, 0, 1000);
        } else if (curPlayList.size() >= 1) {
            emptyView.setVisibility(View.INVISIBLE);
            songListRecyclerView.setVisibility(View.VISIBLE);

            //playListAdpter.setInitPosition(mCurrentIndex);
            playListAdpter.selectPosition(mCurrentIndex, false);
            playListAdpter.notifyDataSetChanged();
            moveToPosition(linearLayoutManager, songListRecyclerView, mCurrentIndex);

        }
        songListRecyclerView.addOnScrollListener(mScrollListener);
        songListRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                addContentItemList();
                addWakeupElements();
            }
        });
        Log.d(TAG, "songListRecyclerView shown");
        int index = getCurrentPosition();
        if (index != -1) {
            int step = endPosition - firstPosition;
            Log.d(TAG, "step : " + step);
            songListRecyclerView.smoothScrollToPosition(index);
        }

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songListPopWindow.isShowing()) {
                    songListPopWindow.dismiss();
                    songListPopWindow = null;
                    if (timer != null) {
                        timer.cancel();
                        timerTask = null;
                        timer = null;
                    }

                }
            }
        });

        songListPopWindow.showAtLocation(songView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * RecyclerView 移动到当前位置，
     *
     * @param manager       设置RecyclerView对应的manager
     * @param mRecyclerView 当前的RecyclerView
     * @param n             要跳转的位置
     */
    public void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }
    }

    PopupWindow menuWindow;
    private View foldView;

    private void showFolder() {
        //先获得当前屏幕虚化背景图
        BitmapDrawable bitmapDrawable = getScreenBlur();

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        foldView = inflater.inflate(R.layout.folder_pop, null);
        menuWindow = new PopupWindow(foldView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        menuWindow.setFocusable(false);
        menuWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
        foldView.setBackgroundResource(R.drawable.music_black_shape);
        menuWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        menuWindow.showAtLocation(foldView, Gravity.BOTTOM, 0, 0);
        LinearLayout bgLayout = foldView.findViewById(R.id.folderBackground);
       /* if (bitmapDrawable != null){
            bgLayout.setBackground(bitmapDrawable);
        }*/
        //bgLayout.setBackground(bitmapDrawable);
        foldView.setBackground(bitmapDrawable);
        RelativeLayout relativeLayout = foldView.findViewById(R.id.closd_music);
        LinearLayout music_local = foldView.findViewById(R.id.music_local);
        LinearLayout play_history = foldView.findViewById(R.id.play_history);
        LinearLayout music_like = foldView.findViewById(R.id.music_like);
        LinearLayout music_win = foldView.findViewById(R.id.music_win);
        music_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //本地
                MusicController.getInstance().setDefaultMode(MusicController.LOCALMODE);
                mHandler.removeMessages(GET_NEW_SONG);
                mHandler.removeMessages(GET_TOP_POPS);
                mHandler.removeMessages(GET_FAV_SONGS);
                try {
                    mHandler.sendEmptyMessageDelayed(GET_LOCAL_SONGS, 500);
                } catch (Exception e) {
                }
                menuWindow.dismiss();
            }
        });
        play_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //新歌榜
                if (!netWorkConnected) {
                    ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                    return;
                }
                MusicController.getInstance().setDefaultMode(MusicController.NEWSONGMODE);
                folderType = getString(R.string.new_music);
                mHandler.removeMessages(GET_LOCAL_SONGS);
                mHandler.removeMessages(GET_TOP_POPS);
                mHandler.removeMessages(GET_FAV_SONGS);
                try {
                    isLoading = true;
                    mHandler.sendEmptyMessage(GET_NEW_SONG);
                    menuWindow.dismiss();
                } catch (Exception e) {

                }
            }
        });
        music_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //我喜欢
                if (!netWorkConnected) {
                    ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                    return;
                }
                isLoading = true;
                MusicController.getInstance().setDefaultMode(MusicController.FAVOURITEMODE);
                mHandler.removeMessages(GET_LOCAL_SONGS);
                mHandler.removeMessages(GET_TOP_POPS);
                mHandler.removeMessages(GET_NEW_SONG);
                try {
                    mHandler.sendEmptyMessage(GET_FAV_SONGS);
                    menuWindow.dismiss();
                } catch (Exception e) {

                }
            }
        });
        music_win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//流行指数榜
                if (!netWorkConnected) {
                    ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                    return;
                }
                folderType = getString(R.string.num_music);
                MusicController.getInstance().setDefaultMode(MusicController.TOPPOPMODE);
                mHandler.removeMessages(GET_LOCAL_SONGS);
                mHandler.removeMessages(GET_FAV_SONGS);
                mHandler.removeMessages(GET_NEW_SONG);
                try {
                    isLoading = true;
                    mHandler.sendEmptyMessage(GET_TOP_POPS);
                    menuWindow.dismiss();
                } catch (Exception e) {

                }
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow.dismiss();
            }
        });
    }

    @NonNull
    private BitmapDrawable getScreenBlur() {
        boolean shouldShow = false;
        albumImg.setVisibility(View.INVISIBLE);
        musicBg.setVisibility(View.INVISIBLE);
        if (pauseImg.isShown()) {
            pauseImg.setVisibility(View.INVISIBLE);
            shouldShow = true;
        }
        Bitmap beforeBlur = getCaptureScreen(BKMusicActivity.this);
        Bitmap afterBlur = BitmapUtis.doBlur(beforeBlur, 10, 20);
        Bitmap roundBlurmap = BitmapUtis.toRoundCorner(afterBlur, 30, BitmapUtis.CORNER_TOP_LEFT | BitmapUtis.CORNER_TOP_RIGHT);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(roundBlurmap);
        albumImg.setVisibility(View.VISIBLE);
        musicBg.setVisibility(View.VISIBLE);
        if (shouldShow) {
            pauseImg.setVisibility(View.VISIBLE);
        }
        return bitmapDrawable;
    }


    private void setMusicListener() {
        IntegrationCore.getIntergrationCore(this).setMusicListener(BKMusicActivity.this);
    }


    private synchronized void updatelist(boolean playFirst) {
        Log.i("Brian_music", "updatelist");
        if (curPlayList == null || curPlayList.size() == 0) {
            ToastUtil.show("获取列表失败，请重试", this);
            return;
        }
        curplaySongIdList.clear();
        for (Data.Song song : curPlayList) {
            curplaySongIdList.add(song.getId());
        }

        if (mNoNeedSwitchFirst) {
            mNoNeedSwitchFirst = false;
            playFirst = false;
        }

        if (playFirst) {
            Log.i("Brian_music", "switch to play first");
            switchToPlayFirst();
        }
    }

    @Override
    public void onBack(ArrayList list, boolean state) {
        /**ArrayList mStaticList = new ArrayList();
         mStaticList.addAll(list);
         Log.d(TAG, "onBack list : " + list.toString());
         isLoading = false;
         curPlayList = mStaticList;
         switch (MusicController.getInstance().getDefaultMode()) {
         case MusicController.LOCALMODE:
         MusicController.getInstance().setLocalList(curPlayList);
         MusicController.getInstance().setDefaultMode(MusicController.LOCALMODE);
         Log.d(TAG, "local mode");
         break;
         case MusicController.NEWSONGMODE:
         MusicController.getInstance().setNewSongList(curPlayList);
         MusicController.getInstance().setDefaultMode(MusicController.NEWSONGMODE);
         Log.d(TAG, "new song mode");
         break;
         case MusicController.TOPPOPMODE:
         MusicController.getInstance().setTopPopsList(curPlayList);
         MusicController.getInstance().setDefaultMode(MusicController.TOPPOPMODE);
         Log.d(TAG, "top pop mode");
         break;
         case MusicController.FAVOURITEMODE:
         MusicController.getInstance().setFavouriteList(curPlayList);
         MusicController.getInstance().setDefaultMode(MusicController.FAVOURITEMODE);
         Log.d(TAG, "favourite mode");
         break;
         case MusicController.SEARCHMODE:
         MusicController.getInstance().setSearchList(curPlayList);
         MusicController.getInstance().setDefaultMode(MusicController.SEARCHMODE);
         Log.d(TAG, "search mode");
         break;
         default:
         break;
         }
         updatelist();
         mHandler.sendEmptyMessage(UPDATELIST);*/
        //switchToPlayFirst();
        if (songListPopWindow != null && songListPopWindow.isShowing()) {
            mHandler.sendEmptyMessage(HIDE_SONG_LIST_WINDOW);
        }

        if (menuWindow != null && menuWindow.isShowing()) {
            mHandler.sendEmptyMessage(HIDE_MENU_WINDOW);
        }

        updateMusicDatas(list, state);
    }


    private void addNumberWakeUp() {

    }

    private void switchToPlayFirst() {
        if (curplaySongIdList == null || curplaySongIdList.size() == 0) {
            return;
        }
        integrationCore.playSongIdAtIndex(curplaySongIdList, 0);
        playListAdpter.selectPosition(0, true);
        playListAdpter.notifyDataSetChanged();
        if (pauseImg.isShown()) {
            pauseImg.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBackFolder(ArrayList list) {
        Log.d(TAG, "onBackFolder");
        for (int i = 0; i < list.size(); i++) {
            Data.FolderInfo folder = (Data.FolderInfo) list.get(i);
            if (folder.getMainTitle() != null && folderType != null &&
                    folder.getMainTitle().indexOf(folderType) != -1) {
                IntegrationCore.getIntergrationCore(this).getSongList(folder);
            }
        }

    }

    @Override
    public void songChange() {
        Log.d(TAG, "songChange");
        mHandler.sendEmptyMessage(UPDATE_UI);
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        mHandler.removeMessages(UPDATE_PAUSEIMG_STATE);
        if (!netWorkConnected) {
            albumImg.setImageResource(R.drawable.music_img_mask);
            rlBackGround.setBackgroundResource(R.drawable.music_bg);
        }
    }

    @Override
    public void playStateChanged() {
        Log.d(TAG, "playStateChanged");
        mHandler.sendEmptyMessage(UPDATE_UI);
        mHandler.sendEmptyMessage(UPDATE_PROGRESS);
        mHandler.sendEmptyMessageDelayed(UPDATE_PAUSEIMG_STATE, 500);
    }

    @Override
    public void favStateChange() {
        Log.d(TAG, "favStateChange");

    }

    @Override
    public void playListChange() {
        Log.d(TAG, "playListChange");
    }

    @Override
    public void playModeChange() {
        Log.d(TAG, "playModeChange");
    }

    @Override
    public void codeState(boolean state) {

    }

    @Override
    public void forSpecialLocalCode(String cmd, int ret) {
        integrationCore.getPlayList();
    }

    @Override
    public void backFolderName(String name) {

    }


    /**
     * 得到当前QQ音乐的播放模式。顺序播放，循环播放等等
     */
    private void getQQMusicPlayMode() {

    }

    private static final int UPDATELIST = 0X3001;
    private static final int GET_NEW_SONG = 0X3002;
    private static final int GET_TOP_POPS = 0X3003;
    private static final int UPDATE_UI = 0X3004;
    private static final int UPDATE_PROGRESS = 0X3005;
    private static final int HIDE_PAUSEIMG = 0X3006;
    private static final int HIDE_VOLUME = 0X3007;
    private static final int GET_LOCAL_SONGS = 0X3008;
    private static final int GET_FAV_SONGS = 0x3009;
    private static final int VOICE_MSG_MUSIC_NEXT = 0X3010;
    private static final int VOICE_MSG_MUSIC_PREV = 0X3011;
    private static final int VOICE_MSG_MUSIC_PAUSE = 0X3012;
    private static final int VOICE_MSG_MUSIC_PLAY = 0X3013;
    private static final int VOICE_MSG_MUSIC_NEW = 0X3014;
    private static final int VOICE_MSG_MUSIC_TOP = 0X3015;
    private static final int VOICE_MSG_MUSIC_FAV = 0X3016;
    private static final int VOICE_MSG_MUSIC_LOCAL = 0x3017;
    private static final int VOICE_MSG_MUSIC_OPENPLAY = 0x3018;
    private static final int VOICE_MSG_MUSIC_NEW_OPENMENU = 0x3019;
    private static final int VOICE_MSG_MUSIC_NEXT_PAGE = 0x3020;
    private static final int VOICE_MSG_MUSIC_PREV_PAGE = 0x3021;
    private static final int VOICE_MSG_MUSIC_CLOSE_PLAY = 0x3022;
    private static final int VOICE_MSG_MUSIC_CLOSE_MENU = 0x3023;
    private static final int VOICE_MSG_MUSIC_EXIT = 0x3024;
    private static final int HIDE_SONG_LIST_WINDOW = 0X3025;
    private static final int HIDE_MENU_WINDOW = 0X3026;
    private static final int UPDATE_PAUSEIMG_STATE = 0X3027;


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATELIST: {
                    if (curPlayList == null || curPlayList.size() == 0) {
                        return;
                    }
                    playListAdpter.setList(curPlayList);
                    playListAdpter.notifyDataSetChanged();

                    mHandler.sendEmptyMessage(UPDATE_UI);
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                    break;
                }
                case GET_NEW_SONG: {
                    Data.FolderInfo mFolder = integrationCore.getFolderByTitle(getString(R.string.new_music));
                    if (mFolder != null) {
                        //更新封面
                        isLoading = false;
                        integrationCore.getSongList(mFolder);
                    } else {
                        integrationCore.getRankFolderList();
                    }
                    break;
                }
                case GET_TOP_POPS: {
                    Data.FolderInfo mFolder = integrationCore.getFolderByTitle(getString(R.string.num_music));
                    if (mFolder != null) {
                        isLoading = false;
                        integrationCore.getSongList(mFolder);
                    } else {
                        integrationCore.mMusicCMD.getListAlias(MODEGETFOLDERLIST, "", RANK, 0);
                    }
                    break;
                }
                case GET_LOCAL_SONGS: {
                    ArrayList<String> mTempList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTURI);
                    if (mTempList == null || mTempList.size() == 0) {
                        ToastUtil.show(R.string.bk_no_localmusic, BKMusicActivity.this);
                        Log.d(TAG, "本地歌曲列表为空");
                        menuWindow.dismiss();
                        return;
                    }
                    integrationCore.playSongLocalPath(mTempList);
                    //integrationCore.getPlayList();

                    break;
                }
                case GET_FAV_SONGS: {
                    IntegrationCore.getIntergrationCore(BKMusicActivity.this).getFavList();
                    break;
                }
                case UPDATE_UI: {
                    updateUI();
                    updateSelectInPopWindow();
                    break;
                }
                case UPDATE_PROGRESS: {
                    int currentposition = (int) integrationCore.getCurrTime();
                    int totalduration = (int) integrationCore.getTotalTime();
                    seekBar.setMax(totalduration);
                    seekBar.setProgress(currentposition);
                    if (currentposition < totalduration) {
                        mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 500);
                    } else {
                        seekBar.setProgress(0);
                    }
                    break;
                }
                case HIDE_PAUSEIMG: {
                    pauseImg.setVisibility(View.INVISIBLE);
                    break;
                }
                case UPDATE_PAUSEIMG_STATE: {
                    if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                        pauseImg.setSelected(false);
                        mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG, 1000);
                    } else {
                        pauseImg.setSelected(true);
                        pauseImg.setVisibility(View.VISIBLE);
                        //mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG, 1000);
                    }
                }
                case HIDE_VOLUME: {
                    volumeRl.setVisibility(View.GONE);
                    everVolume = false;
                    break;
                }
                case VOICE_MSG_MUSIC_NEXT: {
                    if (integrationCore != null) {
                        integrationCore.setMusicNext();
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_PREV: {
                    musicPrevForUIControl();
                    break;
                }
                case VOICE_MSG_MUSIC_PAUSE: {
                    if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                        integrationCore.setMusicPause();
                        //pauseImg.setVisibility(View.VISIBLE);
                        //pauseImg.setSelected(true);
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_PLAY: {
                    if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                        return;
                    } else {
                        integrationCore.playMusic();
                        //pauseImg.setSelected(false);
                        //pauseImg.setVisibility(View.VISIBLE);
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_NEW: {
                    if (!netWorkConnected) {
                        ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                        return;
                    }
                    folderType = getString(R.string.new_music);
                    defaultMode = MusicController.NEWSONGMODE;
                    try {
                        isLoading = true;
                        mHandler.sendEmptyMessage(GET_NEW_SONG);
                    } catch (Exception e) {

                    }
                    break;
                }
                case VOICE_MSG_MUSIC_TOP: {
                    if (!netWorkConnected) {
                        ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                        return;
                    }
                    folderType = getString(R.string.num_music);
                    defaultMode = MusicController.TOPPOPMODE;
                    try {
                        isLoading = true;
                        mHandler.sendEmptyMessage(GET_TOP_POPS);
                    } catch (Exception e) {

                    }
                    break;
                }
                case VOICE_MSG_MUSIC_FAV: {
                    if (!netWorkConnected) {
                        ToastUtil.show(R.string.net_check, BKMusicActivity.this);
                        return;
                    }
                    isLoading = true;
                    defaultMode = MusicController.FAVOURITEMODE;
                    try {
                        IntegrationCore.getIntergrationCore(BKMusicActivity.this).getFavList();
                    } catch (Exception e) {

                    }
                    break;
                }
                case VOICE_MSG_MUSIC_LOCAL: {

                    defaultMode = MusicController.LOCALMODE;
                    try {
                        ArrayList<String> mTempList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTURI);
                        if (mTempList == null || mTempList.size() == 0) {
                            Log.d(TAG, "本地歌曲列表为空");
                            return;
                        }
                        integrationCore.playSongLocalPath(mTempList);
                        integrationCore.getPlayList();
                    } catch (Exception e) {
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_OPENPLAY: {
                    openPlaylist();
                    break;
                }
                case VOICE_MSG_MUSIC_NEW_OPENMENU: {
                    openMenuList();
                    break;
                }
                case VOICE_MSG_MUSIC_NEXT_PAGE: {
                    if (songListPopWindow != null && songListPopWindow.isShowing() && curplaySongIdList != null && curPlayList != null) {
                        isVoiceScroll = true;
                        mIsUp = false;
                        item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                        Log.d(TAG, "next endPosition " + endPosition);
                        Log.d(TAG, "next item : " + item);
                        songListRecyclerView.smoothScrollToPosition(endPosition);
                        //moveToPosition(linearLayoutManager,songListRecyclerView,endPosition + item);
                        curPosition = endPosition;
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_PREV_PAGE: {
                    if (songListPopWindow != null && songListPopWindow.isShowing() && curPlayList != null && curplaySongIdList != null) {
                        isVoiceScroll = true;
                        mIsUp = true;
                        item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                        Log.d(TAG, "prev firstPosition : " + firstPosition);
                        Log.d(TAG, "prev item : " + item);
                        //moveToPosition(linearLayoutManager,songListRecyclerView,endPosition - item);//次方法不触发scroll listener
                        //songListRecyclerView.smoothScrollToPosition(firstPosition - item >= 0?firstPosition - item:0);
                        songListRecyclerView.smoothScrollToPosition(endPosition - 2 * item);
                        curPosition = firstPosition;
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_CLOSE_PLAY: {
                    if (songListPopWindow != null && songListPopWindow.isShowing()) {
                        songListPopWindow.dismiss();
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_CLOSE_MENU: {
                    if (menuWindow != null && menuWindow.isShowing()) {
                        menuWindow.dismiss();
                    }
                    break;
                }
                case VOICE_MSG_MUSIC_EXIT: {
                    finish();
                    break;
                }
                case HIDE_SONG_LIST_WINDOW:
                    if (songListPopWindow != null && songListPopWindow.isShowing()) {
                        songListPopWindow.dismiss();
                    }
                    break;
                case HIDE_MENU_WINDOW:
                    if (menuWindow != null && menuWindow.isShowing()) {
                        menuWindow.dismiss();
                    }
                    break;
            }
        }
    };

    /**
     * 更新播放列表里选中歌曲条目
     */
    private void updateSelectInPopWindow() {
        /**if (songListPopWindow == null || !songListPopWindow.isShowing()){
         return;
         }
         int index = -1;
         if (integrationCore != null && integrationCore.mMusicCMD != null) {
         Data.Song curSong = integrationCore.mMusicCMD.getCurrentSong();
         if (curSong != null) {
         for (int i = 0; i < curplaySongIdList.size(); i++) {
         if (curplaySongIdList.get(i).equals(curSong.getId())) {
         index = i;
         break;
         }
         }
         }
         if (index != -1) {
         playListAdpter.selectPosition(index, false);
         playListAdpter.notifyDataSetChanged();
         } else {
         playListAdpter.selectPosition(-1, false);
         playListAdpter.notifyDataSetChanged();
         }
         }*/

        if (integrationCore != null && integrationCore.mMusicCMD != null) {
            Data.Song curSong = integrationCore.getCurrentMusic();
            if (curSong != null) {
                for (int i = 0; i < curplaySongIdList.size(); i++) {
                    if (curplaySongIdList.get(i).equals(curSong.getId())) {
                        mCurrentIndex = i;
                        break;
                    }
                }
            }

            if (songListPopWindow == null || !songListPopWindow.isShowing()) {
                return;
            }

            playListAdpter.selectPosition(mCurrentIndex, false);
            playListAdpter.notifyDataSetChanged();
            songListRecyclerView.smoothScrollToPosition(mCurrentIndex);
        }
    }


    /**
     * 更新专辑封面，背景虚化图，歌名，歌手信息
     * 第一次进应用有更新不成功的问题。待解决～～～
     */
    private void updateUI() {
        if (integrationCore != null && integrationCore.mMusicCMD != null) {
            Data.Song song = integrationCore.getCurrentMusic();
            if (song != null) {
                wakeupWords.setText(R.string.music_voicewords);
                songName.setText(song.getTitle());
                songAuthor.setText(song.getSinger().getTitle());
                if (!TextUtils.isEmpty(song.getAlbum().getCoverUri())) {
                    String songTag = song.getId();
                    imageLoader.display2(song.getAlbum().getCoverUri(), albumImg, rlBackGround, songTag);
                    /*Bitmap bp = imageLoader.getImageCache().get(songTag);
                    bp = BitmapUtis.doBlur(bp, 10, 20);*/
                    BitmapDrawable bitmapDrawable = getScreenBlur();
                    if (bitmapDrawable != null) {
                        if (songListPopWindow != null && songListPopWindow.isShowing()) {
                            songView.setBackgroundDrawable(bitmapDrawable);
                        }
                        if (menuWindow != null && menuWindow.isShowing()) {
                            foldView.setBackgroundDrawable(bitmapDrawable);
                        }
                    }
                } else {
                    albumImg.setBackground(null);
                }
            } else {
                songName.setText(R.string.no_song);
                songAuthor.setText(R.string.no_singer);
                wakeupWords.setText(R.string.login_qqmusic);
                albumImg.setImageDrawable(null);
                rlBackGround.setBackgroundResource(R.drawable.music_bg);
            }
        } else {
            throw new IllegalStateException("QQ Music CMD init error！");
        }
        /*if (integrationCore.getMusicStatus() == PLAY_STATUS) {
            mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG,1000);
            //pauseImg.setVisibility(View.INVISIBLE);
        } else {

        }*/

        //pauseImg.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audioManager.adjustStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if ((mobNetInfo != null && mobNetInfo.isConnected()) || (wifiNetInfo != null && wifiNetInfo.isConnected())) {
                netWorkConnected = true;
                mHandler.sendEmptyMessage(UPDATE_UI);
                Log.d(TAG, "net work is avaiable");
            } else {
                netWorkConnected = false;
                Log.d(TAG, "net work is unavaiable");
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
        IntegrationCore.getIntergrationCore(this).clearMusicListener(BKMusicActivity.this);
        UIControlMgr.getInstance().releaseResource(mHashCode);
    }

    public void onWakeUpSelect(String action) {
        Log.d(TAG, "onWakeUpSelect action : " + action);

        if (ConstantNavUc.OPEN_MENU_MUSIC.equals(action)) {
            //打开播放列表
            openPlaylist();
        } else if (ConstantNavUc.MUSIC_OPEN_MENU.equals(action)) {
            //打开播放列表
            openPlaylist();
        } else if (ConstantNavUc.MUSIC_CLOSE_MENU.equals(action)) {
            //关闭播放列表
            if (songListPopWindow != null && songListPopWindow.isShowing()) {
                songListPopWindow.dismiss();
            }
        } else if (ConstantNavUc.MUSIC_BACK_MUSIC.equals(action)) {
            finish();
        } else if (ConstantNavUc.MUSIC_PREVIOUS_ITEM.equals(action)) {
            int position = getCurrentPosition();
            if (position == -1) {
                ToastUtil.show(R.string.song_not_list, BKMusicActivity.this);
                return;
            } else {
                if (position == 0) {
                    ToastUtil.show(R.string.cursong_first, BKMusicActivity.this);
                    return;
                } else {
                    integrationCore.playSongIdAtIndex(curplaySongIdList, position - 1);
                    if (playListAdpter != null) {
                        playListAdpter.selectPosition(position - 1, false);
                        playListAdpter.notifyDataSetChanged();
                    }
                }
            }
        } else if (ConstantNavUc.MUSIC_NEXT_MUSIC.equals(action)) {
            int position = getCurrentPosition();
            if (position == -1) {
                ToastUtil.show(R.string.song_not_list, BKMusicActivity.this);
                return;
            } else {
                if (position == curplaySongIdList.size() - 1) {
                    ToastUtil.show(R.string.cursong_last, BKMusicActivity.this);
                    return;
                } else {
                    integrationCore.playSongIdAtIndex(curplaySongIdList, position + 1);
                    if (playListAdpter != null) {
                        playListAdpter.selectPosition(position + 1, false);
                        playListAdpter.notifyDataSetChanged();
                    }
                }
            }
        } else if (ConstantNavUc.MUSIC_PREVIOUS_PAGE.equals(action)) {
            if (songListPopWindow != null && songListPopWindow.isShowing() && curPlayList != null && curplaySongIdList != null) {
                item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                songListRecyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - item);
            }
        } else if (ConstantNavUc.MUSIC_NEXT_PAGE.equals(action)) {
            if (songListPopWindow != null && songListPopWindow.isShowing() && curplaySongIdList != null && curPlayList != null) {
                item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                songListRecyclerView.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + item);
            }
        } else if (ConstantNavUc.MUSIC_PLAY.equals(action)) {
            if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                return;
            } else {
                integrationCore.playMusic();
                pauseImg.setSelected(true);
                pauseImg.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG, 500);
            }
        } else if (ConstantNavUc.MUSIC_PAUSE.equals(action)) {
            if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                integrationCore.setMusicPause();
                pauseImg.setVisibility(View.VISIBLE);
                pauseImg.setSelected(false);
                mHandler.sendEmptyMessageDelayed(HIDE_PAUSEIMG, 500);
            }
        } else if (ConstantNavUc.MUSIC_PREVIOUS.equals(action)) {
            if (integrationCore != null) {
                int currentPosition = getCurrentPosition();
                int targetPosition = 0;
                int code = integrationCore.setMusicPrevious();
                if (code == ERROR_CODE_NO_PERMISSION) {
                    if (currentPosition == 0) {
                        targetPosition = curplaySongIdList.size() - 2;
                    } else if (currentPosition == 1) {
                        targetPosition = curplaySongIdList.size() - 1;
                    } else {
                        targetPosition = currentPosition - 2;
                    }
                    integrationCore.playSongIdAtIndex(curplaySongIdList, targetPosition);
                }
                mHandler.sendEmptyMessage(UPDATE_UI);
            }
        } else if (ConstantNavUc.MUSIC_NEXT.equals(action)) {
            if (integrationCore != null) {
                integrationCore.setMusicNext();
                mHandler.sendEmptyMessage(UPDATE_UI);
            }
        }
    }

    @Override
    public void onSelectCancel() {
        onBackPressed();
    }

    @Override
    public void onSelectBackHome(String action) {

    }

    @Override
    public void onPhoneCallAnswer(String action) {
        CallUtils.answerCall(this);
    }

    @Override
    public void onPhoneCallCancel(String action) {
        CallUtils.rejectCall();
    }

    @Override
    public void onSelectItemPosition(int position) {
        Log.d(TAG, "position : " + position);
    }

    @Override
    public void onMediaPlay(String action) {

    }

    @Override
    public void onMediaPause(String action) {

    }


    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG, "voice control action :" + action);
        String[] strs = action.split(":");
        if (strs.length == 2) {
            if (songListPopWindow != null && songListPopWindow.isShowing()) {
                int index = Integer.parseInt(strs[1]);

                Log.d(TAG, "onSelectOtherOC firstPosition : " + firstPosition);
                Log.d(TAG, "onSelectOtherOC index : " + index);
                Log.d(TAG, "curplaySongIdList size " + curplaySongIdList.size());
                integrationCore.playSongIdAtIndex(curplaySongIdList, index);


            }
        } else if (ConstantNavUc.BK_MUSIC_NEXT.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_NEXT);
        } else if (ConstantNavUc.BK_MUSIC_PREV.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_PREV);
        } else if (ConstantNavUc.BK_MUSIC_STOP_PLAY.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_PAUSE);
        } else if (ConstantNavUc.MUSIC_PLAY.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_PLAY);
        } else if (ConstantNavUc.MUSIC_NEW.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_NEW);
        } else if (ConstantNavUc.MUSIC_TOP.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_TOP);
        } else if (ConstantNavUc.MUSIC_FAV.equals(action)) {
//            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_FAV);
            isLoading = true;
            mHandler.sendEmptyMessage(GET_FAV_SONGS);
        } else if (ConstantNavUc.MUSIC_LOCAL.equals(action)) {
            mHandler.sendEmptyMessage(GET_LOCAL_SONGS);
//            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_LOCAL);
        } else if (ConstantNavUc.BK_MUSIC_OPEN_PLAYLIST.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_OPENPLAY);
        } else if (ConstantNavUc.BK_MUSIC_OPEN_MENU.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_NEW_OPENMENU);
        } else if (ConstantNavUc.MUSIC_PAGE_NEXT.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_NEXT_PAGE);
        } else if (ConstantNavUc.MUSIC_PAGE_PRE.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_PREV_PAGE);
        } else if (ConstantNavUc.BK_MUSIC_CLOSE_PLAYLIST.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_CLOSE_PLAY);
        } else if (ConstantNavUc.BK_MUSIC_CLOSE_MENU.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_CLOSE_MENU);
        } else if (ConstantNavUc.BK_MUSIC_EXIT.equals(action)) {
            mHandler.sendEmptyMessage(VOICE_MSG_MUSIC_EXIT);
        }
    }

    private void musicPrevForUIControl() {
        if (integrationCore != null) {
            int currentPosition = getCurrentPosition();
            int targetPosition = 0;
            int code = integrationCore.setMusicPrevious();
            if (code == ERROR_CODE_NO_PERMISSION) {
                if (currentPosition == 0) {
                    targetPosition = curplaySongIdList.size() - 2;
                } else if (currentPosition == 1) {
                    targetPosition = curplaySongIdList.size() - 1;
                } else {
                    targetPosition = currentPosition - 2;
                }
                integrationCore.playSongIdAtIndex(curplaySongIdList, targetPosition);
            }
        }
    }

    /**
     * 打開播放列表
     */
    private void openPlaylist() {
        if (menuWindow != null) {
            menuWindow.dismiss();
        }
        if (songListPopWindow == null || !songListPopWindow.isShowing()) {
            showSongList();
        }
    }

    /**
     * 打開菜單
     */
    private void openMenuList() {
        if (songListPopWindow != null) {
            songListPopWindow.dismiss();
        }
        Log.d("sasuke", "menu start");
        if (menuWindow == null || !menuWindow.isShowing()) {
            Log.d("sasuke", "menuWindow is null");
            showFolder();
        }
    }


    /**
     * Init music wheel control listener
     */
    private void initMusicWheelControllerListener() {
        if (wheelControl == null) {
            wheelControl = ArielApplication.getWheelControlManager();
        }
        if (musicControlListener != null) musicControlListener = null;
        musicControlListener = new MusicControlListener() {
            @Override
            public void previous() {
                //integrationCore.setMusicPrevious();
                musicPrevForUIControl();
            }

            @Override
            public void next() {
                integrationCore.setMusicNext();
            }
        };
        wheelControl.setMusicListener(musicControlListener);
    }

}