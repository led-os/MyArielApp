package com.qinggan.app.arielapp.capability.volume;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.VolumeListener;
import com.qinggan.qinglink.api.md.VolumeManager;

/**
 * <车机音量控制>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-15]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ArielVolumeManager {

    private static final String TAG = ArielVolumeManager.class.getSimpleName();
    private static volatile ArielVolumeManager instance;

    private boolean isVolumeConnect;
    private VolumeManager volumeManager;
    private AudioManager mAudioManager;
    //手机音量按键增减幅
    private static final int MOBILE_KEY_INCREASE = 1;
    //手机音量语音增减幅
    private static final int MOBILE_VOICE_INCREASE = 2;
    //车机音量按键增减幅
    private static final int VEHICLE_KEY_INCREASE = 1;
    //车机音量语音增减幅
    public static int VEHICLE_VOICE_INCREASE = 4;
    //车机最大音量
    private static final int VEHICLE_MAX_VOLUME = 30;
    //车机A2DP默认音量
    private static final int VEHICLE_DEFAULT_VOLUME = 18;
    //手机最大音量
    private int mobileMaxMediaVolume;
    private int mobileMaxPhoneVolume;
    //音量最小值
    private static final int MIN_VOLUME = 0;
    //手机音量最大舒服值百分比
    private static final int MOBILE_MAX_ADJUST_VOLUME = 70;
    //手机音量最小舒服值百分比
    private static final int MOBILE_MIN_ADJUST_VOLUME = 30;

    public static final int VEHICLE_WHEEL_TYPE = 1; //方控调音量
    public static final int MOBILE_NEWS_TYPE = 2; //新闻界面调音量
    public static final int MOBILE_MUSIC_TYPE = 3; //音乐界面调音量
    public static final int MOBILE_FM_TYPE = 4; //电台调音量
    public static final int MOBILE_KEY_TYPE = 5; //手机按键调音量
    public static final int MOBILE_VOICE_TYPE = 6; //语音调音量

    private int vehicleMediaVol; //车机FM媒体音量
    private int vehicleNavVol; //车机导航音量
    private int vehiclePhoneVol; //车机电话音量
    private int vehicleNotifyVol; //车机通知音音量
    private int vehicleA2DPVol; //车机a2dp音音量
    private int mobileMediaVol; //手机媒体音量
    private int mobilePhoneVol; //手机电话音量

    public static ArielVolumeManager getInstance() {
        if (null == instance) {
            synchronized (ArielVolumeManager.class) {
                if (null == instance)
                    instance = new ArielVolumeManager();
            }
        }
        return instance;
    }

    private ArielVolumeManager() {
        volumeManager = VolumeManager.getInstance(ArielApplication.getApp(), new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {
                Log.d(TAG, "OnInitListener onConnectStatusChange:" + b);
            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                Log.d(TAG, "OnConnectListener onConnect:" + b);
                isVolumeConnect = b;
                if (b) {
                    sendGetCurrentA2DPVolume();
                    sendGetCurrentMediaVolume();
                    sendGetCurrentNavigationVolume();
                    sendGetCurrentNotificationVolume();
                    sendGetCurrentPhoneVolume();
                }
            }
        });
        volumeManager.registerListener(new VolumeListener() {
            @Override
            public void onVolumeUp() {

            }

            @Override
            public void onVolumeDown() {

            }

            @Override
            public void onCurrentA2DPVolumeResponse(int i) {
                Log.d(TAG, "onCurrentA2DPVolumeResponse:" + i);
                vehicleA2DPVol = i;
            }

            @Override
            public void onCurrentMediaVolumeResponse(int i) {
                Log.d(TAG, "onCurrentMediaVolumeResponse:" + i);
                vehicleMediaVol = i;
            }

            @Override
            public void onCurrentNavigationVolumeResponse(int i) {
                Log.d(TAG, "onCurrentNavigationVolumeResponse:" + i);
                vehicleNavVol = i;
            }

            @Override
            public void onCurrentPhoneVolumeResponse(int i) {
                Log.d(TAG, "onCurrentPhoneVolumeResponse:" + i);
                vehiclePhoneVol = i;
            }

            @Override
            public void onCurrentNotificationVolumeResponse(int i) {
                Log.d(TAG, "onCurrentNotificationVolumeResponse:" + i);
                vehicleNotifyVol = i;
            }

            @Override
            public void onEqualizerResponse(String s) {

            }
        });

        mAudioManager = (AudioManager) ArielApplication.getApp().getSystemService(Context.AUDIO_SERVICE);
        mobileMaxMediaVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mobileMaxPhoneVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        mobileMediaVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mobilePhoneVol = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

        if (mobileMediaVol / mobileMaxMediaVolume > MOBILE_MAX_ADJUST_VOLUME / 100) {
            mobileMediaVol = mobileMaxMediaVolume * MOBILE_MAX_ADJUST_VOLUME / 100;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mobileMediaVol, 1);
        } else if (mobileMediaVol / mobileMaxMediaVolume < MOBILE_MIN_ADJUST_VOLUME / 100) {
            mobileMediaVol = mobileMaxMediaVolume * MOBILE_MIN_ADJUST_VOLUME / 100;
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mobileMediaVol, 1);
        }

        Log.d(TAG, "ArielVolumeManager mobileMediaVol :" + mobileMediaVol);
        Log.d(TAG, "ArielVolumeManager mobilePhoneVol :" + mobilePhoneVol);
    }

    private void sendGetCurrentA2DPVolume() {
        Log.d(TAG, "sendGetCurrentA2DPVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect)
            volumeManager.sendGetCurrentA2DPVolume();
    }

    private void sendGetCurrentMediaVolume() {
        Log.d(TAG, "sendSetCurrentPhoneVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect)
            volumeManager.sendGetCurrentMediaVolume();
    }

    private void sendGetCurrentNavigationVolume() {
        Log.d(TAG, "sendGetCurrentNavigationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect)
            volumeManager.sendGetCurrentNavigationVolume();
    }

    private void sendGetCurrentNotificationVolume() {
        Log.d(TAG, "sendGetCurrentNotificationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect)
            volumeManager.sendGetCurrentNotificationVolume();
    }

    private void sendGetCurrentPhoneVolume() {
        Log.d(TAG, "sendGetCurrentPhoneVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect)
            volumeManager.sendGetCurrentPhoneVolume();
    }

    /**
     * @param increase true 调大
     */
    private boolean sendSetCurrentA2DPVolume(boolean increase, boolean voice) {
        Log.d(TAG, "sendSetCurrentA2DPVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentA2DPVolume:before:" + vehicleA2DPVol);
            int REAL_INCREASE = voice ? VEHICLE_VOICE_INCREASE : VEHICLE_KEY_INCREASE;
            Log.d(TAG, "sendSetCurrentA2DPVolume:REAL_INCREASE:" + REAL_INCREASE);
            if (increase) {
                if (vehicleA2DPVol >= VEHICLE_MAX_VOLUME || (vehicleA2DPVol + REAL_INCREASE) >= VEHICLE_MAX_VOLUME) {
                    vehicleA2DPVol = VEHICLE_MAX_VOLUME;
                } else {
                    vehicleA2DPVol = vehicleA2DPVol + REAL_INCREASE;
                }
            } else {
                if (vehicleA2DPVol == VEHICLE_DEFAULT_VOLUME) {
                    return isVolumeConnect;
                } else {
                    vehicleA2DPVol = VEHICLE_DEFAULT_VOLUME;
                }
//                if (vehicleA2DPVol <= MIN_VOLUME || (vehicleA2DPVol - REAL_INCREASE) <= MIN_VOLUME) {
//                    vehicleA2DPVol = MIN_VOLUME;
//                } else {
//                    vehicleA2DPVol = vehicleA2DPVol - REAL_INCREASE;
//                }
            }
            Log.d(TAG, "sendSetCurrentA2DPVolume:after:" + vehicleA2DPVol);
            volumeManager.sendSetCurrentA2DPVolume(vehicleA2DPVol);
        }

        return isVolumeConnect;
    }

    private boolean sendSetCurrentA2DPVolume(int volume) {
        Log.d(TAG, "sendSetCurrentA2DPVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentA2DPVolume:before:" + vehicleA2DPVol);

            if (volume >= VEHICLE_MAX_VOLUME) {
                vehicleA2DPVol = VEHICLE_MAX_VOLUME;
            } else if (volume <= MIN_VOLUME) {
                vehicleA2DPVol = MIN_VOLUME;
            } else {
                vehicleA2DPVol = volume;
            }
            Log.d(TAG, "sendSetCurrentA2DPVolume:after:" + vehicleA2DPVol);
            volumeManager.sendSetCurrentA2DPVolume(vehicleA2DPVol);
        }
        return isVolumeConnect;
    }

    /**
     * @param increase true 调大
     */
    private boolean sendSetCurrentMediaVolume(boolean increase, boolean voice) {
        Log.d(TAG, "sendSetCurrentMediaVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentMediaVolume:before:" + vehicleMediaVol);
            int REAL_INCREASE = voice ? VEHICLE_VOICE_INCREASE : VEHICLE_KEY_INCREASE;
            Log.d(TAG, "sendSetCurrentMediaVolume:REAL_INCREASE:" + REAL_INCREASE);
            if (increase) {
                if (vehicleMediaVol >= VEHICLE_MAX_VOLUME || (vehicleMediaVol + REAL_INCREASE) >= VEHICLE_MAX_VOLUME) {
                    vehicleMediaVol = VEHICLE_MAX_VOLUME;
                } else {
                    vehicleMediaVol = vehicleMediaVol + REAL_INCREASE;
                }
            } else {
                if (vehicleMediaVol <= MIN_VOLUME || (vehicleMediaVol - REAL_INCREASE) <= MIN_VOLUME) {
                    vehicleMediaVol = MIN_VOLUME;
                } else {
                    vehicleMediaVol = vehicleMediaVol - REAL_INCREASE;
                }
            }
            Log.d(TAG, "sendSetCurrentMediaVolume:after:" + vehicleMediaVol);
            volumeManager.sendSetCurrentMediaVolume(vehicleMediaVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentMediaVolume(int volume) {
        Log.d(TAG, "sendSetCurrentMediaVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentMediaVolume:before:" + vehicleMediaVol);

            if (volume >= VEHICLE_MAX_VOLUME) {
                vehicleMediaVol = VEHICLE_MAX_VOLUME;
            } else if (volume <= MIN_VOLUME) {
                vehicleMediaVol = MIN_VOLUME;
            } else {
                vehicleMediaVol = volume;
            }
            Log.d(TAG, "sendSetCurrentMediaVolume:after:" + vehicleMediaVol);
            volumeManager.sendSetCurrentMediaVolume(vehicleMediaVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentNavigationVolume(boolean increase, boolean voice) {
        Log.d(TAG, "sendSetCurrentNavigationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentNavigationVolume:before:" + vehicleNavVol);
            int REAL_INCREASE = voice ? VEHICLE_VOICE_INCREASE : VEHICLE_KEY_INCREASE;
            Log.d(TAG, "sendSetCurrentNavigationVolume:REAL_INCREASE:" + REAL_INCREASE);
            if (increase) {
                if (vehicleNavVol >= VEHICLE_MAX_VOLUME || (vehicleNavVol + REAL_INCREASE) >= VEHICLE_MAX_VOLUME) {
                    vehicleNavVol = VEHICLE_MAX_VOLUME;
                } else {
                    vehicleNavVol = vehicleNavVol + REAL_INCREASE;
                }
            } else {
                if (vehicleNavVol <= MIN_VOLUME || (vehicleNavVol - REAL_INCREASE) <= MIN_VOLUME) {
                    vehicleNavVol = MIN_VOLUME;
                } else {
                    vehicleNavVol = vehicleNavVol - REAL_INCREASE;
                }
            }
            Log.d(TAG, "sendSetCurrentNavigationVolume:after:" + vehicleNavVol);
            volumeManager.sendSetCurrentNavigationVolume(vehicleNavVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentNavigationVolume(int volume) {
        Log.d(TAG, "sendSetCurrentNavigationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentNavigationVolume:before:" + vehicleNavVol);
            if (volume >= VEHICLE_MAX_VOLUME) {
                vehicleNavVol = VEHICLE_MAX_VOLUME;
            } else if (volume <= MIN_VOLUME) {
                vehicleNavVol = MIN_VOLUME;
            } else {
                vehicleNavVol = volume;
            }
            Log.d(TAG, "sendSetCurrentNavigationVolume:after:" + vehicleNavVol);
            volumeManager.sendSetCurrentNavigationVolume(vehicleNavVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentNotificationVolume(boolean increase, boolean voice) {
        Log.d(TAG, "sendSetCurrentNotificationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentNotificationVolume:before:" + vehicleNotifyVol);
            int REAL_INCREASE = voice ? VEHICLE_VOICE_INCREASE : VEHICLE_KEY_INCREASE;
            Log.d(TAG, "sendSetCurrentNotificationVolume:REAL_INCREASE:" + REAL_INCREASE);
            if (increase) {
                if (vehicleNotifyVol >= VEHICLE_MAX_VOLUME || (vehicleNotifyVol + REAL_INCREASE) >= VEHICLE_MAX_VOLUME) {
                    vehicleNotifyVol = VEHICLE_MAX_VOLUME;
                } else {
                    vehicleNotifyVol = vehicleNotifyVol + REAL_INCREASE;
                }
            } else {
                if (vehicleNotifyVol <= MIN_VOLUME || (vehicleNotifyVol - REAL_INCREASE) <= MIN_VOLUME) {
                    vehicleNotifyVol = MIN_VOLUME;
                } else {
                    vehicleNotifyVol = vehicleNotifyVol - REAL_INCREASE;
                }
            }
            Log.d(TAG, "sendSetCurrentNotificationVolume:after:" + vehicleNotifyVol);
            volumeManager.sendSetCurrentNotificationVolume(vehicleNotifyVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentNotificationVolume(int volume) {
        Log.d(TAG, "sendSetCurrentNotificationVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentNotificationVolume:before:" + vehicleNotifyVol);
            if (volume >= VEHICLE_MAX_VOLUME) {
                vehicleNotifyVol = VEHICLE_MAX_VOLUME;
            } else if (volume <= MIN_VOLUME) {
                vehicleNotifyVol = MIN_VOLUME;
            } else {
                vehicleNotifyVol = volume;
            }
            Log.d(TAG, "sendSetCurrentNotificationVolume:after:" + vehicleNotifyVol);
            volumeManager.sendSetCurrentNotificationVolume(vehicleNotifyVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentPhoneVolume(boolean increase) {
        Log.d(TAG, "sendSetCurrentPhoneVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentPhoneVolume:before:" + vehiclePhoneVol);
            if (increase) {
                if (vehiclePhoneVol >= VEHICLE_MAX_VOLUME || (vehiclePhoneVol + VEHICLE_KEY_INCREASE) >= VEHICLE_MAX_VOLUME) {
                    vehiclePhoneVol = VEHICLE_MAX_VOLUME;
                } else {
                    vehiclePhoneVol = vehiclePhoneVol + VEHICLE_KEY_INCREASE;
                }
            } else {
                if (vehiclePhoneVol == VEHICLE_DEFAULT_VOLUME) {
                    return isVolumeConnect;
                } else {
                    vehiclePhoneVol = VEHICLE_DEFAULT_VOLUME;
                }
//                if (vehiclePhoneVol <= MIN_VOLUME || (vehiclePhoneVol - VEHICLE_KEY_INCREASE) <= MIN_VOLUME) {
//                    vehiclePhoneVol = MIN_VOLUME;
//                } else {
//                    vehiclePhoneVol = vehiclePhoneVol - VEHICLE_KEY_INCREASE;
//                }
            }
            Log.d(TAG, "sendSetCurrentPhoneVolume:after:" + vehiclePhoneVol);
            volumeManager.sendSetCurrentPhoneVolume(vehiclePhoneVol);
        }
        return isVolumeConnect;
    }

    private boolean sendSetCurrentPhoneVolume(int volume) {
        Log.d(TAG, "sendSetCurrentPhoneVolume:isVolumeConnect:" + isVolumeConnect);
        if (isVolumeConnect) {
            Log.d(TAG, "sendSetCurrentPhoneVolume:before:" + vehiclePhoneVol);

            if (volume >= VEHICLE_MAX_VOLUME) {
                vehiclePhoneVol = VEHICLE_MAX_VOLUME;
            } else if (volume <= MIN_VOLUME) {
                vehiclePhoneVol = MIN_VOLUME;
            } else {
                vehiclePhoneVol = volume;
            }
            Log.d(TAG, "sendSetCurrentPhoneVolume:after:" + vehiclePhoneVol);
            volumeManager.sendSetCurrentPhoneVolume(vehiclePhoneVol);
        }
        return isVolumeConnect;
    }

    private void setMobileMeidaVolume(boolean increase, boolean voice) {
        Log.d(TAG, "setMobileMeidaVolume:before:" + mobileMediaVol);
        int REAL_INCREASE = voice ? MOBILE_VOICE_INCREASE : MOBILE_KEY_INCREASE;
        Log.d(TAG, "setMobileMeidaVolume:REAL_INCREASE:" + REAL_INCREASE);
        if (increase) {
            if (mobileMediaVol >= mobileMaxMediaVolume) {
                mobileMediaVol = mobileMaxMediaVolume;
                sendSetCurrentA2DPVolume(true, voice);
                return;
            } else if (mobileMediaVol + REAL_INCREASE > mobileMaxMediaVolume) {
                mobileMediaVol = mobileMaxMediaVolume;
            } else {
                mobileMediaVol = mobileMediaVol + REAL_INCREASE;
            }

        } else {
            if (mobileMediaVol <= MIN_VOLUME) {
                mobileMediaVol = MIN_VOLUME;
                sendSetCurrentA2DPVolume(false, voice);
                return;
            } else if (mobileMediaVol - REAL_INCREASE < MIN_VOLUME) {
                mobileMediaVol = MIN_VOLUME;
            } else {
                mobileMediaVol = mobileMediaVol - REAL_INCREASE;
            }
        }
        Log.d(TAG, "setMobileMeidaVolume:after:" + mobileMediaVol);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mobileMediaVol, 1);
    }

    private void setMobileMeidaVolume(int volume) {
        Log.d(TAG, "setMobileMeidaVolume volume :" + volume);
        Log.d(TAG, "setMobileMeidaVolume:before:" + mobileMediaVol);
        if (volume >= mobileMaxMediaVolume) {
            mobileMediaVol = mobileMaxMediaVolume;
        } else if (volume <= MIN_VOLUME) {
            mobileMediaVol = MIN_VOLUME;
        } else {
            mobileMediaVol = volume;
        }
        Log.d(TAG, "setMobileMeidaVolume:after:" + mobileMediaVol);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mobileMediaVol, 1);
    }

    private void setMobilePhoneVolume(boolean increase) {
        Log.d(TAG, "setMobilePhoneVolume:before:" + mobilePhoneVol);
        if (increase) {
            if (mobilePhoneVol >= mobileMaxPhoneVolume) {
                mobilePhoneVol = mobileMaxPhoneVolume;
                sendSetCurrentPhoneVolume(true);
                return;
            } else if (mobilePhoneVol + MOBILE_KEY_INCREASE > mobileMaxPhoneVolume) {
                mobilePhoneVol = mobileMaxPhoneVolume;
            } else {
                mobilePhoneVol = mobilePhoneVol + MOBILE_KEY_INCREASE;
            }

        } else {
            if (mobilePhoneVol <= MIN_VOLUME) {
                mobilePhoneVol = MIN_VOLUME;
                sendSetCurrentPhoneVolume(false);
                return;
            } else if (mobilePhoneVol - MOBILE_KEY_INCREASE < MIN_VOLUME) {
                mobilePhoneVol = MIN_VOLUME;
            } else {
                mobilePhoneVol = mobilePhoneVol - MOBILE_KEY_INCREASE;
            }
        }
        Log.d(TAG, "setMobilePhoneVolume:after:" + mobilePhoneVol);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mobilePhoneVol, 1);
    }

    private void setMobilePhoneVolume(int volume) {
        Log.d(TAG, "setMobilePhoneVolume volume :" + volume);
        Log.d(TAG, "setMobilePhoneVolume:before:" + mobilePhoneVol);
        if (volume >= mobileMaxPhoneVolume) {
            mobilePhoneVol = mobileMaxPhoneVolume;
        } else if (volume <= MIN_VOLUME) {
            mobilePhoneVol = MIN_VOLUME;
        } else {
            mobilePhoneVol = volume;
        }
        Log.d(TAG, "setMobilePhoneVolume:after:" + mobilePhoneVol);
        mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, mobilePhoneVol, 1);
    }

    public void adjustVolume(boolean increase, int mediaType) {
        Log.d(TAG, "adjustVolume increase :" + increase + "--mediaType :" + mediaType);
        switch (mediaType) {
            case MOBILE_NEWS_TYPE:
            case MOBILE_MUSIC_TYPE:
                break;
            case MOBILE_FM_TYPE:
                break;
            case MOBILE_VOICE_TYPE:
                if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                    if (isVolumeConnect) {
                        sendSetCurrentMediaVolume(increase,true);
                    }
                } else {
                    setMobileMeidaVolume(increase, true);
                }
                break;
            case VEHICLE_WHEEL_TYPE:
            case MOBILE_KEY_TYPE:
                if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                    if (isVolumeConnect) {
                        sendSetCurrentMediaVolume(increase,false);
                    }
                } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.PHONE) {
                    setMobilePhoneVolume(increase);
                } else {
                    setMobileMeidaVolume(increase, false);
                }
                break;
        }
    }

    public void setVolume(int volume, int mediaType, boolean isMobile) {
        Log.d(TAG, "setVolume volume :" + volume + "--mediaType :" + mediaType + "--isMobile : " + isMobile);
        switch (mediaType) {
            case MOBILE_NEWS_TYPE:
            case MOBILE_MUSIC_TYPE:
                if (isMobile) {
                    setMobileMeidaVolume(volume);
                } else {
                    sendSetCurrentA2DPVolume(volume);
                }
                break;
            case MOBILE_FM_TYPE:
                sendSetCurrentMediaVolume(volume);
                break;
            case MOBILE_VOICE_TYPE:
                if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                    if (isVolumeConnect) {
                        sendSetCurrentMediaVolume(volume);
                    }
                } else {
                    setMobileMeidaVolume(volume);
                }
                break;
            case VEHICLE_WHEEL_TYPE:
            case MOBILE_KEY_TYPE:
                if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.FM) {
                    if (isVolumeConnect) {
                        sendSetCurrentMediaVolume(volume);
                    }
                } else if (AudioPolicyManager.getInstance().getCurrentAudioType() == AudioPolicyManager.AudioType.PHONE) {
                    setMobilePhoneVolume(volume);
                } else {
                    setMobileMeidaVolume(volume);
                }
                break;
        }
    }

    public boolean isVolumeConnect() {
        Log.d(TAG, "isVolumeConnect:" + isVolumeConnect);
        return isVolumeConnect;
    }

    public int getVehicleA2DPVol() {
        return vehicleA2DPVol;
    }

    public int getVehicleMediaVol() {
        return vehicleMediaVol;
    }

    public int getVehicleNavVol() {
        return vehicleNavVol;
    }

    public int getVehiclePhoneVol() {
        return vehiclePhoneVol;
    }

    public int getVehicleNotifyVol() {
        return vehicleNotifyVol;
    }

    public int getMobileMediaVol() {
        mobileMediaVol = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return mobileMediaVol;
    }

    public int getMobilePhoneVol() {
        mobilePhoneVol = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        return mobilePhoneVol;
    }

    public int getMaxVehicleVolume() {
        return VEHICLE_MAX_VOLUME;
    }

    public int getMaxMobileMediaVolume() {
        return mobileMaxMediaVolume;
    }

    public int getMaxMobilePhoneVolume() {
        return mobileMaxPhoneVolume;
    }
}
