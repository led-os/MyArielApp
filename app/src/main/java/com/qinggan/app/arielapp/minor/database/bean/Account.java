package com.qinggan.app.arielapp.minor.database.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by pateo on 18-11-3.
 */

@DatabaseTable(tableName = "account")
public class Account extends BasicInfo {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "account_id")
    private String accountId;

    @DatabaseField(columnName = "phone_no")
    private String phoneNo;

    @DatabaseField(columnName = "car_license_plate")
    private String carLicensePlate;

    @DatabaseField(columnName = "create_date", dataType = DataType.DATE_STRING)
    private Date createDate;

    @DatabaseField(columnName = "last_modified_date", dataType = DataType.DATE_STRING)
    private Date lastModifiedDate;

    @DatabaseField(columnName = "image_id")
    private String imageId;

    @DatabaseField(columnName = "image",columnDefinition= "LONGBLOB", dataType = DataType.BYTE_ARRAY)
    private byte[] image;

    public Account() {}

    protected Account(Parcel in) {
        id = in.readInt();
        accountId = in.readString();
        phoneNo = in.readString();
        carLicensePlate = in.readString();
        imageId = in.readString();
        image = in.createByteArray();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCarLicensePlate() {
        return carLicensePlate;
    }

    public void setCarLicensePlate(String carLicensePlate) {
        this.carLicensePlate = carLicensePlate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountId='" + accountId + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", carLicensePlate='" + carLicensePlate + '\'' +
                ", createDate=" + createDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", imageId='" + imageId + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(accountId);
        parcel.writeString(phoneNo);
        parcel.writeString(carLicensePlate);
        parcel.writeString(imageId);
        parcel.writeByteArray(image);
    }
}