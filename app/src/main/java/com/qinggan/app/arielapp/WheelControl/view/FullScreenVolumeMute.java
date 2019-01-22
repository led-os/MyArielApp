package com.qinggan.app.arielapp.WheelControl.view;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;

public class FullScreenVolumeMute extends Activity {
    ImageView mMuteImage;
    TextView mMuteText;
    Context mContext;
    static final int DISMISS = 0;
    static final int FINISH = 1;
    static final int UPDATE = 2;
    int disMissTime = 2000;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case DISMISS:
                    finish();
                    break;
                case FINISH:
                    mHandler.removeMessages(FINISH);
                    mHandler.removeMessages(UPDATE);
                    mHandler.removeMessages(DISMISS);
                    mHandler.sendEmptyMessageDelayed(DISMISS,disMissTime);
                    break;
                case UPDATE:
                    mHandler.removeMessages(UPDATE);
                    mHandler.removeMessages(DISMISS);
                    mHandler.removeMessages(FINISH);
                    upDateMute();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = ArielApplication.getApp().getApplicationContext();
        setContentView(R.layout.full_screen_volume_mute);
        mMuteImage =findViewById(R.id.mute_image);
        mMuteText = findViewById(R.id.mute_text);
        mHandler.sendEmptyMessage(FINISH);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    public boolean onTouchEvent(MotionEvent event) {
        IntegrationCore.getIntergrationCore(mContext).getSystemCMD().setAllMute();
        mHandler.sendEmptyMessage(UPDATE);
        return super.onTouchEvent(event);
    }

    public void upDateMute(){
        boolean flag = IntegrationCore.getIntergrationCore(mContext).getSystemCMD().isMute(
                AudioManager.STREAM_MUSIC);
        if(flag){
            mMuteImage.setBackgroundResource(R.drawable.user);
            mMuteText.setText("静音开启"/*R.string.ac_closeed*/);
        }else{
            mMuteImage.setBackgroundResource(R.drawable.user);
            mMuteText.setText("静音关闭"/*R.string.ac_closeed*/);
        }
        mHandler.sendEmptyMessage(FINISH);
    }
}
