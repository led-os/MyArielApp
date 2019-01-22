package com.qinggan.app.arielapp.minor.push.bean;

import android.os.Parcel;

public class PushMessageBodyBean implements android.os.Parcelable{
    private String msgTitle;
    private String pushBody;

    public PushMessageBodyBean(){
    }

    protected PushMessageBodyBean(Parcel in) {
        this.msgTitle = in.readString();
        this.pushBody = in.readString();
    }

    public void setMsgTitle(String msgTitle) {
       this.msgTitle = msgTitle;
    }

    public String getMsgTitle(){
        return this.msgTitle;
    }

    public void setPushBody(String pushBody) {
        this.pushBody = pushBody;
    }

    public String getPushBody(){
        return this.pushBody;
    }

    public static final Creator<PushMessageBodyBean> CREATOR = new Creator<PushMessageBodyBean>() {
        @Override
        public PushMessageBodyBean createFromParcel(Parcel in) {
            return new PushMessageBodyBean(in);
        }

        @Override
        public PushMessageBodyBean[] newArray(int size) {
            return new PushMessageBodyBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(msgTitle);
        parcel.writeString(pushBody);
    }

    @Override
    public String toString() {
        return "PushMessageBodyBean{" +
                "msgTitle='" + msgTitle + '\'' +
                ", pushBody='" + pushBody + '\'' +
                '}';
    }
}
