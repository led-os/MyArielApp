package com.qinggan.app.arielapp.minor.main.entity;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;

import java.util.ArrayList;
import java.util.List;

public class EventBusBean {
    String type;
    String elapsed_time;
    String len;
    String msg;
    Adresss adresss;
    NaviSearchHistory naviSearchHistory;
    List<PoiInfo> allAddr = new ArrayList<PoiInfo>();//poi检索结果list

    int battery=0;//电量
    String time="14:38";
    String date="11月23日";
    String week="星期五";
    int intData=0;
    BDLocation location;
    int status=0;//电量
    public EventBusBean(String msgType,int attery, String time, String date,String week) {
        this.battery = attery;
        this.time = time;
        this.date = date;
        this.type = msgType;
        this.week = week;
    }

    public EventBusBean(String type, int battery, String time, String date, String week, int status) {
        this.type = type;
        this.battery = battery;
        this.time = time;
        this.date = date;
        this.week = week;
        this.status = status;
    }

    public EventBusBean(String type, BDLocation location) {
        this.type = type;
        this.location = location;
    }

    public EventBusBean(String type, int intData) {
        this.type = type;
        this.intData = intData;
    }

    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public EventBusBean(String type, Adresss adresss) {
        this.type = type;
        this.adresss = adresss;
    }

    public EventBusBean(String type, String elapsed_time, String len) {
        this.type = type;
        this.elapsed_time = elapsed_time;
        this.len = len;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public NaviSearchHistory getNaviSearchHistory() {
        return naviSearchHistory;
    }

    public void setNaviSearchHistory(NaviSearchHistory naviSearchHistory) {
        this.naviSearchHistory = naviSearchHistory;
    }

    public EventBusBean(String type, NaviSearchHistory naviSearchHistory) {
        this.type = type;
        this.naviSearchHistory = naviSearchHistory;
    }
    public EventBusBean(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }
    public EventBusBean(String type, List<PoiInfo> allAddr) {
        this.type = type;
        this.allAddr = allAddr;
    }
    public EventBusBean(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Adresss getAdresss() {
        return adresss;
    }

    public void setAdresss(Adresss adresss) {
        this.adresss = adresss;
    }

    public List<PoiInfo> getAllAddr() {
        return allAddr;
    }

    public void setAllAddr(List<PoiInfo> allAddr) {
        this.allAddr = allAddr;
    }

    public int getIntData() {
        return intData;
    }

    public void setIntData(int intData) {
        this.intData = intData;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getElapsed_time() {
        return elapsed_time;
    }

    public void setElapsed_time(String elapsed_time) {
        this.elapsed_time = elapsed_time;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }
}
