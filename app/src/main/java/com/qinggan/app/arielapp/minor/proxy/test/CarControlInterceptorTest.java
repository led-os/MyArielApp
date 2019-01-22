package com.qinggan.app.arielapp.minor.proxy.test;

import com.qinggan.app.arielapp.minor.core.VehicleControlInterface;
import com.qinggan.app.arielapp.minor.integration.PateoVehicleControlCMD;
import com.qinggan.app.arielapp.minor.proxy.DynamicProxyHandler;
import com.qinggan.app.arielapp.minor.proxy.InterceptorInterface;
import com.qinggan.app.arielapp.minor.proxy.impl.CarControlInterceptor;

/**
 * Created by pateo on 18-12-4.
 */

public class CarControlInterceptorTest {

    public static void main(String args[]) {
        /*InterceptorInterface interceptor = new CarControlInterceptor();
        DynamicProxyHandler dynamicProxyHandler = new DynamicProxyHandler(interceptor);

        VehicleControlInterface proxyObject = new PateoVehicleControlCMD();
        VehicleControlInterface businessProxy = (VehicleControlInterface) dynamicProxyHandler.bind(proxyObject);
        System.out.println("main refreshAir");*/
        //businessProxy.refreshAir(null);

    }

}
