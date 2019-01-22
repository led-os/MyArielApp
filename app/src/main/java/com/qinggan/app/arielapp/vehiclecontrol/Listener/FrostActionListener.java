package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.FrostController;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.AirConditionState;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class FrostActionListener implements FrostController.FrostActionListener {

    private Context mContext;
    private String mFrostOpened;
    private String mFrostClosed;
    private CanBusManager sCanBusManager;

    public FrostActionListener(Context mContext) {
        this.mContext = mContext;
        mFrostOpened = mContext.getResources().getString(R.string.frost_opened);
        mFrostClosed = mContext.getResources().getString(R.string.frost_closed);
        sCanBusManager= ArielApplication.getCanBusManager();
    }

    @Override
    public void onClean(int window) {
        Log.i(TAG, "FrostController onOpen(" + window + ")");
        Log.i(TAG, "FrostController onOpen(" + window + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }

        if (window == FrostController.FrostTarget.BACK_WINDOW) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_REAR_DEFROST_SWITCH,AirConditionState.SWITCH_ON);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window_opened));

        } else if (window == FrostController.FrostTarget.ALL_WINDOWS) {
             VoicePolicyManage.getInstance().speak(mFrostOpened);
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_DEF);
        } else {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_DEF);
            VoicePolicyManage.getInstance().speak(mFrostOpened);

        }

    }

    @Override
    public void onClose(int window) {
        Log.i(TAG, "FrostController onClose(" + window + ")");
        Log.i(TAG, "DemistEventsListener onClose()");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        if (window == FrostController.FrostTarget.BACK_WINDOW) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_REAR_DEFROST_SWITCH,AirConditionState.SWITCH_OFF);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window_closed));
        } else if (window == FrostController.FrostTarget.ALL_WINDOWS) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE);
             VoicePolicyManage.getInstance().speak(mFrostClosed);
        } else {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE);
            VoicePolicyManage.getInstance().speak(mFrostClosed);

        }
        //ret = mCanBusManager.setAirConditionStateEx(AirConditionState.AC_REAR_DEFROST_SWITCH, AirConditionState.SWITCH_OFF);


    }

    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        return true;

    }


}
