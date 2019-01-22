package com.qinggan.app.arielapp.minor.main.entity;
/****
 * 地址实体类
 *
 * ***/
public class Adresss {

    String str;//地址详细
    double lon;//经度
    double lat;//纬度
    String remark; //描述说明



    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
