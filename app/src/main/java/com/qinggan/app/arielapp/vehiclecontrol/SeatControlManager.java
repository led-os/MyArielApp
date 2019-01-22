package com.qinggan.app.arielapp.vehiclecontrol;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.user.Bean.SeatBean;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.dcs.location.util.LocationPreferenceUtil;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BlueKeyService;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.seat.DeviceSettingRequest;
import com.qinggan.mobile.tsp.seat.DeviceSettingResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.mobile.tsp.util.NetUtil;
import com.qinggan.qinglink.enumeration.AirConditionState;
import com.qinggan.qinglink.enumeration.SituationalModeState;
import com.qinggan.qinglink.enumeration.VehicleState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yorashe on 18-11-22.
 */

public class SeatControlManager {
    private static SeatControlManager sVehcleControlManager;
    private Context mContext;
    private final String mSeatControl="SeatControl";

    private List<SeatBean> deviceSettingBeans =new ArrayList<>();
    private String sid;

    public static SeatControlManager getInstance(Context context) {
        if (null == sVehcleControlManager) {
            sVehcleControlManager = new SeatControlManager(context);
        }
        return sVehcleControlManager;
    }

    private SeatControlManager(Context context) {
        this.mContext = context;
    }


    /**
     * 保存或者添加座椅记忆设置
     */
    public void addSeatMemorySetting(){
        boolean isadd=true;
        int seat = 0;
        if (deviceSettingBeans!= null && deviceSettingBeans.size()>0){
            for (int i=0;i<deviceSettingBeans.size();i++){
                if (deviceSettingBeans.get(i).getUid()==ArielApplication.getmUserInfo().getUid() && deviceSettingBeans.get(i).getUid()!=0){
                    isadd=false;
                    seat=deviceSettingBeans.get(i).getSeatAccount();
                }
            }
        }

        if (isadd){
            seat=deviceSettingBeans.size()+1;
            SeatBean seatBean =new SeatBean();
            seatBean.setUid(ArielApplication.getmUserInfo().getUid());
            seatBean.setSeatAccount(seat);
            deviceSettingBeans.add(seatBean);
            String value=new Gson().toJson(deviceSettingBeans);
            TspManager.getInstance(mContext).addSeatMemorySetting(value, new RestCallback<DeviceSettingResponse>() {
                @Override
                public void success(DeviceSettingResponse bean, RestResponse restResponse) {
                    if (null != bean && null != bean.getData() && !bean.getData().isEmpty()) {
                        DeviceSettingRequest settingRequest = bean.getData().get(0);
                        sid=settingRequest.getSid();
                        if (!TextUtils.isEmpty(settingRequest.getValue())){
                            deviceSettingBeans=new Gson().fromJson(settingRequest.getValue(),new TypeToken<List<SeatBean>>() {
                            }.getType());
                        }
                    }
                }

                @Override
                public void failure(RestError restError) {

                }
            });
        }
        HashMap<VehicleState, Integer> cmdMap = new HashMap<>();
        cmdMap.put(VehicleState.SMM_MEMORY_ACCOUNT_SET, seat);
        cmdMap.put(VehicleState.SMM_MEMORY_FUNCTION_OPERATION_SET, VehicleState.SMM_NEW);
        ArielApplication.getCanBusManager().setVehicleState(cmdMap);
        LocationPreferenceUtil.put(mContext,mSeatControl,seat);
    }


    /**
     * 查询并添加座椅记忆
     */
    public void getAndAddSeatMemorySetting(){
        TspManager.getInstance(mContext).getSeatMemorySetting( new RestCallback<DeviceSettingResponse>() {
            @Override
            public void success(DeviceSettingResponse bean, RestResponse restResponse) {
                if (null != bean && null != bean.getData() && !bean.getData().isEmpty()) {
                    DeviceSettingRequest settingRequest = bean.getData().get(0);
                    sid=settingRequest.getSid();
                    if (!TextUtils.isEmpty(settingRequest.getValue())){
                        deviceSettingBeans=new Gson().fromJson(settingRequest.getValue(),new TypeToken<List<SeatBean>>() {
                        }.getType());
                        addSeatMemorySetting();
                    }
                }else if (null != bean && TextUtils.equals(bean.getStatusCode(),"500")){
                    addSeatMemorySetting();
                }

            }

            @Override
            public void failure(RestError restError) {

            }
        });
    }


    /**
     * 查询存在座椅记忆配置的情况下执行座椅恢复
     */
    public void transferSeatMemorySetting(){
        int seat = (int) LocationPreferenceUtil.get(mContext,mSeatControl,-1);
        if (seat!=-1 && ArielApplication.getmUserInfo()!=null){
            HashMap<VehicleState, Integer> cmdMap = new HashMap<>();
            cmdMap.put(VehicleState.SMM_MEMORY_ACCOUNT_SET, seat);
            cmdMap.put(VehicleState.SMM_MEMORY_FUNCTION_OPERATION_SET, VehicleState.SMM_TRANSFER);
            ArielApplication.getCanBusManager().setVehicleState(cmdMap);
        }
    }


    /**
     * rfcom连接成功后通知车机端更细座椅账户
     */
    public void updateSeatMemorySetting(){
        if (NetUtil.isNetworkConnected(mContext)){
            addSeatMemorySetting();
        }else{
            int seat = (int) LocationPreferenceUtil.get(mContext,mSeatControl,-1);
            if (seat!=-1 && ArielApplication.getmUserInfo()!=null) {
                HashMap<VehicleState, Integer> cmdMap = new HashMap<>();
                cmdMap.put(VehicleState.SMM_MEMORY_ACCOUNT_SET, seat);
                cmdMap.put(VehicleState.SMM_MEMORY_FUNCTION_OPERATION_SET, VehicleState.SMM_NEW);
                ArielApplication.getCanBusManager().setVehicleState(cmdMap);
                LocationPreferenceUtil.put(mContext, mSeatControl, seat);
            }
        }

    }



    public void clearLocaSeatMemorySetting(){
        LocationPreferenceUtil.put(mContext,mSeatControl,-1);

    }

}
