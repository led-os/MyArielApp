package com.qinggan.app.arielapp.minor.controller;

import android.content.Context;

public class CarControl {
    private Context mContext;
    private static CarControl mCarControl;
    private static final Object mLock = new Object();

    public CarControl(Context context){
        mContext = context;
    }

    public static CarControl getInstance(Context context) {
        synchronized (mLock) {
            if (mCarControl == null) {
                mCarControl = new CarControl(context);
            }
        }
        return mCarControl;
    }

    /*
    *single control
    */
    public void setAirCondition(boolean status){
        //TODO
    }

    public void setWind(boolean status){
        //TODO
    }

    public void setDoor(boolean status){
        //TODO
    }

    public void setEngine(boolean status){
        //TODO
    }

    public void setTopWin(){
        //TODO
    }

    public void setBackDoor(){
        //TODO
    }

    /*
    *Profiles
    */

    public void oneKeyHeatCar(){
        //TODO
    }

    public void oneKeyCool(){
        //TODO
    }

    public void oneKeyWarm(){
        //TODO
    }

    public void oneKeyCloseWins(){
        //TODO
    }
}
