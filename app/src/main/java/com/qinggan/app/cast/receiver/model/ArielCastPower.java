package com.qinggan.app.cast.receiver.model;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-15]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ArielCastPower {
    int power;

    public ArielCastPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public String toString() {
        return "ArielCastPower:power:" + power;
    }
}
