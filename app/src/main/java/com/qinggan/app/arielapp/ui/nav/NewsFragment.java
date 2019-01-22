package com.qinggan.app.arielapp.ui.nav;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.iview.IMediaView;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.music.ImitateIphoneSwitch;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.media.QueryMediaSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.bean.media.MusicBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;

public class NewsFragment extends UIControlBaseFragment implements IMediaView, View.OnClickListener,
        WakeupControlMgr.WakeupControlListener{
    private static final String TAG = "MediaPlayFragment";
    IFragmentStatusListener mFragmentStatusListener;

    private MusicBean mNewsBean;
    private LinearLayout mainLayout;
    private View mCloseBtn;
    private TextView mNewsLayoutTitle;
    private LinearLayout mNewsVoice;
    private ImageView mNewsProgramBg;
    private TextView mNewsProgramTitle;
    private ImageView mNewsPlayStatus;
    private RelativeLayout volumeLinearLayout;
    private ImitateIphoneSwitch imitateIphoneSwitch;
    private boolean isNewsPlaying = false;
    boolean isFirst = true;

    private static final int INIT_VOLUME_VIEW = 1000;
    private static final int HIDDEN_VOLUME_VIEW = 1001;
    private Handler mUIHandler;

    private int VOLUME_SWIPE_THRESHOLD = 4;
    private int SWITCH_RADIO_SWIPE_THRESHOLD = 10;
    private static final int VOLUME_UP = 2;
    private static final int VOLUME_DOWN = -2;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryMediaSession) session).registerOnShowListener(this);
        initUIHandle();
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.voice_news_frag, container, false);

        mCloseBtn = root.findViewById(R.id.voice_news_close);
        mNewsLayoutTitle = root.findViewById(R.id.voice_news_title);
        mNewsVoice = root.findViewById(R.id.voice_news_voice);
        mNewsProgramBg = root.findViewById(R.id.voice_news_image);
        mNewsProgramTitle = root.findViewById(R.id.voice_news_name);
        mNewsPlayStatus = root.findViewById(R.id.voice_news_play);
        mainLayout = root.findViewById(R.id.ll_main);

        volumeLinearLayout = root.findViewById(R.id.volume_linear);
        imitateIphoneSwitch = root.findViewById(R.id.volune_profile);

        if (mFragmentStatusListener != null) {
            mFragmentStatusListener.onLoaded();
        }

        mainLayout.setOnTouchListener(touchListener);
        mCloseBtn.setOnClickListener(this);
        mNewsVoice.setOnClickListener(this);
        mNewsPlayStatus.setOnClickListener(this);
        mNewsLayoutTitle.setText(R.string.voice_news_title);

        initVoiceWords();

        return root;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        VOLUME_SWIPE_THRESHOLD *= dm.density;
        SWITCH_RADIO_SWIPE_THRESHOLD *= dm.density;

        mUIHandler.sendEmptyMessage(INIT_VOLUME_VIEW);
    }

    private void initVoiceWords(){
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem nextNews = new UIControlElementItem();
        nextNews.addWord(getString(R.string.voice_news_next));
        nextNews.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NEXT_NEWS_UI_CONTROL_ITEM);
        mUIControlElements.add(nextNews);

        UIControlElementItem preNews = new UIControlElementItem();
        preNews.addWord(getString(R.string.voice_news_pre));
        preNews.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.PRE_NEWS_UI_CONTROL_ITEM);
        mUIControlElements.add(preNews);

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }

    private void addWakeupElements() {
        WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.NEWS_NAME_SPACE,
                0, 0, this);
    }


    private void updatePlayStatus() {
        if (IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.getNewsStatus() == 1) {
            isNewsPlaying = true;
        } else {
            isNewsPlaying = false;
        }
        mNewsPlayStatus.setSelected(isNewsPlaying);
    }

    @Override
    public void onShowMedia(MusicBean bean) {
        mNewsBean = bean;
        if (!TextUtils.isEmpty(mNewsBean.getSong())) {
            mNewsProgramTitle.setText(mNewsBean.getSong());
        }

        if (!TextUtils.isEmpty(mNewsBean.getArt())) {
            Glide.with(getActivity().getApplicationContext()).load(mNewsBean.getArt()).
                    placeholder(R.drawable.news_img).into(mNewsProgramBg);
        }

    }

    @SuppressLint("HandlerLeak")
    private void initUIHandle() {
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case INIT_VOLUME_VIEW:
                        imitateIphoneSwitch.setValues(getVolumeCurrent(),getViewHeight());
                        break;
                    case HIDDEN_VOLUME_VIEW:
                        volumeLinearLayout.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voice_news_close:
                getActivity().onBackPressed();
                break;
            case R.id.voice_news_voice:
                VoicePolicyManage.getInstance().record(true);
                break;
            case R.id.voice_news_play:
                if (isNewsPlaying) {
                    isNewsPlaying = false;
                    IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsPause();
                } else {
                    isNewsPlaying = true;
                    IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsPlay();
                }
                mNewsPlayStatus.setSelected(isNewsPlaying);
                break;
        }
    }

    @Override
    public void onItemSelected(String type, String key) {
        Log.d(TAG,"NewsFragment onItemSelected type ： " + type + "--key : " + key);

        if (!WakeupControlMgr.NEWS_NAME_SPACE.equals(type)) {
            return;
        }

        if (WakeupControlMgr.NEWS_NEXT_PROGRAM.equals(key)) {
            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsNext();
        } else if (WakeupControlMgr.NEWS_LAST_PROGRAM.equals(key)) {
            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsPrev();
        } else if (WakeupControlMgr.NEWS_BACK_TO.equals(key)) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePlayStatus();
        addWakeupElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.NEWS_NAME_SPACE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG,"onSelectOtherOC action : " + action);
        if (ConstantNavUc.NEXT_NEWS_UI_CONTROL_ITEM.equals(action)) {
            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsNext();
        } else if (ConstantNavUc.PRE_NEWS_UI_CONTROL_ITEM.equals(action)) {
            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsPrev();
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private float startY = 0;//手指按下时的Y坐标
        private float startX = 0;//手指按下时的X坐标
        private int thresholdCount = 0;
        private float lastY = 0;
        private boolean isSlide = false;
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSlide = true;
                    startX = event.getX();
                    lastY = startY = event.getY();
                    thresholdCount = 0;
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
                        if (distanceX > SWITCH_RADIO_SWIPE_THRESHOLD) {
                            //qianyige
                            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsPrev();
                            isSlide = false;
                        } else if (distanceX < -SWITCH_RADIO_SWIPE_THRESHOLD) {
                            //houyige
                            IntegrationCore.getIntergrationCore(getActivity()).mPateoNewsCMD.setNewsNext();
                            isSlide = false;
                        }
                    }

                    mUIHandler.sendEmptyMessageDelayed(HIDDEN_VOLUME_VIEW, 500);
                }
                break;
            }
            return true;
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
        IntegrationCore.getIntergrationCore(getContext()).adjustVolumeF(lp.screenBrightness);
    }

    private float getVolumeCurrent(){
        AudioManager mAudioManager = (AudioManager) (getActivity().getSystemService(Context.AUDIO_SERVICE));
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        return (float)current/max;
    }

    private int getViewHeight(){
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

}
