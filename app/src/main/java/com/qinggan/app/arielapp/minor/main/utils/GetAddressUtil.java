package com.qinggan.app.arielapp.minor.main.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetAddressUtil {
    Context context;

    public GetAddressUtil(Context context) {
        this.context = context;
    }
        // private MKSearch mkSearch;
        // 创建地理编码检索实例
        static GeoCoder geoCoder = GeoCoder.newInstance();

        /**
         * 将经度纬度反向译为文字地址
         *
         * @param lon
         *            经度
         * @param lat
         *            纬度
         * @param listener
         *            OnGetGeoCoderResultListener监听器，对接收到的结果进行处理
         */
        public static void reverseGeoParse(double lon, double lat,
        OnGetGeoCoderResultListener listener) {
            geoCoder.setOnGetGeoCodeResultListener(listener);
            LatLng pt1 = new LatLng(lat, lon);
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(pt1));
        }
    }