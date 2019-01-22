package com.qinggan.app.arielapp.WheelControl.Controller;

import android.content.Context;

import com.qinggan.app.arielapp.WheelControl.Listener.PhoneCallControlListener;

public class PhoneCallControl {
    PhoneCallControlListener mListener;
    Context mContext;
    private static final Object mLock = new Object();
    private static PhoneCallControl mPhoneCallControl;

    private PhoneCallControl(Context context) {
        mContext = context;
    }

    public static PhoneCallControl getInstance(Context context) {
        synchronized (mLock) {
            if (mPhoneCallControl == null) {
                mPhoneCallControl = new PhoneCallControl(context);
            }
        }
        return mPhoneCallControl;
    }

    public void setListener(PhoneCallControlListener listener){
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

    public void hangUp(){
        if(mListener != null) mListener.hangUp();
    }

    public void inCall(){
        if(mListener != null) mListener.inCall();
    }
}
