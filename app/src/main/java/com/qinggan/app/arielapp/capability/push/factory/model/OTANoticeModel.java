package com.qinggan.app.arielapp.capability.push.factory.model;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.qinggan.app.arielapp.ActivityLifecycleListener;
import com.qinggan.app.arielapp.capability.push.factory.BasePushMessageModel;
import com.qinggan.app.arielapp.capability.upgrade.UpgradeManager;
import com.qinggan.app.arielapp.ui.widget.upgrade.OTAUpgradeDialog;
import com.qinggan.mobile.tsp.models.ota.OTACheckUpgradeRsp;

/**
 * <ota升级通知>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OTANoticeModel extends BasePushMessageModel {
    @Override
    public void doService(String pushBody) {
        Log.d(TAG, "doService");
        final OTACheckUpgradeRsp.PackageInfo packageInfo = gson.fromJson(pushBody, OTACheckUpgradeRsp.PackageInfo.class);
        if (null != packageInfo) {
            //收到升级通知的逻辑处理
            if (null != packageInfo.getPatchProfile() && packageInfo.getPatchProfile().size() > 0 || null != packageInfo.getFullProfile()) {
                //有新的版本信息
                UpgradeManager.getUpgradeModel().setOtaNewVersion(true);
                UpgradeManager.getUpgradeModel().setPackageInfo(packageInfo);
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new OTAUpgradeDialog(ActivityLifecycleListener.currentActivity,packageInfo).show();
                    }
                });
            }
        }
    }
}
