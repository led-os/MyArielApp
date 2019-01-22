package com.qinggan.app.arielapp.capability.push;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.capability.push.factory.PushMessageFactory;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.mobile.tsp.mqtt.PushMessage;
import com.qinggan.mobile.tsp.mqtt.QGMqttManagerImpl;
import com.qinggan.mobile.tsp.mqtt.QGMqttService;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * <推送服务>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-27]
 * @see [相关类/方法]
 * @since [V1]
 */
public class PushManager {
    private String TAG = PushManager.class.getSimpleName();
    private static volatile PushManager instance;

    public static PushManager getInstance() {
        if (null == instance) {
            synchronized (PushManager.class) {
                if (null == instance)
                    instance = new PushManager();
            }
        }
        return instance;
    }

    /**
     * service是否连接上
     */
    private AtomicBoolean connect = new AtomicBoolean(false);

    private PushManager() {
        bindMqttService();
    }

    private void bindMqttService() {
        Log.d(TAG, "bindMqttService");
        Intent intent = new Intent();
        intent.setClass(ArielApplication.getApp(), QGMqttService.class);
        ArielApplication.getApp().bindService(intent, mConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            connect.set(true);
            QGMqttService.Binder binder = (QGMqttService.Binder) service;
            QGMqttManagerImpl mQGMqttManagerImpl = binder.getQGMqttManagerImpl();
            Log.i(TAG, "onServiceConnected IMEI = " + VehicleUtils.getIMEI());
            mQGMqttManagerImpl.updateTopic(VehicleUtils.getIMEI());
            mQGMqttManagerImpl.setCallback(new QGMqttManagerImpl.Callback() {
                @Override
                public void messageArrived(PushMessage pushMessage) {
                    Log.d("QGMqttService", "onServiceConnected xx: " + pushMessage.toString());
                    PushMessage.Message message = pushMessage.getMessage();
                    Log.d(TAG, "onMessageArrived message:" + message);
                    if (null == message) {
                        Log.e(TAG, "onMessageArrived message is null");
                        return;
                    }
                    PushMessageFactory.operatePushMessage(pushMessage);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            connect.set(false);
        }
    };

    /**
     * unbindservice
     */
    public void unBindService() {
        Log.d(TAG, "unBindService:connect:" + connect.get());
        if (connect.get()) {
            ArielApplication.getApp().unbindService(mConnection);
        }
    }
}
