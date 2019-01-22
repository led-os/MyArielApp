package com.qinggan.app.arielapp.ui.pin.findback;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-19]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface IFindbackPinView {

    /**
     * 获取短信验证码成功
     */
    void onIdentifyCodeSuccess();

    /**
     * 获取短信验证码失败
     *
     * @param msg
     */
    void onIdentifyCodeFail(String msg);


    /**
     * 校验短信验证码成功
     */
    void onCheckIdentifyCodeSuccess();

    /**
     * 校验短信验证码失败
     */
    void onCheckIdentifyCodeFail(String msg);
}
