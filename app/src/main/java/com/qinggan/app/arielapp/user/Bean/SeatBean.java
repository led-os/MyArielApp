package com.qinggan.app.arielapp.user.Bean;

import java.io.Serializable;

/**
 * Created by Yorashe on 19-1-9.
 */

public class SeatBean implements Serializable{
    private int uid;
    private int seatAccount;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getSeatAccount() {
        return seatAccount;
    }

    public void setSeatAccount(int seatAccount) {
        this.seatAccount = seatAccount;
    }
}
