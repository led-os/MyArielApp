package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.WindowController;
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

public class WindowEventListener implements WindowController.WindowEventListener {

    private String mParkModeClosed2;
    private Context mContext;
    private String mWindowClosed;
    private String mWindowOpened;
    private CanBusManager mCanBusManager;

    public WindowEventListener(Context mContext) {
        this.mContext = mContext;
        this.mCanBusManager= ArielApplication.getCanBusManager();
        if (null != mContext) {
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
            mWindowClosed = mContext.getResources().getString(R.string.window_closed);
            mWindowOpened = mContext.getResources().getString(R.string.window_opened);
        }
    }


    @Override
    public void onOpen(int i) {
        if (!canOpenWindows()) {
            return;
        }

        Log.i(TAG, "WindowEventListener onOpen(" + i + ")");
        if (i == WindowController.WindowClassify.ALL_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
//            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
//            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
//            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mWindowOpened);
        } else if (i == WindowController.WindowClassify.LEFT_FORE_WINDOW) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left_front) + mContext.getResources().getString(R.string.window_down));
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
        } else if (i == WindowController.WindowClassify.BACK_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.LEFT_BACK_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left_back) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.LEFT_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.RIGHT_BACK_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right_back) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.RIGHT_FORE_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right_front) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.RIGHT_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right) + mContext.getResources().getString(R.string.window_down));
        } else if (i == WindowController.WindowClassify.FORE_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.front) + mContext.getResources().getString(R.string.window_down));
        }
    }

    @Override
    public void onClose(int i) {
        Log.i(TAG, "WindowEventListener onClose(" + i + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        if (i == WindowController.WindowClassify.ALL_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
//            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
//            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
//            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_CLOSE);
             VoicePolicyManage.getInstance().speak(mWindowClosed);
        } else if (i == WindowController.WindowClassify.LEFT_FORE_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left_front) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.BACK_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.back_window) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.LEFT_BACK_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left_back) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.LEFT_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.left) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.RIGHT_BACK_WINDOW) {
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right_back) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.RIGHT_FORE_WINDOW) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right_front) + mContext.getResources().getString(R.string.window_up));
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
        } else if (i == WindowController.WindowClassify.RIGHT_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.right) + mContext.getResources().getString(R.string.window_up));
        } else if (i == WindowController.WindowClassify.FORE_WINDOWS) {
            mCanBusManager.setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
            mCanBusManager.setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH,VehicleState.SWITCH_OPEN);
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.front) + mContext.getResources().getString(R.string.window_up));
        }
    }

    @Override
    public void onMove(int window, int percent) {
        Log.i(TAG, "WindowEventListener onMove(" + window + "," + percent + ")");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }


    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        return true;
    }

    private static final int SPEED_80 = 80;

    private boolean canOpenWindows() {
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
//            return false;
//        }
//        if (getSpeed() <= SPEED_80)
//        {
        return true;
//        }
//        else
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.can_not_use_window_high_speed));
//            showSmartTip(mContext.getResources().getString(R.string.can_not_use_window_high_speed));
//        }
//        return false;
    }

//    private int getSpeed()
//    {
//        if (null != mCanBusManager)
//        {
//            return mCanBusManager.getVehicleSpeed();
//        }
//        return 0;
//    }
}
