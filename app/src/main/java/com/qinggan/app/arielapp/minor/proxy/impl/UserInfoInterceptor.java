package com.qinggan.app.arielapp.minor.proxy.impl;

import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.proxy.InterceptorInterface;

public class UserInfoInterceptor implements InterceptorInterface {

    public void doBefore() {
        InterceptorProxyUtils.isAviliableUser();
    }

    public void doAfter() {
        //TODO
    }
}
