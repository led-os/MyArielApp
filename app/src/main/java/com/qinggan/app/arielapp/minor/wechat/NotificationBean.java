package com.qinggan.app.arielapp.minor.wechat;

/**
 * 用于处理notification
 */

public class NotificationBean {
    private String sender;       //发件人
    private String msg;          //消息
    private boolean isRelpy;      //true 表示显示回复界面,false 表示显示消息界面
    private String input;         //记录用户语音输入子串
    private boolean isLastMsg;   //是否是最后一条msg

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isRelpy() {
        return isRelpy;
    }

    public void setRelpy(boolean relpy) {
        isRelpy = relpy;
    }

    public boolean isLastMsg() {
        return isLastMsg;
    }

    public void setLastMsg(boolean lastMsg) {
        isLastMsg = lastMsg;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "NotificationBean{" +
                "sender='" + sender + '\'' +
                ", msg='" + msg + '\'' +
                ", isRelpy=" + isRelpy +
                ", input='" + input + '\'' +
                ", isLastMsg=" + isLastMsg +
                '}';
    }
}
