package com.qinggan.app.arielapp.minor.main.navigation.poisearch;

import com.baidu.mapapi.cloud.BaseSearchResult;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiResult;

import java.util.List;

public interface ISearchCallBack {
    void showSearchResult(PoiResult result);

    void searchFail(String errMsg);

    void searchStart();

    void searchNoResult();

    void searchTimeOut();

    void invalidSearch();

    void showPoiDetail(PoiDetailResult var1);

    void showPoiDetail(PoiDetailSearchResult var1);

}
