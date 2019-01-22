package com.qinggan.app.arielapp.voiceview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.duerstt.utils.VoiceDBProcesser;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.nluresult.DcsResultListener;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.VoiceListener;
import com.qinggan.qinglink.api.md.VoiceManager;
import com.umeng.analytics.MobclickAgent;

public class VoiceFloatView extends BaseFloatView implements View.OnClickListener, VoicePolicyManage.VoiceStateChangeListener {

    private static final String TAG = "VoiceFloatView";

    private static final int SHOW_VOICE_CONTENT = 1000;
    private static final int HIDDEN_VOICE_CONTENT = 1001;
    private static final int UPDATE_UI = 1002;
    private static final int UPDATE_VOICE_LAYOUT = 1003;
    private static final int DELAY_LONG_TIME = 250;
    private static final int DELAY_SHORT_TIME = 150;

    private LinePulseView mFloatWave;
    private TextView mFloatText;

    private Handler mUIHandler;
    private static final int DISMISS_MSG = 1004;
    private static final int DISMISS_DELAY = 500;

    private SoundPool mSoundPool;
    private int mSoundId;

    private VoiceDBProcesser.VoiceDBListener mVoiceDBListener = new VoiceDBProcesser.VoiceDBListener() {
        @Override
        public void onGetVoiceDB(float voiceDB) {
            if(null != mFloatWave){
                mFloatWave.setMaxAmplitude(voiceDB/50f);
            }
        }
    };

    private boolean mIsTtsSpeaking = false;
    private VoicePolicyManage.VoiceState mVoiceState = VoicePolicyManage.VoiceState.UNKNOW;

    public VoiceFloatView(Context context) {
        super(context);
        init();
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.voice_float_layout;
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp74);
    }

    @Override
    protected void onViewCreate(View view) {
        mFloatWave = view.findViewById(R.id.voice_float_wave);
        mFloatText = view.findViewById(R.id.voice_float_text);
        mFloatWave.setOnClickListener(this);
    }

    private void init() {
        initUIHandle();
        VoicePolicyManage.getInstance().init(mContext);
        VoicePolicyManage.getInstance().addListeners(this);
        NluResultManager.getInstance().addDcsResultListener(mDcsResultListener);
        initPhoneState(mContext);
        initWakeupSound();
        initTtsStateLister();
    }

    private VoiceManager mVoiceManager;
    private PhoneStateManager mPhoneStateManager;
    private PhoneStateManager.PhoneStateChangeListener mPhoneStateChangeListener = new PhoneStateManager.PhoneStateChangeListener() {
        @Override
        public void onPhoneStateChange(PhoneState phoneState) {
            if(PhoneState.IN_CAR_MODE == phoneState){
                Log.d(TAG,"IN_CAR_MODE");
                VoicePolicyManage.getInstance().setInCarMode(true);
            }else{
                Log.d(TAG,"OUT_CAR_MODE");
                VoicePolicyManage.getInstance().setInCarMode(false);
            }
        }
    };

    private static boolean mIsInCall = false;
    public static void setInCallOrNot(boolean isInCall){
        mIsInCall = isInCall;
    }

    private VoiceListener mVoiceListener = new VoiceListener() {
        @Override
        public void onWakeUp() {
            if(VoicePolicyManage.getInstance().getVoiceMode() == VoicePolicyManage.VoiceMode.NORMAL) {
                Log.d(TAG, "VoiceManager::OnInitListener::onWakeUp");
                if(mIsInCall){
                    Log.d(TAG, "VoiceManager::OnInitListener::onWakeUp-->In Call");
                    return;
                }
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSoundPool != null) {
                            mSoundPool.play(mSoundId, 1.0F, 1.0F, 0, 0, 1.0F);
                        }
                        //show();
                        VoicePolicyManage.getInstance().record(false);
                        UMAnalyse.count(UMCountEvent.MIC_WAKE_UP);
                    }
                });
            }else {
                Log.d(TAG, "VoiceManager::OnInitListener::onWakeUp--->wakeup mode, do not need wakeup");
            }
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "VoiceManager::OnInitListener::onCancel");
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    //dismiss();
                    VoicePolicyManage.getInstance().record(false);
                    mVoiceManager.sendStopRecord();
                }
            });
        }
    };

    private void initPhoneState(Context context){
        Log.d(TAG, "initTransProxy");
        mPhoneStateManager = PhoneStateManager.getInstance(context);
        VoicePolicyManage.getInstance().setInCarMode(mPhoneStateManager.getPhoneState() == PhoneState.IN_CAR_MODE);
        mPhoneStateManager.addPhoneStateChangeListener(mPhoneStateChangeListener);
        mVoiceManager = VoiceManager.getInstance(context, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                Log.d(TAG, "VoiceManager::OnInitListener::onConnectStatusChange");
            }
        }, new OnConnectListener(){

            @Override
            public void onConnect(boolean b) {
                Log.d(TAG, "mVoiceManager.OnConnectListener() = " + b);
                //mPhoneStateManager.setPhoneStateForTest(b ? PhoneState.IN_CAR_MODE : PhoneState.OUT_CAR_MODE);
                mPhoneStateManager.setConnectedToCar(b);
            }
        });

        mVoiceManager.registerListener(mVoiceListener);
    }

    private void initWakeupSound() {
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundId = mSoundPool.load(mContext, R.raw.ding, 1);

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                Log.e(TAG, "playWakeupSound onLoadComplete");
            }
        });
    }

    private void initTtsStateLister(){
        VoicePolicyManage.getInstance().addTtsStatusListeners(new VoicePolicyManage.TtsStateChangeListener() {
            @Override
            public void onStart() {
                Log.d(TAG, "TTS onStart");
                mIsTtsSpeaking = true;
            }

            @Override
            public void onDone() {
                Log.d(TAG, "TTS onDone");
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIsTtsSpeaking = false;
                        if(mVoiceState == VoicePolicyManage.VoiceState.IDLE ||
                                mVoiceState == VoicePolicyManage.VoiceState.ERROR) {
                            dismiss();
                        }
                    }
                });
            }

            @Override
            public void onError() {
                Log.d(TAG, "TTS onError");
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mIsTtsSpeaking = false;
                        if(mVoiceState == VoicePolicyManage.VoiceState.IDLE ||
                                mVoiceState == VoicePolicyManage.VoiceState.ERROR) {
                            dismiss();
                        }
                    }
                });
            }
        });
    }

    private void callbackUpdateVoiceStatus(final VoicePolicyManage.VoiceState voiceState) {
        Log.d(TAG, "callbackUpdateVoiceStatus = " + voiceState);
        mVoiceState = voiceState;
        if(voiceState == VoicePolicyManage.VoiceState.LISTENING){
            mFloatText.setText(R.string.wake_up_default);
            VoiceDBProcesser.getInstance().setVoiceDBListener(mVoiceDBListener);
            VoiceDBProcesser.getInstance().needCalDB(true);
        }else{
            VoiceDBProcesser.getInstance().needCalDB(false);
        }
        if(voiceState == VoicePolicyManage.VoiceState.THINKING ||
                voiceState == VoicePolicyManage.VoiceState.ERROR ||
                voiceState == VoicePolicyManage.VoiceState.IDLE){
            mVoiceManager.sendStopRecord();
            Log.d(TAG, "callbackUpdateVoiceStatus VoicePolicyManage.VoiceState.THINKING");
        }
        if (voiceState == VoicePolicyManage.VoiceState.LISTENING) {
            if (mFloatWave.getPulseViewState() != LinePulseView.LinePulseViewState.LISTENING ||
                    mFloatWave.getPulseViewState() != LinePulseView.LinePulseViewState.DECISION_MAKING) {
                Log.e(TAG, "floatwave toListeneing");
                show();
                mFloatWave.toListening();
            }
        } else if (voiceState == VoicePolicyManage.VoiceState.THINKING) {
            Log.e(TAG, "floatwave THINKING");
            if (mFloatWave.getPulseViewState() != LinePulseView.LinePulseViewState.DECISION_MAKING) {
                mFloatWave.toDecisionMaking();
            }
        } else if (voiceState == VoicePolicyManage.VoiceState.IDLE) {
            Log.e(TAG, "floatwave IDLE");
            if(!mIsTtsSpeaking) {
                Log.e(TAG, "dismiss floatview");
                dismiss();
                updateFloatTextContent(null);
            }
            mFloatWave.toIdle();
        } else if (voiceState == VoicePolicyManage.VoiceState.ERROR) {
            Log.e(TAG, "floatwave ERROR");
            if (mFloatWave.getPulseViewState() != LinePulseView.LinePulseViewState.ERROR) {
                mFloatWave.toError();
            }
        }
    }

    @Override
    public void destroy() {
        NluResultManager.getInstance().removeResultListener(mDcsResultListener);
        VoicePolicyManage.getInstance().removeListener(this);
        mPhoneStateManager.removePhoneStateChangeListener(mPhoneStateChangeListener);
    }

//    @Override
//    public void show() {
//        Log.d(TAG, "show()");
//        mUIHandler.removeMessages(DISMISS_MSG);
//        super.show();
//    }
//
//    @Override
//    public void dismiss(){
//        Log.d(TAG, "dismiss()");
//        mUIHandler.sendEmptyMessageDelayed(DISMISS_MSG, DISMISS_DELAY);
//    }

    @Override
    public void onStateChange(VoicePolicyManage.VoiceMode voiceMode, final VoicePolicyManage.VoiceState voiceState) {
        callbackUpdateVoiceStatus(voiceState);
    }

    @Override
    public void onContentChange(String content) {
        updateFloatTextContent(content);
    }

    @Override
    public void onUserClick(boolean userClick, boolean wakeUpOrInterrupt) {
        if(null == mVoiceManager) return;
        if(wakeUpOrInterrupt){
            mVoiceManager.sendStartRecord();
        }else{
            mVoiceManager.sendStopRecord();
            Log.d(TAG, "onUserClick sendStopRecord()");
        }
    }

    private void handleResultContent(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
//        if(content.contains("拨打")) {
            VoicePolicyManage.getInstance().speak(content);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.voice_float_wave:
                VoicePolicyManage.getInstance().record(true);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private void initUIHandle() {
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_VOICE_CONTENT:
                        String textContent = (String) msg.obj;
                        mFloatText.setText(textContent);
                        show();
                        break;
                    case HIDDEN_VOICE_CONTENT:
                        mFloatText.setText("");
                        dismiss();
                        break;
                    case UPDATE_UI:
                        VoicePolicyManage.VoiceState voiceState = (VoicePolicyManage.VoiceState) msg.obj;
                        callbackUpdateVoiceStatus(voiceState);
                        break;
                    case UPDATE_VOICE_LAYOUT:
                        //boolean isInit = msg.getData().getBoolean(INIT_STATUS_KEY);
                        break;
                    case DISMISS_MSG:
                        VoiceFloatView.super.dismiss();
                        break;
                }
            }
        };
    }

    public void updateFloatTextContent(String text) {
        Message msg = mUIHandler.obtainMessage();
        if (text != null && !text.isEmpty()) {
            msg.what = SHOW_VOICE_CONTENT;
            msg.obj = text;

            if (mUIHandler.hasMessages(HIDDEN_VOICE_CONTENT)) {
                mUIHandler.removeMessages(HIDDEN_VOICE_CONTENT);
                mUIHandler.sendMessage(msg);
            } else if (mUIHandler.hasMessages(SHOW_VOICE_CONTENT)) {
                mUIHandler.sendMessageDelayed(msg, DELAY_SHORT_TIME);
            } else {
                mUIHandler.sendMessage(msg);
            }
            Log.d(TAG, "SHOW_VOICE_CONTENT");
        } else {
            msg.what = HIDDEN_VOICE_CONTENT;
            if (mUIHandler.hasMessages(SHOW_VOICE_CONTENT)) {
                mUIHandler.sendMessageDelayed(msg, DELAY_LONG_TIME);
            } else {
                mUIHandler.sendMessage(msg);
            }
            Log.d(TAG, "HIDDEN_VOICE_CONTENT");
        }
    }

    private DcsResultListener mDcsResultListener = new DcsResultListener() {
        @Override
        public void onResult(final DcsDataWrapper dataWrapper) {

        }

        @Override
        public void onShowSpeechContent(String content) {
            updateFloatTextContent(content);
        }

        @Override
        public void onShowBroadcastContent(String content) {
            handleResultContent(content);
        }
    };
}
