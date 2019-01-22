package com.qinggan.app.arielapp.minor.core;

import android.content.Context;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by brian on 18-10-30.
 */

public interface NaviInterface {
    public void planRoute(Context context,String from,LatLng dest);
    public void planRoute(Context context,String from,LatLng dest,LatLng origin);
    public void startWalkNavi(Context context,LatLng origin,LatLng dest);
}
