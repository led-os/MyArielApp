package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.DoorController;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class DoorEventListener implements DoorController.DoorEventListener {

    private Context mContext;
    private String mParkModeClosed2;

    public DoorEventListener(Context mContext) {
        this.mContext = mContext;
        if (null != mContext) {
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        }
    }

    @Override
    public void onLock(int classify) {
        Log.i(TAG, "DoorEventListener onLock(" + classify + ")");
//         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        VehcleControlManager.getInstance(mContext).vehiclelocking( new RemoteResponseListener() {
            @Override
            public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                VoicePolicyManage.getInstance().speak("车门已上锁");

            }

            @Override
            public void onSendFailure(RestError restError) {
                VoicePolicyManage.getInstance().speak("车门上锁失败");

            }

            @Override
            public void onCmdResult(VhlCtlResult result, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {

            }
        });
    }

    @Override
    public void onUnlock(int classify) {
        Log.i(TAG, "DoorEventListener onUnlock(" + classify + ")");
//         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        VehcleControlManager.getInstance(mContext).vehicleUnlocking( new RemoteResponseListener() {
            @Override
            public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                VoicePolicyManage.getInstance().speak("车门已解锁");

            }

            @Override
            public void onSendFailure(RestError restError) {
                VoicePolicyManage.getInstance().speak("车门解锁失败");

            }

            @Override
            public void onCmdResult(VhlCtlResult result, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {

            }
        });
    }

    @Override
    public void onOpen() {
        Log.i(TAG, "DoorEventListener onOpen()");
//         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        VehcleControlManager.getInstance(mContext).vehicleUnlocking( new RemoteResponseListener() {
            @Override
            public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                VoicePolicyManage.getInstance().speak("车门已解锁");

            }

            @Override
            public void onSendFailure(RestError restError) {
                VoicePolicyManage.getInstance().speak("车门解锁失败");

            }

            @Override
            public void onCmdResult(VhlCtlResult result, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {

            }
        });
    }

    @Override
    public void onClose() {
        Log.i(TAG, "DoorEventListener onClose()");
//         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        VehcleControlManager.getInstance(mContext).vehiclelocking( new RemoteResponseListener() {
            @Override
            public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                VoicePolicyManage.getInstance().speak("车门已上锁");

            }

            @Override
            public void onSendFailure(RestError restError) {
                VoicePolicyManage.getInstance().speak("车门上锁失败");

            }

            @Override
            public void onCmdResult(VhlCtlResult result, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {

            }
        });
    }

}
