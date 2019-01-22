package com.qinggan.app.arielapp.minor.integration;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.core.NaviInterface;
import com.qinggan.app.dcs.location.util.LocationPreferenceUtil;

import org.json.JSONObject;

/**
 * Created by brian on 18-11-3.
 */

public class BaiduNaviCMD implements NaviInterface{
    private static final String SAVE_PREFILE = "com.baidu.duer.dcs.sample.sdk.location.LocationImpl.SAVE_PREFILE";

    @Override
    public void planRoute(Context context, String from, LatLng dest) {
        boolean isInstalled = IntegrationCore.
                getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                isSomeAppInstalled("com.baidu.BaiduMap",
                        ArielApplication.getApp().getApplicationContext());
        LatLng origin = null;
        if(isInstalled) {
            try {
                String saveLocalString = (String) LocationPreferenceUtil.get(context, SAVE_PREFILE, "");
                if (!TextUtils.isEmpty(saveLocalString)) {
                    JSONObject saveLocalJson = new JSONObject(saveLocalString);
                    double longitude = saveLocalJson.optDouble("longitude");
                    double latitude = saveLocalJson.optDouble("latitude");
                    origin = new LatLng(latitude, longitude);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (origin == null) {
                return;
            }

//            Intent GuiHuaIntent = new Intent();
//            GuiHuaIntent.setData(Uri.parse("baidumap://map/direction?origin=name:" +
//                    from + "|latlng:" + origin.latitude + "," + origin.longitude + "&destination="
//                    + dest.latitude + "," + dest.longitude + "&mode=driving&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"));
//            GuiHuaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(GuiHuaIntent);
            gotoBaidu(context,dest.latitude,dest.longitude);
        }

    }


    @Override
    public void planRoute(Context context,String from,LatLng dest,LatLng origin) {
        boolean isInstalled = IntegrationCore.
                getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                isSomeAppInstalled("com.baidu.BaiduMap",
                        ArielApplication.getApp().getApplicationContext());
        if(isInstalled) {
//            Intent GuiHuaIntent = new Intent();
//            GuiHuaIntent.setData(Uri.parse("baidumap://map/direction?origin=name:" +
//                    from + "|latlng:" + origin.latitude + "," + origin.longitude + "&destination="
//                    + dest.latitude + "," + dest.longitude + "&mode=driving&sy=3&index=0&target=1&src=andr.baidu.openAPIdemo"));
//            GuiHuaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(GuiHuaIntent);
            gotoBaidu(context,dest.latitude,dest.longitude);
        }
    }



    private void gotoBaidu(Context context,double latitude, double longitude) {
        StringBuilder loc = new StringBuilder();
        loc.append("baidumap://map/navi?src=");
        loc.append("com.qinggan.app.arielapp");
        loc.append("&coord_type=gcj02");
        loc.append("&location=");
        loc.append(latitude);
        loc.append(",");
        loc.append(longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse(loc.toString()));
        intent.setPackage("com.baidu.BaiduMap");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void startWalkNavi(Context context,LatLng origin,LatLng dest) {
        boolean isInstalled = IntegrationCore.
                getIntergrationCore(ArielApplication.getApp().getApplicationContext()).
                isSomeAppInstalled("com.baidu.BaiduMap",
                        ArielApplication.getApp().getApplicationContext());
        if(isInstalled) {
            Intent walkNavIntent = new Intent();
            walkNavIntent.setData(Uri.parse("baidumap://map/walknavi?origin=" + origin.latitude + ","
                    + origin.longitude + "&destination=" + dest.latitude + "," + dest.longitude
                    + "&src=andr.baidu.openAPIdemo"));
            walkNavIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(walkNavIntent);
        }
    }

}
