package com.qinggan.app.arielapp.minor.entity;

import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.restmiddle.RestError;

/**
 * Created by pateo on 18-12-3.
 */

public class EventBusTSPInfo<Module> {
    public static int BUSSINESS_TYPE_NAVI_INFO = 1;
    public static int BUSSINESS_TYPE_VEHICLE_INFO = 0;

    private boolean isSuccess;
    private int bussinessType;//0:VehicleDetailInfo,1:NaviInfo
    private int commandType;

    private Module module;
    private RestError restError;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getBussinessType() {
        return bussinessType;
    }

    public void setBussinessType(int bussinessType) {
        this.bussinessType = bussinessType;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public RestError getRestError() {
        return restError;
    }

    public void setRestError(RestError restError) {
        this.restError = restError;
    }

    @Override
    public String toString() {
        return "EventBusTSPInfo{" +
                "isSuccess=" + isSuccess +
                ", bussinessType=" + bussinessType +
                ", commandType=" + commandType +
                ", module=" + module +
                ", restError=" + restError +
                '}';
    }
}