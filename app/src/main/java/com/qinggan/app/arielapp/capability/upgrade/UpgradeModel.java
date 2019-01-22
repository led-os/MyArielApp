package com.qinggan.app.arielapp.capability.upgrade;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.qinggan.app.arielapp.BR;
import com.qinggan.mobile.tsp.models.ota.OTACheckUpgradeRsp;

/**
 * <升级对象>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class UpgradeModel extends BaseObservable {

    public boolean appNewVersion = false;

    public boolean otaNewVersion = false;

    public OTACheckUpgradeRsp.PackageInfo packageInfo;

    @Bindable
    public boolean isAppNewVersion() {
        return appNewVersion;
    }

    public void setAppNewVersion(boolean appNewVersion) {
        this.appNewVersion = appNewVersion;
        notifyPropertyChanged(BR.appNewVersion);
    }

    @Bindable
    public boolean isOtaNewVersion() {
        return otaNewVersion;
    }

    public void setOtaNewVersion(boolean otaNewVersion) {
        this.otaNewVersion = otaNewVersion;
        notifyPropertyChanged(BR.otaNewVersion);
    }

    public OTACheckUpgradeRsp.PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(OTACheckUpgradeRsp.PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}
