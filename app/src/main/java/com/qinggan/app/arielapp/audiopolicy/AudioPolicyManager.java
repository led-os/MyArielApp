package com.qinggan.app.arielapp.audiopolicy;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhongquansun on 2018/12/10.
 */
public class AudioPolicyManager {

    private static final String TAG = AudioPolicyManager.class.getSimpleName();
    private static AudioPolicyManager sAudioPolicyManager;
    private HashMap<AudioType, OnAudioPolicyListener> mOnAudioPolicyListener = new HashMap<>();
    private AudioType mCurAudioType = AudioType.NO_MEDIA;
    private AudioType mCurAudioTypeMedia = AudioType.NO_MEDIA;
    private AudioType mLastAudioType = AudioType.NO_MEDIA;
    private AudioType mPendingAudioType = AudioType.NO_MEDIA;
    private ArrayList<AudioType> mNeedResumeList = new ArrayList<>();

    private AudioPolicyManager() {

    }

    public static AudioPolicyManager getInstance() {
        if (null == sAudioPolicyManager) {
            sAudioPolicyManager = new AudioPolicyManager();
        }
        return sAudioPolicyManager;
    }

    public synchronized boolean requestAudioPolicy(AudioPolicyManager.OnAudioPolicyListener listener, AudioType audioType) {
        Log.d(TAG, "requestAudioPolicy = " + "audioType = " + audioType);
        Log.d(TAG, "mCurAudioType = " + mCurAudioType);
        if (audioType == AudioType.NO_MEDIA) {
            return false;
        }
        if(audioType == AudioType.TTS || audioType == AudioType.PHONE){
            Log.d(TAG, "mNeedResumeList.add = " + audioType);
            mNeedResumeList.add(audioType);
        }
        if (audioType == mCurAudioType) {
            return true;
        }
        mPendingAudioType = AudioType.NO_MEDIA;
        //当前是电话，除TTS外都pending
        if (mCurAudioType == AudioType.PHONE) {
            if (audioType != AudioType.TTS) {
                mPendingAudioType = audioType;
                mLastAudioType = AudioType.NO_MEDIA;
                Log.d(TAG, "mPendingAudioType = " + mPendingAudioType);
            }
            return false;
        //当前是TTS
        } else if (mCurAudioType == AudioType.TTS) {
            if(audioType != AudioType.PHONE) {
                mPendingAudioType = audioType;
                Log.d(TAG, "mPendingAudioType = " + mPendingAudioType);
            }
        } else {
            //当前非电话非tts时，tts电话申请时暂停媒体
            if (audioType == AudioType.TTS || audioType == AudioType.PHONE) {
                if(null != mOnAudioPolicyListener.get(mCurAudioType)) {
                    mOnAudioPolicyListener.get(mCurAudioType).onPause();
                    Log.d(TAG, "pause audio = " + mCurAudioType);
                    mLastAudioType = mCurAudioType;
                }
            //其它都停止媒体
            } else {
                if(null != mOnAudioPolicyListener.get(mCurAudioType)) {
                    mOnAudioPolicyListener.get(mCurAudioType).onStop();
                    Log.d(TAG, "stop audio mCurAudioType = " + mCurAudioType);
                    mLastAudioType = AudioType.NO_MEDIA;
                }
                if(null != mOnAudioPolicyListener.get(mCurAudioTypeMedia)) {
                    mOnAudioPolicyListener.get(mCurAudioTypeMedia).onStop();
                    Log.d(TAG, "stop audio mCurAudioTypeMedia= " + mCurAudioTypeMedia);
                    mLastAudioType = AudioType.NO_MEDIA;
                }
            }
            mCurAudioType = audioType;
            Log.d(TAG, "mCurAudioType = " + mCurAudioType);
            if(audioType != AudioType.TTS && audioType != AudioType.PHONE){
                mCurAudioTypeMedia = audioType;
                Log.d(TAG, "mCurAudioTypeMedia = " + mCurAudioTypeMedia);
            }
        }
        registerAudioPolicyListener(listener, audioType);
        return true;
    }

    public synchronized void abandonAudioPolicy(AudioType audioType) {
        abandonAudioPolicyForIvoka(audioType, true);
    }

    public void abandonAudioPolicyForIvoka(AudioType audioType, boolean resume) {
        Log.d(TAG, "abandonAudioPolicy = " + "audioType = " + audioType + "mNeedResumeList.size() = " +
                mNeedResumeList.size() + ", mCurAudioType" + mCurAudioType);
        if ((audioType == AudioType.TTS || audioType == AudioType.PHONE)) {
            if(mNeedResumeList.size() == 1 && resume) {
                Log.d(TAG, "mLastAudioType = " + mLastAudioType);
                Log.d(TAG, "mPendingAudioType = " + mPendingAudioType);
                if (null != mOnAudioPolicyListener.get(mPendingAudioType)) {
                    mOnAudioPolicyListener.get(mPendingAudioType).onResume();
                    Log.d(TAG, "resume mPendingAudioType audio = " + mPendingAudioType);
                    mCurAudioType = mPendingAudioType;
                    mCurAudioTypeMedia = mPendingAudioType;
                    mPendingAudioType = mLastAudioType = AudioType.NO_MEDIA;
                }else if (null != mOnAudioPolicyListener.get(mLastAudioType)) {
                    mOnAudioPolicyListener.get(mLastAudioType).onResume();
                    Log.d(TAG, "resume mLastAudioType audio = " + mLastAudioType);
                    mCurAudioType = mLastAudioType;
                    mCurAudioTypeMedia = mLastAudioType;
                    mLastAudioType = AudioType.NO_MEDIA;
                }else{
                    mCurAudioType = AudioType.NO_MEDIA;
                    mCurAudioTypeMedia = AudioType.NO_MEDIA;
                    mLastAudioType = AudioType.NO_MEDIA;
                }
            }else{
                if ((mCurAudioType == AudioType.TTS || mCurAudioType == AudioType.PHONE)) {
                    mCurAudioType = AudioType.NO_MEDIA;
                }
            }

            Log.d(TAG, "mCurAudioType = " + mCurAudioType);
            if(mNeedResumeList.contains(audioType)){
                Log.d(TAG, "mNeedResumeList.remove = " + audioType);
                mNeedResumeList.remove(audioType);
            }
            unregisterAudioPolicyListener(audioType);
        } else {
            //mCurAudioType = AudioType.NO_MEDIA;
            //mCurAudioTypeMedia = AudioType.NO_MEDIA;
            //mLastAudioType = AudioType.NO_MEDIA;
        }
    }

    public synchronized AudioType getCurrentAudioType(){
        Log.e("AudioPolicyManager","--mCurAudioTypeMedia : " + mCurAudioTypeMedia);
        return mCurAudioTypeMedia;
    }

    private void registerAudioPolicyListener(AudioPolicyManager.OnAudioPolicyListener listener, AudioType audioType) {
        mOnAudioPolicyListener.put(audioType, listener);
    }

    private void unregisterAudioPolicyListener(AudioType audioType) {
        mOnAudioPolicyListener.remove(audioType);
    }

    public interface OnAudioPolicyListener {
        /**
         * 应用收到此回调，需暂停播放，失去音频焦点
         *
         * @return
         */
        boolean onPause();

        /**
         * 应用收到此回调，需继续播放，获得音频焦点
         *
         * @return
         */
        boolean onResume();

        /**
         * 应用收到此回调，需停止/暂停播放，失去音频焦点
         *
         * @return
         */
        boolean onStop();
    }

    public enum AudioType {
        MUSIC,
        FM,
        NEWS,
        TTS,
        PHONE,
        NO_MEDIA
    }
}
