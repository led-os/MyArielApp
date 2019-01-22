package com.qinggan.app.arielapp.voiceview;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qinggan.app.arielapp.ActivityLifecycleListener;
import com.qinggan.app.arielapp.MainActivity;

/**
 * Created by zhongquansun on 2018/11/22.
 */
public class VoiceFloatViewService extends Service implements VoicePolicyManage.VoiceStateChangeListener{
    private static final String TAG = VoiceFloatViewService.class.getSimpleName();
    private DragFloatView mWakeUpFloatView;
    private VoiceFloatView mVoiceFloatView;
    private OutOfAppHintFloatView mOutOfAppHintFloatView;
    private SelectHintFloatView mSelectHintFloatView;
    private NavHintFloatView mNavHintFloatView;
    private PhoneHintFloatView mPhoneHintFloatView;
    private boolean mIsInApp = true;
    private boolean mIsInCar = false;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        initFloatView();
    }

    private void initFloatView(){
        VoicePolicyManage.getInstance().addListeners(this);
        mVoiceFloatView = new VoiceFloatView(getApplicationContext());
        mWakeUpFloatView = DragFloatView.getInstance(getApplicationContext());
        mOutOfAppHintFloatView = new OutOfAppHintFloatView(getApplicationContext());
        mSelectHintFloatView = SelectHintFloatView.getInstance(getApplicationContext());
        mNavHintFloatView = NavHintFloatView.getInstance(getApplicationContext());
        mPhoneHintFloatView = PhoneHintFloatView.getInstance(getApplicationContext());
        mWakeUpFloatView.setOnDragFloatViewClickListener(new DragFloatView.DragFloatViewClickListener() {
            @Override
            public void onWakeUpViewClicked() {
                VoicePolicyManage.getInstance().record(true);
                Log.d(TAG, "onWakeUpViewClicked()");
            }

            @Override
            public void onBackToAppViewClicked() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }
        });
        mWakeUpFloatView.init(ActivityLifecycleListener.getInstance().isInApp());
        mWakeUpFloatView.show();
    }

    public void updateWakeUpViews(boolean isInApp, boolean isMainActivity){
        Log.d(TAG, "updateWakeUpViews() = " + isInApp);
        mIsInApp = isInApp;
        if(mIsInApp && mIsInCar) {
            mWakeUpFloatView.dismiss();
        }else if((!mIsInApp) && (!mIsInCar)){
            mWakeUpFloatView.dismiss();
        }else if(mIsInApp && (!isMainActivity)){
            mWakeUpFloatView.dismiss();
        }else {
            mWakeUpFloatView.show();
            mWakeUpFloatView.updateViews(isInApp);
        }

        VoicePolicyManage.getInstance().setInAppOrNot(isInApp);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        VoicePolicyManage.getInstance().removeListener(this);
        mWakeUpFloatView.removeOnDragFloatViewClickListener();
        mWakeUpFloatView.dismiss();
        mWakeUpFloatView.destroy();
        mVoiceFloatView.dismiss();
        mVoiceFloatView.destroy();
    }

    @Override
    public void onStateChange(VoicePolicyManage.VoiceMode voiceMode, VoicePolicyManage.VoiceState voiceState) {
//        if(voiceState == VoicePolicyManage.VoiceState.LISTENING && (!mIsInApp)) {
//            mOutOfAppHintFloatView.show();
//        }
        if(voiceState != VoicePolicyManage.VoiceState.LISTENING){
            if(mOutOfAppHintFloatView.isShown()) {
                mOutOfAppHintFloatView.dismiss();
            }

            if(mNavHintFloatView.isShown()){
                mNavHintFloatView.dismiss();
            }

            if(mSelectHintFloatView.isShown()){
                mSelectHintFloatView.dismiss();
            }

            if(mPhoneHintFloatView.isShown()){
                mPhoneHintFloatView.dismiss();
            }
        }
    }

    @Override
    public void onContentChange(String content) {

    }

    @Override
    public void onUserClick(final boolean userClick, final boolean wakeUpOrInterrupt) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if((!mIsInApp) && wakeUpOrInterrupt && userClick) {
                    mOutOfAppHintFloatView.show();
                }
            }
        });
    }

    public class LocalBinder extends Binder{
        public LocalBinder(){

        }

        public VoiceFloatViewService getService(){
            return VoiceFloatViewService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new VoiceFloatViewService.LocalBinder();
    }

    public void changeInCarMode(boolean isInCar){
        mIsInCar = isInCar;
        if(!mIsInApp) return;
        if(isInCar){
            mWakeUpFloatView.dismiss();
        }else{
            mWakeUpFloatView.show();
            mWakeUpFloatView.updateViews(mIsInApp);
        }
    }
}
