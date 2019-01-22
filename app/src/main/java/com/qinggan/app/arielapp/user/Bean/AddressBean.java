package com.qinggan.app.arielapp.user.Bean;

import java.util.List;

/**
 * Created by Yorashe on 18-11-27.
 */

public class AddressBean {
    private String address;
    private double latitude;
    private double longitude;
    private String sid;
    private int type;// 0常用地址,1 home,2 comp

    private String name;
    private String displayName;
    private String isPreset;//true,false
    private String isFavour;
    private String uid;
    private Long lastModifiedDate;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIsPreset() {
        return isPreset;
    }

    public void setIsPreset(String isPreset) {
        this.isPreset = isPreset;
    }

    public String getIsFavour() {
        return isFavour;
    }

    public void setIsFavour(String isFavour) {
        this.isFavour = isFavour;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public String toString() {
        return "AddressBean{" +
                "address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", sid='" + sid + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", isPreset='" + isPreset + '\'' +
                ", isFavour='" + isFavour + '\'' +
                ", uid='" + uid + '\'' +
                ", lastModifiedDate='" + lastModifiedDate + '\'' +
                '}';
    }
}
