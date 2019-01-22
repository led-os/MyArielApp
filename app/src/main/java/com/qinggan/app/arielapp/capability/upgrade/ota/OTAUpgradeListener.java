package com.qinggan.app.arielapp.capability.upgrade.ota;

/**
 * <ota升级接口监听>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-22]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface OTAUpgradeListener {

    /**
     * 当前版本
     *
     * @param mpuVersion
     * @param mcuVersion
     */
    void onCurrentVersion(String mpuVersion, String mcuVersion);

    /**
     * 没有新版本
     */
    void onUpgradeNoVersion();

    /**
     * 有新的版本
     *
     * @param fromUdisk   true:U盘升级 false:网络升级
     * @param mpuVersion
     * @param mcuVersion
     * @param description 描述
     */
    void onNewVersion(boolean fromUdisk, String mpuVersion, String mcuVersion, String description);

    /**
     * 升级进度
     *
     * @param downloading true:下载中 false:升级中
     * @param current
     * @param total
     */
    void onUpgrading(boolean downloading, int current, int total);

    /**
     * 错误信息
     *
     * @param errorType 错误类型
     * @param errorMsg  错误信息
     */
    void onError(int errorType, String errorMsg);

    /**
     * 升级状态
     */
    void onUpgradeStateChanged(int state);
}
