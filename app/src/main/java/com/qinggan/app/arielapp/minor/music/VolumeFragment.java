package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.music.PlayListFragment;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import static com.baidu.turbonet.base.ThreadUtils.runOnUiThread;

/**
 * Created by yang
 * Time on 2018/11/8.
 * Function 音量页面
 */
public class VolumeFragment  extends AbstractBaseFragment implements View.OnClickListener,MainActivity.MyTouchListener {

    View mView;
    private Context mContext = ArielApplication.getApp();
    ImageView mImageView;
    ImitateIphoneSwitch imitateIphoneSwitch;
    private float startY = 0;//手指按下时的Y坐标
    private float startX = 0;//手指按下时的Y坐标
    boolean isFirst = true;
//    Timer timer=new Timer();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_volume, container, false);
        return mView;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        mImageView = mView.findViewById(R.id.delete_image);
        imitateIphoneSwitch = mView.findViewById(R.id.volune_profile);
        mImageView.setOnClickListener(this);
        imitateIphoneSwitch.registerCallback(new ImitateIphoneSwitch.ValueChangeCallback() {
            @Override
            public void onValueChanged(double value) {
//                IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(value);
            }
        });
        ((MainActivity)this.getActivity()).registerMyTouchListener(this);
        imitateIphoneSwitch.setValues(getVolumeCurrent(),getViewHeight());
//        Timer();
//        mView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        startY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float endY = event.getY();
//                        float distanceY = startY - endY;
//                        final double FLING_MIN_VELOCITY = 0;
//                        if (distanceY > FLING_MIN_VELOCITY && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                            setTouchVolume(1);
//                        }
//                        if (distanceY < FLING_MIN_VELOCITY && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
//                            setTouchVolume(-1);
//                        }
//                        break;
//                }
//                return true;
//            }
//        });
    }

    public int getViewHeight(){
        ViewGroup.LayoutParams layoutParams = imitateIphoneSwitch.getLayoutParams();
        return layoutParams.height;
    }

    public void setTouchVolume(float volume) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        if (isFirst){
            lp.screenBrightness =  getVolumeCurrent() +volume / 255.0f;
            isFirst = false;
        }else {
            lp.screenBrightness =  lp.screenBrightness+volume / 255.0f;
        }
        Log.i("---LY","brightness / 255.0f: "+volume / 255.0f +" lp.screenBrightness:  "+ lp.screenBrightness+" brightness:  "+ volume);
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0) {
            lp.screenBrightness = 0;
        }
        imitateIphoneSwitch.setValues(lp.screenBrightness,getViewHeight());
        IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(lp.screenBrightness);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void eventBusReceive(String key) {
        if (key.equals("down")||key.equals("up")){
            imitateIphoneSwitch.setValues( getVolumeCurrent(),getViewHeight());
            IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(getVolumeCurrent());
        }else if (key.equals("onRestart")){
            imitateIphoneSwitch.setValues(getVolumeCurrent(),getViewHeight());
        }
    }

     @Override
    public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = event.getY();
                float distanceY = startY - endY;
                final double FLING_MIN_VELOCITY = 0;
                if (distanceY > FLING_MIN_VELOCITY && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                    setTouchVolume(1);
                }
                if (distanceY < FLING_MIN_VELOCITY && Math.abs(distanceY) > FLING_MIN_VELOCITY) {
                    setTouchVolume(-1);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.delete_image:
                getFragmentManager().popBackStack();
                break;
        }
    }

//    private void Timer(){
//        TimerTask task=new TimerTask() {
//            @Override public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override public void run() {
//                        imitateIphoneSwitch.setValues( getVolumeCurrent(),getViewHeight());
//                        IntegrationCore.getIntergrationCore(mContext).adjustVolumeF(getVolumeCurrent());
//                    } });
//            }
//        };
//        timer.schedule(task,0,500);
//    }

    public float getVolumeCurrent(){
        AudioManager mAudioManager = (AudioManager) (getActivity().getSystemService(Context.AUDIO_SERVICE));
        int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        return (float)current/max;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MainActivity)this.getActivity()).unRegisterMyTouchListener(this);
        EventBus.getDefault().unregister(this);
//        if (null != timer){
//            timer.cancel();
//        }
    }
}


