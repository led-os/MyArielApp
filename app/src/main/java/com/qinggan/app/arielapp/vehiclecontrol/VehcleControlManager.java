package com.qinggan.app.arielapp.vehiclecontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.utils.InterceptorProxyUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BlueKeyService;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.mobile.tsp.util.NetUtil;
import com.qinggan.qinglink.enumeration.AirConditionState;
import com.qinggan.qinglink.enumeration.SituationalModeState;
import com.qinggan.qinglink.enumeration.VehicleState;


/**
 * Created by Yorashe on 18-11-22.
 */

public class VehcleControlManager {
    //    一键热车，一键透气，一键关窗，车辆解锁，切换内循环
    private static VehcleControlManager sVehcleControlManager;
    private Context mContext;
    private String mOpenString;
    public static final String VEHCLECONTROL_WORM_CAR = "VEHCLECONTROL_WORM_CAR";//1:一键热车
    public static final String VEHCLECONTROL_ONE_KEY_WORM = "VEHCLECONTROL_ONE_KEY_WORM";//2：一键温暖
    public static final String VEHCLECONTROL_ONE_KEY_CLOD = "VEHCLECONTROL_ONE_KEY_CLOD";//3：一键清凉,一键透气
    public static final String VEHCLECONTROL_CLOSE_WINDOW = "VEHCLECONTROL_CLOSE_WINDOW";//4：关闭所有门窗,一键关窗
    public static final String VEHCLECONTROL_RAIN_MODE = "VEHCLECONTROL_RAIN_MODE";// 5：雨雪雾模式
    public static final String VEHCLECONTROL_SMOK_MODE = "VEHCLECONTROL_SMOK_MODE";//6：抽烟模式
    public static final String VEHCLECONTROL_SMOG_MODE = "VEHCLECONTROL_SMOG_MODE";// 7：雾霾
    private boolean isRemoting=false;
    private Handler mHandler =new Handler();

    public static VehcleControlManager getInstance(Context context) {
        if (null == sVehcleControlManager) {
            sVehcleControlManager = new VehcleControlManager(context);
        }
        return sVehcleControlManager;
    }

    private VehcleControlManager(Context context) {
        this.mContext = context;
    }

    /**
     * 一键情景模式
     *
     * @param type     1:一键热车 2：一键温暖 3：一键清凉 ,一键透气4：关闭所有门窗 ,一键关窗5：雨雪雾模式 6：抽烟模式 7：雾霾
     * @param callback
     */
    public void oneSmartControl(String type, RemoteResponseListener callback) {
        if (!canUseVehiClecontrol()) {
            return;
        }
        //        if (isInCarMode()) {
//            ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_ONE_BUTTON_WARMTH_MODE, VehicleState.SWITCH_OPEN);
//            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
//            callbackBean.setCode("0");
//            callback.onSendSuccess(callbackBean, null);
//        } else {
//            TspManager.getInstance(mContext).vhlCtrlSmart(type, callback);
//        }
        switch (type) {
            case VEHCLECONTROL_WORM_CAR:
                    startEnine(callback);
                break;
            case VEHCLECONTROL_ONE_KEY_WORM:
                if (VehicleUtils.isACCOn()){
                    ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_ONE_BUTTON_WARMTH_MODE, VehicleState.SWITCH_OPEN);
                }else{
                    setAirWithWorm(callback);
                }
                break;
            case VEHCLECONTROL_ONE_KEY_CLOD:
                if (VehicleUtils.isACCOn()){
                    ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_RAPID_COOLING_MODE, VehicleState.SWITCH_OPEN);
                }else{
                    setAirWithCold(callback);
                }
                break;
            case VEHCLECONTROL_CLOSE_WINDOW:
                openWindow(false, callback);
                break;
            case VEHCLECONTROL_RAIN_MODE:
//                if (VehicleUtils.isACCOn())
                ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_RAIN_SNOW_MODE, VehicleState.SWITCH_OPEN);
                break;
            case VEHCLECONTROL_SMOK_MODE:
//                if (VehicleUtils.isACCOn())
                ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_SMOKING_MODE, VehicleState.SWITCH_OPEN);
                break;
            case VEHCLECONTROL_SMOG_MODE:
//                if (VehicleUtils.isACCOn())
                ArielApplication.getCanBusManager().setSituationalModeState(SituationalModeState.AC_HAZE_MODE, VehicleState.SWITCH_OPEN);

                break;
            default:
                break;
        }

    }

    /**
     * 切换内循环
     *
     * @param callback
     * @param isInternalLoop true　内循环,false 外循环
     */
    public void loopSwitch(boolean isInternalLoop, RemoteResponseListener callback) {
        if (!canUseVehiClecontrol()) {
            return;
        }
        if (isInCarMode()) {
            ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_RECIRC_AIR, AirConditionState.INTERNAL_LOOP);
            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
            callbackBean.setCode("0");
            callback.onSendSuccess(callbackBean, null);
        } else {
            TspManager.getInstance(mContext).vhlCtrlLoopSwitch(true, callback);
        }

    }

    /**
     * 车辆解锁
     *
     * @param callback
     */
    public void vehicleUnlocking(RemoteResponseListener callback) {
        if (BleKeyManager.getInstance(mContext).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
            BleKeyManager.getInstance(mContext).setDoor(1);
            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
            callbackBean.setCode("0");
            callback.onSendSuccess(callbackBean, null);
        }else{
            if (!canUseRemoteVehiClecontrol()) {
                return;
            }
            mOpenString="车辆解锁";
            TspManager.getInstance(mContext).vhlCtrlLookDoor(false, callback!=null?callback:defaultListener);
        }

    }

    /**
     * 启动发动机
     *
     * @param callback
     */
    public void startEnine(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
            mOpenString="打开热车模式";
            TspManager.getInstance(mContext).vhlCtrlEnine(true, callback!=null?callback:defaultListener);

    }


    /**
     * 设置空调24度
     *
     * @param callback
     */
    public void setAirWithWorm(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
//        if (isInCarMode()) {
//            ArielApplication.getCanBusManager().setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);
//            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
//            callbackBean.setCode("0");
//            callback.onSendSuccess(callbackBean, null);
//        } else {
        mOpenString="打开一键温暖";
        TspManager.getInstance(mContext).vhlCtrlOpenAirWithWorm(callback!=null?callback:defaultListener);
//        }

    }

    /**
     * 设置空调16度
     *
     * @param callback
     */
    public void setAirWithCold(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="打开一键制冷";
        TspManager.getInstance(mContext).vhlCtrlOpenAirWithCold(callback!=null?callback:defaultListener);

    }


    public boolean isInCarMode() {
        return PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.IN_CAR_MODE;

    }

    /**
     * 登录后才可以使用
     */

    private boolean canUseVehiClecontrol() {
        if (!NetUtil.isNetworkConnected(mContext)){
//            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.no_network_tips));
            ToastUtil.show(R.string.no_network_tips,mContext);
            return false;
        } else if (ArielApplication.getmUserInfoWithLogin() == null) {
            VoicePolicyManage.getInstance().speak("请先登录并绑定车辆后再使用车控");
            return false;
        } else if (TextUtils.isEmpty(TspManager.getmPdsn())) {
            Context context = ArielApplication.getApp();
            Intent intent = new Intent(context, BindCarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            VoicePolicyManage.getInstance().speak("请先绑定车辆后再使用车控");
            return false;
        }
        return true;
    }

    /**
     * 远程车控使用权限
     */
    private boolean canUseRemoteVehiClecontrol() {
        if (!NetUtil.isNetworkConnected(mContext)){
//            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.no_network_tips));
            ToastUtil.show(R.string.no_network_tips,mContext);
            return false;
        } else if (ArielApplication.getmUserInfoWithLogin() == null) {
            VoicePolicyManage.getInstance().speak("请先登录并绑定车辆后再使用车控");
            return false;
//        } else if(!BleKeyHelper.isCarOwnerKey()){
//            VoicePolicyManage.getInstance().speak("只有车主才能使用远程车控");
//            return false;
        }
        else if (TextUtils.isEmpty(TspManager.getmPdsn())) {
            Context context = ArielApplication.getApp();
            Intent intent = new Intent(context, BindCarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            VoicePolicyManage.getInstance().speak("请先绑定车辆后再使用车控");
            return false;
        }

        if (isRemoting){
            ToastUtil.show(mContext.getString(R.string.cannot_use_vehicle_control_hint),mContext);
//            VoicePolicyManage.getInstance().speak("请误连续使用车控,稍等５秒后再试");
            return false;
        };
        isRemoting=true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isRemoting=false;
            }
        },10000);
        return true;
    }
    /**
     * 车辆bi锁
     *
     * @param callback
     */
    public void vehiclelocking(RemoteResponseListener callback) {
        if (BleKeyManager.getInstance(mContext).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
            BleKeyManager.getInstance(mContext).setDoor(0);
            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
            callbackBean.setCode("0");
            callback.onSendSuccess(callbackBean, null);
        }else{
            if (!canUseRemoteVehiClecontrol()){
                return;
            }
            mOpenString="车门上锁";
            TspManager.getInstance(mContext).vhlCtrlLookDoor(true, callback!=null?callback:defaultListener);
        }



    }


    /**
     * 关闭发动机
     *
     * @param callback
     */
    public void closeEnine(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="关闭发动机";
        TspManager.getInstance(mContext).vhlCtrlEnine(false, callback!=null?callback:defaultListener);

    }


    /**
     * 关闭空调
     *
     * @param callback
     */
    public void closeAir(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="关闭空调";
        TspManager.getInstance(mContext).vhlCtrlCloseAir(callback!=null?callback:defaultListener);

    }

    /**
     * 打开空调
     *
     * @param callback
     */
    public void openAir(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="打开空调";
        TspManager.getInstance(mContext).vhlCtrlsetAir(null, callback!=null?callback:defaultListener);

    }

    /**
     * 设置温度
     *
     * @param callback
     */
    public void setTemp(String tem, RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="温度设置成"+tem+"度";
        TspManager.getInstance(mContext).vhlCtrlsetAir(tem, callback!=null?callback:defaultListener);

    }

    /**
     * findcar
     *
     * @param callback
     */
    public void findCar(RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="寻车";
        TspManager.getInstance(mContext).vhlCtrlFindCar(callback!=null?callback:defaultListener);

    }

    /**
     * OpenTrunk
     *
     * @param callback
     */
    public void openTrunk(RemoteResponseListener callback) {
        if (BleKeyManager.getInstance(mContext).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
            BleKeyManager.getInstance(mContext).setTrunk(1);
            CarCtrlRespBean callbackBean = new CarCtrlRespBean();
            callbackBean.setCode("0");
            callback.onSendSuccess(callbackBean, null);
        }else {
            if (!canUseRemoteVehiClecontrol()) {
                return;
            }
            mOpenString = "解锁后背门";
            TspManager.getInstance(mContext).vhlCtrlOpenTrunk(callback != null ? callback : defaultListener);
        }
    }

    /**
     * OpenWindow
     *
     * @param callback
     */
    public void openWindow(boolean isopen, RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="一键开窗";
        TspManager.getInstance(mContext).vhlCtrlFourWindow(isopen, callback!=null?callback:defaultListener);

    }

    /**
     * OpenSunroof
     *
     * @param callback
     */
    public void openSunroof(boolean isopen, RemoteResponseListener callback) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="打开天窗";
        TspManager.getInstance(mContext).vhlCtrlSunroofWindow(isopen, callback!=null?callback:defaultListener);

    }


    /**
     * 获取车况
     *
     * @param callback
     */
    public void getVehicleStatus(RemoteResponseListener callback,int timeoutSecond) {
        if (!canUseRemoteVehiClecontrol()) {
            return;
        }
        mOpenString="获取车况";
        TspManager.getInstance(mContext).getVehicleStatus(callback!=null?callback:defaultListener,timeoutSecond);

    }




    public RemoteResponseListener defaultListener = new RemoteResponseListener() {
        @Override
        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
            VoicePolicyManage.getInstance().speak(mOpenString+"成功");


        }

        @Override
        public void onSendFailure(RestError restError) {
            VoicePolicyManage.getInstance().speak(mOpenString+"失败");


        }

        @Override
        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {

        }

        @Override
        public void onCmdTimeout() {
            VoicePolicyManage.getInstance().speak("指令执行超时");


        }
    };

    public boolean isRemoting() {
        return isRemoting;
    }

    public void setRemoting(boolean remoting) {
        isRemoting = remoting;
    }
}
