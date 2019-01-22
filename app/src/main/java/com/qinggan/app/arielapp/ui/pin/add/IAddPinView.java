package com.qinggan.app.arielapp.ui.pin.add;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-19]
 * @see [相关类/方法]
 * @since [V1]
 */
public interface IAddPinView {

    void modifyPinSuccess();

    void modifyPinFail(String msg);

    void checkPinSuccess();

    void checkPinFail(String msg);

    /**
     * 验证安全码错误
     *
     * @param leftCount 剩余次数
     */
    void checkPinError(int leftCount);
}
