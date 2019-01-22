package com.qinggan.app.arielapp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.qinggan.app.arielapp.utils.AppManager;
import com.qinggan.app.arielapp.voiceview.VoiceFloatViewService;

/**
 * Created by zhongquansun on 2018/11/13.
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = ActivityLifecycleListener.class.getSimpleName();
    private static ActivityLifecycleListener sActivityLifecycleListener;
    private int refCount = 0;
    private VoiceFloatViewService mVoiceFloatViewService;

    public static boolean mIsForgroud = false;
    public static Activity lastActivity;

    private ActivityLifecycleListener(){

    }

    public static ActivityLifecycleListener getInstance(){
        if(null == sActivityLifecycleListener){
            sActivityLifecycleListener = new ActivityLifecycleListener();
        }
        return sActivityLifecycleListener;
    }

    public boolean isInApp() {
        return refCount > 0;
    }

    public void setVoiceFloatViewService(VoiceFloatViewService voiceFloatViewService) {
        mVoiceFloatViewService = voiceFloatViewService;
    }

    public static Activity currentActivity;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        // 把actvity放到application栈中管理
        AppManager.getAppManager().addActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
        boolean isMainActivity = activity instanceof MainActivity;
        Log.d(TAG, "onActivityStarted() = " + activity.getLocalClassName());
        if (refCount > 0 && null != mVoiceFloatViewService) {
            mVoiceFloatViewService.updateWakeUpViews(true, isMainActivity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        mIsForgroud = true;
        boolean isMainActivity = activity instanceof MainActivity;
        if (refCount > 0 && null != mVoiceFloatViewService && isMainActivity) {
            mVoiceFloatViewService.updateWakeUpViews(true, true);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        lastActivity = activity;
        mIsForgroud = false;
        boolean isMainActivity = activity instanceof MainActivity;
        if (refCount == 0 && null != mVoiceFloatViewService && isMainActivity) {
            mVoiceFloatViewService.updateWakeUpViews(false, false);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        Log.d(TAG, "onActivityStopped() = " + activity.getLocalClassName());
        if (refCount == 0 && null != mVoiceFloatViewService) {
            mVoiceFloatViewService.updateWakeUpViews(false, false);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // 把actvity移除Stack
        AppManager.getAppManager().removeActivity(activity);
    }
}
