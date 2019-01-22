package com.qinggan.app.arielapp.WheelControl.Observer;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.qinggan.app.arielapp.WheelControl.Controller.AppSwitchControl;
import com.qinggan.app.arielapp.WheelControl.Controller.MusicControl;
import com.qinggan.app.arielapp.WheelControl.Controller.NaviControl;
import com.qinggan.app.arielapp.WheelControl.Controller.PhoneCallControl;
import com.qinggan.app.arielapp.WheelControl.Controller.RadioControl;
import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.NaviControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.RadioControlListener;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.capability.volume.ArielVolumeManager;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;

import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_IVOKA;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MEDIA_NEXT;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MEDIA_PREVIOUS;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MUTE;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_PHONE;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_SRC;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_UP;

public class WheelControlObImpl implements IWheelControlOb {
    Context mContext;
    MusicControl mMusicControl = null;
    NaviControl mNaviControl = null;
    PhoneCallControl mPhoneCallControl = null;
    RadioControl mRadioControl = null;
    AppSwitchControl mAppSwitchControl = null;
    private IntegrationCore integrationCore;
    /* volume max / min value to set system volume */
    private static final float SYSTEM_MAX_VOLUME = 1.0f;
    private static final float SYSTEM_MIN_VOLUME = 0.0f;
    /* step value with settings */
    private static final float EPOLL_STEP = 0.1f;
    /* store previous system volume */
    private float preVolume = 0.0f;
    private static final int CODE_PLAY_STATUS = 4;
    /* environment controller status */
    private final int VALIDATE_VAL = 1;
    private final int INVALIDATE_VAL = -1;
    private MusicControlListener musicControllerListener;
    private NaviControlListener naviControlListener;
    private PhoneCallControlListener phoneCallControlListener;
    private RadioControlListener radioControlListener;
    private final int MSG_BASE = 0x20;
    private final int MSG_MEDIA_NEXT = MSG_BASE + 1;
    private final int MSG_MEDIA_PREVIOUS = MSG_BASE + 2;
    private final int MSG_PHONE = MSG_BASE + 3;
    private final int MSG_IVOKA = MSG_BASE + 4;
    private final int MSG_SRC = MSG_BASE + 5;
    private final int MSG_MUTE = MSG_BASE + 6;
    private final int MSG_VOLUME_UP = MSG_BASE + 7;
    private final int MSG_VOLUME_DOWN = MSG_BASE + 8;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MEDIA_NEXT:
                    Log.e("PateoFMCMD", "--WheelControlObImpl--MSG_MEDIA_NEXT isShown ： " + mAppSwitchControl.getInstance(mContext).isShown());
                    if (mAppSwitchControl.getInstance(mContext).isShown()) {
                        mAppSwitchControl.updateAppSwitchView(false);
                        break;
                    }
                    doKeycodeUp();
                    break;
                case MSG_MEDIA_PREVIOUS:
                    Log.e("PateoFMCMD", "--WheelControlObImpl--MSG_MEDIA_PREVIOUS isShown ： " + mAppSwitchControl.getInstance(mContext).isShown());
                    if (mAppSwitchControl.getInstance(mContext).isShown()) {
                        mAppSwitchControl.updateAppSwitchView(true);
                        break;
                    }
                    doKeycodeDown();
                    break;
                case MSG_IVOKA:
                    activateIvoka();
                    break;
                case MSG_PHONE:
                    phoneInCallControl();
                    break;
                case MSG_SRC:
                    showSRCFloatWindow();
                    break;
                case MSG_MUTE:
                    if (StageController.getStageController().getIsDuringCall()) {
                        mPhoneCallControl.hangUp();
                    } else {
                        IntegrationCore.getIntergrationCore(mContext).getSystemCMD().setAllMute();
                    }
                    break;
                case MSG_VOLUME_DOWN:
                    Log.e("PateoFMCMD", "--WheelControlObImpl--MSG_VOLUME_DOWN ");
                    Log.e("PateoFMCMD", "--WheelControlObImpl--type : " + AudioPolicyManager.getInstance().getCurrentAudioType());
                    ArielVolumeManager.getInstance().adjustVolume(false, ArielVolumeManager.VEHICLE_WHEEL_TYPE);
                    break;
                case MSG_VOLUME_UP:
                    Log.e("PateoFMCMD", "--WheelControlObImpl--MSG_VOLUME_UP ");
                    Log.e("PateoFMCMD", "--WheelControlObImpl--type : " + AudioPolicyManager.getInstance().getCurrentAudioType());
                    ArielVolumeManager.getInstance().adjustVolume(true, ArielVolumeManager.VEHICLE_WHEEL_TYPE);
                    break;
                default:
                    break;
            }
        }
    };

    public WheelControlObImpl(Context context) {
        mContext = context;
        mAppSwitchControl = AppSwitchControl.getInstance(mContext);
        mMusicControl = MusicControl.getInstance(mContext);
        mNaviControl = NaviControl.getInstance(mContext);
        mPhoneCallControl = PhoneCallControl.getInstance(mContext);
        mRadioControl = RadioControl.getInstance(mContext);
        initWheelControlImpl();
        initVolumeSettings(context);
    }

    private void initWheelControlImpl() {
        /* foreground runtime env */
        mMusicControl.setListener(new MusicControlListener() {
            @Override
            public void previous() {
                integrationCore.setMusicPrevious();
            }

            @Override
            public void next() {
                integrationCore.setMusicNext();
            }
        });

        mPhoneCallControl.setListener(new PhoneCallControlListener() {
            @Override
            public void previous() {

            }

            @Override
            public void next() {

            }

            @Override
            public void hangUp() {
                CallUtils.rejectCall();
            }

            @Override
            public void inCall() {
                CallUtils.answerCall(mContext);
            }
        });

        mNaviControl.setListener(new NaviControlListener() {
            @Override
            public void previous() {
                //TODO: do nothing
            }

            @Override
            public void next() {
                //TODO: do nothing
            }
        });

        mRadioControl.setListener(new RadioControlListener() {
            @Override
            public void previous() {
                Log.e("PateoFMCMD", "--previous");
                IntegrationCore.getIntergrationCore(mContext).mPateoFMCMD.seekToPlay(false);
            }

            @Override
            public void next() {
                Log.e("PateoFMCMD", "--next");
                IntegrationCore.getIntergrationCore(mContext).mPateoFMCMD.seekToPlay(true);
            }
        });
    }

    @Override
    public void handleEvents(int keycode, int keyaction) {
        switch (keycode) {
            case KEYCODE_MEDIA_NEXT:
                mHandler.sendEmptyMessage(MSG_MEDIA_NEXT);
                break;
            case KEYCODE_MEDIA_PREVIOUS:
                mHandler.sendEmptyMessage(MSG_MEDIA_PREVIOUS);
                break;
            case KEYCODE_PHONE:
                mHandler.sendEmptyMessage(MSG_PHONE);
                break;
            case KEYCODE_SRC:
                mHandler.sendEmptyMessage(MSG_SRC);
                break;
            case KEYCODE_MUTE:
                mHandler.sendEmptyMessage(MSG_MUTE);
                break;
            case KEYCODE_VOLUME_UP:
                mHandler.sendEmptyMessage(MSG_VOLUME_UP);
                break;
            case KEYCODE_VOLUME_DOWN:
                mHandler.sendEmptyMessage(MSG_VOLUME_DOWN);
                break;
            case KEYCODE_IVOKA:
                //mHandler.sendEmptyMessage(MSG_IVOKA);
                break;
            default:
                break;
        }
    }

    /**
     * Init system volume setting by core
     *
     * @param context
     */
    private void initVolumeSettings(Context context) {
        integrationCore = IntegrationCore.getIntergrationCore(context);
        preVolume = getVolumeCurrent();
    }

    public float getVolumeCurrent() {
        if (mContext == null) return 0.0f;
        AudioManager mAudioManager = (AudioManager) (mContext.getSystemService(Context.AUDIO_SERVICE));
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return (float) current / max;
    }

    /**
     * Set system core volume
     *
     * @param volume
     */
    public void setTouchVolume(float volume) {
        integrationCore.adjustVolumeF(volume);
    }

    /**
     * show src float window with wheel controller
     */
    private void showSRCFloatWindow() {
        if (StageController.getStageController().getIsDuringCall()) {
            return;
        }
        //show or hide src float window. It should be dismissed after 2 seconds.
        if (!mAppSwitchControl.getInstance(mContext).isShown()) {
            mAppSwitchControl.getInstance(mContext).show();
        }else{//如果已经显示，则切换应用
            mHandler.sendEmptyMessage(MSG_MEDIA_NEXT);
        }
    }

    /**
     * Handle keycode up events. It will return 0 when validate listener has been called,
     * or return -1 with invalidate listener
     *
     * @return
     */
    private int doKeycodeUp() {
        Log.e("PateoFMCMD", "--doKeycodeUp IsDuringCall : " + StageController.getStageController().getIsDuringCall());
        int ret = INVALIDATE_VAL;
        /* in phone call, ignore any listener */
        if (StageController.getStageController().getIsDuringCall()) return INVALIDATE_VAL;
        AudioPolicyManager.AudioType type = AudioPolicyManager.getInstance().getCurrentAudioType();
        Log.e("PateoFMCMD", "--doKeycodeUp type : " + type);
        Log.e("PateoFMCMD", "--doKeycodeUp musicControllerListener : " + musicControllerListener);
        Log.e("PateoFMCMD", "--doKeycodeUp radioControlListener : " + radioControlListener);
        if (musicControllerListener != null) {
            musicControllerListener.previous();
            ret = VALIDATE_VAL;
        } else if (radioControlListener != null) {
            radioControlListener.previous();
            ret = VALIDATE_VAL;
        } else if (phoneCallControlListener != null) {
            phoneCallControlListener.previous();
            ret = VALIDATE_VAL;
        }
        if (ret > 0) {
            return 0;
        }


        if (type == AudioPolicyManager.AudioType.MUSIC) {
            mMusicControl.previous();
        } else if (type == AudioPolicyManager.AudioType.FM) {
            mRadioControl.previous();
        }
        return 0;
    }

    /**
     * Handle keycode down events. It will return 0 when validate listener has been called,
     * or return -1 with invalidate listener
     *
     * @return
     */
    private int doKeycodeDown() {
        int ret = INVALIDATE_VAL;
        Log.e("PateoFMCMD", "--doKeycodeDown IsDuringCall : " + StageController.getStageController().getIsDuringCall());
        /* in phone call, ignore any listener */
        if (StageController.getStageController().getIsDuringCall()) return INVALIDATE_VAL;
        AudioPolicyManager.AudioType type = AudioPolicyManager.getInstance().getCurrentAudioType();
        Log.e("PateoFMCMD", "--doKeycodeDown type : " + type);
        Log.e("PateoFMCMD", "--doKeycodeDown musicControllerListener : " + musicControllerListener);
        Log.e("PateoFMCMD", "--doKeycodeDown radioControlListener : " + radioControlListener);
        if (musicControllerListener != null) {
            musicControllerListener.next();
            ret = VALIDATE_VAL;
        } else if (radioControlListener != null) {
            radioControlListener.next();
            ret = VALIDATE_VAL;
        } else if (phoneCallControlListener != null) {
            phoneCallControlListener.next();
            ret = VALIDATE_VAL;
        }
        if (ret > 0) {
            return 0;
        }
        if (type == AudioPolicyManager.AudioType.MUSIC) {
            mMusicControl.next();
        } else if (type == AudioPolicyManager.AudioType.FM) {
            mRadioControl.next();
        }
        return 0;
    }

    private void phoneInCallControl() {
        if (phoneCallControlListener != null) {
            phoneCallControlListener.inCall();
            return;
        }
        if (StageController.getStageController().getIsDuringCall()) {
            mPhoneCallControl.inCall();
        }
    }

    private void activateIvoka() {
        //VoicePolicyManage.getInstance().record(true);
    }

    @Override
    public void setMusicListener(MusicControlListener musicListener) {
        this.musicControllerListener = musicListener;
    }

    @Override
    public void setNaviListener(NaviControlListener naviListener) {
        this.naviControlListener = naviListener;
    }

    @Override
    public void setRadioListener(RadioControlListener radioListener) {
        this.radioControlListener = radioListener;
    }

    @Override
    public void setPhoneListener(PhoneCallControlListener phoneListener) {
        this.phoneCallControlListener = phoneListener;
    }

    @Override
    public void destroyResource() {
        mAppSwitchControl.dismiss();
    }
}
