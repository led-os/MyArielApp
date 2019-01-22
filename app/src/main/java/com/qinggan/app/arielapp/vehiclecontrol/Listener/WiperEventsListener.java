package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.WiperController;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.VehicleState;

import static com.qinggan.app.arielapp.utils.VehicleUtils.isACCOn;
import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class WiperEventsListener implements WiperController.WiperEventsListener {

    private Context mContext;
    private String mWipeOpened;
    private String mWipeClosed;
    private CanBusManager sCanBusManager;

    public WiperEventsListener(Context mContext,CanBusManager sCanBusManager) {
        this.mContext = mContext;
        this.sCanBusManager = sCanBusManager;
        mWipeOpened = mContext.getString(R.string.wipe_opened);
        mWipeClosed = mContext.getString(R.string.wipe_closed);


    }

    @Override
    public void onOpen(int type) {
        Log.i(TAG, "WiperEventsListener onOpen(" + type + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.LOW_SPEED);
         VoicePolicyManage.getInstance().speak(mWipeOpened);

    }

    @Override
    public void onClose(int type) {
        Log.i(TAG, "WiperEventsListener onClose(" + type + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.OFF);
        VoicePolicyManage.getInstance().speak(mWipeClosed);

    }

    @Override
    public void onQuick(int type) {
        Log.i(TAG, "WiperEventsListener onQuick(" + type + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.FAST_SPEED);
        VoicePolicyManage.getInstance().speak(mContext.getString(R.string.wipe_speed_up));
    }

    @Override
    public void onSlow(int type) {
        Log.i(TAG, "WiperEventsListener onSlow(" + type + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.LOW_SPEED);
        VoicePolicyManage.getInstance().speak(mContext.getString(R.string.wipe_speed_down));

    }

    @Override
    public void onFreshLevel(int type, int mode) {
        Log.i(TAG, "WiperEventsListener onFreshLevel(" + type + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.FAST_SPEED);
        VoicePolicyManage.getInstance().speak(mContext.getString(R.string.wipe_speed_up));

    }

    @Override
    public void onClean(int type) {
        Log.i(TAG, "WiperEventsListener onClean()");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getString(R.string.accoff_hint));
            return;
        }
//        if (type == WiperController.WiperClassify.BACK_WIPER) {
//        } else {
//        }
        sCanBusManager.setVehicleState(VehicleState.FRONT_WASHER_WIPER,VehicleState.LOW_SPEED);
        VoicePolicyManage.getInstance().speak(mWipeOpened);


    }



}
