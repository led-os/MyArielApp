package com.qinggan.app.arielapp.minor.music;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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

import com.bumptech.glide.Glide;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PlayListAdpter;
import com.qinggan.app.arielapp.minor.scenario.ClickViewInterface;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.widget.custom.RoundCornorImageView;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.tencent.qqmusic.third.api.contract.Data;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.EMPTYSTRINGVALUE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTARTIST;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTTITLE;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.LOCALLISTURI;
import static com.qinggan.app.arielapp.minor.integration.MusicContacts.UNKNOWSTRINGVALUE;
import static com.tencent.qqmusic.third.api.contract.Events.API_EVENT_PLAY_SONG_CHANGED;
import java.util.List;

/**
 * Created by yang
 * Time on 2018/11/8.
 * Function  音乐界面
 */
public class MusicFragment extends UIControlBaseFragment implements View.OnClickListener,ClickViewInterface,IntegrationCore.MusicListener {
    private static final String TAG = MusicFragment.class.getSimpleName();
    private Context mContext = ArielApplication.getApp();
    private ImageView mImageView;
    private ImageView mNextView;
    private ImageView mSoundView;
    private RelativeLayout volumeLinearLayout;
    private RoundCornorImageView pauseMusic;
    private TextView musicName;
    private LinePulseView wakeupIcon;
    private IntegrationCore integrationCore = null;
    private ArrayList<String> list = new ArrayList<String>(){{
        add(API_EVENT_PLAY_SONG_CHANGED);}};
    private ImitateIphoneSwitch imitateIphoneSwitch;
    boolean isFirst = true;
    private LinearLayout musicContainer;
    private RelativeLayout bottomRl;
    private TextView textViewNmae;
    private ImageView mImagePause;
    private Data.Song currentSong;
    // List
    private RecyclerView recyclerView_broad;
    private PlayListAdpter playListAdpter = new PlayListAdpter(mContext);
    private List<Data.Song> playlist = new ArrayList<>();
    private ArrayList mList = new ArrayList<>();
    private String currentSongId;
    private MySeekBar seekbar;
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
    private static final int HIDE_PLAY_PAUSE_TIME = 1000;

    private static final int HANDLER_DELAY = 500;
    private static final int PLAY_STATUS = 4;

    private static final int VOLUME_UP = 2;
    private static final int VOLUME_DOWN = -2;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        VOLUME_SWIPE_THRESHOLD *= dm.density;
        SWITCH_MUSIC_SWIPE_THRESHOLD *= dm.density;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_music, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        initView(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        seekbar = view.findViewById(R.id.my_seekbar);
        mImagePause =  view.findViewById(R.id.music_status_pause);
        textViewNmae = view.findViewById(R.id.name);
        musicContainer = view.findViewById(R.id.fragment_music);
        bottomRl = view.findViewById(R.id.rl_func);
        wakeupIcon= view.findViewById(R.id.wakeup);
        imitateIphoneSwitch = view.findViewById(R.id.volune_profile);
        musicName = view.findViewById(R.id.music_name);
        mNextView = view.findViewById(R.id.delete_image);
        mImageView = view.findViewById(R.id.list_img);
        volumeLinearLayout =view.findViewById(R.id.volume_linear);
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        mSoundView =view.findViewById(R.id.sound_img);
        pauseMusic = view.findViewById(R.id.music_pause);
        pauseMusic.setRadium(getResources().getDimensionPixelSize(R.dimen.dp20));
        mSoundView.setOnClickListener(this);
        volumeLinearLayout.setOnClickListener(this);
        mImageView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        integrationCore.registerEventListener(list);
        wakeupIcon.setOnClickListener(this);
        textViewNmae.setOnClickListener(this);
        bottomRl.setOnClickListener(this);
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(this);
        addUIControlItems();
        loadImage();
        setMusicListener();
        ArrayList<Data.Song> songList = integrationCore.getPlayListCache();
        initBack(songList);
        if (integrationCore.getMusicStatus() == CODE_PLAY_STATUS){
            mImagePause.setSelected(true);
        }
        if(integrationCore != null && integrationCore.mMusicCMD != null){
            currentSong = integrationCore.mMusicCMD.getCurrentSong();
            if(currentSong != null) {
                musicName.setText(!TextUtils.isEmpty(currentSong.getTitle())?
                        currentSong.getTitle():EMPTYSTRINGVALUE);
                textViewNmae.setText(currentSong.getSinger()!= null?currentSong.getSinger().getTitle():
                        EMPTYSTRINGVALUE);
            }
        }else{
            musicName.setText(UNKNOWSTRINGVALUE);
            textViewNmae.setText(UNKNOWSTRINGVALUE);
        }
        imitateIphoneSwitch.registerCallback(new ImitateIphoneSwitch.ValueChangeCallback() {
            @Override
            public void onValueChanged(double value) {
//                IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(value);
            }
        });
        mHandler.sendEmptyMessage(CODE_INITVOLUME);
        mHandler.sendEmptyMessage(CODE_SEEKBAR);
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
                        break;
                    case MotionEvent.ACTION_MOVE: {
                        float endX = event.getX();
                        float endY = event.getY();
                        float distanceX = startX - endX;
                        float distanceY = startY - endY;
                        float absX = Math.abs(distanceX);
                        float absY = Math.abs(distanceY);
                        int count = (int)absY/VOLUME_SWIPE_THRESHOLD;
                        if (absY > absX && thresholdCount != count) {
                            if (endY - lastY < 0) {
                                setTouchVolume(VOLUME_UP);
                            }else{
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
                                integrationCore.setMusicPrevious();
                                isSlide = false;
                            } else if (distanceX < -SWITCH_MUSIC_SWIPE_THRESHOLD) {
                                integrationCore.setMusicNext();
                                isSlide = false;
                            }
                        }
                        if((!hasSlideVertical) && absX < VOLUME_SWIPE_THRESHOLD && absY < VOLUME_SWIPE_THRESHOLD){
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
        mHandler.sendEmptyMessage(CODE_SEEKBAR);
    }

    private void loadImage() {
        mHandler.sendEmptyMessage(CODE_IMAGELOAD);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CODE_VOLUME:
                    volumeLinearLayout.setVisibility(View.GONE);
                    break;
                case CODE_LIST:
                    playListAdpter.setList(playlist);
                    playListAdpter.notifyDataSetChanged();
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
                    if (currentposition < totalduration){
                        mHandler.sendEmptyMessageDelayed(CODE_SEEKBAR, HANDLER_DELAY);
                    }else {
                        seekbar.setProgress(CODE_VOLUME);
                    }
                    break;
                case CODE_SONGNAME:
                    isNetWork();
                    if(integrationCore != null && integrationCore.mMusicCMD != null){
                        currentSong = integrationCore.mMusicCMD.getCurrentSong();
                        if(currentSong != null) {
                            musicName.setText(!TextUtils.isEmpty(currentSong.getTitle())?
                                    currentSong.getTitle():EMPTYSTRINGVALUE);
                            textViewNmae.setText(currentSong.getSinger()!= null?currentSong.getSinger().getTitle():
                                    EMPTYSTRINGVALUE);
                        }
                    }
                    break;
                case CODE_INITVOLUME:
                    imitateIphoneSwitch.setValues(getVolumeCurrent(),getViewHeight());
                    break;
                case CODE_IMAGELOAD:
                    isNetWork();
                    if(integrationCore != null && integrationCore.mMusicCMD != null){
                        if(integrationCore.mMusicCMD.getCurrentSong() != null) {
                            if (integrationCore.mMusicCMD.getCurrentSong().getAlbum() != null){
                                if (!TextUtils.isEmpty(integrationCore.mMusicCMD.getCurrentSong().getAlbum().getCoverUri())) {
                                    Glide.with(mContext)
                                            .load(integrationCore.mMusicCMD.getCurrentSong().getAlbum().getCoverUri())
                                            .into(pauseMusic);
                                }
                            }
                        }
                    }
                    break;
                case HIDE_PLAY_PAUSE:
                    mImagePause.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    public void setTouchVolume(float volume) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        if (isFirst){
            lp.screenBrightness =  getVolumeCurrent() +volume / 255.0f;
            isFirst = false;
        }else {
            lp.screenBrightness =  lp.screenBrightness+volume / 255.0f;
        }
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0) {
            lp.screenBrightness = 0;
        }
        imitateIphoneSwitch.setValues(lp.screenBrightness,getViewHeight());
        IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(lp.screenBrightness);
    }

    public float getVolumeCurrent(){
        AudioManager mAudioManager = (AudioManager) (getActivity().getSystemService(Context.AUDIO_SERVICE));
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        return (float)current/max;
    }

    public int getViewHeight(){
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.music_pause:
                mHandler.removeMessages(HIDE_PLAY_PAUSE);
                isNetWork();
                if (integrationCore.getMusicStatus() == PLAY_STATUS){
                    integrationCore.setMusicPause();
                    mImagePause.setSelected(false);
                }else{
                    integrationCore.playMusic();
                    mImagePause.setSelected(true);
                }
                mImagePause.setVisibility(View.VISIBLE);
                if(integrationCore.getMusicStatus() != PLAY_STATUS) {
                    mHandler.sendEmptyMessageDelayed(HIDE_PLAY_PAUSE, HIDE_PLAY_PAUSE_TIME);
                }
                break;
            case R.id.sound_img:
                musicPop();
                break;
            case R.id.volume_linear:
//               LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.MUSICSELECT,R.id.main_content_view);
                break;
            case R.id.delete_image:
                integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                getFragmentManager().popBackStack();
                break;
            case R.id.list_img:
                showFolder();
//               integrationCore.getListAlias();
//               LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.MUSICLIST,R.id.main_content_view);
                break;
            case R.id.music_name:
//               integrationCore.searchMusic("周杰伦");
//               LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.SEARCHMUSIC,R.id.main_content_view);
                break;
            case R.id.wakeup:
                VoicePolicyManage.getInstance().record(true);
//               LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.VOLUME,R.id.main_content_view);
                break;
            case R.id.name:
                //LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.VOLUME,R.id.main_content_view);
                break;
        }
    }

    private void addUIControlItems() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem musicMenuElement = new UIControlElementItem();
        musicMenuElement.addWord(getString(R.string.music_open_menu1));
        musicMenuElement.addWord(getString(R.string.music_open_menu2));
        musicMenuElement.addWord(getString(R.string.music_open_menu3));
        musicMenuElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_OPEN_MENU);
        mUIControlElements.add(musicMenuElement);

        UIControlElementItem musicVolumeElement = new UIControlElementItem();
        musicVolumeElement.addWord(getString(R.string.music_open_volume1));
        musicVolumeElement.addWord(getString(R.string.music_open_volume2));
        musicVolumeElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.MUSIC_OPEN_VOLUME);
        mUIControlElements.add(musicVolumeElement);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onSelectItemPosition(int position) {
    }

    @Override
    public void onSelectOtherOC(String action) {
        if (ConstantNavUc.MUSIC_OPEN_MENU.equals(action)) {
            integrationCore.getListAlias();
            LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.MUSICLIST,R.id.main_content_view);
        } else if (ConstantNavUc.MUSIC_OPEN_VOLUME.equals(action)) {
            LocalFragmentManager.getInstance().showSubFragment(getFragmentManager(),LocalFragmentManager.FragType.VOLUME,R.id.main_content_view);
        }
    }

    private PopupWindow mMusicListWindow;
    private void musicPop() {
        if(null == mMusicListWindow) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.music_pop, null);
            mMusicListWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            mMusicListWindow.setFocusable(true);
            mMusicListWindow.setBackgroundDrawable(new BitmapDrawable(null, ""));
            mMusicListWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            RelativeLayout relativeLayout = view.findViewById(R.id.closd_music);
            recyclerView_broad = view.findViewById(R.id.recycle_pop);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView_broad.setLayoutManager(linearLayoutManager);
            recyclerView_broad.setAdapter(playListAdpter);
            playListAdpter.setmClickViewInterface(this);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mMusicListWindow.isShowing()){
                        mMusicListWindow.dismiss();
                    }
                }
            });
        }
        mMusicListWindow.showAtLocation(wakeupIcon, Gravity.BOTTOM, 0, 0);
        showPopSelectItem();
    }

    private void initData(ArrayList list) {
        if (list.size() != 0 && null != list){
            Data.Song song;
            for(int i = 0;i<list.size();i++){
                song = (Data.Song) list.get(i);
                playlist.add(song);
                mList.add(song.getId());
            }
        }
        mHandler.sendEmptyMessage(CODE_LIST);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusReceive(String key) {
        if (key.equals("onRestart")){
            showPopSelectItem();
        }
    }

    @Override
    public void songChange() {
        isNetWork();
        loadImage();
        mHandler.sendEmptyMessage(CODE_SONGNAME);
    }

    private void initCache(ArrayList list) {
        initData(list);
        initPosition(list);
    }

    private void initPosition(ArrayList list){
        integrationCore.playSongIdAtIndex(mList,0);
        mHandler.sendEmptyMessage(CODE_SONGNAME);
        playListAdpter.selectPosition(0,true);
        playListAdpter.notifyDataSetChanged();
    }

    public void showPopSelectItem(){
        if (mList.size() != 0 && null != mList){
            currentSongId = integrationCore.getCurrentSong();
            for(int i = 0;i<mList.size();i++){
                if (!TextUtils.isEmpty(currentSongId)) {
                    if (currentSongId.equals(mList.get(i))){
                        isNetWork();
                        playListAdpter.selectPosition(i,true);
                        playListAdpter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
    @Override
    public void OnClickPositionListener(View view, int... position) {
        switch (view.getId()){
            case R.id.paly_item:
                isNetWork();
                if (mList.size() != 0 && null != mList){
                    integrationCore.playSongIdAtIndex(mList,position[0]);
                }
                if (position[1] == 1){
                    playListAdpter.selectPosition(position[0],true);
                    playListAdpter.notifyDataSetChanged();
                }
                mHandler.sendEmptyMessage(CODE_SONGNAME);
                break;
        }
    }

    @Override
    public void OnClickContentListener(View view, String... content) {

    }

    private void showFolder() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.folder_pop, null);
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setFocusable(true);
        window.setBackgroundDrawable(new BitmapDrawable(null, ""));
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        window.showAtLocation(wakeupIcon, Gravity.BOTTOM, 0, 0);
        RelativeLayout relativeLayout = view.findViewById(R.id.closd_music);
        LinearLayout music_local = view.findViewById(R.id.music_local);
        LinearLayout play_history = view.findViewById(R.id.play_history);
        LinearLayout music_like = view.findViewById(R.id.music_like);
        LinearLayout music_win = view.findViewById(R.id.music_win);
        music_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {  //本地
                setMusicListener();
                try{
                    ArrayList<String> mTempList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTURI);
                    integrationCore.playSongLocalPath(mTempList);
//                    ArrayList<String> mTitleList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTTITLE);
//                    ArrayList<String> mArtistList = integrationCore.getSystemCMD().getLocalSongList(LOCALLISTARTIST);
                    integrationCore.getPlayList();
                }catch (Exception e){

                }
                window.dismiss();
            }
        });
        play_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //新歌榜
                setMusicListener();
                try{
                    getFolderList(getString(R.string.new_music));
                    window.dismiss();
                }catch (Exception e){

                }
            }
        });
        music_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //我喜欢
                setMusicListener();
                try{
                    IntegrationCore.getIntergrationCore(mContext).getFavList();
                    window.dismiss();
                }catch (Exception e){

                }
            }
        });
        music_win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//流行指数榜
                setMusicListener();
                try{
                    getFolderList(getString(R.string.num_music));
                    window.dismiss();
                }catch (Exception e){

                }
            }
        });
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(window.isShowing()){
                    window.dismiss();
                }
            }
        });
    }

    public void setMusicListener(){
        IntegrationCore.getIntergrationCore(mContext).setMusicListener(MusicFragment.this);
        isNetWork();
    }

    public void getFolderList(String title){
        Data.FolderInfo mFolder = integrationCore.getFolderByTitle(title);
        if (null != mFolder){
            integrationCore.getSongList(mFolder);
        }
    }

    @Override
    public void onBack(ArrayList list,boolean state) {
        initBack(list);
    }

    public void initBack(ArrayList list){
        if (null != mList || mList.size() != 0){
            mList.clear();
        }
        if (null != playlist || playlist.size() != 0){
            playlist.clear();
        }
        if (null != list && list.size() != 0){
            isNetWork();
            initCache(list);
        }
    }


    @Override
    public void onBackFolder(ArrayList list) {
        for(int i = 0;i<list.size();i++){
            Data.FolderInfo folder = (Data.FolderInfo)list.get(i);
            IntegrationCore.getIntergrationCore(mContext).getSongList(folder);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
    }

    public void isNetWork(){
        if (!isNetworkConnected(mContext)){
            ToastUtil.show(getString(R.string.not_net),mContext);
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

    @Override
    public void codeState(boolean state) {

    }

    @Override
    public void forSpecialLocalCode(String cmd, int ret) {

    }

    @Override
    public void backFolderName(String name) {

    }
}
