package com.qinggan.app.arielapp.minor.proxy.impl;

import android.util.Log;

import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.proxy.InterceptorInterface;

public class CarControlInterceptor implements InterceptorInterface {

    private static CarControlInterceptor instance;

    private CarControlInterceptor(){}

    public static CarControlInterceptor getInstance(){
        if (instance == null){
            Log.d("zfsasuke","init CarControlInterceptor");
            instance = new CarControlInterceptor();
        }
        return instance;
    }

    public void doBefore() {
        InterceptorProxyUtils.isAviliableUserVehicle();
    }

    public void doAfter() {
        //TODO
    }
}
