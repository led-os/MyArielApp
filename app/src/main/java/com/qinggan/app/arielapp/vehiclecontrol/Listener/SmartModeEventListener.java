package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.SmartModeController;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.AirConditionState;
import com.qinggan.qinglink.enumeration.SituationalModeState;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;
import static com.qinggan.app.voiceapi.control.car.SmartModeController.SmartMode.BAD_WEATHER;
import static com.qinggan.app.voiceapi.control.car.SmartModeController.SmartMode.COOL;
import static com.qinggan.app.voiceapi.control.car.SmartModeController.SmartMode.SMOKING;
import static com.qinggan.app.voiceapi.control.car.SmartModeController.SmartMode.WARM;

/**
 * Created by Yorashe on 18-9-12.
 */

public class SmartModeEventListener implements SmartModeController.SmartModeEventListener {

    private Context mContext;
    private String mColdModeOpen;
    private String mWarmModeOpen;
    private String mPmModeOpen;
    private String mParkModeOpen;
    private String mBabyModeOpen;
    private String mSmokeModeOpen;
    private String mSnowModeOpen;
    private String mRomanticModeOpen;
    private String mColdModeClosed;
    private String mWarmModeClosed;
    private String mPmModeClosed;
    private String mParkModeClosed;
    private String mParkModeClosed2;
    private String mBabyModeClosed;
    private String mSmokeModeClosed;
    private String mSnowModeClosed;
    private String mRomanticModeClosed;
    private String mSmartmodeAlreadyCloseHint;
    private String mSmartmodeAlreadyOpenHint;
    private static final int SPEED_80 = 80;
    private String mAirConditionOpened;
    private long mLastSetParmModeTime;
    private boolean mOpenParkMode = false;
    private CanBusManager sCanBusManager;

    public SmartModeEventListener(Context mContext,CanBusManager sCanBusManager) {
        this.sCanBusManager=sCanBusManager;
        this.mContext = mContext;
        mColdModeOpen = mContext.getResources().getString(R.string.cold_mode_open);
        mWarmModeOpen = mContext.getResources().getString(R.string.warm_mode_open);
        mPmModeOpen = mContext.getResources().getString(R.string.pm_mode_open);
        mParkModeOpen = mContext.getResources().getString(R.string.park_mode_open);
        mBabyModeOpen = mContext.getResources().getString(R.string.baby_mode_open);
        mSmokeModeOpen = mContext.getResources().getString(R.string.smoke_mode_open);
        mSnowModeOpen = mContext.getResources().getString(R.string.snow_mode_open);
        mRomanticModeOpen = mContext.getResources().getString(R.string.romantic_mode_open);
        mAirConditionOpened = mContext.getResources().getString(R.string.air_condition_opened);

        mColdModeClosed = mContext.getResources().getString(R.string.smart_mode_cool_closed);
        mWarmModeClosed = mContext.getResources().getString(R.string.smart_mode_warm_closed);
        mPmModeClosed = mContext.getResources().getString(R.string.smart_mode_smog_closed);
        mParkModeClosed = mContext.getResources().getString(R.string.smart_mode_stop_close1);
        mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        mBabyModeClosed = mContext.getResources().getString(R.string.smart_mode_baby_closed);
        mSmokeModeClosed = mContext.getResources().getString(R.string.smart_mode_smoke_closed);
        mSnowModeClosed = mContext.getResources().getString(R.string.smart_mode_snow_closed);
        mRomanticModeClosed = mContext.getResources().getString(R.string.smart_mode_romantic_closed);
        mSmartmodeAlreadyCloseHint = mContext.getResources().getString(R.string.smartmode_already_close_hint);
        mSmartmodeAlreadyOpenHint = mContext.getResources().getString(R.string.smartmode_already_open_hint);
    }


    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        return true;
    }

    @Override
    public void onSetMode(int mode) {
        Log.i(TAG, "onSetMode(" + mode + ")");
        if (!isACCOn() && mode != 8) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        String str = null;
        if (COOL == mode) {
            str = mColdModeOpen;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_RAPID_COOLING_MODE, AirConditionState.SWITCH_ON);
        } else if (WARM == mode) {
            str = mWarmModeOpen;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_ONE_BUTTON_WARMTH_MODE,AirConditionState.SWITCH_ON);
        } else if (BAD_WEATHER == mode) {
            str = mSnowModeOpen;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_RAIN_SNOW_MODE,AirConditionState.SWITCH_ON);
        } else if (SMOKING == mode) {
            str = mSmokeModeOpen;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_SMOKING_MODE,AirConditionState.SWITCH_ON);

        } else{
            str = mParkModeClosed2;

        }
        if (null != str) {
             VoicePolicyManage.getInstance().speak(str);

        }
    }

    @Override
    public void onCloseMode(int mode) {
        Log.i(TAG, "onSetMode(" + mode + ")");

        if (!isACCOn() && mode != 8) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        String str = null;
        if (COOL == mode) {
            str = mColdModeClosed;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_RAPID_COOLING_MODE, AirConditionState.SWITCH_ON);
        } else if (WARM == mode) {
            str = mWarmModeClosed;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_ONE_BUTTON_WARMTH_MODE,AirConditionState.SWITCH_ON);
        } else if (BAD_WEATHER == mode) {
            str = mSnowModeClosed;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_RAIN_SNOW_MODE,AirConditionState.SWITCH_ON);
        } else if (SMOKING == mode) {
            str = mSmokeModeClosed;
            sCanBusManager.setSituationalModeState(SituationalModeState.AC_SMOKING_MODE,AirConditionState.SWITCH_ON);

        } else{
            str = mParkModeClosed2;

        }
        if (null != str) {
             VoicePolicyManage.getInstance().speak(str);

        }
    }





}
