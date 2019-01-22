package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.AirCleanerController;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class AirCleanerEventsListener implements AirCleanerController.AirCleanerEventsListener {
    private Context mContext;

    private String mParkModeClosed2;
    private String mAirCleanOpend;
    private String mAirCleanClosed;
    private String mWindMaxAlready;
    private String mWindMinAlready;
    private String mAirConditionOpened;
    private String mWindUp;
    private String mWindDown;


    private int mMaxWind = 8;
    private int mMinWind = 1;

    public AirCleanerEventsListener(Context mContext) {
        this.mContext = mContext;
        if (null != mContext) {

            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
            mAirCleanOpend = mContext.getResources().getString(R.string.air_clean_opened);
            mAirCleanClosed = mContext.getResources().getString(R.string.air_clean_closed);
            mWindMaxAlready = mContext.getResources().getString(R.string.wind_max_already);
            mWindMinAlready = mContext.getResources().getString(R.string.wind_min_already);
            mAirConditionOpened = mContext.getResources().getString(R.string.air_condition_opened);
            mWindUp = mContext.getResources().getString(R.string.wind_upped);
            mWindDown = mContext.getResources().getString(R.string.wind_downed);
        }
    }

    @Override
    public void onOpen() {
        Log.i(TAG, "AirCleanerEventsListener onOpen()");
        if (!isCarSupportPM25()) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            showSmartTip(mParkModeClosed2);
            return;
        }

        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirCleanerEventsListener onOpen()");
//        mHandler.postDelayed(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                mCanBusManager.setAirConditionStateEx(AirConditionState.AC_AIR_CLEANER, AirConditionState.SWITCH_ON);
//            }
//        }, 100);
         VoicePolicyManage.getInstance().speak(mAirCleanOpend);
        showSmartTip(mAirCleanOpend);
    }

    @Override
    public void onClose() {
        Log.i(TAG, "AirCleanerEventsListener onClose()");
        if (!isCarSupportPM25()) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            showSmartTip(mParkModeClosed2);
            return;
        }
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirCleanerEventsListener onClose()");
//        int ret = mCanBusManager.setAirConditionStateEx(AirConditionState.AC_AIR_CLEANER, AirConditionState.SWITCH_OFF);
         VoicePolicyManage.getInstance().speak(mAirCleanClosed);
        showSmartTip(mAirCleanClosed);
    }


    @Override
    public void onMaxWindVolume() {
        Log.i(TAG, "AirCleanerEventsListener onMaxWindVolume()");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
//        float curSpeed = mAirConditionPresenter.getAirCondition().getAirWindSpeed();
//        if (mMaxWind == curSpeed)
//        {
//             VoicePolicyManage.getInstance().speak(mWindMaxAlready);
//            showSmartTip(mWindMaxAlready);
//            return;
//        }
         VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.wind_max));
        showWindspeed(mMaxWind);
    }

    @Override
    public void onMinWindVolume() {
        Log.i(TAG, "AirCleanerEventsListener onMinWindVolume()");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }

         VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.wind_min));
        showWindspeed(1);
    }

    @Override
    public void onAdjustWindVolume(int level) {
        Log.i(TAG, "AirCleanerEventsListener onAdjustWindVolume(" + level + ")");
        if (!isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            showSmartTip(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
         VoicePolicyManage.getInstance().speak(mAirConditionOpened);
        showWindType(6);
//        float curSpeed = mAirConditionPresenter.getAirCondition().getAirWindSpeed();

        if (level > 0) {
             VoicePolicyManage.getInstance().speak(mWindUp);
        } else {
             VoicePolicyManage.getInstance().speak(mWindDown);
        }
    }


    /*
     **是否支持pm2.5(雾霾模式,抽烟模式）
     */
    private boolean isCarSupportPM25() {
//        return VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.PM25) == 1;
        return true;

    }

    public boolean isACCOn() {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
//        if (CarApplication.DEBUG)
//        {
        return true;
//        }
//        return null != mCanBusManager && 2 == mCanBusManager.getAccStatus();
    }


    private void showSmartTip(final String tipStr) {
//        if (null != timer)
//        {
//            timer.cancel();
//        }
//        FuncUtils.showSmartTip(mContext, mHandler, tipStr);

    }


    private boolean isAirConditionOn() {
        return true;
    }


    private void showWindspeed(final int mWindSpeed) {
//        FuncUtils.showWindspeed(mContext, mHandler, mWindSpeed);
    }


    /**
     * @param type 0内循环，１外循环,2前窗除霜,3后窗除霜,,4打开auto,5关闭auto,6打开ac,7关闭ac
     *             8吹脸,9吹脸脚,10吹脚,11吹脚窗
     */
    private void showWindType(final int type) {
//        mHandler.post(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                FuncUtils.showWindType(mContext, type);
//            }
//
//        });


    }
}
