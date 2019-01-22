package com.qinggan.app.arielapp.minor.music;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;
import com.qinggan.app.arielapp.WheelControl.WheelControl;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PlayListAdpter;
import com.qinggan.app.arielapp.minor.music.imagecache.DoubleCache;
import com.qinggan.app.arielapp.minor.music.imagecache.ImageLoader;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.ArielLog;
import com.qinggan.app.arielapp.ui.pin.findback.IFindbackPinView;
import com.qinggan.app.arielapp.utils.AllWakeupEvent;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.voiceapi.nluresult.music.MusicCommandCallback;
import com.qinggan.app.voiceapi.nluresult.music.MusicSearchCallback;
import com.qinggan.app.widget.custom.RoundCornorImageView;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;
import com.tencent.qqmusic.third.api.contract.Data;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.arielapp.minor.integration.MusicContacts.EMPTYSTRINGVALUE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.ERROR_CODE_NO_PERMISSION;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTURI;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.MODEGETFOLDERLIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.PLAYSONGLOCALPATHCMD;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.UNKNOWSTRINGVALUE;
import static com.tencent.qqmusic.third.api.contract.Data.FolderType.RANK;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_SONG_CHANGED;

/**
 * Created by yang
 * Time on 2018/11/8.
 * Function  音乐界面
 */
public class MusicActivity extends UIControlBaseActivity implements View.OnClickListener,
        ClickViewInterface, IntegrationCore.MusicListener, ImageLoader.AlbumDownloadCallback {

    private static final String TAG = MusicActivity.class.getSimpleName();
    private Context mContext = ArielApplication.getApp();
    private ImageView mImageView;
    private ImageView mNextView;
    private ImageView mSoundView;
    private RelativeLayout volumeLinearLayout;
    private RelativeLayout rl_bottom;
    private RelativeLayout title_relativelayout;
    private RoundCornorImageView pauseMusic;
    private TextView musicName;
    private LinePulseView wakeupIcon;
    private IntegrationCore integrationCore = null;
    private ArrayList<String> list = new ArrayList<String>() {{
        add(API_EVENT_PLAY_SONG_CHANGED);
    }};
    private ImitateIphoneSwitch imitateIphoneSwitch;
    private LinearLayout musicContainer;
    private TextView textViewNmae;
    private ImageView mImagePause;
    private Data.Song mCurrentSong;
    private RecyclerView recyclerView_music;
    private PlayListAdpter playListAdpter = new PlayListAdpter(mContext);
    private List<Data.Song> playlist = new ArrayList<>();
    private ArrayList mList = new ArrayList<>();
    private String currentSongId;
    private MySeekBar seekbar;
    private PopupWindow mMusicListWindow;
    private int VOLUME_SWIPE_THRESHOLD = 10;
    private int SWITCH_MUSIC_SWIPE_THRESHOLD = 20;
    private static final int CODE_VOLUME = 0;
    private static final int CODE_LIST = 1;
    private static final int CODE_PLAY = 2;
    private static final int CODE_SEEKBAR = 3;
    private static final int CODE_SONGNAME = 4;
    private static final int CODE_INITVOLUME = 5;
    private static final int CODE_IMAGELOAD = 6;
    private static final int CODE_PLAY_STATUS = 4;
    private static final int HIDE_PLAY_PAUSE = 7;
    private static final int SONG_CHANGE = 8;
    private static final int SHOULD_REFLAS = 0x888;
    private static final int HIDE_PLAY_PAUSE_TIME = 1000;
    private static final int HANDLER_DELAY = 500;
    private static final int PLAY_STATUS = 4;
    private static final int VOLUME_UP = 4;
    private static final int VOLUME_DOWN = -4;
    private static final int POSITION_ZERO = 0;
    private static final int POSITION_ONE = 1;
    private float mVolumnPercent = 0;
    ImageLoader mImageLoader;
    //ImageCache imageCache;
    //DoubleCache doubleCache;
    int currentPostion = POSITION_ZERO;
    LinearLayoutManager linearLayoutManager;
    int itemPositioon = POSITION_ZERO;
    int item;
    private MusicControlListener musicControlListener;
    private WheelControl wheelControl;
    String folderType;
    boolean isCanPlay = true;
    private static final String MODULE_NAME = "music";
    private boolean folderLocalMutex = false;
    private String firstSongId;
    private String secondSongId;
    private boolean previousTime = true;
    ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> elementItems = new ArrayList<>();
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;
    private PopupWindow mMusicFolderWindow;

    ImageView local_image;
    ImageView new_image;
    ImageView like_image;
    ImageView popular_image;

    private static final int CODE_MUSIC_PLAY = 10;
    private static final int CODE_MUSIC_PAUSE = 11;
    private static final int CODE_PREVIOUS_ITEM = 12;
    private static final int CODE_NEXT_ITEM = 13;
    private static final int CODE_OPEN_LIST = 14;
    private static final int CODE_CLOSE_LIST = 15;
    private static final int CODE_MUSIC_BACK = 16;
    private static final int CODE_PREVIOUS_PAGE = 17;
    private static final int CODE_NEXT_PAGE = 18;
    private static final int CODE_MUSIC_PREVIOUS = 19;
    private static final int CODE_MUSIC_NEXT = 20;

    private static final int CODE_MUSIC_LOACL = 21;
    private static final int CODE_MUSIC_NEW = 22;
    private static final int CODE_MUSIC_LIKE = 23;
    private static final int CODE_MUSIC_POPULAR = 24;

    private static final int CODE_OPEN_MENU = 25;
    private static final int CODE_CLOSE_MENU = 26;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        VOLUME_SWIPE_THRESHOLD *= dm.density;
        SWITCH_MUSIC_SWIPE_THRESHOLD *= dm.density;
        initView();
//        initMusicWheelControllerListener();

        mImageLoader = ImageLoader.getInstance();
        mImageLoader.setImageCache(DoubleCache.getCacheInstance());
        mImageLoader.addDownloadCallback(this);
        getSwipeBackLayout().setEnableGesture(false);

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
    }

    private void addWakeupElements() {
        if (mHotwordManager == null) {
            return;
        }
        elementItems.clear();

        com.qinggan.qinglink.bean.UIControlElementItem musicListElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        musicListElement1.setWord(getString(R.string.music_open_list1));
        musicListElement1.setIdentify(ConstantNavUc.MUSIC_OPEN_lIST1);
        elementItems.add(musicListElement1);

        com.qinggan.qinglink.bean.UIControlElementItem musicListElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        musicListElement2.setWord(getString(R.string.music_open_list2));
        musicListElement2.setIdentify(ConstantNavUc.MUSIC_OPEN_lIST2);
        elementItems.add(musicListElement2);

        com.qinggan.qinglink.bean.UIControlElementItem musicMenuElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        musicMenuElement2.setWord(getString(R.string.music_open_menu2));
        musicMenuElement2.setIdentify(ConstantNavUc.MUSIC_OPEN_MENU2);
        elementItems.add(musicMenuElement2);

        com.qinggan.qinglink.bean.UIControlElementItem backElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        backElement2.setWord(getString(R.string.music_back2));
        backElement2.setIdentify(ConstantNavUc.MUSIC_BACK2);
        elementItems.add(backElement2);

        com.qinggan.qinglink.bean.UIControlElementItem backElement3 = new com.qinggan.qinglink.bean.UIControlElementItem();
        backElement3.setWord(getString(R.string.music_back3));
        backElement3.setIdentify(ConstantNavUc.MUSIC_BACK3);
        elementItems.add(backElement3);

        com.qinggan.qinglink.bean.UIControlElementItem uiPreviousItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        uiPreviousItem.setWord(getString(R.string.previous_item));
        uiPreviousItem.setIdentify(ConstantNavUc.MUSIC_PREVIOUS_ITEM);
        elementItems.add(uiPreviousItem);

        com.qinggan.qinglink.bean.UIControlElementItem uiNextItem = new com.qinggan.qinglink.bean.UIControlElementItem();
        uiNextItem.setWord(getString(R.string.next_item));
        uiNextItem.setIdentify(ConstantNavUc.MUSIC_NEXT_MUSIC);
        elementItems.add(uiNextItem);

        com.qinggan.qinglink.bean.UIControlElementItem uiPreviousPage = new com.qinggan.qinglink.bean.UIControlElementItem();
        uiPreviousPage.setWord(getString(R.string.music_previous_page));
        uiPreviousPage.setIdentify(ConstantNavUc.MUSIC_PREVIOUS_PAGE);
        elementItems.add(uiPreviousPage);

        com.qinggan.qinglink.bean.UIControlElementItem uiNextPage = new com.qinggan.qinglink.bean.UIControlElementItem();
        uiNextPage.setWord(getString(R.string.music_next_page));
        uiNextPage.setIdentify(ConstantNavUc.MUSIC_NEXT_PAGE);
        elementItems.add(uiNextPage);

        com.qinggan.qinglink.bean.UIControlElementItem closeListElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeListElement1.setWord(getString(R.string.music_list_close1));
        closeListElement1.setIdentify(ConstantNavUc.MUSIC_LIST_CLOSE1);
        elementItems.add(closeListElement1);

        com.qinggan.qinglink.bean.UIControlElementItem closeListElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeListElement2.setWord(getString(R.string.music_list_close2));
        closeListElement2.setIdentify(ConstantNavUc.MUSIC_LIST_CLOSE2);
        elementItems.add(closeListElement2);

        com.qinggan.qinglink.bean.UIControlElementItem localFolderElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        localFolderElement.setWord(getString(R.string.music_local));
        localFolderElement.setIdentify(ConstantNavUc.MUSIC_FOLDER_LOCAL);
        elementItems.add(localFolderElement);

        com.qinggan.qinglink.bean.UIControlElementItem newFolderElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        newFolderElement.setWord(getString(R.string.music_new));
        newFolderElement.setIdentify(ConstantNavUc.MUSIC_FOLDER_NEW);
        elementItems.add(newFolderElement);

        com.qinggan.qinglink.bean.UIControlElementItem likeFolderElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        likeFolderElement.setWord(getString(R.string.music_like));
        likeFolderElement.setIdentify(ConstantNavUc.MUSIC_FOLDER_LIKE);
        elementItems.add(likeFolderElement);

        com.qinggan.qinglink.bean.UIControlElementItem popularFolderElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        popularFolderElement.setWord(getString(R.string.music_popular));
        popularFolderElement.setIdentify(ConstantNavUc.MUSIC_FOLDER_POPULAR);
        elementItems.add(popularFolderElement);

        com.qinggan.qinglink.bean.UIControlElementItem closeMenuElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeMenuElement1.setWord(getString(R.string.music_menu_close1));
        closeMenuElement1.setIdentify(ConstantNavUc.MUSIC_MENU_CLOSE1);
        elementItems.add(closeMenuElement1);

        com.qinggan.qinglink.bean.UIControlElementItem closeMenuElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        closeMenuElement2.setWord(getString(R.string.music_menu_close2));
        closeMenuElement2.setIdentify(ConstantNavUc.MUSIC_MENU_CLOSE2);
        elementItems.add(closeMenuElement2);

//        com.qinggan.qinglink.bean.UIControlElementItem playMenuElement = new com.qinggan.qinglink.bean.UIControlElementItem();
//        playMenuElement.setWord(getString(R.string.play_music));
//        playMenuElement.setIdentify(ConstantNavUc.MUSIC_PLAY);
//        elementItems.add(playMenuElement);
//
//        com.qinggan.qinglink.bean.UIControlElementItem pauseElement = new com.qinggan.qinglink.bean.UIControlElementItem();
//        pauseElement.setWord(getString(R.string.pause_music));
//        pauseElement.setIdentify(ConstantNavUc.MUSIC_PAUSE);
//        elementItems.add(pauseElement);

//        com.qinggan.qinglink.bean.UIControlElementItem uiPreviousMusic = new com.qinggan.qinglink.bean.UIControlElementItem();
//        uiPreviousMusic.setWord(getString(R.string.previous_music));
//        uiPreviousMusic.setIdentify(ConstantNavUc.MUSIC_PREVIOUS);
//        elementItems.add(uiPreviousMusic);
//
//        com.qinggan.qinglink.bean.UIControlElementItem uiNextMusic = new com.qinggan.qinglink.bean.UIControlElementItem();
//        uiNextMusic.setWord(getString(R.string.next_music));
//        uiNextMusic.setIdentify(ConstantNavUc.MUSIC_NEXT);
//        elementItems.add(uiNextMusic);

        mHotwordManager.setElementUCWords(MODULE_NAME, elementItems);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {
            @Override
            public void onItemSelected(String identify) {
                ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                        " onItemSelected:   ");
//                onSelectOtherOC(identify);
                onWakeUpSelect(identify);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_music;
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void initView() {
        seekbar = (MySeekBar) findViewById(R.id.my_seekbar);
        mImagePause = (ImageView) findViewById(R.id.music_status_pause);
        textViewNmae = (TextView) findViewById(R.id.name);
        musicContainer = (LinearLayout) findViewById(R.id.fragment_music);
        wakeupIcon = (LinePulseView) findViewById(R.id.wakeup);
        imitateIphoneSwitch = (ImitateIphoneSwitch) findViewById(R.id.volune_profile);
        musicName = (TextView) findViewById(R.id.music_name);
        mNextView = (ImageView) findViewById(R.id.delete_image);
        mImageView = (ImageView) findViewById(R.id.list_img);
        volumeLinearLayout = (RelativeLayout) findViewById(R.id.volume_linear);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_func);
        title_relativelayout = (RelativeLayout) findViewById(R.id.title_relativelayout);
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        mSoundView = (ImageView) findViewById(R.id.sound_img);
        pauseMusic = (RoundCornorImageView) findViewById(R.id.music_pause);
        pauseMusic.setRadium(getResources().getDimensionPixelSize(R.dimen.dp20));
        mSoundView.setOnClickListener(this);
        volumeLinearLayout.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        integrationCore.registerEventListener(list);
        wakeupIcon.setOnClickListener(this);
        textViewNmae.setOnClickListener(this);
//        rl_bottom.setOnClickListener(this);
        title_relativelayout.setOnClickListener(this);
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(this);
        ArrayList<Data.Song> songList = integrationCore.getPlayListCache();
        if (songList != null && songList.size() != 0) {
            initBack(songList, false);
        } else {
            integrationCore.getPlayList();
        }
        musicIconChange();
        imitateIphoneSwitch.registerCallback(new ImitateIphoneSwitch.ValueChangeCallback() {
            @Override
            public void onValueChanged(double value) {
            }
        });
        mHandler.sendEmptyMessage(CODE_INITVOLUME);
        mHandler.sendEmptyMessage(CODE_IMAGELOAD);
        mHandler.sendEmptyMessage(CODE_SONGNAME);
        mHandler.sendEmptyMessage(CODE_SEEKBAR);
        mCurrentSong = integrationCore.mMusicCMD.getCurrentSong();
        musicContainer.setOnTouchListener(new View.OnTouchListener() {
            private float startY = 0;//手指按下时的Y坐标
            private float startX = 0;//手指按下时的X坐标
            private int thresholdCount = 0;
            private float lastY = 0;
            private boolean isSlide = false;
            private boolean hasSlideVertical = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isSlide = true;
                        startX = event.getX();
                        lastY = startY = event.getY();
                        thresholdCount = 0;
                        hasSlideVertical = false;
                        mVolumnPercent = getVolumeCurrent();
                        break;
                    case MotionEvent.ACTION_MOVE: {

                        float endX = event.getX();
                        float endY = event.getY();
                        float distanceX = startX - endX;
                        float distanceY = startY - endY;
                        float absX = Math.abs(distanceX);
                        float absY = Math.abs(distanceY);
                        int count = (int) absY / VOLUME_SWIPE_THRESHOLD;
                        if (absY > absX && thresholdCount != count) {
                            if (endY - lastY < 0) {
                                setTouchVolume(VOLUME_UP);
                            } else {
                                setTouchVolume(VOLUME_DOWN);
                            }
                            volumeLinearLayout.setVisibility(View.VISIBLE);
                            thresholdCount = count;
                            hasSlideVertical = true;
                        }
                        lastY = endY;
                    }
                    break;
                    case MotionEvent.ACTION_UP: {
                        float endX = event.getX();
                        float endY = event.getY();
                        float distanceX = startX - endX;
                        float distanceY = startY - endY;
                        float absX = Math.abs(distanceX);
                        float absY = Math.abs(distanceY);
                        if (isSlide && absX > absY) {
                            if (distanceX > SWITCH_MUSIC_SWIPE_THRESHOLD) {
                                //integrationCore.setMusicPrevious();
                                //controllerPrevious();
                                int currentPosition = getCurrentPosition();
                                int targetPosition = 0;
                                int code = integrationCore.setMusicPrevious();
                                if (code == ERROR_CODE_NO_PERMISSION) {
                                    if (currentPosition == 0) {
                                        targetPosition = mList.size() - 2;
                                    }else if (currentPosition == 1) {
                                        targetPosition = mList.size() - 1;
                                    } else {
                                        targetPosition = currentPosition - 2;
                                    }
                                    integrationCore.playSongIdAtIndex(mList, targetPosition);
                                }
                                isSlide = false;
                                palyMusicIcon();
                            } else if (distanceX < -SWITCH_MUSIC_SWIPE_THRESHOLD) {
                                integrationCore.setMusicNext();
                                isSlide = false;
                                palyMusicIcon();
                            }
                        }
                        if ((!hasSlideVertical) && absX < VOLUME_SWIPE_THRESHOLD && absY < VOLUME_SWIPE_THRESHOLD) {
                            onClick(pauseMusic);
                        }
                        hasSlideVertical = false;
                        mHandler.sendEmptyMessageDelayed(CODE_VOLUME, HANDLER_DELAY);
                    }
                    break;
                }
                return true;
            }
        });
        NluResultManager.getInstance().setMusicSearchCallback(mMusicSearchCallback);
        NluResultManager.getInstance().setMusicCommandCallback(mMusicCommandCallback);
        //imageCache = doubleCache.getCacheInstance();
        //imageLoader = imageLoader.getInstance();
        //imageLoader.setImageCache(imageCache);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mHandler.sendEmptyMessageAtTime(SONG_CHANGE,HIDE_PLAY_PAUSE_TIME);
        musicIconChange();
        mHandler.sendEmptyMessageDelayed(CODE_SEEKBAR, HANDLER_DELAY);
        mHandler.sendEmptyMessage(CODE_SONGNAME);
        mHandler.sendEmptyMessage(CODE_IMAGELOAD);
    }

    @Override
    protected void onStart() {
        super.onStart();
        addUIControlItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicIconChange();
        setMusicListener();
        initMusicWheelControllerListener();
        isOnpause = false;
        addWakeupElements();
    }

    void musicIconChange() {
        if (integrationCore.getMusicStatus() == CODE_PLAY_STATUS) {
            mImagePause.setSelected(true);
            mHandler.sendEmptyMessageDelayed(HIDE_PLAY_PAUSE, HIDE_PLAY_PAUSE_TIME);
        } else {
            mImagePause.setSelected(false);
            mImagePause.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(this);
        mHandler.sendEmptyMessageDelayed(CODE_SEEKBAR, HANDLER_DELAY);
        mHandler.sendEmptyMessageDelayed(SHOULD_REFLAS,5000);
    }

    private void addUIControlItems() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem musicMenuElement = new UIControlElementItem();
        musicMenuElement.addWord(getString(R.string.music_open_list1));
        musicMenuElement.addWord(getString(R.string.music_open_menu2));
        musicMenuElement.addWord(getString(R.string.music_open_menu3));
        musicMenuElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_OPEN_MENU);
        mUIControlElements.add(musicMenuElement);

        UIControlElementItem closeMenuElement = new UIControlElementItem();
        closeMenuElement.addWord(getString(R.string.music_close_menu1));
        closeMenuElement.addWord(getString(R.string.music_close_menu2));
        closeMenuElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_CLOSE_MENU);
        mUIControlElements.add(closeMenuElement);

        UIControlElementItem backElement = new UIControlElementItem();
        backElement.addWord(getString(R.string.music_back));
        backElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_BACK_MUSIC);
        mUIControlElements.add(backElement);

        UIControlElementItem uiPreviousItem = new UIControlElementItem();
        uiPreviousItem.addWord(getString(R.string.previous_item));
        uiPreviousItem.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_PREVIOUS_ITEM);
        mUIControlElements.add(uiPreviousItem);

        UIControlElementItem uiNextItem = new UIControlElementItem();
        uiNextItem.addWord(getString(R.string.next_item));
        uiNextItem.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_NEXT_MUSIC);
        mUIControlElements.add(uiNextItem);

        UIControlElementItem uiPreviousPage = new UIControlElementItem();
        uiPreviousPage.addWord(getString(R.string.music_previous_page));
        uiPreviousPage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_PREVIOUS_PAGE);
        mUIControlElements.add(uiPreviousPage);

        UIControlElementItem uiNextPage = new UIControlElementItem();
        uiNextPage.addWord(getString(R.string.music_next_page));
        uiNextPage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_NEXT_PAGE);
        mUIControlElements.add(uiNextPage);

        UIControlElementItem uiPreviousMusic = new UIControlElementItem();
        uiPreviousMusic.addWord(getString(R.string.previous_music));
        uiPreviousMusic.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_PREVIOUS);
        mUIControlElements.add(uiPreviousMusic);

        UIControlElementItem uiNextMusic = new UIControlElementItem();
        uiNextMusic.addWord(getString(R.string.next_music));
        uiNextMusic.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_NEXT);
        mUIControlElements.add(uiNextMusic);

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();

    }

    @Override
    public void onSelectOtherOC(String action) {
        if(ConstantNavUc.OPEN_MENU_MUSIC.equals(action)){
            musicPop();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    " MUSIC_OPEN_MENU:   ");
        } else if (ConstantNavUc.MUSIC_OPEN_MENU.equals(action)) {
            musicPop();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    " MUSIC_OPEN_MENU:   ");
        } else if (ConstantNavUc.MUSIC_CLOSE_MENU.equals(action)) {
            if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                mMusicListWindow.dismiss();
            }

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "MUSIC_CLOSE_MENU   ");
        } else if (ConstantNavUc.MUSIC_BACK_MUSIC.equals(action)) {
            finish();
        } else if (ConstantNavUc.MUSIC_PREVIOUS_ITEM.equals(action)) {
            if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                controllerPrevious();
                palyMusicIcon();
            }

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    " ControllerPrevious():   ");
        } else if (ConstantNavUc.MUSIC_NEXT_MUSIC.equals(action)) {
            if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                ControllerNext();
                palyMusicIcon();
            }

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    " ControllerNext():   ");
        } else if (ConstantNavUc.MUSIC_PREVIOUS_PAGE.equals(action)) {
            if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                if (null != recyclerView_music && null != playListAdpter) {
                    recyclerView_music.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - item);
                }
            }

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "MUSIC_PREVIOUS_PAGE:   ");
        } else if (ConstantNavUc.MUSIC_NEXT_PAGE.equals(action)) {
            if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                if (null != recyclerView_music && null != playListAdpter) {
                    recyclerView_music.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + item);
                }
            }

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "MUSIC_NEXT_PAGE:   ");
        }else if (ConstantNavUc.MUSIC_PLAY.equals(action)) {
            integrationCore.playMusic();
            musicIconChange();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "playMusic():   " );
        } else if (ConstantNavUc.MUSIC_PAUSE.equals(action)) {
            integrationCore.setMusicPause();
            musicIconChange();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "setMusicPause():   ");
        }else if (ConstantNavUc.MUSIC_PREVIOUS.equals(action)) {
            integrationCore.setMusicPrevious();
            palyMusicIcon();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "setMusicPrevious():   ");
        } else if (ConstantNavUc.MUSIC_NEXT.equals(action)) {
            integrationCore.setMusicNext();
            palyMusicIcon();

            ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                    "setMusicNext():   ");
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    public void onSelectCancel() {
//        onBackPressed();
    }

    @Override
    public void onSelectBackHome(String action) {
        super.onSelectBackHome(action);
    }

    @Override
    public void onSelectItemPosition(int position) {

        ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                "onSelectItemPosition:   " + position);

        if (null != recyclerView_music && null != playListAdpter){
            recyclerView_music.smoothScrollToPosition(position);
            if (mList.size() != POSITION_ZERO && null != mList) {
                integrationCore.playSongIdAtIndex(mList, position);
            }
            playListAdpter.selectPosition(position, true);
            playListAdpter.notifyDataSetChanged();
        }
    }

    public void onWakeUpSelect(String action) {

        ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                " onWakeUpSelect:   " + action);

        if(ConstantNavUc.MUSIC_OPEN_lIST1.equals(action)){
            mHandler.sendEmptyMessage(CODE_OPEN_LIST);
        } else if (ConstantNavUc.MUSIC_OPEN_lIST2.equals(action)) {
            mHandler.sendEmptyMessage(CODE_OPEN_LIST);
        } else if (ConstantNavUc.MUSIC_OPEN_MENU2.equals(action)) {
            mHandler.sendEmptyMessage(CODE_OPEN_MENU);
        }else if (ConstantNavUc.MUSIC_BACK2.equals(action)) {
            mHandler.sendEmptyMessage(CODE_MUSIC_BACK);
        }else if (ConstantNavUc.MUSIC_BACK3.equals(action)) {
            mHandler.sendEmptyMessage(CODE_MUSIC_BACK);
        }
        else if (ConstantNavUc.MUSIC_PREVIOUS_ITEM.equals(action)) {
            mHandler.sendEmptyMessage(CODE_PREVIOUS_ITEM);
        } else if (ConstantNavUc.MUSIC_NEXT_MUSIC.equals(action)) {
            mHandler.sendEmptyMessage(CODE_NEXT_ITEM);
        } else if (ConstantNavUc.MUSIC_PREVIOUS_PAGE.equals(action)) {
            mHandler.sendEmptyMessage(CODE_PREVIOUS_PAGE);
        } else if (ConstantNavUc.MUSIC_NEXT_PAGE.equals(action)) {
            mHandler.sendEmptyMessage(CODE_NEXT_PAGE);
        }

        else if (ConstantNavUc.MUSIC_LIST_CLOSE1.equals(action)) {
            mHandler.sendEmptyMessage(CODE_CLOSE_LIST);
        } else if (ConstantNavUc.MUSIC_LIST_CLOSE2.equals(action)) {
            mHandler.sendEmptyMessage(CODE_CLOSE_LIST);
        }

        else if (ConstantNavUc.MUSIC_FOLDER_LOCAL.equals(action)) {
            local_image.setImageResource(R.mipmap.music_buttun_music_press);
            mHandler.sendEmptyMessage(CODE_MUSIC_LOACL);
        }else if (ConstantNavUc.MUSIC_FOLDER_NEW.equals(action)) {
            new_image.setImageResource(R.mipmap.music_buttun_newsong_press);
            mHandler.sendEmptyMessage(CODE_MUSIC_NEW);
        }else if (ConstantNavUc.MUSIC_FOLDER_LIKE.equals(action)) {
            like_image.setImageResource(R.mipmap.music_buttun_like_press);
            mHandler.sendEmptyMessage(CODE_MUSIC_LIKE);
        }else if (ConstantNavUc.MUSIC_FOLDER_POPULAR.equals(action)) {
            popular_image.setImageResource(R.mipmap.music_buttun_popular_press);
            mHandler.sendEmptyMessage(CODE_MUSIC_POPULAR);
        }

        else if (ConstantNavUc.MUSIC_MENU_CLOSE1.equals(action)) {
            mHandler.sendEmptyMessage(CODE_CLOSE_MENU);
        }else if (ConstantNavUc.MUSIC_MENU_CLOSE2.equals(action)) {
            mHandler.sendEmptyMessage(CODE_CLOSE_MENU);
        }

//        else if (ConstantNavUc.MUSIC_PLAY.equals(action)) {
//            mHandler.sendEmptyMessageAtTime(CODE_MUSIC_PLAY,HANDLER_DELAY);
//        } else if (ConstantNavUc.MUSIC_PAUSE.equals(action)) {
//            mHandler.sendEmptyMessageAtTime(CODE_MUSIC_PAUSE,HANDLER_DELAY);
//        }else if (ConstantNavUc.MUSIC_PREVIOUS.equals(action)) {
//            mHandler.sendEmptyMessageAtTime(CODE_MUSIC_PREVIOUS,HANDLER_DELAY);
//        } else if (ConstantNavUc.MUSIC_NEXT.equals(action)) {
//            mHandler.sendEmptyMessageAtTime(CODE_MUSIC_NEXT,HANDLER_DELAY);
//        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_VOLUME:
                    volumeLinearLayout.setVisibility(View.GONE);
                    break;
                case CODE_LIST:
                    playListAdpter.setList(playlist);
                    playListAdpter.notifyDataSetChanged();
                    mHandler.sendEmptyMessage(CODE_IMAGELOAD);
                    mHandler.sendEmptyMessage(CODE_SONGNAME);
                    break;
                case CODE_PLAY:
                    mHandler.removeMessages(HIDE_PLAY_PAUSE);
                    mImagePause.setVisibility(View.VISIBLE);
                    mImagePause.setSelected(false);
                    mHandler.sendEmptyMessageDelayed(HIDE_PLAY_PAUSE, HIDE_PLAY_PAUSE_TIME);
                    break;
                case CODE_SEEKBAR:
                    int currentposition = (int) integrationCore.getCurrTime();
                    int totalduration = (int) integrationCore.getTotalTime();
                    seekbar.setMax(totalduration);
                    seekbar.setProgress(currentposition);
                    if (currentposition < totalduration) {
                        mHandler.sendEmptyMessageDelayed(CODE_SEEKBAR, HANDLER_DELAY);
                    } else {
                        seekbar.setProgress(CODE_VOLUME);
                    }
                    break;
                case CODE_SONGNAME:
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                            "CODE_SONGNAME   ");
                    if (integrationCore != null && integrationCore.mMusicCMD != null) {
                        //currentSong = integrationCore.mMusicCMD.getCurrentSong();
                        if (mCurrentSong != null) {
                            musicName.setText(!TextUtils.isEmpty(mCurrentSong.getTitle()) ?
                                    mCurrentSong.getTitle() : EMPTYSTRINGVALUE);
                            textViewNmae.setText(mCurrentSong.getSinger() != null ? mCurrentSong.getSinger().getTitle() :
                                    EMPTYSTRINGVALUE);
                        }
                    } else {
                        musicName.setText(UNKNOWSTRINGVALUE);
                        textViewNmae.setText(UNKNOWSTRINGVALUE);
                    }
                    break;
                case CODE_INITVOLUME:
                    imitateIphoneSwitch.setValues(getVolumeCurrent(), getViewHeight());
                    break;
                case CODE_IMAGELOAD:
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                            "CODE_IMAGELOAD   ");
                    if (integrationCore != null && integrationCore.mMusicCMD != null) {
                        mCurrentSong = integrationCore.mMusicCMD.getCurrentSong();
                        if (mCurrentSong != null) {
                            if (mCurrentSong.getAlbum() != null) {
                                if (!TextUtils.isEmpty(mCurrentSong.getAlbum().getCoverUri())) {
                                    String songTag = mCurrentSong.getId();
                                    Bundle songBundle = new Bundle();
                                    songBundle.putString("songUrl", mCurrentSong.getAlbum().getCoverUri());
                                    songBundle.putString("songTag", songTag);
                                    songBundle.putBoolean("isReturn", false);
                                    EventBus.getDefault().postSticky(songBundle);
                                    mImageLoader.display(mCurrentSong.getAlbum().getCoverUri(), pauseMusic, songTag);
                                }
                            }
                        }
                    }
                    break;
                case HIDE_PLAY_PAUSE:
                    mImagePause.setVisibility(View.INVISIBLE);
                    break;
                case SONG_CHANGE:
                    showPopSelectItem();
                    if (null != recyclerView_music){
                        recyclerView_music.smoothScrollToPosition(getCurrentPosition());
                    }
                    break;

//                case CODE_MUSIC_PLAY:
//                    integrationCore.playMusic();
//                    musicIconChange();
//                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
//                            "playMusic():   " );
//                    break;
//                case CODE_MUSIC_PAUSE:
//                    integrationCore.setMusicPause();
//                    musicIconChange();
//
//                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
//                            "setMusicPause():   ");
//                    break;
//                case CODE_MUSIC_PREVIOUS:
//                    integrationCore.setMusicPrevious();
//                    palyMusicIcon();
//
//                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
//                            "setMusicPrevious():   ");
//                    break;
//                case CODE_MUSIC_NEXT:
//                    integrationCore.setMusicNext();
//                    palyMusicIcon();
//
//                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
//                            "setMusicNext():   ");
//                    break;

                case CODE_OPEN_MENU:
                    showFolder();

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "CODE_OPEN_MENU:   ");
                    break;
                case CODE_CLOSE_MENU:
                    if (null != mMusicFolderWindow && mMusicFolderWindow.isShowing()){
                        mMusicFolderWindow.dismiss();
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "CODE_CLOSE_MENU  ");
                    break;
                case CODE_OPEN_LIST:
                    musicPop();

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            " CODE_OPEN_LIST:   ");
                    break;
                case CODE_CLOSE_LIST:
                    if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                        mMusicListWindow.dismiss();
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "CODE_CLOSE_LIST   ");
                    break;
                case CODE_MUSIC_BACK:
                    finish();
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "finish():   " );
                    break;
                case CODE_PREVIOUS_PAGE:
                    if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                        item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                        recyclerView_music.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() - item);
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "MUSIC_PREVIOUS_PAGE:   ");
                    break;
                case CODE_NEXT_PAGE:
                    if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                        item = linearLayoutManager.findLastVisibleItemPosition() - linearLayoutManager.findFirstVisibleItemPosition();
                        recyclerView_music.smoothScrollToPosition(linearLayoutManager.findLastVisibleItemPosition() + item);
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "MUSIC_NEXT_PAGE:   ");
                    break;
                case CODE_PREVIOUS_ITEM:
                    if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                        controllerPrevious();
                        palyMusicIcon();
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            " ControllerPrevious():   ");
                    break;
                case CODE_NEXT_ITEM:
                    if (mMusicListWindow != null && mMusicListWindow.isShowing()) {
                        ControllerNext();
                        palyMusicIcon();
                    }

                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            " ControllerNext():   ");
                    break;

                case CODE_MUSIC_LOACL:
                    if (null != mMusicFolderWindow && mMusicFolderWindow.isShowing()){
                        getMusicLocalList();
                    }
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "getMusicLocalList():   ");
                    break;
                case CODE_MUSIC_NEW:
                    if (null != mMusicFolderWindow && mMusicFolderWindow.isShowing()){
                        getMusicNewList();
                    }
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "getMusicNewList():   ");
                    break;
                case CODE_MUSIC_LIKE:
                    if (null != mMusicFolderWindow && mMusicFolderWindow.isShowing()){
                        getMusicLikeList();
                    }
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "getMusicLikeList():   ");
                    break;
                case CODE_MUSIC_POPULAR:
                    if (null != mMusicFolderWindow && mMusicFolderWindow.isShowing()){
                        getMusicPopularList();
                    }
                    ArielLog.logMusic(ArielLog.LEVEL_INFO, "WakeUpElement",
                            "getMusicPopularList():   ");
                    break;
                case SHOULD_REFLAS:
                    mHandler.sendEmptyMessage(CODE_IMAGELOAD);
                    mHandler.sendEmptyMessage(CODE_SONGNAME);
                    musicIconChange();
                    break;
            }
        }
    };

    public void setTouchVolume(float volume) {
        mVolumnPercent = mVolumnPercent + volume / 255.0f;

        if (mVolumnPercent > 1) {
            mVolumnPercent = 1;
        } else if (mVolumnPercent < 0) {
            mVolumnPercent = 0;
        }
        imitateIphoneSwitch.setValues(mVolumnPercent, getViewHeight());
        IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(mVolumnPercent);
    }

    public float getVolumeCurrent() {
        AudioManager mAudioManager = (AudioManager) (MusicActivity.this.getSystemService(Context.AUDIO_SERVICE));
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return (float) current / max;
    }

    public int getViewHeight() {
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

    private MusicSearchCallback mMusicSearchCallback = new MusicSearchCallback() {
        @Override
        public void onMusicSearchResult(String song, ArrayList<String> singers, ArrayList<String> tags) {
            Log.e(TAG,"-onMusicSearchResult : " + song + "--singers : " + singers + "--tags : " + tags);
            String searchString = "";
            if (singers != null && singers.size() > 0) {
                for (int i = 0; i < singers.size(); i++) {
                    if (i == 0) {
                        searchString = singers.get(i);
                    } else {
                        searchString = searchString + getString(R.string.and) + singers.get(i);
                    }
                }
            }
            if (!TextUtils.isEmpty(song)) {
                searchString = searchString + " " + song;
            }
            integrationCore.searchMusic(searchString);
        }
    };

    /**
     * 以下内容为语音接口部分
     */
    //音乐操作指令返回
    private MusicCommandCallback mMusicCommandCallback = new MusicCommandCallback() {
        @Override
        public void onMusicPlay() {
            integrationCore.playMusic();
            musicIconChange();
        }

        @Override
        public void onMusicPause() {
            integrationCore.setMusicPause();
            musicIconChange();
        }

        @Override
        public void onMusicStop() {
            integrationCore.setMusicStop();
            musicIconChange();
        }

        @Override
        public void onMusicPrevious() {
//            integrationCore.setMusicPrevious();
        }

        @Override
        public void onMusicNext() {
//            integrationCore.setMusicNext();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_pause:
                mHandler.removeMessages(HIDE_PLAY_PAUSE);
                isNetWork();
                if (integrationCore.getMusicStatus() == PLAY_STATUS) {
                    integrationCore.setMusicPause();
                    mImagePause.setSelected(false);
                } else {
                    integrationCore.playMusic();
                    mImagePause.setSelected(true);
                    mHandler.sendEmptyMessageDelayed(HIDE_PLAY_PAUSE, HIDE_PLAY_PAUSE_TIME);
                }
                mImagePause.setVisibility(View.VISIBLE);
                break;
            case R.id.sound_img:
                musicPop();
                break;
            case R.id.volume_linear:
                break;
            case R.id.delete_image:
                finish();
                break;
            case R.id.list_img:
                showFolder();
                break;
            case R.id.music_name:
                break;
            case R.id.wakeup:
                VoicePolicyManage.getInstance().record(true);
                break;
            case R.id.name:
                break;
        }
    }

    public void controllerPrevious() {
        if (previousTime) {
            firstSongId = integrationCore.getCurrentSong();
            previousTime = false;
        } else {
            secondSongId = integrationCore.getCurrentSong();
            previousTime = true;
        }
        if (!TextUtils.isEmpty(firstSongId) && !TextUtils.isEmpty(secondSongId)
                && firstSongId.equals(secondSongId)) {
            currentPostion = getCurrentPosition() - POSITION_ONE;
            ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                    "sameSong");
        } else {
            currentPostion = getCurrentPosition();
        }

        if (currentPostion > POSITION_ZERO) {
            if (null != recyclerView_music && null != playListAdpter) {
                recyclerView_music.smoothScrollToPosition(currentPostion - POSITION_ONE);

                playListAdpter.selectPosition(currentPostion - POSITION_ONE, true);
                playListAdpter.notifyDataSetChanged();
                if (mList.size() != POSITION_ZERO && null != mList) {
                    integrationCore.playSongIdAtIndex(mList, currentPostion - POSITION_ONE);
                }
            }
        } else {
            if (null != recyclerView_music && null != playListAdpter) {
                recyclerView_music.smoothScrollToPosition(POSITION_ZERO);

                playListAdpter.selectPosition(POSITION_ZERO, true);
                playListAdpter.notifyDataSetChanged();
                if (mList.size() != POSITION_ZERO && null != mList) {
                    integrationCore.playSongIdAtIndex(mList, POSITION_ZERO);
                }
            }
        }
    }

    public void ControllerNext() {
        currentPostion = getCurrentPosition();
        int previousItem = playListAdpter.getItemCount() - POSITION_ONE;
        if (currentPostion < previousItem) {
            if (null != recyclerView_music && null != playListAdpter) {
                recyclerView_music.smoothScrollToPosition(currentPostion + POSITION_ONE);

                playListAdpter.selectPosition(currentPostion + POSITION_ONE, true);
                playListAdpter.notifyDataSetChanged();
                if (mList.size() != POSITION_ZERO && null != mList) {
                    integrationCore.playSongIdAtIndex(mList, currentPostion + POSITION_ONE);
                }
            }
        } else {
            if (null != recyclerView_music && null != playListAdpter){
                recyclerView_music.smoothScrollToPosition(previousItem);

                playListAdpter.selectPosition(previousItem, true);
                playListAdpter.notifyDataSetChanged();
                if (mList.size() != POSITION_ZERO && null != mList) {
                    integrationCore.playSongIdAtIndex(mList, previousItem);
                }
            }
        }
    }

    private void musicPop() {
        if (null == mMusicListWindow) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.music_pop, null);
            mMusicListWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mMusicListWindow.setFocusable(true);
            mMusicListWindow.setOutsideTouchable(true);
            mMusicListWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
            mMusicListWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            RelativeLayout relativeLayout = view.findViewById(R.id.closd_music);
            recyclerView_music = view.findViewById(R.id.recycle_pop);
            linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView_music.setLayoutManager(linearLayoutManager);
            recyclerView_music.setAdapter(playListAdpter);
            playListAdpter.setmClickViewInterface(this);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicListWindow.isShowing()) {
                        mMusicListWindow.dismiss();
                    }
                }
            });
        }
        mMusicListWindow.showAtLocation(wakeupIcon, Gravity.BOTTOM, 0, 0);
        mHandler.sendEmptyMessageAtTime(SONG_CHANGE,HIDE_PLAY_PAUSE_TIME);
    }

    private void initData(ArrayList list) {
        if (list.size() != 0 && null != list) {
            Data.Song song;
            for (int i = 0; i < list.size(); i++) {
                song = (Data.Song) list.get(i);
                playlist.add(song);
                mList.add(song.getId());
            }
        }
        mHandler.sendEmptyMessage(CODE_LIST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusReceive(AllWakeupEvent allWakeupEvent) {
        //上一首
        if (allWakeupEvent == AllWakeupEvent.LAST_SONG) {
            palyMusicIcon();
            return;
        }
        //下一首
        if (allWakeupEvent == AllWakeupEvent.NEXT_SONG) {
            palyMusicIcon();
            return;
        }
        if (allWakeupEvent == AllWakeupEvent.PAUSE_MUSIC) {
            musicIconChange();
            return;
        }
        if (allWakeupEvent == AllWakeupEvent.PLAY_MUSIC) {
            palyMusicIcon();
            return;
        }
    }

    @Override
    public void songChange() {
        ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                "songChange()   ");
        Data.Song currentSong = IntegrationCore.getIntergrationCore(mContext).getCurrentMusic();

        if (mCurrentSong != null) {
            if (currentSong == null) {
                ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--MusicActivity--songChange",
                        "current song is null.");
                return;
            }

            if (currentSong.getId() == null) {
                ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--MusicActivity--songChange",
                        "current song id is null.");
                return;
            }

            if (currentSong.getId().equalsIgnoreCase(mCurrentSong.getId())) {
                ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--MusicActivity--songChange",
                        "song id is the same, do nothing.");
                return;
            }
        }

        mCurrentSong = currentSong;
        mHandler.sendEmptyMessage(CODE_IMAGELOAD);
        mHandler.sendEmptyMessage(CODE_SONGNAME);
        mHandler.sendEmptyMessageAtTime(SONG_CHANGE,HIDE_PLAY_PAUSE_TIME);
    }

    @Override
    public void playStateChanged() {

    }

    @Override
    public void favStateChange() {

    }

    @Override
    public void playListChange() {

    }

    @Override
    public void playModeChange() {

    }

    private void initCache(ArrayList list, boolean isPlay) {
        initData(list);
        if (isPlay) {
            initPosition(list);
        }
    }

    private void initPosition(ArrayList list) {
        if (!folderLocalMutex) integrationCore.playSongIdAtIndex(mList, POSITION_ZERO);
        playListAdpter.selectPosition(POSITION_ZERO, true);
        playListAdpter.notifyDataSetChanged();
        palyMusicIcon();
    }

    public void showPopSelectItem() {
        if (mList.size() != POSITION_ZERO && null != mList) {
            currentSongId = integrationCore.getCurrentSong();
            for (int i = POSITION_ZERO; i < mList.size(); i++) {
                if (!TextUtils.isEmpty(currentSongId)) {
                    if (currentSongId.equals(mList.get(i))) {
                        isNetWork();
                        playListAdpter.selectPosition(i, true);
                        playListAdpter.notifyDataSetChanged();

                        ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                                "selectPosition" + i);
                    }
                }
            }
        }
    }

    public int getCurrentPosition() {
        if (mList.size() != POSITION_ZERO && null != mList) {
            currentSongId = integrationCore.getCurrentSong();
            for (int i = POSITION_ZERO; i < mList.size(); i++) {
                if (!TextUtils.isEmpty(currentSongId)) {
                    if (currentSongId.equals(mList.get(i))) {
                        itemPositioon = i;
                    }
                }
            }
        }
        return itemPositioon;
    }

    @Override
    public void OnClickPositionListener(View view, int... position) {
        switch (view.getId()) {
            case R.id.paly_item:
                isNetWork();
                if (mList.size() != POSITION_ZERO && null != mList) {
                    integrationCore.playSongIdAtIndex(mList, position[POSITION_ZERO]);
                }
                if (position[POSITION_ONE] == POSITION_ONE) {
                    playListAdpter.selectPosition(position[POSITION_ZERO], true);
                    playListAdpter.notifyDataSetChanged();
                }
                palyMusicIcon();

                ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                        "OnClickPositionListener" + position[POSITION_ZERO]);
                break;
        }
    }

    @Override
    public void OnClickContentListener(View view, String... content) {

    }

    private void showFolder() {
        if (null == mMusicFolderWindow) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.folder_pop, null);
            mMusicFolderWindow = new PopupWindow(view,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            mMusicFolderWindow.setFocusable(true);
            mMusicFolderWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
            mMusicFolderWindow.setAnimationStyle(R.style.mypopwindow_anim_style);

            RelativeLayout relativeLayout = view.findViewById(R.id.closd_music);
            LinearLayout music_local = view.findViewById(R.id.music_local);
            LinearLayout music_new = view.findViewById(R.id.play_history);
            LinearLayout music_like = view.findViewById(R.id.music_like);
            LinearLayout music_popular = view.findViewById(R.id.music_win);
            local_image = view.findViewById(R.id.local_image);
            new_image = view.findViewById(R.id.new_image);
            like_image = view.findViewById(R.id.like_image);
            popular_image = view.findViewById(R.id.popular_image);

            music_local.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {  //本地
                    getMusicLocalList();
                }
            });
            music_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //新歌榜
                    getMusicNewList();
                }
            });
            music_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //我喜欢
                    getMusicLikeList();
                }
            });
            music_popular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//流行指数榜
                    getMusicPopularList();
                }
            });
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMusicFolderWindow.isShowing()) {
                        mMusicFolderWindow.dismiss();
                    }
                }
            });
        }

        local_image.setImageResource(R.drawable.music_local_select);
        new_image.setImageResource(R.drawable.music_newsong_select);
        like_image.setImageResource(R.drawable.music_like_select);
        popular_image.setImageResource(R.drawable.music_win_select);

        mMusicFolderWindow.showAtLocation(wakeupIcon, Gravity.BOTTOM, 0, 0);
    }

    void getMusicLocalList(){
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(MusicActivity.this);
        try {
            ArrayList<String> mTempList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTURI);
            if (mTempList != null && mTempList.size() > 0) {
                integrationCore.playSongLocalPath(mTempList);
            } else {
                Toast.makeText(mContext, R.string.pls_add_local_music, Toast.LENGTH_LONG).show();
            }
            folderLocalMutex = true;
            //integrationCore.getPlayList();
        } catch (Exception e) {

        }
        if (mMusicFolderWindow.isShowing() && mMusicFolderWindow != null){
            mMusicFolderWindow.dismiss();
        }
        palyMusicIcon();
    }

    void getMusicNewList(){
        folderType = getString(R.string.new_music);
        setMusicListener();
        try {
            getFolderList(getString(R.string.new_music));
            folderLocalMutex = false;
            if (mMusicFolderWindow.isShowing() && mMusicFolderWindow != null){
                mMusicFolderWindow.dismiss();
            }
        } catch (Exception e) {

        }
    }

    void getMusicLikeList(){
        setMusicListener();
        try {
            IntegrationCore.getIntergrationCore(mContext).getFavList();
            folderLocalMutex = false;
            if (mMusicFolderWindow.isShowing() && mMusicFolderWindow != null){
                mMusicFolderWindow.dismiss();
            }
        } catch (Exception e) {

        }
    }

    void getMusicPopularList(){
        folderType = getString(R.string.num_music);
        setMusicListener();
        try {
            getFolderList(getString(R.string.num_music));
            folderLocalMutex = false;
            if (mMusicFolderWindow.isShowing() && mMusicFolderWindow != null){
                mMusicFolderWindow.dismiss();
            }
        } catch (Exception e) {

        }
    }

    public void setMusicListener() {
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(MusicActivity.this);
        isNetWork();
    }

    public void getFolderList(String title) {
        Data.FolderInfo mFolder = integrationCore.getFolderByTitle(title);
        if (null != mFolder) {
            integrationCore.getSongList(mFolder);
        } else {
            integrationCore.mMusicCMD.getListAlias(MODEGETFOLDERLIST, "", RANK, 0);
        }
    }

    @Override
    public void onBack(ArrayList list, boolean state) {
        ArrayList mStaticList = new ArrayList();
        mStaticList.addAll(list);
        ArielLog.logMusic(ArielLog.LEVEL_INFO, "MusicFolderBack",
                "mStaticList.size() =   " + mStaticList.size() + "   state:   " + state);
        initBack(mStaticList, state);
    }

    public void initBack(ArrayList list, boolean isPlay) {
        if (null != mList || mList.size() != POSITION_ZERO) {
            mList.clear();
        }
        if (null != playlist || playlist.size() != POSITION_ZERO) {
            playlist.clear();
        }
        if (null != list && list.size() != POSITION_ZERO) {
            if (isNetworkConnected(mContext)) {
                initCache(list, isPlay);
            } else {
                ToastUtil.show(getString(R.string.not_net), mContext);
            }

        }
    }

    @Override
    public void onBackFolder(ArrayList list) {
        for (int i = 0; i < list.size(); i++) {
            Data.FolderInfo folder = (Data.FolderInfo) list.get(i);
            if (folder.getMainTitle() != null && folderType != null &&
                    folder.getMainTitle().indexOf(folderType) != -1) {
                IntegrationCore.getIntergrationCore(mContext).getSongList(folder);
            }
        }
    }

    void palyMusicIcon() {
        mImagePause.setSelected(true);
        mHandler.sendEmptyMessageDelayed(HIDE_PLAY_PAUSE, HIDE_PLAY_PAUSE_TIME);
    }

    public void isNetWork() {
        if (!isNetworkConnected(mContext)) {
            ToastUtil.show(getString(R.string.not_net), mContext);
        }
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
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
            public void previous() {
                if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                    controllerPrevious();
                    palyMusicIcon();
                } else {
                    integrationCore.setMusicPrevious();
                }
            }

            @Override
            public void next() {
                if (mMusicListWindow != null && mMusicListWindow.isShowing() && playlist != null && mList != null) {
                    ControllerNext();
                    palyMusicIcon();
                } else {
                    integrationCore.setMusicNext();
                }
            }
        };
        wheelControl.setMusicListener(musicControlListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        musicControlListener = null;
        if (wheelControl != null) {
            wheelControl.setMusicListener(null);
        }
        isOnpause = true;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }

    @Override
    public void onAlbumDownloadSuccess(String key) {
        ArielLog.logMusic(ArielLog.LEVEL_DEBUG, "Music--MusicActivity--onAlbumDownloadSuccess",
                "onAlbumDownloadSuccess");
        Data.Song currentSong = integrationCore.mMusicCMD.getCurrentSong();
        if (currentSong == null || mCurrentSong == null) {
            return;
        }

        if (currentSong.getId() == null) {
            return;
        }

        if (!currentSong.getId().equalsIgnoreCase(mCurrentSong.getId())) {
            return;
        }

        final String songTag = mCurrentSong.getId();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageLoader.display(mCurrentSong.getAlbum().getCoverUri(), pauseMusic, songTag);
            }
        });
    }

    @Override
    public void onAlbumDownloadFailed(String key) {

    }

    @Override
    public void codeState(boolean state) {
        isCanPlay = state;
    }

    @Override
    public void forSpecialLocalCode(String cmd, int ret) {
        if (PLAYSONGLOCALPATHCMD.equals(cmd) && 0 == ret)
            integrationCore.getPlayList();
    }

    @Override
    public void backFolderName(String name) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        ImageLoader.getInstance().removeDownloadCallback(this);
        playlist.clear();
        mList.clear();

//        unregisterReceiver(mReceiver);
    }

}