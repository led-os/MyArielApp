package com.qinggan.app.arielapp.capability.push.factory;

import android.text.TextUtils;
import android.util.Log;

import com.qinggan.mobile.tsp.mqtt.PushMessage;

/**
 * <处理推送消息>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public class PushMessageFactory {
    private static final String TAG = PushMessageFactory.class.getSimpleName();

    public static void operatePushMessage(PushMessage pushMessage) {
        Log.d(TAG, "operatePushMessage");
        try {
            String pushType = pushMessage.getMessage().getPushType();
            Log.d(TAG, "operatePushMessage pushType:"+pushType);
            if (TextUtils.isEmpty(pushType)) return;
            String pushBody = pushMessage.getMessage().getPushBody();
            if (TextUtils.isEmpty(pushBody)) return;
            MessageEnum.getModelByPushType(pushType).newInstance().doService(pushBody);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
