package com.qinggan.app.arielapp.capability.push.factory;

import android.util.Log;

import com.qinggan.app.arielapp.capability.push.factory.model.HUIPushMode;
import com.qinggan.app.arielapp.capability.push.factory.model.LoginKickModel;
import com.qinggan.app.arielapp.capability.push.factory.model.OTANoticeModel;
import com.qinggan.app.arielapp.capability.push.factory.model.OTAProgressModel;

/**
 * <根据pushType定义推送消息的逻辑处理对象>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public enum MessageEnum {
    OTA_UG_NOTICE("OTA_UG_NOTICE", OTANoticeModel.class),
    OTA_UG_PROCESS("OTA_UG_PROCES", OTAProgressModel.class),
    HUI(HUIPushConstants.PUSH_TYPE, HUIPushMode.class),
    LOGIN_KICK("12", LoginKickModel.class);

    String pushType;
    Class<? extends BasePushMessageModel> modelClass;

    MessageEnum(String pushType, Class<? extends BasePushMessageModel> modelClass) {
        this.pushType = pushType;
        this.modelClass = modelClass;
    }

    /**
     * 根据推送类型,返回业务处理对象
     *
     * @param pushType
     * @return
     */
    public static Class<? extends BasePushMessageModel> getModelByPushType(String pushType) {
        Log.d("MessageEnum", "getModelByPushType:" + pushType);
        MessageEnum[] list = MessageEnum.values();
        for (MessageEnum messageEnum : list) {
            if (pushType.equals(messageEnum.pushType)) {
                Log.d("MessageEnum", "find model");
                return messageEnum.modelClass;
            }
        }
        return BasePushMessageModel.class;
    }
}
