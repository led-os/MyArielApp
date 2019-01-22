package com.qinggan.app.arielapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.qinggan.app.arielapp.ArielApplication;

/**
 * Created by Yorashe on 18-11-19.
 */

public class VehicleUtils {

    public static boolean isACCOn() {

        //ACC 0FF = 0
        //ACC =1
        //ACC ON = 2
        if (null !=   ArielApplication.getCanBusManager()
                &&null != ArielApplication.getCanBusManager().getVehicleInfo()
                && 2 ==  ArielApplication.getCanBusManager().getVehicleInfo().getAccStatus())
        {
        return true;
        }
        return false;
    }

    /**
     * 获取手机的IMEI
     *
     * @return
     */
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) ArielApplication.getApp()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(ArielApplication.getApp(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (null == tm || TextUtils.isEmpty(tm.getDeviceId())) {
                return "";
            }

            return tm.getDeviceId();
        }else {
            return "";
        }

    }
    public static float AutoPX(int px, Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return px * dm.heightPixels / 1920;
    }
}
