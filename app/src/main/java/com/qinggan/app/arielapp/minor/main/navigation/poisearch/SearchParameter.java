package com.qinggan.app.arielapp.minor.main.navigation.poisearch;

import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;

public class SearchParameter {
    private int loopTimes;

    private int addRadius;


    private  long timeOut = 10000;


    private PoiNearbySearchOption nearbySearchOption;



    public int getLoopTimes() {
        return loopTimes;
    }

    public void setLoopTimes(int loopTimes) {
        this.loopTimes = loopTimes;
    }


    public int getAddRadius() {
        return addRadius;
    }

    public void setAddRadius(int addRadius) {
        this.addRadius = addRadius;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }



    public PoiNearbySearchOption getNearbySearchOption() {
        return nearbySearchOption;
    }

    public void setNearbySearchOption(PoiNearbySearchOption nearbySearchOption) {
        this.nearbySearchOption = nearbySearchOption;
    }

}
