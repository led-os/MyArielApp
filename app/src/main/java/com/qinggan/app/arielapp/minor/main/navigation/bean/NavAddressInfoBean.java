package com.qinggan.app.arielapp.minor.main.navigation.bean;

import com.baidu.mapapi.search.core.PoiInfo;

public class NavAddressInfoBean {
    private String mAddress;
    private String mName;
    private String mDistance;
    private String mLat;
    private String mLng;
    private String uid;
    private PoiInfo poiInfo;
    private String type;

    public NavAddressInfoBean(String address, String name, String distance){
        this.mAddress = address;
        this.mName = name;
        this.mDistance = distance;
    }

    public String getmAddress(){
        return this.mAddress;
    }

    public void setmAddress(String address){
        this.mAddress = address;
    }

    public String getmName(){
        return this.mName;
    }

    public void setmName(String name){
        this.mName = name;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmLat() {
        return mLat;
    }

    public void setmLat(String mLat) {
        this.mLat = mLat;
    }

    public String getmLng() {
        return mLng;
    }

    public void setmLng(String mLng) {
        this.mLng = mLng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public PoiInfo getPoiInfo() {
        return poiInfo;
    }

    public void setPoiInfo(PoiInfo poiInfo) {
        this.poiInfo = poiInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NavAddressInfoBean{" +
                "mAddress='" + mAddress + '\'' +
                ", mName='" + mName + '\'' +
                ", mDistance='" + mDistance + '\'' +
                ", mLat='" + mLat + '\'' +
                ", mLng='" + mLng + '\'' +
                ", uid='" + uid + '\'' +
                ", poiInfo=" + poiInfo +
                ", type='" + type + '\'' +
                '}';
    }
}
