package com.qinggan.app.arielapp.capability.upgrade;

/**
 * <升级管理>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-3]
 * @see [相关类/方法]
 * @since [V1]
 */
public class UpgradeManager {

    private static UpgradeModel upgradeModel = new UpgradeModel();

    public static UpgradeModel getUpgradeModel() {
        return upgradeModel;
    }
}
