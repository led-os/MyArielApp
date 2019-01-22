package com.qinggan.app.arielapp.minor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by brian on 18-11-2.
 */

public class NetUtils {
    public static final int NETWORK_NONE = -1;
    public static final int NETWORK_WIFI = 0;
    public static final int NETWORK_MOBILE = 1;

    public static int getNetWorkState(ConnectivityManager connectivityManager) {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }

        return NETWORK_NONE;
    }
}
