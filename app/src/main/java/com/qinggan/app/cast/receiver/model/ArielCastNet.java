package com.qinggan.app.cast.receiver.model;

/**
 * <投屏用的网络信息model>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class ArielCastNet {

    /**
     * 网络类型
     */
    int type;

    int value;

    public ArielCastNet() {
    }

    public ArielCastNet(int type, int value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ArielCastNet:type:" + type + ",value:" + value;
    }
}
