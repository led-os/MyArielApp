package com.qinggan.app.arielapp.minor.phone.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by pateo on 18-10-31.
 */

public class ContactsInfo implements Parcelable {
    private int contactId; //id
    private String displayName;//姓名
    private String phoneNum; // 电话号码
    private String sortKey; // 排序用的
    private Long photoId; // 图片id
    private String lookUpKey;
    private int selected = 0;
    private String formattedNumber;
    private String pinyin; // 姓名拼音

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDesplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getLookUpKey() {
        return lookUpKey;
    }

    public void setLookUpKey(String lookUpKey) {
        this.lookUpKey = lookUpKey;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getFormattedNumber() {
        return formattedNumber;
    }

    public void setFormattedNumber(String formattedNumber) {
        this.formattedNumber = formattedNumber;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "ContactsInfo{" +
                "contactId=" + contactId +
                ", displayName='" + displayName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", sortKey='" + sortKey + '\'' +
                ", photoId=" + photoId +
                ", lookUpKey='" + lookUpKey + '\'' +
                ", selected=" + selected +
                ", formattedNumber='" + formattedNumber + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.contactId);
        dest.writeString(this.displayName);
        dest.writeString(this.phoneNum);
        dest.writeString(this.sortKey);
        dest.writeValue(this.photoId);
        dest.writeString(this.lookUpKey);
        dest.writeInt(this.selected);
        dest.writeString(this.formattedNumber);
        dest.writeString(this.pinyin);
    }

    public ContactsInfo() {
    }

    protected ContactsInfo(Parcel in) {
        this.contactId = in.readInt();
        this.displayName = in.readString();
        this.phoneNum = in.readString();
        this.sortKey = in.readString();
        this.photoId = (Long) in.readValue(Long.class.getClassLoader());
        this.lookUpKey = in.readString();
        this.selected = in.readInt();
        this.formattedNumber = in.readString();
        this.pinyin = in.readString();
    }

    public static final Creator<ContactsInfo> CREATOR = new Creator<ContactsInfo>() {
        @Override
        public ContactsInfo createFromParcel(Parcel source) {
            return new ContactsInfo(source);
        }

        @Override
        public ContactsInfo[] newArray(int size) {
            return new ContactsInfo[size];
        }
    };
}
