package com.qinggan.app.arielapp.minor.core;

import android.content.Context;

public interface VehicleControlInterface {

    /*一键热车*/
    public void startCar(Context context);


    /*一键透气*/
    public void refreshAir(Context context);


    /*车辆解锁*/
    public void unlockCar(Context context);


    /*关闭车窗*/
    public void closeWindow(Context context);


    /*内外循环切换*/
    public void switchLoop(Context context,boolean inner);

    /*雨雪雾模式*/
    public void snowMode(Context context);

    /*抽烟模式*/
    public void smokeMode(Context context);

    /*一键温暖*/
    public void warmMode(Context context);
}
