package com.qinggan.app.arielapp.minor.phone.listener;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.qinggan.app.arielapp.minor.phone.Constants;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 18-11-8.
 */

public class ArielPhoneStateListener extends PhoneStateListener {

    private static final String TAG = "ArielPhoneStateListener";
    private static final int MSG_CALL_OFFHOOK = 1;
    private static final int MSG_CALL_RINGING = 2;
    private Context mContext;
    private AudioManager audioManager;
    private List<ArielPhoneStateCallback> phoneStateCallbackList = new ArrayList<>();

    public ArielPhoneStateListener(Context context) {
        mContext = context;
        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                CallUtils.logd(TAG, "CALL_STATE_IDLE");
                if (checkPermission()) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                CallUtils.logd(TAG, "CALL_STATE_OFFHOOK");
                Message msg = Message.obtain(mHandler);
                msg.what = MSG_CALL_OFFHOOK;
                msg.obj = true;
                mHandler.sendMessageDelayed(msg, 1 * 1000);
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                CallUtils.logd(TAG, "CALL_STATE_RINGING" + "  incomingNumber:" + incomingNumber);
                //setSpeekModle(true);
                /*if (checkPermission()) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }*/
                CallUtils.logd(TAG, "phone BRAND:" + getPhoneProperty("BRAND"));

                Message msg1 = Message.obtain(mHandler);
                msg1.what = MSG_CALL_RINGING;
                msg1.obj = incomingNumber;
                mHandler.sendMessageDelayed(msg1, 300);
                break;
            default:
                break;

        }
        syncCallState(state);
        super.onCallStateChanged(state, incomingNumber);
    }


    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            CallUtils.logd(TAG, "mHandler handleMessage: " + msg.what);
            switch (msg.what) {
                case MSG_CALL_OFFHOOK:
                    boolean isSpeekModleOn = (boolean) msg.obj;
                    audioManager.setSpeakerphoneOn(isSpeekModleOn);
                    break;
                case MSG_CALL_RINGING:

                    break;
                default:
                    return false;
            }
            return false;
        }
    });

    public void registCallback(ArielPhoneStateCallback callback){
        phoneStateCallbackList.add(callback);
    }

    public void unregistCallback(ArielPhoneStateCallback callback){
        phoneStateCallbackList.remove(callback);
    }

    private void syncCallState(int state) {
        for (ArielPhoneStateCallback callback : phoneStateCallbackList) {
            callback.onPhoneStateChange(state);
        }
    }

    /**
     * whether has Notification Policy Access Permission
     *
     * @return
     */
    private boolean checkPermission() {
        boolean hasPermission = false;
        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted()) {
            CallUtils.logd(TAG, "set ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS");
            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            mContext.startActivity(intent);
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    /**
     * set Microphone Mute
     *
     * @param isOpenMicrophoneMute
     */
    private void openMicrophoneMute(boolean isOpenMicrophoneMute) {
        audioManager.setMicrophoneMute(isOpenMicrophoneMute);
    }

    /**
     * set phone speaker
     *
     * @param isOpenSpeaker
     */
    private void openPhoneSpeaker(boolean isOpenSpeaker) {
        audioManager.setSpeakerphoneOn(isOpenSpeaker);
    }


    /**
     * set Speek Modle
     *
     * @param isOpen
     */
    private void setSpeekModle(boolean isOpen) {
        //audioManager.setMode(AudioManager.ROUTE_SPEAKER);
        int currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        audioManager.setMode(AudioManager.MODE_IN_CALL);

        if (!audioManager.isSpeakerphoneOn() && true == isOpen) {
            CallUtils.logd(TAG, "set SpeakerphoneOn");
            audioManager.setSpeakerphoneOn(true);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL),
                    AudioManager.STREAM_VOICE_CALL);
        } else if (audioManager.isSpeakerphoneOn() && false == isOpen) {
            audioManager.setSpeakerphoneOn(false);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currVolume,
                    AudioManager.STREAM_VOICE_CALL);
        }
    }

    /**
     * get phone factory property
     *
     * @param key
     * @return
     */
    private String getPhoneProperty(String key) {
        Field[] fields = Build.class.getFields();
        String value = "";
        for (Field f : fields) {
            try {
                String name = f.getName();
                if ((key.toUpperCase()).equals(name.toUpperCase())) {
                    value = f.get(key).toString();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}