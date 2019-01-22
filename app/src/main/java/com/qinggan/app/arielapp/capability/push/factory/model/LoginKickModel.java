package com.qinggan.app.arielapp.capability.push.factory.model;

import android.util.Log;

import com.qinggan.app.arielapp.ActivityLifecycleListener;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.capability.push.factory.BasePushMessageModel;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.ui.widget.KickoutDialog;

import org.greenrobot.eventbus.EventBus;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;

/**
 * <登录账户在其他终端登录>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-12-7]
 * @see [相关类/方法]
 * @since [V1]
 */
public class LoginKickModel extends BasePushMessageModel {
    @Override
    public void doService(String pushBody) {
        Log.d(TAG, "doService");
        Kick kick = gson.fromJson(pushBody, Kick.class);
        if (null != kick) {
            Log.d(TAG, "kick type:" + kick.getSubType());
            //pushType 12 subType 是1或者2,代表被踢
            if ("1".equals(kick.getSubType()) || "2".equals(kick.getSubType())) {
                //应用内退出登录
                ArielApplication.setmUserInfo(null);
                EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                        "1"
                ));
                Log.d(TAG, "alert kick out dialog");
                new KickoutDialog(ActivityLifecycleListener.currentActivity).show();
                EventBus.getDefault().post(kick);
            }
        }
    }


    public static class Kick {
        private String subType;
        private String subTitle;
        private String message;

        public String getSubType() {
            return subType;
        }

        public void setSubType(String subType) {
            this.subType = subType;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
