package com.qinggan.app.arielapp.vehiclecontrol.Listener;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.car.AtmosphereLightController;

import java.util.Random;

import static com.qinggan.app.arielapp.vehiclecontrol.VoiceVehicleControl.TAG;


/**
 * Created by Yorashe on 18-9-12.
 */

public class AtmosphereLightEventListener implements AtmosphereLightController.AtmosphereLightEventListener
{


    private Context mContext;

    private String mAmbientLampOpened;
    private String mParkModeClosed2;


    private String mAmbientLampRed;
    private String mAmbientLampPurple;
    private String mAmbientLampGolden;
    private String mAmbientLampOrange;
    private String mAmbientLampBlue;
    private String mAmbientLampWhite;
    private String mAmbientLampGreen;

    public AtmosphereLightEventListener(Context mContext)
    {
        this.mContext = mContext;
        if (null != mContext)
        {
            mAmbientLampOpened = mContext.getResources().getString(R.string.ambient_lamp_opened);
            mParkModeClosed2 = mContext.getResources().getString(R.string.smart_mode_stop_close2);

            mAmbientLampRed = mContext.getResources().getString(R.string.ambient_lamp_red);
            mAmbientLampPurple = mContext.getResources().getString(R.string.ambient_lamp_purple);
            mAmbientLampGolden = mContext.getResources().getString(R.string.ambient_lamp_golden);
            mAmbientLampOrange = mContext.getResources().getString(R.string.ambient_lamp_orange);
            mAmbientLampBlue = mContext.getResources().getString(R.string.ambient_lamp_blue);
            mAmbientLampWhite = mContext.getResources().getString(R.string.ambient_lamp_white);
            mAmbientLampGreen = mContext.getResources().getString(R.string.ambient_lamp_green);

        }

    }

    private Handler mHandler = new Handler(Looper.getMainLooper())
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 0)
            {
            }
        }
    };

    @Override
    public void onChangeSpecificColor(int color)
    {
        Log.i(TAG, "AtmosphereLightEventListener onChangeSpecificColor(" + color + ")");
//
//        if (!isCarSupportAtmosphere_Lamp())
//        {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            return;
//        }
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            return;
//        }
//        String str = null;
//        if (AtmosphereLightController.AtmosphereColor.RED == color)
//        {
//            str = mAmbientLampRed;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_VIOLET == color)
//        {
//            str = mAmbientLampPurple;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.GOLDEN == color)
//        {
//            str = mAmbientLampGolden;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ORANGE == color)
//        {
//            str = mAmbientLampOrange;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_BLUE == color)
//        {
//            str = mAmbientLampBlue;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.WHITE == color)
//        {
//            str = mAmbientLampWhite;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_GREEN == color)
//        {
//            str = mAmbientLampGreen;
//        }
//        if (null != str)
//        {
//             VoicePolicyManage.getInstance().speak(str);
//        }
    }

    @Override
    public void onSwitchColor()
    {
        Log.i(TAG, "AtmosphereLightEventListener onSwitchColor()");
//        if (!isCarSupportAtmosphere_Lamp())
//        {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            return;
//        }
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            return;
//        }
//        Random rand = new Random();
//        int color = rand.nextInt(7);
//        String str = null;
//        if (AtmosphereLightController.AtmosphereColor.RED == color)
//        {
//            str = mAmbientLampRed;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_VIOLET == color)
//        {
//            str = mAmbientLampPurple;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.GOLDEN == color)
//        {
//            str = mAmbientLampGolden;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ORANGE == color)
//        {
//            str = mAmbientLampOrange;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_BLUE == color)
//        {
//            str = mAmbientLampBlue;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.WHITE == color)
//        {
//            str = mAmbientLampWhite;
//        }
//        else if (AtmosphereLightController.AtmosphereColor.ICE_GREEN == color)
//        {
//            str = mAmbientLampGreen;
//        }
//        if (null != str)
//        {
//             VoicePolicyManage.getInstance().speak(str);
//        }

    }

    @Override
    public void onOpen()
    {
        Log.i(TAG, "AtmosphereLightEventListener onOpen()");
//        if (!isCarSupportAtmosphere_Lamp())
//        {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
//            return;
//        }
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            return;
//        }
//             VoicePolicyManage.getInstance().speak(mAmbientLampOpened);
    }

    @Override
    public void onClose()
    {
        Log.i(TAG, "AtmosphereLightEventListener onClose()");
//        if (!isCarSupportAtmosphere_Lamp())
//        {
             VoicePolicyManage.getInstance().speak(mParkModeClosed2);
            return;
//        }
//        if (!isACCOn())
//        {
//             VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.accoff_hint));
//            return;
//        }
//         VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.romantic_close_hint));
    }

    @Override
    public void onAdjustBrightness(int value)
    {
         VoicePolicyManage.getInstance().speak(mParkModeClosed2);
        Log.i(TAG, "AtmosphereLightEventListener onAdjustBrightness()");
    }



    /*
     **是否支持氛围灯（浪漫模式）
     */
    private boolean isCarSupportAtmosphere_Lamp()
    {
//        return VehicleConfigHelper.readOnlineConfigItem(VehicleOnlineState.ATMOSPHERE_LAMP) == 1;
        return true;
    }

    public boolean isACCOn()
    {
        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
            return true;
    }


}
