package com.qinggan.app.arielapp.minor.main.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.core.VehicleControlInterface;
import com.qinggan.app.arielapp.minor.integration.PateoVehicleControlCMD;
import com.qinggan.app.arielapp.minor.proxy.DynamicProxyHandler;
import com.qinggan.app.arielapp.minor.proxy.InterceptorInterface;
import com.qinggan.app.arielapp.minor.proxy.impl.CarControlInterceptor;
import com.qinggan.app.arielapp.minor.scenario.RefreshUICallback;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.mobile.tsp.manager.TspManager;

/**
 * Created by pateo on 18-12-4.
 */

public class InterceptorProxyUtils {

    public static boolean isAviliableUserVehicle() {

        boolean isAviliable = true;
        if (ArielApplication.getmUserInfoWithLogin() == null) {
            //UserInfo is null, forward to login page
            isAviliable = false;
        } else if (TextUtils.isEmpty(TspManager.getmPdsn())) {
            Context context = ArielApplication.getApp();
            Intent intent = new Intent(context, BindCarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            isAviliable = false;
        }
        return isAviliable;
    }

    public static boolean isAviliableUser() {

        boolean isAviliable = true;
        if (ArielApplication.getmUserInfoWithLogin() == null) {
            //UserInfo is null, forward to login page
            isAviliable = false;
        }
        return isAviliable;
    }

    public static VehicleControlInterface getVehicleControlProxy() {
        InterceptorInterface interceptor = CarControlInterceptor.getInstance();
        DynamicProxyHandler dynamicProxyHandler = DynamicProxyHandler.getInstance(interceptor);

        VehicleControlInterface proxyObject = PateoVehicleControlCMD.getInstance();
        VehicleControlInterface businessProxy = (VehicleControlInterface) dynamicProxyHandler.bind(proxyObject);
        return businessProxy;
    }


    public static VehicleControlInterface getVehicleControlProxyWithUICallback(RefreshUICallback uiCallback) {
        InterceptorInterface interceptor = CarControlInterceptor.getInstance();
        DynamicProxyHandler dynamicProxyHandler = DynamicProxyHandler.getInstance(interceptor);


        VehicleControlInterface proxyObject = PateoVehicleControlCMD.getInstance();
        ((PateoVehicleControlCMD) proxyObject).setUiCallback(uiCallback);
        VehicleControlInterface businessProxy = (VehicleControlInterface) dynamicProxyHandler.bind(proxyObject);
        return businessProxy;
    }



}
