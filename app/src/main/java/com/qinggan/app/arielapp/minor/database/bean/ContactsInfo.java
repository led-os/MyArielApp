package com.qinggan.app.arielapp.minor.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;

/**
 * Created by pateo on 18-11-13.
 */

@DatabaseTable(tableName = "contacts_info")
public class ContactsInfo extends BasicInfo {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "contact_id")
    private int contactId; //id

    @DatabaseField(columnName = "display_name")
    private String displayName;//姓名

    @DatabaseField(columnName = "phone_number")
    private String phoneNum;

    public ContactsInfo() {}

    protected ContactsInfo(Parcel in) {
        id = in.readInt();
        contactId = in.readInt();
        displayName = in.readString();
        phoneNum = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(contactId);
        dest.writeString(displayName);
        dest.writeString(phoneNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContactsInfo> CREATOR = new Creator<ContactsInfo>() {
        @Override
        public ContactsInfo createFromParcel(Parcel in) {
            return new ContactsInfo(in);
        }

        @Override
        public ContactsInfo[] newArray(int size) {
            return new ContactsInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    public String toString() {
        return "ContactsInfo{" +
                "id=" + id +
                ", contactId=" + contactId +
                ", displayName='" + displayName + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                '}';
    }
}
