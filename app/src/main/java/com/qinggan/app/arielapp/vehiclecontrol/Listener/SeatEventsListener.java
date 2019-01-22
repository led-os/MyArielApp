package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.SeatController;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;

/**
 * Created by Yorashe on 18-9-12.
 */

public class SeatEventsListener implements SeatController.SeatEventsListener {

    private Context mContext;
    private String mParkModeClosed2;
    private String mSeatHeatOpend;
    private String mSeatHeatClosed;

    public SeatEventsListener(Context mContext) {
        this.mContext = mContext;
        mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        mSeatHeatOpend = mContext.getResources().getString(R.string.seatheat_opened);
        mSeatHeatClosed = mContext.getResources().getString(R.string.seatheat_closed);
    }


    @Override
    public void onOpen(int seat) {
        Log.i(TAG, "SeatEventsListener onOpen(" + seat + ")");
        if (!isCarSupportSeat_Heating()) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            return;
        }
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
         VoicePolicyManage.getInstance().speak(mSeatHeatOpend);
        Log.i(TAG, "SeatEventsListener onOpen(" + seat + ")");
    }

    @Override
    public void onClose(int seat) {
        Log.i(TAG, "SeatEventsListener onClose(" + seat + ")");
        if (!isCarSupportSeat_Heating()) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            return;
        }
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "SeatEventsListener onClose(" + seat + ")");
         VoicePolicyManage.getInstance().speak(mSeatHeatClosed);
    }

    @Override
    public void onLowTempHeat(int seat) {
        Log.i(TAG, "SeatEventsListener onLowTempHeat(" + seat + ")");
        if (!isCarSupportSeat_Heating()) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            return;
        }
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
         VoicePolicyManage.getInstance().speak(mSeatHeatOpend);
    }

    @Override
    public void onHighTempHeat(int seat) {
        Log.i(TAG, "SeatEventsListener onHighTempHeat(" + seat + ")");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onMediumTempHeat(int i) {
        Log.i(TAG, "SeatEventsListener onMediumTempHeat()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onTempIncHeat(int i) {
        Log.i(TAG, "SeatEventsListener onTempIncHeat()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onTempDecHeat(int i) {
        Log.i(TAG, "SeatEventsListener onTempDecHeat()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }


    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        return true;

    }


    public boolean isCarSupportSeat_Heating() {
        return true;
    }
}
