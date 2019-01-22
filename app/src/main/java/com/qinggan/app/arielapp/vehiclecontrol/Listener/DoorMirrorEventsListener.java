package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.MirrorController;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class DoorMirrorEventsListener implements MirrorController.DoorMirrorEventsListener {

    private String mParkModeClosed2;
    private Context mContext;

    public DoorMirrorEventsListener(Context mContext) {
        this.mContext = mContext;
        if (null != mContext) {
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        }
    }


    @Override
    public void onSpread(int classify) {
        Log.i(TAG, "DoorMirrorEventsListener onSpread(" + classify + ")");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onFold(int classify) {
        Log.i(TAG, "DoorMirrorEventsListener onFold(" + classify + ")");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

}
