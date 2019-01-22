package com.qinggan.app.arielapp.minor.wechat;

/**
 * send msg event
 */

public class MessageEvent {
    private String msg;
    private String name;
    private String index;
    private int step;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "msg='" + msg + '\'' +
                ", name='" + name + '\'' +
                ", index='" + index + '\'' +
                ", step=" + step +
                '}';
    }
}
