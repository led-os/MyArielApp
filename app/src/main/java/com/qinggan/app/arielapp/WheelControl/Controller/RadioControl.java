package com.qinggan.app.arielapp.WheelControl.Controller;

import android.content.Context;

import com.qinggan.app.arielapp.WheelControl.Listener.RadioControlListener;

public class RadioControl {
    RadioControlListener mListener;
    Context mContext;
    private static final Object mLock = new Object();
    private static RadioControl mRadioControl;

    private RadioControl(Context context) {
        mContext = context;
    }

    public static RadioControl getInstance(Context context) {
        synchronized (mLock) {
            if (mRadioControl == null) {
                mRadioControl = new RadioControl(context);
            }
        }
        return mRadioControl;
    }

    public void setListener(RadioControlListener listener){
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
