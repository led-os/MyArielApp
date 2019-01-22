package com.qinggan.app.arielapp.minor.integration;

import android.content.Context;
import android.util.Log;

import com.qinggan.app.arielapp.minor.core.VehicleControlInterface;
import com.qinggan.app.arielapp.minor.scenario.RefreshUICallback;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.app.voiceapi.DataTypeConstant;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.car.CarOrderBean;
import com.qinggan.app.voiceapi.control.car.WindowController;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.qinglink.api.md.RadioManager;

import java.util.UUID;

public  class PateoVehicleControlCMD implements VehicleControlInterface {

    private final String TAG = "VehicleControlCMD_LOG";

    RefreshUICallback uiCallback;

    private static PateoVehicleControlCMD instance;
    private PateoVehicleControlCMD(){};

    public static PateoVehicleControlCMD getInstance(){
        if (instance == null){
            Log.d("zfsasuke","init PateoVehicleControlCMD");
            instance = new PateoVehicleControlCMD();
        }
        return instance;
    }

    public void setUiCallback(RefreshUICallback callback){
        Log.d("sasuke","set call back");
        uiCallback = callback;
    }

    private boolean result = false;

    /**
     * 一键热车(远程启动车辆发动机)
     * @param context
     */
    @Override
    public  void startCar(final Context context) {
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_WORM_CAR, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("一键热车成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("一键热车失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }

    }


    /**
     * 一键透气
     * @param context
     */
    @Override
    public void refreshAir(final Context context) {
        Log.d(TAG,"refresh air");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_CLOD, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("一键清凉成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("一键清凉失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }
    }


    /**
     * 车辆解锁
     * @param context
     */
    @Override
    public void unlockCar(final Context context) {
        Log.d(TAG,"unlock car");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.vehicleUnlocking(new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("车辆解锁成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("车辆解锁失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }
    }


    /**
     * 关闭所有车窗
     * @param context
     */
    @Override
    public void closeWindow(final Context context) {
        Log.d(TAG,"close window");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_CLOSE_WINDOW, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("车窗关闭成功",context);
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("车窗关闭失败",context);
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
            }
        });
    }


    /**
     *
     * @param context
     * @param isInner  true:切换内循环　　false:切换外循环
     */
    @Override
    public void switchLoop(final Context context, boolean isInner) {
        Log.d(TAG,"switch loop");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.loopSwitch(isInner, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("循环切换成功",context);
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("循环切换失败",context);
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
            }
        });
    }

    @Override
    public void snowMode(final Context context) {
        Log.d(TAG,"snow mode");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_RAIN_MODE, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("切换雨雪模式成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("切换雨雪模式失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }
    }

    @Override
    public void smokeMode(final Context context) {
        Log.d(TAG,"smoke mode");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_SMOK_MODE, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("切换抽烟模式成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("切换抽烟模式失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }
    }

    @Override
    public void warmMode(final Context context) {
        Log.d(TAG,"warm mode");
        VehcleControlManager vehcleControlManager = VehcleControlManager.getInstance(context);
        vehcleControlManager.oneSmartControl(VehcleControlManager.VEHCLECONTROL_ONE_KEY_WORM, new RemoteResponseListener() {

            @Override
            public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                ToastUtil.show("一键温暖成功",context);
                result = true;
            }

            @Override
            public void onSendFailure(RestError restError) {
                ToastUtil.show("一键温暖失败",context);
                result = false;
            }

            @Override
            public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

            }

            @Override
            public void onCmdTimeout() {
                ToastUtil.show("命令执行超时",context);
                result = false;
            }
        });
        if (uiCallback != null){
            uiCallback.refreshUI(result);
        }
    }

}
