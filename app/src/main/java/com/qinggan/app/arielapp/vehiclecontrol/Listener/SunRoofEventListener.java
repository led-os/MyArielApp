package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.SunRoofController;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.VehicleState;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;

/**
 * Created by Yorashe on 18-9-12.
 */

public class SunRoofEventListener implements SunRoofController.SunRoofEventListener {

    private Context mContext;
    private static final int SPEED_80 = 80;
    private String mSunroofOpend;
    private String mSunroofClosed;
    private String mSunroofTilt;
    private CanBusManager mCanBusManager;


    public SunRoofEventListener(Context mContext) {
        this.mContext = mContext;
        mSunroofOpend = mContext.getString(R.string.sunroof_opened);
        mSunroofClosed = mContext.getString(R.string.sunroof_closed);
        mSunroofTilt = mContext.getString(R.string.sunroof_tilt);
        this.mCanBusManager= ArielApplication.getCanBusManager();

    }

    @Override
    public void onOpen(int roofType) {
        Log.i(TAG, "SunRoofEventListener onOpen(" + roofType + ")");
        if (!canOpenSunroof()) {
            return;
        }
//        if (getSpeed() > SPEED_80)
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.can_not_use_sunroof_high_speed));
//            showSmartTip(mContext.getResources().getString(R.string.can_not_use_sunroof_high_speed));
//            return;
//        }
        int ret = -1;
        if (SunRoofController.SunRoofClassify.SUNROOF == roofType) {
            mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mSunroofOpend);
        } else if (SunRoofController.SunRoofClassify.SUNROOF_SHADE == roofType) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_shade_opened));
        } else if (SunRoofController.SunRoofClassify.SUNROOF_WINDOW == roofType) {
            mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mSunroofOpend);
        }


    }

    @Override
    public void onClose(int roofType) {
        Log.i(TAG, "SunRoofEventListener onClose(" + roofType + ")");
        if (!canOpenSunroof()) {
            return;
        }
        Log.i(TAG, "SunRoofEventListener onClose(" + roofType + ")");
        int ret = -1;
        if (SunRoofController.SunRoofClassify.SUNROOF == roofType) {
            mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
            VoicePolicyManage.getInstance().speak(mSunroofClosed);
        } else if (SunRoofController.SunRoofClassify.SUNROOF_SHADE == roofType) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_shade_closed));
        } else if (SunRoofController.SunRoofClassify.SUNROOF_WINDOW == roofType) {
            mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
            VoicePolicyManage.getInstance().speak(mSunroofClosed);
        }


    }

    @Override
    public void onUp() {
        Log.i(TAG, "SunRoofEventListener onUp()");
        if (!canOpenSunroof()) {
            return;
        }
//        if (getSpeed() > SPEED_80)
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.can_not_use_sunroof_high_speed));
//            showSmartTip(mContext.getResources().getString(R.string.can_not_use_sunroof_high_speed));
//            return;
//        }
        mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT,VehicleState.SWITCH_CLOSE);
        VoicePolicyManage.getInstance().speak(mSunroofTilt);
    }

    @Override
    public void onMove(int roofType, int percent) {
        Log.i(TAG, "SunRoofEventListener onMove(" + roofType + ", " + percent + ")");
        if (!canOpenSunroof()) {
            return;
        }
        int currentPercent= (int) mCanBusManager.getVehicleInfo().getSunroofWindowPercent()+100;
        if (-10 == percent) {
            if (currentPercent==100){
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_close_already));
            }else{
                mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT, currentPercent-10);
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_close_little));
            }
        } else if (10 == percent) {
            if (currentPercent==200){
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_open_already));
            }else{
                mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT, currentPercent+10);
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_open_little));
            }

        } else if (50 == percent) {
            if (currentPercent==150){
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_half_already));
            }else {
                mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT, 150);
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_open_half));
            }

        }else if (-50 == percent) {
            if (currentPercent==150){
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_half_already));
            }else {
                mCanBusManager.setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_PERCENT, 150);
                VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.sunroof_close_half));
            }

        }


    }

    private boolean canOpenSunroof() {
//        if (!isCarSupportSunroof())
//        {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            showSmartTip(mParkModeClosed2);
//            return false;
//        }
//        if (mCanBusManager.getVehicleState(X37VehicleState.AUTO_SUNROOFE_SWITCH) == 1)
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.voice_control_sunroof_hint));
//            showSmartTip(mContext.getResources().getString(R.string.voice_control_sunroof_hint));
//            return false;
//        }
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
//            return false;
//        }
//        if (null != mCanBusManager)
//        {
//            int ret = mCanBusManager.getVehicleState(VehicleState.BCM_RCM_ERROR);
//            if (BAICVehicleState.ERROR == ret)
//            {
//                showFixSunroofTip();
//                return false;
//            }
//        }

        return true;
    }


//    private int getSpeed()
//    {
//        if (null != mCanBusManager)
//        {
//            return mCanBusManager.getVehicleSpeed();
//        }
//        return 0;
//    }

    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        return true;
    }


}
