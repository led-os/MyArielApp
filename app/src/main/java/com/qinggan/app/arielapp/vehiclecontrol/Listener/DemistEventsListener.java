package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.DemistController;
import com.qinggan.app.voiceapi.control.car.FrostController;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.AirConditionState;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class DemistEventsListener implements DemistController.DemistEventsListener {

    private Context mContext;
    private String mDemistOpened;
    private String mDemistClosed;
    private String mParkModeClosed2;
    private CanBusManager sCanBusManager;

    public DemistEventsListener(Context mContext) {
        this.mContext = mContext;
        mDemistOpened = mContext.getString(R.string.demist_opened);
        mDemistClosed = mContext.getString(R.string.demist_closed);
        mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        sCanBusManager= ArielApplication.getCanBusManager();

    }

    @Override
    public void onOpen(int window) {
        Log.i(TAG, "DemistEventsListener onOpen(" + window + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "DemistEventsListener onOpen(" + window + ")");
        if (DemistController.DemistClassify.WIND_SHIELD == window) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_DEF);
            VoicePolicyManage.getInstance().speak(mDemistOpened);

        } else if (DemistController.DemistClassify.BACK_WINDOW == window) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_REAR_DEFROST_SWITCH,AirConditionState.SWITCH_ON);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window_opened));

        } else if (window == FrostController.FrostTarget.ALL_WINDOWS) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_DEF);
            VoicePolicyManage.getInstance().speak(mDemistOpened);

        }

    }

    @Override
    public void onClose(int window) {
        Log.i(TAG, "DemistEventsListener onClose(" + window + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "DemistEventsListener onClose()");
        if (window == FrostController.FrostTarget.BACK_WINDOW) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_REAR_DEFROST_SWITCH,AirConditionState.SWITCH_OFF);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window_closed));
        } else if (window == FrostController.FrostTarget.ALL_WINDOWS) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE);
            VoicePolicyManage.getInstance().speak(mDemistClosed);
        } else {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE);
            VoicePolicyManage.getInstance().speak(mDemistClosed);

        }
    }

    @Override
    public void onClean(int window) {
        Log.i(TAG, "DemistEventsListener onClean(" + window + ")");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    public boolean isACCOn() {
        return true;
    }

//
//    private void setFrontDefroset(boolean isOPen){
//        int front = mAirConditionPresenter.getAirCondition().getAirFrontWindowDefogger();
//        if (front!=AirConditionState.SWITCH_ON == isOPen){
//            mCanBusManager.setAirConditionStateEx(AirConditionState.AC_FRONT_DEFROST_SWITCH, AirConditionState.SWITCH_ON);
//        }
//    }


}
