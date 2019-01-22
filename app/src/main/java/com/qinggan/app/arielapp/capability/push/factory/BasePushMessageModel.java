package com.qinggan.app.arielapp.capability.push.factory;

import com.google.gson.Gson;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-30]
 * @see [相关类/方法]
 * @since [V1]
 */
public abstract class BasePushMessageModel {
    protected String TAG = this.getClass().getSimpleName();
    protected Gson gson = new Gson();

    public abstract void doService(String pushBody);
}
