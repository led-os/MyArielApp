package com.qinggan.app.arielapp.capability.push.factory.model;

import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.capability.push.factory.BasePushMessageModel;
import com.qinggan.app.arielapp.capability.upgrade.UpgradeManager;
import com.qinggan.app.arielapp.capability.upgrade.tspota.TspOtaManager;
import com.qinggan.mobile.tsp.models.ota.OTAUpgradeStatusRsp;

/**
 * <ota升级过程>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class OTAProgressModel extends BasePushMessageModel {
    @Override
    public void doService(String pushBody) {
        Log.d(TAG, "doService");
        OTAUpgradeStatusRsp.UpgradeStatusModel progress = gson.fromJson(pushBody, OTAUpgradeStatusRsp.UpgradeStatusModel.class);
        if (null != progress) {
            //ota升级状态逻辑
            String status = progress.getProcessStatus();
            Log.d(TAG, "doService status:" + status);
            if (TextUtils.isEmpty(status)) return;
            if ("09".equals(status) || "14".equals(status)) {
                //升级成功
                UpgradeManager.getUpgradeModel().setOtaNewVersion(false);
                UpgradeManager.getUpgradeModel().setPackageInfo(null);
            }
            TspOtaManager.getInstance().notifyListener(true, progress.getProcessId(), progress.getProcessStatus(), progress.getProcessDesc());
//            EventBus.getDefault().post(progress);
        }
    }
}
