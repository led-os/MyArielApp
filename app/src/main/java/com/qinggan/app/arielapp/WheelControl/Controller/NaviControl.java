package com.qinggan.app.arielapp.WheelControl.Controller;

import android.content.Context;

import com.qinggan.app.arielapp.WheelControl.Listener.NaviControlListener;

public class NaviControl {
    NaviControlListener mListener;
    Context mContext;
    private static final Object mLock = new Object();
    private static NaviControl mNaviControl;

    private NaviControl(Context context) {
        mContext = context;
    }

    public static NaviControl getInstance(Context context) {
        synchronized (mLock) {
            if (mNaviControl == null) {
                mNaviControl = new NaviControl(context);
            }
        }
        return mNaviControl;
    }

    public void setListener(NaviControlListener listener){
        mListener = listener;
    }

    public void clearListener(){
        mListener = null;
    }

    public void previous(){
        if(mListener != null) mListener.previous();
    }
    public void next(){
        if(mListener != null) mListener.next();
    }
}
