package com.qinggan.app.arielapp.minor.main.navigation;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.virtualclick.utils.AppNameConstants;

public class NotificationMonitorService extends NotificationListenerService {
    private String TAG = NotificationMonitorService.class.getSimpleName();

    @Override
    public void onListenerConnected() {
        Log.d(TAG, "BdNotificationMonitorService Connected");
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "BdNotificationMonitorService onUnbind");
        return super.onUnbind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        String notificationPkg = sbn.getPackageName();
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        if (notificationPkg.equals(AppNameConstants.BAIDU_APP_PACKAGE_NAME)) {
            handleBaiduPostMsg(notificationTitle, notificationText);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        String notificationPkg = sbn.getPackageName();
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        if (notificationPkg.equals(AppNameConstants.BAIDU_APP_PACKAGE_NAME)) {
            handleBaiduRemoveMsg(notificationTitle, notificationText);
        }
    }


    public void handleBaiduPostMsg(String title, String content) {
        if (title.equals(getString(R.string.baidu_navi_ing)) && content.equals(getString(R.string.baidu_name))) {
            BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_ING;
        } else if (title.equals(getString(R.string.baidu_wnavi_ing)) && content.equals(getString(R.string.baidu_name))) {
            BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_ING;
        }

    }


    public void handleBaiduRemoveMsg(String title, String content) {
        if (title.equals(getString(R.string.baidu_navi_ing)) && content.equals(getString(R.string.baidu_name))) {
            if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING && !BdMapUIcontrol.isVirtualBack) {
                BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHED;
            } else if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING && BdMapUIcontrol.isVirtualBack) {
                BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
            }
        } else if (title.equals(getString(R.string.baidu_wnavi_ing)) && content.equals(getString(R.string.baidu_name))) {
            if (BdMapUIcontrol.walkNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING && !BdMapUIcontrol.isVirtualBack) {
                BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHED;
            } else if (BdMapUIcontrol.walkNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING && BdMapUIcontrol.isVirtualBack) {
                BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
            }
        }

    }


}
