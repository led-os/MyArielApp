package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.AirConditionerController;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.qinglink.api.md.CanBusManager;
import com.qinggan.qinglink.enumeration.AirConditionState;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;
import static com.qinggan.qinglink.enumeration.AirConditionState.LEVEL_1;
import static com.qinggan.qinglink.enumeration.AirConditionState.LEVEL_6;


/**
 * Created by Yorashe on 18-9-12.
 */

public class AirConditionerEventsListener implements AirConditionerController.AirConditionerEventsListener {

    private Context mContext;


    private static int mMaxTemp = 32;
    private static int mMinTemp = 16;
    private static int mMaxWind = 8;
    private static int mMinWind = 1;

    private String mAirConditionOpened;
    private String mAirConditionClosed;
    private String mAutoOpened;
    private String mAutoClosed;
    private String mTemperatureMaxAlready;
    private String mTemperatureMinAlready;
    private String mTemperatureUp;
    private String mTemperatureDown;
    private String mTemperatureSet;
    private String mTemperatureSetUnit;
    private String mWindMaxAlready;
    private String mWindMinAlready;
    private String mWindUp;
    private String mWindDown;
    private String mInnerCircleOpend;
    private String mOuterCircleOpend;
    private String mBlowHeadOpend;
    private String mBlowFootOpend;
    private String mBlowHeadFootOpend;
    private String mBlowFootWindowOpend;
    private String mFrostOpened;
    private CanBusManager sCanBusManager;


    private String mParkModeClosed2;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
            }
        }
    };

    public AirConditionerEventsListener(Context mContext,CanBusManager canBusManager) {
        this.mContext = mContext;
        this.sCanBusManager = canBusManager;
        if (null != mContext) {
            mAirConditionOpened = mContext.getResources().getString(R.string.air_condition_opened);
            mAirConditionClosed = mContext.getResources().getString(R.string.air_condition_closed);
            mAutoOpened = mContext.getResources().getString(R.string.auto_opened);
            mAutoClosed = mContext.getResources().getString(R.string.auto_closed);
            mWindMaxAlready = mContext.getResources().getString(R.string.wind_max_already);
            mWindMinAlready = mContext.getResources().getString(R.string.wind_min_already);
            mWindUp = mContext.getResources().getString(R.string.wind_upped);
            mWindDown = mContext.getResources().getString(R.string.wind_downed);
            mTemperatureMaxAlready = mContext.getResources().getString(R.string.temperature_max_already);
            mTemperatureMinAlready = mContext.getResources().getString(R.string.temperature_min_already);
            mTemperatureUp = mContext.getResources().getString(R.string.temperature_upped);
            mTemperatureDown = mContext.getResources().getString(R.string.temperature_downed);
            mTemperatureSet = mContext.getResources().getString(R.string.temperature_set);
            mTemperatureSetUnit = mContext.getResources().getString(R.string.temperature_set_unit);
            mInnerCircleOpend = mContext.getResources().getString(R.string.inner_circle_opened);
            mOuterCircleOpend = mContext.getResources().getString(R.string.outer_circle_opened);
            mBlowHeadOpend = mContext.getResources().getString(R.string.blow_head_opened);
            mBlowFootOpend = mContext.getResources().getString(R.string.blow_foot_opened);
            mBlowHeadFootOpend = mContext.getResources().getString(R.string.blow_head_foot_opened);
            mBlowFootWindowOpend = mContext.getResources().getString(R.string.blow_foot_window_opened);
            mFrostOpened = mContext.getResources().getString(R.string.frost_opened);
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
        }
    }


    @Override
    public void onOpen(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onOpen(" + classify + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        if (AirConditionerController.AirConditionerClassify.ALL_AIR_CONDITIONER == classify) {
            openAirCondition();
        } else if (AirConditionerController.AirConditionerClassify.AUTO_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_AUTO,AirConditionState.SWITCH_ON);
            VoicePolicyManage.getInstance().speak(mAutoOpened);
        } else if (AirConditionerController.AirConditionerClassify.COOL_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_AUTO,AirConditionState.SWITCH_ON);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.cool_opened));
        } else if (AirConditionerController.AirConditionerClassify.HEAT_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.hot_opened));
        } else if (AirConditionerController.AirConditionerClassify.COOL_WIND_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.cool_opened));
        } else if (AirConditionerController.AirConditionerClassify.HEAT_WIND_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.hot_opened));
        } else if (AirConditionerController.AirConditionerClassify.INNER_RECYCLE_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_RECIRC_AIR,AirConditionState.INTERNAL_LOOP);
            VoicePolicyManage.getInstance().speak(mInnerCircleOpend);
            showWindType(0);
        } else if (AirConditionerController.AirConditionerClassify.OUT_RECYCLE_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_RECIRC_AIR,AirConditionState.EXTERNAL_LOOP);

            VoicePolicyManage.getInstance().speak(mOuterCircleOpend);
            showWindType(1);
        }  else if (AirConditionerController.AirConditionerClassify.AIR_CONDITIONER_AC == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_SWITCH,AirConditionState.SWITCH_ON);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.ac_opened));
        } else {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        }
    }

    @Override
    public void onClose(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onClose(" + classify + ")");

        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        if (AirConditionerController.AirConditionerClassify.ALL_AIR_CONDITIONER == classify) {
//            sCanBusManager.setAirConditionState(AirConditionState.AC_POWER_SWITCH,AirConditionState.SWITCH_OFF);
//            VoicePolicyManage.getInstance().speak(mAirConditionClosed);
            closeAirCondition();
        } else if (AirConditionerController.AirConditionerClassify.AUTO_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_AUTO,AirConditionState.SWITCH_OFF);
            VoicePolicyManage.getInstance().speak(mAutoClosed);
        } else if (AirConditionerController.AirConditionerClassify.COOL_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mAirConditionClosed);
        } else if (AirConditionerController.AirConditionerClassify.HEAT_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mAirConditionClosed);
        } else if (AirConditionerController.AirConditionerClassify.COOL_WIND_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mAirConditionClosed);
        } else if (AirConditionerController.AirConditionerClassify.HEAT_WIND_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mAirConditionClosed);
        } else if (AirConditionerController.AirConditionerClassify.INNER_RECYCLE_MODE == classify) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.inner_circle_closed));
            sCanBusManager.setAirConditionState(AirConditionState.AC_RECIRC_AIR,AirConditionState.EXTERNAL_LOOP);
        } else if (AirConditionerController.AirConditionerClassify.OUT_RECYCLE_MODE == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_RECIRC_AIR,AirConditionState.INTERNAL_LOOP);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.outer_circle_closed));
        } else if (AirConditionerController.AirConditionerClassify.AIR_CONDITIONER_AC == classify) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_SWITCH,AirConditionState.SWITCH_OFF);
            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.ac_closeed));
        } else {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        }
    }

    @Override
    public void onAdjustTemp(int classify, int upValue) {
        Log.i(TAG, "AirConditionerEventsListener onAdjustTemp(" + classify + ", " + upValue + ")");

        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirConditionerEventsListener onAdjustTemp(" + classify + ", " + upValue + ")");
//        if (!canDoAirConditionControlImmediately()) {
//             VoicePolicyManage.getInstance().speak(mAirConditionOpened);
//            return;
//        }
        float curTemp = sCanBusManager.getAirCondition().getAirLeftTemperature();
        Log.i("TAG", "onAdjustTemp curTemp--->" + curTemp);
        if (upValue > 0) {
            if (mMaxTemp == curTemp)
            {
                 VoicePolicyManage.getInstance().speak(mTemperatureMaxAlready);
                return;
            }
//            if (upValue==1){
//                upValue++;
//            }
            int temp = (int) (curTemp + upValue);
            if (temp > mMaxTemp) temp = mMaxTemp;
             Log.i("TAG", "onAdjustTemp endTemp--->" + temp);
            sCanBusManager.setAirConditionState(AirConditionState.AC_LEFT_TEMP, temp);
            VoicePolicyManage.getInstance().speak(mTemperatureUp);
        } else {
            if (mMinTemp == curTemp)
            {
                 VoicePolicyManage.getInstance().speak(mTemperatureMinAlready);
                return;
            }
//            if (upValue==-1){
//                upValue--;
//            }
            int temp = (int) (curTemp + upValue);
            if (temp < mMinTemp) temp = mMinTemp;
            Log.i("TAG", "onAdjustTemp endTemp--->" + temp);
            sCanBusManager.setAirConditionState(AirConditionState.AC_LEFT_TEMP, temp);
             VoicePolicyManage.getInstance().speak(mTemperatureDown);
        }
    }

    @Override
    public void onSetTemp(int classify, int temp) {
        Log.i(TAG, "AirConditionerEventsListener onSetTemp(" + classify + ", " + temp + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
//        canDoAirConditionControlImmediately();
        if (temp < mMinTemp) temp = (int) mMinTemp;
        if (temp > mMaxTemp) temp = (int) mMaxTemp;
        sCanBusManager.setAirConditionState(AirConditionState.AC_LEFT_TEMP,temp);
        VoicePolicyManage.getInstance().speak(mTemperatureSet + temp + mTemperatureSetUnit);

        final int endTemp = temp;
    }

    @Override
    public void onMaxWindVolume(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onMaxWindVolume(" + classify + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        canDoAirConditionControlImmediately();
        float curSpeed = sCanBusManager.getAirCondition().getAirWindSpeedLevel();
        if (LEVEL_6 == curSpeed)
        {
             VoicePolicyManage.getInstance().speak(mWindMaxAlready);
            return;
        }
        sCanBusManager.setAirConditionState(AirConditionState.AC_BLOWER,LEVEL_6);
         VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.wind_max));
    }

    @Override
    public void onMinWindVolume(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onMinWindVolume(" + classify + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        canDoAirConditionControlImmediately();
        float curSpeed = sCanBusManager.getAirCondition().getAirWindSpeedLevel();
        if (AirConditionState.LEVEL_1 == curSpeed)
        {
             VoicePolicyManage.getInstance().speak(mWindMinAlready);
            return;
        }
        sCanBusManager.setAirConditionState(AirConditionState.AC_BLOWER,LEVEL_1);
         VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.wind_min));
    }

    @Override
    public void onAdjustWindVolume(int classify, int level) {
        Log.i(TAG, "AirConditionerEventsListener onAdjustWindVolume(" + classify + ", " + level + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirConditionerEventsListener onAdjustWindVolume(" + classify + ", " + level + ")");
//        if (!canDoAirConditionControlImmediately()) {
//             VoicePolicyManage.getInstance().speak(mAirConditionOpened);
//            showWindType(6);
//            return;
//        }
        int curSpeed = sCanBusManager.getAirCondition().getAirWindSpeedLevel();
//
        if (level > 0) {
            if (mMaxWind == curSpeed)
            {
                 VoicePolicyManage.getInstance().speak(mWindMaxAlready);
                return;
            }
            sCanBusManager.setAirConditionState(AirConditionState.AC_BLOWER,curSpeed+1);
            VoicePolicyManage.getInstance().speak(mWindUp);
        } else {
            if (AirConditionState.LEVEL_1 == curSpeed)
            {
                 VoicePolicyManage.getInstance().speak(mWindMinAlready);
                return;
            }
            sCanBusManager.setAirConditionState(AirConditionState.AC_BLOWER,curSpeed-1);
            VoicePolicyManage.getInstance().speak(mWindDown);
        }
    }

    @Override
    public void onSetWindVolume(int classify, int level) {
        Log.i(TAG, "AirConditionerEventsListener onSetWindVolume(" + classify + ", " + level + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        //                    canDoAirConditionControlImmediately();
        sCanBusManager.setAirConditionState(AirConditionState.AC_BLOWER,level);
        VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.wind_set) + level + mContext.getResources().getString(R.string.wind_level));

    }

    @Override
    public void onChangeWindDirection(int classify, int direction) {
        Log.i(TAG, "AirConditionerEventsListener onChangeWindDirection(" + classify + ", " + direction + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirConditionerEventsListener onChangeWindDirection(" + classify + ", " + direction + ")");
        if (AirConditionerController.AirWindDirection.FOOT_AND_HEAD_DIRECTION == direction) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE_LEG);
            VoicePolicyManage.getInstance().speak(mBlowHeadFootOpend);
        } else if (AirConditionerController.AirWindDirection.FOOT_DIRECTION == direction) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_LEG);
            VoicePolicyManage.getInstance().speak(mBlowFootOpend);
        } else if (AirConditionerController.AirWindDirection.HEAD_DIRECTION == direction) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_FACE);
            VoicePolicyManage.getInstance().speak(mBlowHeadOpend);
        } else if (AirConditionerController.AirWindDirection.WINDOW_DIRECTION == direction) {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_DEF);
            VoicePolicyManage.getInstance().speak(mFrostOpened);
        } else {
            sCanBusManager.setAirConditionState(AirConditionState.AC_FLOW_MODE,AirConditionState.FLOW_MODE_LEG_DEF);
            VoicePolicyManage.getInstance().speak(mBlowFootWindowOpend);
        }
    }

    @Override
    public void onMaxTemp(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onMaxTemp(" + classify + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirConditionerEventsListener onMaxTemp(" + classify + ")");

        canDoAirConditionControlImmediately();

        float curTemp = sCanBusManager.getAirCondition().getAirLeftTemperature();
        if (mMaxTemp == curTemp)
        {
             VoicePolicyManage.getInstance().speak(mTemperatureMaxAlready);
            return;
        }
        sCanBusManager.setAirConditionState(AirConditionState.AC_LEFT_TEMP,mMaxTemp);
        VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.tem_max));
    }

    @Override
    public void onMinTemp(int classify) {
        Log.i(TAG, "AirConditionerEventsListener onMinTemp(" + classify + ")");
        if (!VehicleUtils.isACCOn()) {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
            return;
        }
        Log.i(TAG, "AirConditionerEventsListener onMinTemp(" + classify + ")");
        canDoAirConditionControlImmediately();
        float curTemp = sCanBusManager.getAirCondition().getAirLeftTemperature();
        if (mMinTemp == curTemp)
        {
             VoicePolicyManage.getInstance().speak(mTemperatureMinAlready);
            return;
        }
        sCanBusManager.setAirConditionState(AirConditionState.AC_LEFT_TEMP,mMinTemp);
        VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.tem_min));
    }

    /**
     * 打开空调
     */
    private void openAirCondition() {
        Log.i(TAG, "openAirCondition()");
        sCanBusManager.setAirConditionState(AirConditionState.AC_POWER_SWITCH,AirConditionState.SWITCH_ON);
        VoicePolicyManage.getInstance().speak(mAirConditionOpened);
    }

    /**
     * 关闭空调
     */
    private void closeAirCondition() {
        Log.i(TAG, "closeAirCondition()");
        sCanBusManager.setAirConditionState(AirConditionState.AC_POWER_SWITCH,AirConditionState.SWITCH_OFF);
        VoicePolicyManage.getInstance().speak(mAirConditionClosed);

    }


    /**
     * @param type 0内循环，１外循环,2前窗除霜,3后窗除霜,,4打开auto,5关闭auto,6打开ac,7关闭ac
     *             8吹脸,9吹脸脚,10吹脚,11吹脚窗
     */
    private void showWindType(final int type) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {


                // FuncUtils.showWindType(mContext, type);
            }
        });


    }

    private boolean canDoAirConditionControlImmediately() {
        return true;
    }


}
