package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.LightController;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;

/**
 * Created by Yorashe on 18-9-12.
 */

public class LightEventListener implements LightController.LightEventListener {

    private Context mContext;
    private String mAmbientLampOpened;
    private String mParkModeClosed2;
    private String mFrogLampOpened;
    private String mFrogLampClosed;
    private String mHighBeamOpened;
    private String mHighBeamClosed;
    private String mLowBeamOpened;
    private String mLowBeamClosed;

    public LightEventListener(Context mContext) {
        this.mContext = mContext;
        if (null != mContext) {
            mAmbientLampOpened = mContext.getResources().getString(R.string.ambient_lamp_opened);
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);
            mFrogLampOpened = mContext.getResources().getString(R.string.frog_lamp_opened);
            mFrogLampClosed = mContext.getResources().getString(R.string.frog_lamp_closed);
            mHighBeamOpened = mContext.getResources().getString(R.string.high_beam_opened);
            mHighBeamClosed = mContext.getResources().getString(R.string.high_beam_closed);
            mLowBeamOpened = mContext.getResources().getString(R.string.low_beam_opened);
            mLowBeamClosed = mContext.getResources().getString(R.string.low_beam_closed);
        }

    }


    @Override
    public void onOpen(int i) {
        Log.i(TAG, "LightEventListener onOpen(" + i + ")");
//        if (i == LightController.LightClassify.AMBIENT_LAMP) {
//            if (isCarSupportAtmosphere_Lamp()) {
//                 VoicePolicyManage.getInstance().speak(mAmbientLampOpened);
//            } else {
//                 VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            }
//
//        } else if (i == LightController.LightClassify.FROG_LAMP) {
//             VoicePolicyManage.getInstance().speak(mFrogLampOpened);
//        } else if (i == LightController.LightClassify.HIGH_BEAM) {
//             VoicePolicyManage.getInstance().speak(mHighBeamOpened);
//        } else if (i == LightController.LightClassify.LOW_BEAM) {
//             VoicePolicyManage.getInstance().speak(mLowBeamOpened);
//        } else if (i == LightController.LightClassify.LEFT_DIRECTION_INDICATOR) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.RIGHT_DIRECTION_INDICATOR) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.HAZARD_WARNING_LAMP) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.REVERSING_LAMP) {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.position_beam_opened));
//        } else if (i == LightController.LightClassify.DEFAULT) {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.little_beam_opened));
//        } else if (i == LightController.LightClassify.FORE_FORG_LAMP) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.BACK_FORG_LAMP) {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        }
    }

    @Override
    public void onClose(int i) {
        Log.i(TAG, "LightEventListener onClose(" + i + ")");
//        if (i == LightController.LightClassify.AMBIENT_LAMP) {
//            if (!isCarSupportAtmosphere_Lamp()) {
                 VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            }
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.romantic_close_hint));
//        } else if (i == LightController.LightClassify.FROG_LAMP) {
//             VoicePolicyManage.getInstance().speak(mFrogLampClosed);
//        } else if (i == LightController.LightClassify.HIGH_BEAM) {
//             VoicePolicyManage.getInstance().speak(mHighBeamClosed);
//        } else if (i == LightController.LightClassify.LOW_BEAM) {
//             VoicePolicyManage.getInstance().speak(mLowBeamClosed);
//        } else if (i == LightController.LightClassify.LEFT_DIRECTION_INDICATOR) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.RIGHT_DIRECTION_INDICATOR) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.HAZARD_WARNING_LAMP) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.REVERSING_LAMP) {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.position_beam_closed));
//        } else if (i == LightController.LightClassify.DEFAULT) {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.little_beam_closed));
//        } else if (i == LightController.LightClassify.FORE_FORG_LAMP) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        } else if (i == LightController.LightClassify.BACK_FORG_LAMP) {
//             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//        }
    }


    /*
     **是否支持氛围灯（浪漫模式）
     */
    private boolean isCarSupportAtmosphere_Lamp() {
//        return VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.ATMOSPHERE_LAMP) == 1;
        return true;
    }

}
