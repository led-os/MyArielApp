package com.qinggan.app.arielapp.voiceview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by zhongquansun on 2018/11/26.
 */
public abstract class BaseFloatView {
    protected static final String TAG = BaseFloatView.class.getSimpleName();

    protected Context mContext;
    protected int mScreenWidth;
    protected int mScreenHeight;
    private WindowManager mWindowManager;
    protected WindowManager.LayoutParams mFloatViewLayoutParams;
    protected View mFloatView;
    private boolean mIsShown;

    public BaseFloatView(Context context){
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScreenHeight = dm.heightPixels;
        mScreenWidth = dm.widthPixels;
        Log.e(TAG, "mScreenWidth = " + mScreenWidth + ", mScreenHeight = " + mScreenHeight);

        initWindowParams();
    }

    private void initWindowParams(){
        mFloatViewLayoutParams = new WindowManager.LayoutParams();
        mFloatViewLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mFloatViewLayoutParams.format = PixelFormat.TRANSLUCENT;
        onWindowParamsCreate(mFloatViewLayoutParams);
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mFloatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mFloatViewLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mFloatView = LayoutInflater.from(mContext).inflate(getViewLayoutID(), null);
        Log.e(TAG, "mFloatView = " + mFloatView);
        onViewCreate(mFloatView);
    }

    protected abstract void onWindowParamsCreate(WindowManager.LayoutParams layoutParams);
    protected abstract int getViewLayoutID();
    protected abstract void onViewCreate(View view);

    public void show(){
        if(!mIsShown) {
            mWindowManager.addView(mFloatView, mFloatViewLayoutParams);
            mIsShown = true;
            if (mFloatViewCallBack != null) {
                mFloatViewCallBack.onViewShow();
            }
        }
    }

    public void dismiss(){
        if(mIsShown) {
            mWindowManager.removeViewImmediate(mFloatView);
            mIsShown = false;
            if (mFloatViewCallBack != null) {
                mFloatViewCallBack.onViewDismiss();
            }
        }
    }

    public boolean isShown() {
        return mIsShown;
    }

    public void update(){
        if(isShown()) {
            mWindowManager.updateViewLayout(mFloatView, mFloatViewLayoutParams);
        }
    }

    public void destroy(){

    }

    // add
    private FloatViewCallBack mFloatViewCallBack;

    public void setFloatViewCallBack(FloatViewCallBack callBack) {
        this.mFloatViewCallBack = callBack;
    }

    public interface FloatViewCallBack {
        void onViewShow();

        void onViewDismiss();
    }
}
