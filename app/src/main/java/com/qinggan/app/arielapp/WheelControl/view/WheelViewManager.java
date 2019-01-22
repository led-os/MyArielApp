package com.qinggan.app.arielapp.WheelControl.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.qinglink.api.Constant;

import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_HANGUP;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_ICALL;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_IVOKA;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_SRC;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_UP;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_DOWN;
import static com.qinggan.qinglink.api.Constant.Button.KeyCode.KEYCODE_VOLUME_UP;

public class WheelViewManager {
    private static final Object mLock = new Object();
    private static volatile WheelViewManager mWheelViewManager;
    Context mContext;
    WindowManager mWindowManager;
    WindowManager.LayoutParams WheelViewParams;
    TextView mDownText, mUpText, mVolumeUp, mVolumeDown, mInCall, mHangUp, mVoice, mSRC;
    boolean isWheelViewShow = false;
    private static final int SHOW = 0;
    private static final int DISMISS = 1;
    private static final int UPDATE = 2;
    View mView;
    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //TODO
        }
    };

    View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    };

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW:
                    mWindowManager.addView(mView, WheelViewParams);
                    isWheelViewShow = true;
                    mHandler.sendEmptyMessageDelayed(DISMISS, 5000);
                    break;
                case DISMISS:
                    mWindowManager.removeViewImmediate(mView);
                    isWheelViewShow = false;
                    break;
                case UPDATE:
                    mHandler.removeMessages(SHOW);
                    mHandler.removeMessages(DISMISS);
                    mHandler.removeMessages(UPDATE);
                    mHandler.sendEmptyMessageDelayed(DISMISS, 5000);
                    break;
            }
        }
    };

    private WheelViewManager() {
        mContext = ArielApplication.getApp().getApplicationContext();
        initWheelView();
    }

    public static WheelViewManager getInstance() {
        synchronized (mLock) {
            if (mWheelViewManager == null) {
                mWheelViewManager = new WheelViewManager();
            }
        }
        return mWheelViewManager;
    }

    public void initWheelView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).
                    inflate(R.layout.wheel_view_src, null);
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
        if (Build.VERSION.SDK_INT >= 26) {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    /**
     * For wheel test view
     */
    public void initTestWheelView() {
        if (mView == null) {
            mView = LayoutInflater.from(mContext).
                    inflate(R.layout.wheel_view_test, null);
            mDownText = mView.findViewById(R.id.btn_down);
            mUpText = mView.findViewById(R.id.btn_up);
            mVolumeDown = mView.findViewById(R.id.btn_volume_down);
            mVolumeUp = mView.findViewById(R.id.btn_volume_up);
            mInCall = mView.findViewById(R.id.btn_incall);
            mHangUp = mView.findViewById(R.id.btn_hangup);
            mVoice = mView.findViewById(R.id.btn_voice);
            mSRC = mView.findViewById(R.id.btn_src);
        }
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (WheelViewParams == null) {
            WheelViewParams = new WindowManager.LayoutParams();
            WheelViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
            WheelViewParams.gravity = Gravity.CENTER;
            WheelViewParams.format = PixelFormat.RGBA_8888;
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            WheelViewParams.height = 500;
            WheelViewParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            WheelViewParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }

    public void show() {
        mHandler.removeMessages(SHOW);
        mHandler.removeMessages(DISMISS);
        mHandler.removeMessages(UPDATE);
        if (!isWheelViewShow) mHandler.sendEmptyMessage(SHOW);
    }

    public void dismiss() {
        mHandler.removeMessages(SHOW);
        mHandler.removeMessages(DISMISS);
        mHandler.removeMessages(UPDATE);
        if (isWheelViewShow) mHandler.sendEmptyMessage(DISMISS);
    }

    public void remove() {
        if (isWheelViewShow) mWindowManager.removeViewImmediate(mView);
        mWindowManager = null;
        WheelViewParams = null;
        mView = null;
        isWheelViewShow = false;
    }

    public boolean getWheelShow() {
        return isWheelViewShow;
    }

    public void updateWheelView(int keycode, int keyaction) {
        switch (keycode) {
            case KEYCODE_UP:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mUpText.setText("点击上按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mUpText.setText("放开上按钮");
                }
                break;
            case KEYCODE_DOWN:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mDownText.setText("点击下按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mDownText.setText("放开下按钮");
                }
                break;
            case KEYCODE_VOLUME_DOWN:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mVolumeDown.setText("点击音量减按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mVolumeDown.setText("放开音量减按钮");
                }
                break;
            case KEYCODE_VOLUME_UP:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mVolumeUp.setText("点击音量加按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mVolumeUp.setText("放开音量加按钮");
                }
                break;
            case KEYCODE_HANGUP:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mHangUp.setText("点击挂断按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mHangUp.setText("放开挂断按钮");
                }
                break;
            case KEYCODE_ICALL:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mInCall.setText("点击接通电话按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mInCall.setText("放开接通电话按钮");
                }
                break;
            case KEYCODE_IVOKA:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mVoice.setText("点击语音按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mVoice.setText("放开语音按钮");
                }
                break;
            case KEYCODE_SRC:
                if (keyaction == Constant.Button.KeyAction.KEY_DOWN) {
                    mSRC.setText("点击源按钮");
                } else if (keyaction == Constant.Button.KeyAction.KEY_UP) {
                    mSRC.setText("放开源按钮");
                }
                break;
        }
        mHandler.sendEmptyMessage(UPDATE);
    }
}
