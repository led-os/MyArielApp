package com.qinggan.app.arielapp.capability.upgrade.bugly;

import com.tencent.bugly.beta.UpgradeInfo;

/**
 * <检查升级回调>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface BuglyUpgradeListener {

    /**
     * 有新版本
     */
    void onNewVersion(UpgradeInfo strategy);

    /**
     * 正在检查版本升级
     */
    void onCheckVersion();

    /**
     * 没有新版本
     */
    void onUpgradeNoVersion();

    /**
     * 检查失败
     */
    void onCheckFailed();
}
