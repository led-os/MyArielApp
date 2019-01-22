package com.qinggan.app.arielapp.WheelControl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.NaviControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;
import com.qinggan.app.arielapp.WheelControl.Listener.RadioControlListener;
import com.qinggan.app.arielapp.WheelControl.Observer.IWheelControlOb;
import com.qinggan.app.arielapp.WheelControl.Observer.WheelControlObImpl;
import com.qinggan.qinglink.api.Constant;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.ButtonListener;
import com.qinggan.qinglink.api.md.ButtonManager;

import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_IVOKA;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MEDIA_NEXT;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MEDIA_PREVIOUS;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_PHONE;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_SRC;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_UP;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_MUTE;

public class WheelControl {
    private static final String TAG = "WheelControl";
    private WheelControlBroadcast controlBroadcast;
    Context mContext;
    private static final Object mLock = new Object();
    private static WheelControl wheelControl;
    IWheelControlOb mIWheelControlOb;
    OnInitListener mOnInitListener = new OnInitListener() {
        @Override
        public void onConnectStatusChange(boolean b) {
            Log.e("WheelControl", "onConnectStatusChange===" + b);

            if (b) {
                //TODO

            } else {
                //TODO
            }
        }
    };

    ButtonManager mButtonManager;
    ButtonListener mButtonListener = new ButtonListener() {
        @Override
        public void onButton(int i, int i1) {
            Log.e("WheelControl", "start -> keyCode===" + i + "," + "keyStatus===" + i1);
            if (i1 == Constant.Button.KeyAction.SHORT_PRESS) {
                mIWheelControlOb.handleEvents(i, i1);
            }
            Log.e("WheelControl", "complete ->");
        }
    };

    public WheelControl(Context context) {
        mContext = context;
        mIWheelControlOb = new WheelControlObImpl(mContext);
        mButtonManager = ButtonManager.getInstance(mContext, mOnInitListener);
        mButtonManager.registerListener(mButtonListener);
        testBd();
    }

    public void setMusicListener(MusicControlListener musicListener) {
        mIWheelControlOb.setMusicListener(musicListener);
    }

    public void setNaviListener(NaviControlListener naviListener) {
        mIWheelControlOb.setNaviListener(naviListener);
    }

    public void setRadioListener(RadioControlListener radioListener) {
        mIWheelControlOb.setRadioListener(radioListener);
    }

    public void setPhoneListener(PhoneCallControlListener phoneListener) {
        mIWheelControlOb.setPhoneListener(phoneListener);
    }

    public void destroyResource() {
        mIWheelControlOb.destroyResource();
    }

    private void testBd() {
        controlBroadcast = new WheelControlBroadcast();
        initTestBroadcast(mContext);
    }

    private void initTestBroadcast(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pateo.lj.volume_up");
        filter.addAction("com.pateo.lj.volume_down");
        filter.addAction("com.pateo.lj.keycode_media_next");
        filter.addAction("com.pateo.lj.keycode_media_previous");
        filter.addAction("com.pateo.lj.keycode_src");
        filter.addAction("com.pateo.lj.keycode_mute");
        filter.addAction("com.pateo.lj.keycode_phone");
        filter.addAction("com.pateo.lj.keycode_ivoka");
        context.registerReceiver(controlBroadcast, filter);
    }

    private class WheelControlBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.pateo.lj.volume_up".equals(intent.getAction())) {
                Log.d(TAG, "volume_up");
                mIWheelControlOb.handleEvents(KEYCODE_VOLUME_UP, 0);
            } else if ("com.pateo.lj.volume_down".equals(intent.getAction())) {
                Log.d(TAG, "volume_down");
                mIWheelControlOb.handleEvents(KEYCODE_VOLUME_DOWN, 0);
            } else if ("com.pateo.lj.keycode_media_next".equals(intent.getAction())) {
                Log.d(TAG, "keycode_media_next");
                mIWheelControlOb.handleEvents(KEYCODE_MEDIA_NEXT, 0);
            } else if ("com.pateo.lj.keycode_media_previous".equals(intent.getAction())) {
                Log.d(TAG, "keycode_media_previous");
                mIWheelControlOb.handleEvents(KEYCODE_MEDIA_PREVIOUS, 0);
            } else if ("com.pateo.lj.keycode_src".equals(intent.getAction())) {
                Log.d(TAG, "keycode_src");
                mIWheelControlOb.handleEvents(KEYCODE_SRC, 0);
            } else if ("com.pateo.lj.keycode_mute".equals(intent.getAction())) {
                Log.d(TAG, "keycode_mute");
                mIWheelControlOb.handleEvents(KEYCODE_MUTE, 0);
            } else if ("com.pateo.lj.keycode_phone".equals(intent.getAction())) {
                Log.d(TAG, "keycode_phone");
                mIWheelControlOb.handleEvents(KEYCODE_PHONE, 0);
            } else if("com.pateo.lj.keycode_ivoka".equals(intent.getAction())){
                Log.d(TAG, "keycode_ivoka");
                mIWheelControlOb.handleEvents(KEYCODE_IVOKA, 0);
            }
        }
    }

    public void destroyBroadcast() {
        mContext.unregisterReceiver(controlBroadcast);
    }
}
