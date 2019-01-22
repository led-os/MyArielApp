package com.qinggan.app.arielapp.minor.controller;

import com.qinggan.app.arielapp.minor.utils.ArielLog;

/**
 * Created by brian on 18-11-1.
 */

public class StageController {
    public enum Stage{
        MAIN_LEAVE,     //主界面-离车模式
        MAIN_IN_CAR,    //主界面-驾驶模式
        CENTER,         //个人中心
        MUSIC,          //音乐界面
        NAVIGATION,     //导航界面
        RADIO,          //电台界面
        PHONE,          //电话界面
        SCENARIO,       //场景界面
        OUT_OF_APP,     //非本应用界面下
    }

    private static StageController mStageController;
    private static final Object mLock = new Object();
    private boolean isDuringCall = false;

    private Stage mCurrentStage = Stage.OUT_OF_APP;

    public static final int BG_STREAM_NONE = 0xA1;
    public static final int BG_STREAM_MUSIC = 0xA2;
    public static final int BG_STREAM_RADIO = 0xA3;
    public static final int BG_STREAM_NEWS = 0xA4;
    private int mBGStream = BG_STREAM_NONE;

    public void setBGStream(int bgStream){
        ArielLog.logController(ArielLog.LEVEL_DEBUG, "Brian", "Set bg stream " +
                mBGStream);
        mBGStream = bgStream;
    }

    public int getBGStream(){
        return mBGStream;
    }

    public synchronized Stage getCurrentStage(){
        return mCurrentStage;
    }

    public static StageController getStageController(){
        synchronized (mLock) {
            if (mStageController == null) {
                mStageController = new StageController();
            }
        }
        return mStageController;
    }

    private StageController(){
        //TODO:
    }

    public void setStage(Stage stage){
        mCurrentStage = stage;
    }

    public void setIsDuringCall(boolean flag) {
        isDuringCall = flag;
    }

    public boolean getIsDuringCall() {
        return isDuringCall;
    }
}
