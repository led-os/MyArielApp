package com.qinggan.app.arielapp.WheelControl.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.BKMusicActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.audiopolicy.AudioPolicyManager;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.integration.MusicContacts;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.music.MusicActivity;
import com.qinggan.app.arielapp.minor.phone.ui.PhoneMainActivity;
import com.qinggan.app.arielapp.minor.radio.FMActivity;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.arielapp.MainActivity;

public class AppSwitchControl {
    public static int i = 0, j = 0;
    Context mContext;
    private static final Object mLock = new Object();
    private static volatile AppSwitchControl mAppSwitchControl;
    View mView;
    WindowManager mWindowManager;
    WindowManager.LayoutParams WheelViewParams;
    private static final int MSG_BASE = 0;
    private static final int SHOW = MSG_BASE + 0;
    private static final int DISMISS = MSG_BASE + 1;
    private static final int UPDATE = MSG_BASE + 2;
    private boolean isAppSwitchShow = false;
    private LinearLayout mNaviBtn, mMusicBtn, mPhoneBtn, mRadioBtn;
    private static final int CODE_PLAY_STATUS = MSG_BASE + 4;
    private static final int HIDE_PLAY_PAUSE = MSG_BASE + 5;
    private static final int MSG_SHOW_NAVI = MSG_BASE + 6;
    private static final int MSG_SHOW_MUSIC = MSG_BASE + 7;
    private static final int MSG_SHOW_PHONE = MSG_BASE + 8;
    private static final int MSG_SHOW_RADIO = MSG_BASE + 9;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO
        }
    };
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    mWindowManager.addView(mView, WheelViewParams);
                    isAppSwitchShow = true;
                    initDisplayWindow();
                    /* src float window should be hidden after 2 seconds and in selected function */
                    mHandler.sendEmptyMessageDelayed(DISMISS, 5000);
                    break;
                case DISMISS:
                    mWindowManager.removeViewImmediate(mView);
                    isAppSwitchShow = false;
                    flashToWindow();
                    break;
                case UPDATE:
                    mHandler.removeMessages(SHOW);
                    mHandler.removeMessages(DISMISS);
                    mHandler.removeMessages(UPDATE);
                    mHandler.sendEmptyMessageDelayed(DISMISS, 5000);
                    break;
                case MSG_SHOW_NAVI:
                    initNavi();
                    break;
                case MSG_SHOW_MUSIC:
                    initMusic();
                    break;
                case MSG_SHOW_PHONE:
                    initPhone();
                    break;
                case MSG_SHOW_RADIO:
                    initRadio();
                    break;
                default:
                    break;
            }
        }
    };

    private AppSwitchControl(Context context) {
        mContext = context;
        initAppSwitchView();
    }

    public static AppSwitchControl getInstance(Context context) {
        synchronized (mLock) {
            if (mAppSwitchControl == null) {
                mAppSwitchControl = new AppSwitchControl(context);
            }
        }
        return mAppSwitchControl;
    }

    public void funcAreaSwitch() {
        int type = Math.abs(i % 4);
        switch (type) {
            case 0:
                IntegrationCore.getIntergrationCore(mContext).
                        VoiceJump(StageController.Stage.NAVIGATION);
                break;
            case 1:
                IntegrationCore.getIntergrationCore(mContext).
                        VoiceJump(StageController.Stage.MUSIC);
                break;
            case 2:
                IntegrationCore.getIntergrationCore(mContext).
                        VoiceJump(StageController.Stage.PHONE);
                break;
            case 3:
                IntegrationCore.getIntergrationCore(mContext).
                        VoiceJump(StageController.Stage.RADIO);
                IntegrationCore.getIntergrationCore(mContext).mPateoFMCMD.playCurrent();
                break;
        }
        i++;
    }

    /**
     * for src app switch
     */
    public void initAppSwitchViewTest() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).
                    inflate(R.layout.app_switch_test, null);
            mNaviBtn = mView.findViewById(R.id.m_nav_btn);
            mNaviBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            mMusicBtn = mView.findViewById(R.id.m_music_btn);
            mMusicBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            mPhoneBtn = mView.findViewById(R.id.m_phone_btn);
            mPhoneBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
            mRadioBtn = mView.findViewById(R.id.m_radio_btn);
            mRadioBtn.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
        }
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (WheelViewParams == null) {
            WheelViewParams = new WindowManager.LayoutParams();
            WheelViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN;
            WheelViewParams.gravity = Gravity.CENTER;
            WheelViewParams.format = PixelFormat.TRANSLUCENT;
            WheelViewParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            WheelViewParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    public void initAppSwitchView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).
                    inflate(R.layout.wheel_view_src, null);
            mNaviBtn = mView.findViewById(R.id.m_nav_btn);
            mMusicBtn = mView.findViewById(R.id.m_music_btn);
            mPhoneBtn = mView.findViewById(R.id.m_phone_btn);
            mRadioBtn = mView.findViewById(R.id.m_radio_btn);
        }
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (WheelViewParams == null) {
            WheelViewParams = new WindowManager.LayoutParams();
            WheelViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            WheelViewParams.gravity = Gravity.CENTER;
            WheelViewParams.format = PixelFormat.TRANSLUCENT;
            /* window parameter dp to pixel, for screen size of 5.5 inch temp */
            WheelViewParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            WheelViewParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        initDisplayWindow();
    }

    private void initDisplayWindow() {
        j = getCurrentCore();
        int type = Math.abs(j % 4);
        mHandler.sendEmptyMessage(MSG_SHOW_NAVI + type);
    }

    public void dismiss() {
        mHandler.removeMessages(SHOW);
        mHandler.removeMessages(DISMISS);
        mHandler.removeMessages(UPDATE);
        mHandler.removeMessages(MSG_SHOW_MUSIC);
        mHandler.removeMessages(MSG_SHOW_NAVI);
        mHandler.removeMessages(MSG_SHOW_PHONE);
        mHandler.removeMessages(MSG_SHOW_RADIO);
        if (isAppSwitchShow) mHandler.sendEmptyMessage(DISMISS);
    }

    public void show() {
        mHandler.removeMessages(SHOW);
        mHandler.removeMessages(DISMISS);
        mHandler.removeMessages(UPDATE);
        mHandler.removeMessages(MSG_SHOW_MUSIC);
        mHandler.removeMessages(MSG_SHOW_NAVI);
        mHandler.removeMessages(MSG_SHOW_PHONE);
        mHandler.removeMessages(MSG_SHOW_RADIO);
        if (!isAppSwitchShow) mHandler.sendEmptyMessage(SHOW);
    }

    public void updateAppSwitchView(boolean next) {
        if (!isAppSwitchShow) show();
        if (next) {
            ++j;
        } else {
            --j;
        }

        if (j >= 4) {
            j = 0;
        } else if (j < 0) {
            j = 3;
        }
        int type = Math.abs(j % 4);
        mHandler.sendEmptyMessage(MSG_SHOW_NAVI + type);
        mHandler.sendEmptyMessage(UPDATE);
    }

    public void initNavi() {
        mNaviBtn.setEnabled(true);
        mNaviBtn.setBackground(mContext.getResources().getDrawable(R.mipmap.fk_press));
        mMusicBtn.setEnabled(false);
        mMusicBtn.setBackgroundResource(0);
        mPhoneBtn.setEnabled(false);
        mPhoneBtn.setBackgroundResource(0);
        mRadioBtn.setEnabled(false);
        mRadioBtn.setBackgroundResource(0);
    }

    public void initMusic() {
        mNaviBtn.setEnabled(false);
        mNaviBtn.setBackgroundResource(0);
        mMusicBtn.setEnabled(true);
        mMusicBtn.setBackground(mContext.getResources().getDrawable(R.mipmap.fk_press));
        mPhoneBtn.setEnabled(false);
        mPhoneBtn.setBackgroundResource(0);
        mRadioBtn.setEnabled(false);
        mRadioBtn.setBackgroundResource(0);
    }

    public void initPhone() {
        mNaviBtn.setEnabled(false);
        mNaviBtn.setBackgroundResource(0);
        mMusicBtn.setEnabled(false);
        mMusicBtn.setBackgroundResource(0);
        mPhoneBtn.setEnabled(true);
        mPhoneBtn.setBackground(mContext.getResources().getDrawable(R.mipmap.fk_press));
        mRadioBtn.setEnabled(false);
        mRadioBtn.setBackgroundResource(0);
    }

    public void initRadio() {
        mNaviBtn.setEnabled(false);
        mNaviBtn.setBackgroundResource(0);
        mMusicBtn.setEnabled(false);
        mMusicBtn.setBackgroundResource(0);
        mPhoneBtn.setEnabled(false);
        mPhoneBtn.setBackgroundResource(0);
        mRadioBtn.setEnabled(true);
        mRadioBtn.setBackground(mContext.getResources().getDrawable(R.mipmap.fk_press));
    }

    public boolean isShown() {
        return isAppSwitchShow;
    }

    /**
     * Get system runtime env to have been selected list
     *
     * @return
     */
    private int getCurrentCore() {
        AudioPolicyManager.AudioType type = AudioPolicyManager.getInstance().getCurrentAudioType();
        if (type == AudioPolicyManager.AudioType.MUSIC) {
            return 1;
        } else if (type == AudioPolicyManager.AudioType.FM) {
            return 2;
        }
        return 0;
    }

    /**
     * System will be in selected function called by window when float windows dismissed
     */
    private void flashToWindow() {
        int type = Math.abs(j % 4);
        switch (type) {
            case 0:
                activateNaviActivity();
                break;
            case 1:
                activateMusicActivity();
                break;
            case 2:
                activatePhoneActivity();
                break;
            case 3:
                activateRadioActivity();
                break;
            default:
                break;
        }
    }

    private void clearOtherFuncActivity(){
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    private void activateMusicActivity() {
        clearOtherFuncActivity();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerVoice(mContext.getString(R.string.driving_music));
                Intent intent = new Intent(mContext, BKMusicActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
                fmMusicTransfer(true);
            }
        }, 1000);

    }

    private void activateNaviActivity() {
        clearOtherFuncActivity();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerVoice(mContext.getString(R.string.driving_nav));
                Intent intent = new Intent(mContext, NavigationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        }, 1000);

    }

    private void activateRadioActivity() {
        clearOtherFuncActivity();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerVoice(mContext.getString(R.string.driving_radio));
                Intent intent = new Intent(mContext, FMActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
                fmMusicTransfer(false);
            }
        }, 1000);

    }

    private void activatePhoneActivity() {
        clearOtherFuncActivity();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerVoice(mContext.getString(R.string.driving_phone));
                Intent intent = new Intent(mContext, PhoneMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mContext.startActivity(intent);
            }
        }, 1000);

    }

    private void triggerVoice(String voice) {
        VoicePolicyManage.getInstance().speak(voice);
    }

    private void fmMusicTransfer(boolean music) {
        IntegrationCore integrationCore = IntegrationCore.getIntergrationCore(mContext);
        if (music) {
            /* play music and disable fm*/
            if (integrationCore.mMusicCMD.getPlaybackState()
                    == MusicContacts.PLAYPAUSED || integrationCore.mMusicCMD.getPlaybackState()
                    == MusicContacts.PLAYSTOPPED) {
                integrationCore.mMusicCMD.playMusic();
            }
            integrationCore.mPateoFMCMD.doRadioOff();
        } else {
            integrationCore.mMusicCMD.pauseMusic();
            integrationCore.mPateoFMCMD.doRadioOn();
        }
    }
}
