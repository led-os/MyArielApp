package com.qinggan.app.arielapp.voiceview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.duer.dcs.framework.internalapi.IErrorListener;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.duerstt.WakeupListener;
import com.qinggan.app.voiceapi.DataTypeConstant;
import com.qinggan.app.voiceapi.SttManager;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMCountEvent;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.nluresult.IASRResultCallback;
import com.qinggan.app.voiceapi.nluresult.IASRStatusCallback;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.app.voiceapi.tts.AbstractTtsImpl;
import com.qinggan.app.voiceapi.tts.TtsHelper;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 语音管理策略
 */
public class VoicePolicyManage {

    private static final String TAG = "VoicePolicyManage";

    private static final int MSG_UPDATE_UI = 100;
    private static final int MSG_UPDATE_CONTENT = 200;
    private static final int MSG_INTERRUPT = 300;
    private static final int MSG_RETRY_TTS = 400;
    private static final int MSG_ERROR_TO_IDLE = 500;
    private static final int interruptDelayTime = 200;
    private static final int retryTtsDelayTime = 2000;
    private ArrayList<Integer> mWrapperCache;

    /**
     * 语音模式   正常,播报,收音
     */
    public enum VoiceMode {
        NORMAL, ERROR, RECORD
    }

    /**
     * 语音状态  收听，分析，空闲
     */
    public enum VoiceState {
        LISTENING, THINKING, IDLE, ERROR, UNKNOW
    }

    private static volatile VoicePolicyManage instance = null;
    private Context mContext;
    private List<VoiceStateChangeListener> listeners = new ArrayList<>();
    private VoiceMode speechMode = VoiceMode.NORMAL;
    private String mContent = null;
    private boolean isTTSSpeeching = false;
    private int mErrorType = 0;

    private List<TtsStateChangeListener> mTtsStateListeners = new ArrayList<>();

    public static VoicePolicyManage getInstance() {
        if (instance == null) {
            synchronized (VoicePolicyManage.class) {
                if (instance == null) {
                    instance = new VoicePolicyManage();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
        initSttListener();
        initTtsListener();
    }

    /**
     * 百度收音状态回调
     */
    private void initSttListener() {
        SttManager.getInstance().setWakeupListener(new WakeupListener() {
            @Override
            public void onWakeup() {
                AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.TTS);
            }
        });
        NluResultManager.getInstance().addASRStatusCallback(new IASRStatusCallback() {
            @Override
            public void onListening() {
                Log.e(TAG, "IASRStatusCallback onListening");
                if (isTTSSpeeching) {
                    TtsHelper.getInstance().stopSpeak();
                    Log.e(TAG, "IASRStatusCallback onListening  isTTSSpeeching");
                }
                speechMode = VoiceMode.RECORD;
                notifyListeners(VoiceState.LISTENING);
                mWrapperCache = null;
            }

            @Override
            public void onThinking() {
                Log.e(TAG, "IASRStatusCallback onThinking");
                notifyListeners(VoiceState.THINKING);
            }

            @Override
            public void onIdle() {
                Log.e(TAG, "IASRStatusCallback onIdle");
                speechMode = VoiceMode.NORMAL;
                notifyListeners(VoiceState.IDLE);
            }

            @Override
            public void onError(IErrorListener.ErrorCode errorCode) {
                Log.e(TAG, "IASRStatusCallback onError = " + errorCode);
                speechMode = VoiceMode.ERROR;
                notifyListeners(VoiceState.ERROR);
                AudioPolicyManager.getInstance().abandonAudioPolicy(AudioPolicyManager.AudioType.TTS);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(speechMode == VoiceMode.ERROR) {
                            speechMode = VoiceMode.NORMAL;
                            notifyListeners(VoiceState.IDLE);
                        }
                    }
                }, MSG_ERROR_TO_IDLE);
            }
        });
        NluResultManager.getInstance().setIASRResultCallback(new IASRResultCallback() {
            private ArrayList<Integer> mDataWrapperTypeList = new ArrayList<>();
            @Override
            public void onShowSpeechContent(String content) {

            }

            @Override
            public void onResult(DcsDataWrapper dataWrapper) {
                mDataWrapperTypeList.add(dataWrapper.getType());
            }

            @Override
            public void onShowBroadcastContent(String content) {

            }

            @Override
            public void onStartSpeech() {

            }

            @Override
            public void onEndSpeech() {

            }

            @Override
            public void onEnd() {
                boolean resume = true;
                for(int type : mDataWrapperTypeList){
                    if(DataTypeConstant.MUSIC_SEARCH_TYPE == type ||
                            DataTypeConstant.LOCAL_RADIO_TYPE == type ||
                            DataTypeConstant.NEWS_PLAY_INFO_TYPE == type){
                        resume = false;
                        break;
                    }
                }
                AudioPolicyManager.getInstance().abandonAudioPolicyForIvoka(AudioPolicyManager.AudioType.TTS, resume);
                mDataWrapperTypeList.clear();
            }

            @Override
            public void onError(int error) {
                Log.d(TAG, "onError = " + error);
            }
        });
    }

    private void initTtsListener() {
        TtsHelper.getInstance().addTtsStatusCallback(new AbstractTtsImpl.TtsStatusCallback() {

            @Override
            public void onStart() {
                Log.e(TAG, "TtsStatusCallback onStart");
                boolean ret = AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.TTS);
                if(ret) {
                    isTTSSpeeching = true;
                    mErrorType = 0;
                    mHandler.sendEmptyMessage(MSG_UPDATE_CONTENT);
                }

                for (TtsStateChangeListener listener : mTtsStateListeners) {
                    listener.onStart();
                }
            }

            @Override
            public void  onDone() {
                Log.e(TAG, "TtsStatusCallback onDone");
                isTTSSpeeching = false;
                mErrorType = 0;
                mContent = null;
                mHandler.sendEmptyMessage(MSG_UPDATE_CONTENT);
                mWrapperCache = NluResultManager.getInstance().getWrapperList();

                for (TtsStateChangeListener listener : mTtsStateListeners) {
                    listener.onDone();
                }

                if (null != mWrapperCache && (mWrapperCache.contains(DataTypeConstant.RESTAURANT_TYPE) ||
                        mWrapperCache.contains(DataTypeConstant.SCENIC_TYPE)) ||
                        //mWrapperCache.contains(DataTypeConstant.TRAFFIC_CONDITION_TYPE) ||
                        //mWrapperCache.contains(DataTypeConstant.ROUTE_SEARCH_TYPE) ||
                        //mWrapperCache.contains(DataTypeConstant.POI_SEARCH_TYPE) ||
                        //mWrapperCache.contains(DataTypeConstant.ETA_TYPE) ||
                        mWrapperCache.contains(DataTypeConstant.VOICE_INPUT_TYPE)) {
                    record(false);
                }

                if(speechMode != VoiceMode.NORMAL){
                    AudioPolicyManager.getInstance().abandonAudioPolicyForIvoka(AudioPolicyManager.AudioType.TTS, false);
                }else {
                    AudioPolicyManager.getInstance().abandonAudioPolicy(AudioPolicyManager.AudioType.TTS);
                }
                Log.e(TAG, "TtsStatusCallback onDone end");
            }

            @Override
            public void onError(int errorType, int errorCode) {
                Log.e(TAG, "TtsStatusCallback onError : " + errorType + " errorCode : " + errorCode);
                mErrorType = errorType;
                isTTSSpeeching = false;
                mContent = null;
                mHandler.sendEmptyMessage(MSG_UPDATE_CONTENT);
                //百度引擎错误重新初始化引擎
                if (errorCode == (-107) || errorCode == (-111)) {
                    mHandler.sendEmptyMessageDelayed(MSG_RETRY_TTS, retryTtsDelayTime);
                }
                AudioPolicyManager.getInstance().abandonAudioPolicy(AudioPolicyManager.AudioType.TTS);

                for (TtsStateChangeListener listener : mTtsStateListeners) {
                    listener.onError();
                }
            }
        });
    }

    /**
     * 添加语音策略改变的回调
     */
    public void addListeners(VoiceStateChangeListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(VoiceStateChangeListener listener) {
        this.listeners.remove(listener);
    }

    public void addTtsStatusListeners(TtsStateChangeListener listener) {
        if (listener != null) {
            this.mTtsStateListeners.add(listener);
        }
    }

    public void removeTtsStatusListener(TtsStateChangeListener listener) {
        mTtsStateListeners.remove(listener);
    }

    private AudioPolicyManager.OnAudioPolicyListener mOnAudioPolicyListener  = new AudioPolicyManager.OnAudioPolicyListener() {
        @Override
        public boolean onPause() {
            return false;
        }

        @Override
        public boolean onResume() {
            return false;
        }

        @Override
        public boolean onStop() {
            return false;
        }
    };

    public VoiceMode getVoiceMode() {
        return speechMode;
    }

    /**
     * @param
     */
    public boolean record(boolean istouch) {
        Log.e(TAG, "record speechMode = " + speechMode);
        if (this.speechMode != VoiceMode.NORMAL && this.speechMode != VoiceMode.ERROR) {
            if (mHandler.hasMessages(MSG_INTERRUPT)) {

            } else {
                mHandler.sendEmptyMessageDelayed(MSG_INTERRUPT, interruptDelayTime);
            }
            for (VoiceStateChangeListener listener : listeners) {
                listener.onUserClick(istouch,false);
            }
            Log.e(TAG, "record return");
            return false;
        }

        boolean ret = AudioPolicyManager.getInstance().requestAudioPolicy(mOnAudioPolicyListener, AudioPolicyManager.AudioType.TTS);
        if(ret) {
            this.speechMode = VoiceMode.RECORD;
            notifyListeners(VoiceState.LISTENING);
            SttManager.getInstance().start();
            UMAnalyse.count(UMCountEvent.ALL_WAKE_UP);
            if(istouch){
                UMAnalyse.count(UMCountEvent.CLICK_WAKE_UP);
            }
            for (VoiceStateChangeListener listener : listeners) {
                listener.onUserClick(istouch, true);
            }
            return true;
        }
        return false;
    }

    public void speak(String content) {
        Log.e(TAG, "speak ");
        TtsHelper.getInstance().stopSpeak();
        isTTSSpeeching = true;
        mContent = content;
        TtsHelper.getInstance().startSpeak(content);
    }

    public void speakStop() {
        Log.e(TAG, "speak ");
        TtsHelper.getInstance().stopSpeak();
        isTTSSpeeching = false;
    }

    public void interrupt() {
        Log.e(TAG, "interrupt ");
        if (speechMode == VoiceMode.RECORD) {
            SttManager.getInstance().interrupt();
            SttManager.getInstance().finish();
            AudioPolicyManager.getInstance().abandonAudioPolicy(AudioPolicyManager.AudioType.TTS);
        }

        TtsHelper.getInstance().stopSpeak();
        isTTSSpeeching = false;
        speechMode = VoiceMode.NORMAL;
        notifyListeners(VoiceState.IDLE);
    }

    private synchronized void notifyListeners(VoiceState voiceState) {
        Message msg = new Message();
        msg.what = MSG_UPDATE_UI;
        msg.obj = voiceState;
        mHandler.sendMessage(msg);
    }

    public void setInCarMode(boolean isInCarMode){
        SttManager.getInstance().setInCarMode(isInCarMode);
    }

    public void setInAppOrNot(boolean isInApp){
        SttManager.getInstance().setInAppOrNot(isInApp);
    }

    public void keepMicOn(boolean micOn){
        SttManager.getInstance().keepMicOn(micOn);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_UI:
                    for (VoiceStateChangeListener listener : listeners) {
                        listener.onStateChange(speechMode, (VoiceState) msg.obj);
                    }
                    break;
                case MSG_UPDATE_CONTENT:
                    for (VoiceStateChangeListener listener : listeners) {
                        listener.onContentChange(mContent);
                    }
                    break;
                case MSG_INTERRUPT:
                    interrupt();
                    break;
                case MSG_RETRY_TTS:
                    TtsHelper.getInstance().release();
                    TtsHelper.getInstance().initTtsHelper(mContext);
                    break;
            }

        }
    };

    /**
     * 语音状态变化回调
     */
    public interface VoiceStateChangeListener {
        void onStateChange(VoiceMode voiceMode, VoiceState voiceState);
        void onContentChange(String content);

        /**
         * 当用户手动点击语音图标唤醒或中断时的回调
         * @param wakeUpOrInterrupt
         */
        void onUserClick(boolean userClick, boolean wakeUpOrInterrupt);
    }

    /**
     *TTS 播报状态回调
     */
    public interface TtsStateChangeListener {
        void onStart();
        void onDone();
        void onError();
    }
}
