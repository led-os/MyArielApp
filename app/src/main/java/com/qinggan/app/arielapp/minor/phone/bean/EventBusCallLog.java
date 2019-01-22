package com.qinggan.app.arielapp.minor.phone.bean;

import java.util.List;

/**
 * Created by pateo on 18-10-30.
 */

public class EventBusCallLog {
    List<CallRecord> recentCallLogList;
    List<CallRecord> favourCallLogList;

    public List<CallRecord> getRecentCallLogList() {
        return recentCallLogList;
    }

    public void setRecentCallLogList(List<CallRecord> recentCallLogList) {
        this.recentCallLogList = recentCallLogList;
    }

    public List<CallRecord> getFavourCallLogList() {
        return favourCallLogList;
    }

    public void setFavourCallLogList(List<CallRecord> favourCallLogList) {
        this.favourCallLogList = favourCallLogList;
    }
}
