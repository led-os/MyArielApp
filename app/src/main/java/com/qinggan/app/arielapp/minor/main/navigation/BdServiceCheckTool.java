package com.qinggan.app.arielapp.minor.main.navigation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;


import java.util.List;

public class BdServiceCheckTool {


    private AccessibilityManager accessibilityManager;

    private Context mContext;

    private boolean mNotificationServiceAvailable;

    private boolean mAccessibilityServiceAvailable;


    public BdServiceCheckTool(Context ctx) {
        this.mContext = ctx;
        checkService();
    }


    public void checkService() {
        ensureServiceRunning();
        if (!mNotificationServiceAvailable && isNotificationListenerEnabled()) {
            toggleNotificationListenerService();
        }
        if (!mAccessibilityServiceAvailable && isBarrierFreeEnabled()) {
            toggleAccessibilityService();
        }

    }


    /**
     * 判断无障碍开关是否打开
     *
     * @return
     */
    public boolean isBarrierFreeEnabled() {
        accessibilityManager = (AccessibilityManager) mContext.getSystemService(mContext.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> accessibilityServices =
                accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : accessibilityServices) {
            if (info.getId().equals(mContext.getPackageName() + "/.minor.main.navigation.BdNaviService")) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检测通知监听服务是否被授权
     *
     * @return
     */
    public boolean isNotificationListenerEnabled() {
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        final String[] names = flat.split(":");
        for (int i = 0; i < names.length; i++) {
            final ComponentName cn = ComponentName.unflattenFromString(names[i]);
            if (cn != null) {
                if (cn.getPackageName().equals(mContext.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 打开通知监听设置页面
     */
    public void openNotificationListenSettings() {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 应用进程被杀后再次启动时，让服务重生
     */
    public void toggleNotificationListenerService() {
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(mContext, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(mContext, NotificationMonitorService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }


    public void toggleAccessibilityService() {
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(mContext, BdNaviService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(new ComponentName(mContext, BdNaviService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }


    private void ensureServiceRunning() {
        mNotificationServiceAvailable = false;
        mAccessibilityServiceAvailable = false;
        ComponentName nComponent = new ComponentName(mContext, NotificationMonitorService.class);
        ComponentName bComponent = new ComponentName(mContext, BdNaviService.class);
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
        if (runningServices == null) {
            return;
        }
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.equals(nComponent)) {
                mNotificationServiceAvailable = true;
            } else if (service.service.equals(bComponent)) {
                mAccessibilityServiceAvailable = true;
            }
        }
    }


}
