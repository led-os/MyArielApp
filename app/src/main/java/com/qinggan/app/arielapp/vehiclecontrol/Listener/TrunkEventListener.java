package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.TrunkController;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.qinglink.api.md.CanBusManager;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class TrunkEventListener implements TrunkController.TrunkEventListener
{

    private Context mContext;
    private View mTimerTipView;


    private String mParkModeClosed2;
    private String mTrunkLocked;
    private String mTrunkUnlocked;
    private CanBusManager mCanBusManager;

    public TrunkEventListener(Context mContext)
    {
        this.mContext = mContext;
        this.mCanBusManager= ArielApplication.getCanBusManager();
        if (null != mContext)
        {
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
            mTrunkLocked = mContext.getResources().getString(R.string.trunk_locked);
            mTrunkUnlocked = mContext.getResources().getString(R.string.trunk_unlocked);
        }

    }



    @Override
    public void onLock()
    {
        if (!canUseTrunk(false))
        {
            return;
        }
        Log.i(TAG, "TrunkEventListener onLock()");
             VoicePolicyManage.getInstance().speak(mTrunkLocked);

    }

    @Override
    public void onUnlock()
    {
        Log.i(TAG, "TrunkEventListener onUnlock()");
//        if (!canUseTrunk(true))
//        {
//            return;
//        }
        VehcleControlManager.getInstance(mContext).openTrunk(new RemoteResponseListener() {
            @Override
            public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                VoicePolicyManage.getInstance().speak(mTrunkUnlocked);

            }

            @Override
            public void onSendFailure(RestError restError) {

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
    public void onOpen()
    {
        Log.i(TAG, "TrunkEventListener onOpen()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onClose()
    {
        Log.i(TAG, "TrunkEventListener onClose()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }


    private static final int SPEED_0 = 0;

    /**
     * @param toOpen true unlock,false lock
     * @return
     */
    private boolean canUseTrunk(boolean toOpen)
    {
        if (!isCarSupportTrunk())
        {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            return false;
        }
        if (!isACCOn())
        {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return false;
        }



//        if (getSpeed() <= SPEED_0)
//        {
            return true;
//        }
//        else
//        {
//            if (toOpen)
//            {
//                 VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.can_not_unlock_trunk));
//            }
//            else
//            {
//                 VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.can_not_lock_trunk));
//            }
//        }
//        return false;
    }

    public boolean isACCOn()
    {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
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

    /*
     **是否支持电动尾门
     */
    private boolean isCarSupportTrunk()
    {
//        return VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.POWER_TRUNK) == 1;
        return true;

    }



}
