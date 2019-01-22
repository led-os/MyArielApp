package com.qinggan.app.arielapp.minor.main.navigation.poisearch;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import  com.baidu.mapapi.search.core.SearchResult;

import java.text.DecimalFormat;


public class PoiSearchUtil implements
        OnGetPoiSearchResultListener {
    private PoiSearch poiSearch = null;
    private SearchParameter searchParameter;
    private int loopedTimes;
    private boolean stopSearch;
    private Handler handler;
    private double EARTH_RADIUS = 6371.393;
    protected ISearchCallBack searchCallBack;

    private Runnable runnableTimeout = new Runnable() {
        @Override
        public void run() {
            searchCallBack.searchTimeOut();
            stopSearch = true;
        }
    };

    public void initPresent(ISearchCallBack callBack) {
        searchCallBack = callBack;
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(this);
        handler = new Handler();
    }


    public void clearData() {
        poiSearch.destroy();
        handler = null;
    }


    //    @Override
    public void searchNearBy(SearchParameter parameter) {
        stopSearch = false;
        searchCallBack.searchStart();
        searchParameter = parameter;
        poiSearch.searchNearby(parameter.getNearbySearchOption());
        handler.removeCallbacksAndMessages(runnableTimeout);
        handler.postDelayed(runnableTimeout, searchParameter.getTimeOut());

    }

    public void nextPage() {
        stopSearch = false;
        PoiNearbySearchOption option = searchParameter.getNearbySearchOption();
        if (option != null) {
            option.mPageNum++;
        }
        poiSearch.searchNearby(option);
        handler.removeCallbacksAndMessages(runnableTimeout);
        handler.postDelayed(runnableTimeout, searchParameter.getTimeOut());
    }

    public void searchInBound(SearchParameter parameter) {
        stopSearch = false;
        searchCallBack.searchStart();
        searchParameter = parameter;
        poiSearch.searchNearby(parameter.getNearbySearchOption());
        handler.removeCallbacksAndMessages(runnableTimeout);
        handler.postDelayed(runnableTimeout, searchParameter.getTimeOut());

    }

    public void searchByPoiInfo(PoiInfo poi) {
        Log.i("minos", "searchByPoiInfo poi = " + poi.toString());
        if (poi == null) {
            searchCallBack.invalidSearch();
        }
        poiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(poi.uid));
        Log.i("minos", "searchByPoiInfo poi =====");
    }

    public void searchByPoiUid(String uid) {
        if (TextUtils.isEmpty(uid)) {
            searchCallBack.invalidSearch();
        }
        poiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUid(uid));
    }

    /**
     * 以周边搜索搜索参数中设置的经纬度计算
     * 默认单位:米
     *
     * @param latLng
     * @return
     */
    public double getDistance(LatLng latLng) {
        PoiNearbySearchOption option = searchParameter.getNearbySearchOption();
        double radLat1 = rad(option.mLocation.latitude);
        double radLat2 = rad(latLng.latitude);
        double radLon1 = rad(option.mLocation.longitude);
        double radLon2 = rad(latLng.longitude);
        double a = radLat1 - radLat2;
        double b = radLon1 - radLon2;
        double d = Math.acos(Math.sin(option.mLocation.latitude) * Math.sin(latLng.latitude)
                + Math.cos(option.mLocation.latitude) * Math.cos(latLng.latitude) * Math.cos(option.mLocation.longitude - latLng.longitude)) * EARTH_RADIUS;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = Math.round(s * 1000);
        return s;
    }


    /**
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public String getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double radLon1 = rad(lng1);
        double radLon2 = rad(lng2);
        double a = radLat1 - radLat2;
        double b = radLon1 - radLon2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        DecimalFormat df = new DecimalFormat("#.00");
        String dis = null;
        if (s > 1) {
            dis = df.format(s) + "km";
        } else {
            s = Math.round(s * 1000);
            dis = (int) s + "m";
        }
        return dis;
    }


    @Override
    public void onGetPoiResult(PoiResult result) {
        if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR) {
            handler.removeCallbacksAndMessages(runnableTimeout);
            if (result.getAllPoi() != null && result.getAllPoi().size() != 0) {
                searchCallBack.showSearchResult(result);
            }
        } else if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            if (loopedTimes == 0 || loopedTimes == searchParameter.getLoopTimes()) {
                handler.removeCallbacksAndMessages(runnableTimeout);
                searchCallBack.searchNoResult();
            } else if (!stopSearch && result == null && loopedTimes < searchParameter.getLoopTimes()) {
                loopedTimes += 1;
                PoiNearbySearchOption poiNearbySearchOption = searchParameter.getNearbySearchOption();
                poiNearbySearchOption.radius(searchParameter.getAddRadius() * loopedTimes);
            }
        } else {
            searchCallBack.searchFail(getErrorString(result.error));
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        handler.removeCallbacksAndMessages(runnableTimeout);
        if (poiDetailResult != null && poiDetailResult.error.equals(SearchResult.ERRORNO.NO_ERROR)) {
            searchCallBack.showPoiDetail(poiDetailResult);
        }

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
        handler.removeCallbacksAndMessages(runnableTimeout);
        if (poiDetailSearchResult != null && poiDetailSearchResult.error.equals(SearchResult.ERRORNO.NO_ERROR)) {
            searchCallBack.showPoiDetail(poiDetailSearchResult);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    public static String getErrorString(SearchResult.ERRORNO errNo) {
        if (errNo == SearchResult.ERRORNO.PERMISSION_UNFINISHED) {
            return "key错误";
        } else if(errNo == SearchResult.ERRORNO.NETWORK_TIME_OUT) {
            return "网络连接错误";
        } else {
            return "网络连接错误";
        }
    }


    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

}
