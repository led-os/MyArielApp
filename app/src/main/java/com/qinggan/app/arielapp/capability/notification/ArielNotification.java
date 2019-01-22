package com.qinggan.app.arielapp.capability.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ArielNotification {

    private static volatile ArielNotification instance;

    public static ArielNotification getInstance() {
        if (null == instance) {
            synchronized (ArielNotification.class) {
                if (null == instance) {
                    instance = new ArielNotification();
                }
            }
        }
        return instance;
    }

    private ArielNotification() {
        createNotificationChannel();
    }

    /**
     * 8.0设置通知渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("upgrade", "升级", NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableVibration(false);
            channel.enableLights(false);
            channel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) ArielApplication.getApp().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * 创建通知
     *
     * @param content
     */
    public void createNotification(String content) {
        try {
            NotificationManager manager = (NotificationManager) ArielApplication.getApp().getSystemService(NOTIFICATION_SERVICE);
            android.app.Notification notification = new NotificationCompat.Builder(ArielApplication.getApp(), "upgrade")
                    .setContentTitle(ArielApplication.getApp().getString(R.string.app_name))
                    .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                    .setContentText(content).setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true).build();
            manager.notify(1, notification);
        } catch (Exception e) {
            Log.d("ArielNotification", "createNotification exception:" + e.getMessage());
        }
    }
}
