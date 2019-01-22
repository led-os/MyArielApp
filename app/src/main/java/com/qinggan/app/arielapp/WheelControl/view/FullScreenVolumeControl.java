package com.qinggan.app.arielapp.WheelControl.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;

public class FullScreenVolumeControl extends Activity implements GestureDetector.
        OnGestureListener {
    GestureDetector detector;
    ImageView mUp,mButtom;
    ProgressBar mProgress;
    static final int DISMISS = 0;
    static final int FINISH = 1;
    static final int UPDATE = 2;
    int disMissTime = 2000;
    int volumeStep = 10;
    AudioManager mAudioManager;
    int mVolumeMax,mVolumeCurrent;
    Context mContext;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DISMISS:
                    finish();
                    break;
                case FINISH:
                    mHandler.removeMessages(FINISH);
                    mHandler.removeMessages(DISMISS);
                    mHandler.sendEmptyMessageDelayed(DISMISS,disMissTime);
                    break;
                case UPDATE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ArielApplication.getApp().getApplicationContext();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        detector = new GestureDetector(this,this);
        setContentView(R.layout.full_screen_volume_control);
        mUp = findViewById(R.id.up);
        mButtom = findViewById(R.id.buttom);
        mProgress = findViewById(R.id.mProgress);
        //current use music stream,you can try getActiveStreamType,but it may have some issues.
        mVolumeMax = mAudioManager.getStreamMaxVolume(mAudioManager.STREAM_MUSIC);
        mVolumeCurrent = mAudioManager.getStreamVolume(mAudioManager.STREAM_MUSIC);
        mProgress.setMax(mVolumeMax);
        mProgress.setProgress(mVolumeCurrent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(FINISH);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1,
                            float v, float v1) {
        int adjustStep = (Math.round(v1))/volumeStep;
        mVolumeCurrent = mVolumeCurrent+adjustStep;
        mProgress.incrementProgressBy(adjustStep);
        //mHandler.sendEmptyMessage(UPDATE);
        IntegrationCore.getIntergrationCore(mContext).getSystemCMD().adjustMusicVolumeF(
                (double)mVolumeCurrent/mVolumeMax);
        return false;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        //TODO
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        //TODO
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1,
                           float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mHandler.sendEmptyMessage(FINISH);
        this.detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
