package com.qinggan.app.arielapp.minor.wechat.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.qinggan.app.arielapp.minor.wechat.NotificationBean;

public class WeChatUtils {
    private final String TAG = WeChatUtils.class.getSimpleName();

    public NotificationBean handleNotificationStrToBean(String notification) {
        if (notification == null || notification.equals("") ||
                !notification.contains(":")) {
            return null;
        }
        NotificationBean notificationBean = new NotificationBean();
        int index = notification.indexOf(":");
        Log.d(TAG, "index : " + index);
        notificationBean.setSender(notification.substring(0, index));
        notificationBean.setMsg(notification.substring(index + 2, notification.length()));
        notificationBean.setRelpy(false);
        Log.d(TAG, "notificationBean : " + notificationBean.toString());
        return notificationBean;
    }

    public void speak(Context context, final String msg) {
        final SystemTTS mTTS = SystemTTS.getInstance(context);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTTS.playText(msg);
            }
        }, 3000);
    }
}
