package com.qinggan.app.arielapp.minor.proxy;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * DynamicProxyHandler
 */
public class DynamicProxyHandler implements InvocationHandler {
    // 被代理对象
    private Object proxyObject;

    // interceptor
    private InterceptorInterface interceptor;

    private DynamicProxyHandler(InterceptorInterface interceptor) {
        this.interceptor = interceptor;
    }

    private static DynamicProxyHandler instance = null;
    public static DynamicProxyHandler getInstance(InterceptorInterface interceptor){
        if (instance == null){
            Log.d("zfsasuke","init DynamicProxyHandler");
            instance = new DynamicProxyHandler(interceptor);
        }
        return instance;
    }
    /**
     * 动态生成代理对象
     *
     * @param business
     * @return 代理类对象
     */
    public Object bind(Object business) {
        this.proxyObject = business;
        /**
         * Proxy.newProxyInstance
         */

        return Proxy.newProxyInstance(business.getClass().getClassLoader(),
                business.getClass().getInterfaces(),this);
    }

    /**
     * 代理回调
     *
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        interceptor.doBefore();
        result = method.invoke(proxyObject, args);
        interceptor.doAfter();
        return null;
    }
}
