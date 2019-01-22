package com.qinggan.app.arielapp.capability.upgrade.tspota;

/**
 * <tsp请求ota相关接口回调>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface TspOtaListener {

    /**
     * 当前版本
     *
     * @param version
     */
    void onRspCurrentVersion(String version);

    /**
     * 当前所处的升级状态
     *
     * @param processId
     * @param upgradeStatus
     * @param processDesc
     */
    void onUpgradeProcess(String processId, String upgradeStatus, String processDesc);

    /**
     * 新版本
     *
     * @param version
     * @param releaseNote
     * @param size
     */
    void onRspNewVersion(String version, String releaseNote, String size);

    /**
     * 没有升级
     */
    void onRspNoUpgrade();

    /**
     * 网络请求错误
     *
     * @param message
     */
    void onError(String message);
}
