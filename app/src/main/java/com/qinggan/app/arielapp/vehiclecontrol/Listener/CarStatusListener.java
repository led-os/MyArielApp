package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.CarStatusController;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class CarStatusListener implements CarStatusController.CarStatusListener {


    private Context mContext;
    private String mQueryFailText;

    private String mParkModeClosed2;

    private String mContinueMilesRessure;

    public CarStatusListener(Context mContext) {
        this.mContext = mContext;
        if (null != mContext) {
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
            mQueryFailText = mContext.getResources().getString(R.string.query_fail);
            mContinueMilesRessure = mContext.getResources().getString(R.string.continue_miles_ressure);
        }

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
            }
        }
    };


    @Override
    public void onCarInspect() {
        Log.i(TAG, "CarStatusListener onCarInspect()");
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
    }

    @Override
    public void onQuery(int classify) {
        Log.i(TAG, "CarStatusListener onQuery(" + classify + ")");
        String mQueryText = null;
        //胎压检测
        if (classify == CarStatusController.CarStatusClassify.TYPE_PRESSURE) {
//                TPMSInfo mTpmsInfo = mCanBusManager.getTPMSInfo();
//                String tireStr = "";
//                if (mTpmsInfo.mLeftFrontTireWarningStatus != 0)
//                {
//                    tireStr = mContext.getResources().getString(R.string.left_front);
//                }
//                if (mTpmsInfo.mRightFrontTireWarningStatus != 0)
//                {
//                    tireStr = mContext.getResources().getString(R.string.right_front);
//                }
//                if (mTpmsInfo.mLeftRearTireWarningStatus != 0)
//                {
//                    tireStr = mContext.getResources().getString(R.string.left_back);
//                }
//                if (mTpmsInfo.mRightRearTireWarningStatus != 0)
//                {
//                    tireStr = mContext.getResources().getString(R.string.right_back);
//                }
//                if (tireStr.length() == 0)
//                {
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.tire_warning_status));
//                }
//                else
//                {
//                     VoicePolicyManage.getInstance().speak(String.format(mContext.getResources().getString(R.string.tire_warning_status_err), tireStr));
//                    showSmartTip(String.format(mContext.getResources().getString(R.string.tire_warning_status_err), tireStr));
//                }
        } else if (classify == CarStatusController.CarStatusClassify.MAINTAIN_TIME) {
            //保养
             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.no_access_to_maintenance_mileage));
            return;
        } else if (classify == CarStatusController.CarStatusClassify.AFC) {
//            //平均油耗
//            if (VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.VEHICLE_TYPE) == 0)
//            {
//                FuelLevel fuelLevel = mCanBusManager.getFuelLevelValue();
//                if (fuelLevel != null && fuelLevel.mAvgFuelConsumption >= 0)
//                {
//                    mQueryText = String.format(mContext.getResources().getString(R.string.oil_consumption_ressure), fuelLevel.mAvgFuelConsumption +
//                            "");
//                }
//                else
//                {
//                    mQueryText = mContext.getResources().getString(R.string.oil_consumption) + mQueryFailText;
//                }
//            }
//            else
//            {
            mQueryText = mContext.getResources().getString(R.string.not_oil_condition_pm);

//            }
        } else if (classify == CarStatusController.CarStatusClassify.APC) {
//            //平均电耗
//            if (VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.VEHICLE_TYPE) == 1)
//            {
//                PowerLevel powerLevel = mCanBusManager.getPowerLevelValue();
//                if (null != powerLevel && powerLevel.mInstantaneousPowerConsumption >= 0)
//                {
//                    mQueryText = String.format(mContext.getResources().getString(R.string.power_consumption_ressure), powerLevel
//                            .mInstantaneousPowerConsumption + "");
//                }
//                else
//                {
//                    mQueryText = mContext.getResources().getString(R.string.power_consumption) + mQueryFailText;
//
//                }
//            }
//            else
//            {
            mQueryText = mContext.getResources().getString(R.string.not_power_condition_pm);

//            }

        } else {
            if (!isACCOn()) {
                 VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.oil_quantity_hint));
                return;
            }
            if (classify == CarStatusController.CarStatusClassify.RANGE) {
//                Odometer odometer = mCanBusManager.getOdometer();
//                if (null != odometer)
//                {
//                    mQueryText = getContinueMiles();
//                }
//                else
//                {
//                    mQueryText = mContext.getResources().getString(R.string.extension_mileage) + mQueryFailText;
//                }

            } else if (classify == CarStatusController.CarStatusClassify.MILEAGE) {
                //总里程
//                if (getTotalMiles() < 0)
//                {
//                    mQueryText = mContext.getResources().getString(R.string.total_mileage) + mQueryFailText;
//                }
//                else
//                {
//                    mQueryText = String.format(mContext.getResources().getString(R.string.total_miles_ressure), getTotalMiles() + "");
//                }
            } else if (classify == CarStatusController.CarStatusClassify.OIL) {
//                FuelLevel fuelLevel = mCanBusManager.getFuelLevelValue();
//                int percent = (int) (fuelLevel.getPercentage() * 100);
                String mOilText;
//                String mContinueMilesText;
//                if (percent < 0)
//                {
//                    mOilText = mContext.getResources().getString(R.string.oil_quantity) + mQueryFailText;
//                }
//                else
//                {
//                    mOilText = String.format(mContext.getResources().getString(R.string.oil_remain_ressure), percent + "");
//                }
//                Odometer odometer = mCanBusManager.getOdometer();
//                int oilMil = odometer.getCanTravelMileage();
//                if (oilMil < 0)
//                {
//                    mContinueMilesText = mContext.getResources().getString(R.string.extension_mileage) + mQueryFailText;
//                }
//                else
//                {
//                    mContinueMilesText = String.format(mContext.getResources().getString(R.string.continue_miles_ressurtwo), oilMil + "");
//                }

//                mQueryText = mOilText + "," + mContinueMilesText;
            } else if (classify == CarStatusController.CarStatusClassify.BATTERY_POWER) {
//                if (VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.VEHICLE_TYPE) == 1)
//                {
//                    String mPowerLevelText;
//                    String mContinueMilesText;
//                    PowerLevel powerLevel = mCanBusManager.getPowerLevelValue();
//                    if (powerLevel.getPercentage() < 0)
//                    {
//                        mPowerLevelText = mContext.getResources().getString(R.string.electric_quantity) + mQueryFailText;
//                    }
//                    else
//                    {
//                        mPowerLevelText = String.format(mContext.getResources().getString(R.string.power_remain_ressure), powerLevel.getPercentage
//                                () * 100 + "");
//                    }
//                    Odometer odometer = mCanBusManager.getOdometer();
//                    int oilMil = odometer.getCanTravelMileage();
//                    if (oilMil < 0)
//                    {
//                        mContinueMilesText = mContext.getResources().getString(R.string.extension_mileage) + mQueryFailText;
//                    }
//                    else
//                    {
//                        mContinueMilesText = String.format(mContext.getResources().getString(R.string.continue_miles_ressurtwo), oilMil + "");
//                    }
//                    mQueryText = mPowerLevelText + "," + mContinueMilesText;
//                }
//                else
//                {
                mQueryText = mContext.getResources().getString(R.string.not_power_condition_pm_two);
//                }


            } else {
                mQueryText = mParkModeClosed2;
            }

        }
        if (!TextUtils.isEmpty(mQueryText)) {
             VoicePolicyManage.getInstance().speak(mQueryText);
        }

    }


    public boolean isACCOn() {
        return true;
    }

    /**
     * 续航里程
     *
     * @return
     */
//    public String getContinueMiles()
//    {
//        if (null != mCanBusManager)
//        {
//            Odometer odometer = mCanBusManager.getOdometer();
//            if (null != odometer)
//            {
//                int oilMil = odometer.getCanTravelMileage();
//                if (oilMil < 0)
//                {
//                    return mContext.getResources().getString(R.string.oil_quantity) + mQueryFailText;
//                }
//                return VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.VEHICLE_TYPE) == 1 ? String.format(mContinueMilesRessure,
//                        mContext.getResources().getString(R.string.electric_quantity), odometer.getmEVCruisingRange()) : String.format
//                        (mContinueMilesRessure, mContext.getResources().getString(R.string.oil_quantity), oilMil);
//            }
//        }
//        return mContext.getResources().getString(R.string.extension_mileage) + mQueryFailText;
//    }
//
//    public int getTotalMiles()
//    {
//        if (null != mCanBusManager)
//        {
//            Odometer odometer = mCanBusManager.getOdometer();
//            if (null != odometer)
//            {
//                return odometer.getBeenTravelingMileage();
//            }
//        }
//        return 0;
//    }


}


