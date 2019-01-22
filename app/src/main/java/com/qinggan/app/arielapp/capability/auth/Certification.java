package com.qinggan.app.arielapp.capability.auth;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.vehiclesim.BindVehicleInfo;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.staryea.ui.CerStepFourActivity;
import com.staryea.ui.CerStepOneActivity;

/**
 * <实名认证>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-26]
 * @see [相关类/方法]
 * @since [V1]
 */
public class Certification {

    /**
     * 去实名认证
     *
     * @param context
     */
    public static void goToAuth(Context context) {
        String iccid = BindVehicleInfo.getIccid();
        Log.d("Certification", "goToCertification.iccid:" + iccid);
        if (TextUtils.isEmpty(iccid)) {
            ToastUtil.show(R.string.iccid_empty, context);
            return;
        }
        if (iccid.length() > 19) {
            Log.d("Certification", "goToCertification.iccid length over 19");
            iccid = iccid.substring(0, 19);
        }
        Intent intent = new Intent(context, CerStepOneActivity.class);
        // 统一app注册的账号
        intent.putExtra("userCode", "13965102853");
        intent.putExtra("passWord", "QWer1234@@");
        intent.putExtra("iccid", iccid);
        context.startActivity(intent);
    }

    /**
     * 注册实名认证结果监听
     *
     * @param listener
     */
    public static void registAuthResultListener(CerStepFourActivity.OnAuthrizeResultListener listener) {
        CerStepFourActivity.setOnAuthrizeResultListener(listener);
    }

    public static void unRegistAuthResultListener() {
        CerStepFourActivity.setOnAuthrizeResultListener(null);
    }
}
