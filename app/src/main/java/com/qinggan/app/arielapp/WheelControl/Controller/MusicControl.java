package com.qinggan.app.arielapp.WheelControl.Controller;

import android.content.Context;

import com.qinggan.app.arielapp.WheelControl.Listener.MusicControlListener;

public class MusicControl {
    MusicControlListener mListener;
    Context mContext;
    private static final Object mLock = new Object();
    private static MusicControl mMusicControl;

    private MusicControl(Context context) {
        mContext = context;
    }

    public static MusicControl getInstance(Context context) {
        synchronized (mLock) {
            if (mMusicControl == null) {
                mMusicControl = new MusicControl(context);
            }
        }
        return mMusicControl;
    }

    public void setListener(MusicControlListener listener){
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
