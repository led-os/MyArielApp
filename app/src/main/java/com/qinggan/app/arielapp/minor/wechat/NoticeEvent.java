package com.qinggan.app.arielapp.minor.wechat;

/**
 * Notification event msg
 */

public class NoticeEvent {
    private NotificationBean notice;
    private boolean isDoactionSuccess;
    private int actionCode;

    public boolean isDoactionSuccess() {
        return isDoactionSuccess;
    }

    public void setDoactionSuccess(boolean doactionSuccess) {
        isDoactionSuccess = doactionSuccess;
    }

    public NotificationBean getNotice() {
        return notice;
    }

    public void setNotice(NotificationBean notice) {
        this.notice = notice;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    @Override
    public String toString() {
        return "NoticeEvent{" +
                "notice=" + notice +
                ", isDoactionSuccess=" + isDoactionSuccess +
                ", actionCode=" + actionCode +
                '}';
    }
}
