package com.qinggan.app.arielapp.capability.push.factory.model;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.HUIActivity;
import com.qinggan.app.arielapp.capability.push.factory.BasePushMessageModel;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;

import java.util.Map;

public class HUIPushMode extends BasePushMessageModel {

    private String domain;
    private String domainDescribe;

    @Override
    public void doService(String pushBody) {
        if (TextUtils.isEmpty(pushBody)) {
            return;
        }
        Map<String, Object> retMap = gson.fromJson(pushBody, new TypeToken<Map<String, Object>>() {
        }.getType());
        if (retMap != null) {
            String commonData = retMap.get("commonData").toString();
            domain = commonData.replace(" ", "").split(",")[1].split("=")[1];
            domainDescribe = commonData.replace(" ", "").split(",")[2].split("=")[1];
            Log.d("HUIPushMode","domain : "+domain+" domainDescribe : "+domainDescribe);
            showHUIActivity(domain, domainDescribe);
        } else {
            ToastUtil.show("push message format has changed!",ArielApplication.getApp());
            Log.d("HUIPushMode","pushBody : "+pushBody);
        }
    }


    private void showHUIActivity(String domain, String domainDescribe) {
        Bundle bundle = new Bundle();
        bundle.putString("domain", domain);
        bundle.putString("domainDescribe", domainDescribe);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(ArielApplication.getApp(), HUIActivity.class);
        ArielApplication.getApp().startActivity(intent);
    }


}
