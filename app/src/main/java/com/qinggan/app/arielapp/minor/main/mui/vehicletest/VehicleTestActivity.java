package com.qinggan.app.arielapp.minor.main.mui.vehicletest;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.user.Bean.SeatBean;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.arielapp.vehiclecontrol.SeatControlManager;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.mobile.tsp.auth.AuthLoginResponseItem;
import com.qinggan.mobile.tsp.bean.BaseBean;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.device.DeviceOnlineStatusInfo;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.seat.DeviceSettingRequest;
import com.qinggan.mobile.tsp.seat.DeviceSettingResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.qinglink.bean.AirCondition;
import com.qinggan.qinglink.enumeration.AirConditionState;
import com.qinggan.qinglink.enumeration.VehicleState;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;

/**
 * Created by Yorashe on 18-12-10.
 */

public class VehicleTestActivity extends BaseActivity implements OnClickListener {
    @BindView(R.id.set_pdsn)
    Button setPdsn;
    @BindView(R.id.pdsn_text)
    EditText pdsnText;
    @BindView(R.id.login)
    Button login;
    @BindView(R.id.open_lock)
    Button openLock;
    @BindView(R.id.start_engine)
    Button startEngine;
    @BindView(R.id.open_air)
    Button openAir;
    @BindView(R.id.open_init)
    Button open_init;
    @BindView(R.id.open_window_by_blue)
    Button openWindowByBlue;
    @BindView(R.id.open_sunroof_by_blue)
    Button openSunroofByBlue;
    @BindView(R.id.open_air_by_blue)
    Button openAirByBlue;
    @BindView(R.id.open_set_by_blue)
    Button open_set_by_blue;
    @BindView(R.id.open_set_by_blue2)
    Button open_set_by_blue2;
    @BindView(R.id.two_car)
    RadioButton twoCar;
    @BindView(R.id.three_car)
    RadioButton threeCar;
    @BindView(R.id.switch_car)
    RadioGroup switchCar;
    @BindView(R.id.get_aircondition_text)
    TextView get_aircondition_text;
    private static String mPdsn1 = "P866793030061280";//1号车
    private static String mPdsn2 = "P866793030056959";//2号车
    private static String mPdsn3 = "P866793030055753";//3号车
    @BindView(R.id.one_car)
    RadioButton oneCar;
    @BindView(R.id.login1)
    Button login1;
    @BindView(R.id.add_seat)
    Button addSeat;
    @BindView(R.id.get_seat)
    Button getSeat;
    @BindView(R.id.update_seat)
    Button updateSeat;
    @BindView(R.id.del_seat)
    Button delSeat;
    @BindView(R.id.temp1)
    Button temp1;
    @BindView(R.id.temp2)
    Button temp2;
    @BindView(R.id.query_tbox_status)
    Button queryTboxStatus;
    @BindView(R.id.wakeup_tbox)
    Button wakeupTbox;

    private List<SeatBean> deviceSettingBeans = new ArrayList<>();
    private String sid;

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(TspManager.getInstance(mContext).getmPdsn()))
            pdsnText.setText(TspManager.getInstance(mContext).getmPdsn());
    }

    @Override
    protected void initListener() {
        switchCar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.one_car:
                        pdsnText.setText(mPdsn1);
                        TspManager.getInstance(mContext).setmPdsn(pdsnText.getText().toString());
                        show("pdsn已设置为1号车：" + pdsnText.getText().toString());
                        break;
                    case R.id.two_car:
                        pdsnText.setText(mPdsn2);
                        TspManager.getInstance(mContext).setmPdsn(pdsnText.getText().toString());
                        show("pdsn已设置为2号车：" + pdsnText.getText().toString());
                        break;
                    case R.id.three_car:
                        pdsnText.setText(mPdsn3);
                        TspManager.getInstance(mContext).setmPdsn(pdsnText.getText().toString());
                        show("pdsn已设置为3号车：" + pdsnText.getText().toString());
                        break;
                }
            }
        });

    }

    @Override
    public int getLayoutId() {
        return R.layout.car_contorl_test;
    }

    private RemoteResponseListener remoteResponseListener = new RemoteResponseListener() {
        @Override
        public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
            show("发送成功");
        }

        @Override
        public void onSendFailure(RestError restError) {
            show("发送失败，请尝试登录");

        }

        @Override
        public void onCmdResult(VhlCtlResult result, RestResponse restResponse) {

        }

        @Override
        public void onCmdTimeout() {
            show("指令超时");
        }
    };
    @OnClick({R.id.set_pdsn, R.id.login, R.id.open_lock, R.id.start_engine, R.id.open_set_by_blue, R.id.open_set_by_blue2
            , R.id.open_air, R.id.open_window_by_blue, R.id.open_sunroof_by_blue, R.id.open_air_by_blue, R.id.open_init, R.id.get_aircondition
            , R.id.login1, R.id.add_seat, R.id.get_seat, R.id.update_seat, R.id.del_seat,R.id.query_tbox_status,R.id.wakeup_tbox
    })
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_pdsn:
                if (pdsnText.getText() != null) {
                    TspManager.getInstance(mContext).setmPdsn(pdsnText.getText().toString());
                    show("pdsn已设置为：" + pdsnText.getText().toString());
                }
                break;
            case R.id.login:
                TspManager.getInstance(mContext).requestUserLogin("13119489482", "123456", new RestCallback<AuthLoginResponseItem>() {
                    @Override
                    public void success(AuthLoginResponseItem identifyCodeResp, RestResponse response) {
                        setUserInfo(identifyCodeResp);
                        UserInfo mUser = ArielApplication.getmUserInfo();
                        EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                                "1"
                        ));
                        show("登录成功可以使用远程车控了");
                    }

                    @Override
                    public void failure(RestError restError) {

                    }
                });
                break;
            case R.id.login1:
                TspManager.getInstance(mContext).requestUserLogin("13914793745", "123456", new RestCallback<AuthLoginResponseItem>() {
                    @Override
                    public void success(AuthLoginResponseItem identifyCodeResp, RestResponse response) {
                        setUserInfo(identifyCodeResp);
                        UserInfo mUser = ArielApplication.getmUserInfo();
                        EventBus.getDefault().post(new EventBusBean(LOGIN_EVENT,
                                "1"
                        ));
                        show("切换账号成功可以使用远程车控了");
                    }

                    @Override
                    public void failure(RestError restError) {

                    }
                });
                break;
            case R.id.open_lock:
                VehcleControlManager.getInstance(mContext).vehicleUnlocking(remoteResponseListener);
                break;
            case R.id.start_engine:
                VehcleControlManager.getInstance(mContext).startEnine(remoteResponseListener);
                break;
            case R.id.open_air:
                VehcleControlManager.getInstance(mContext).setAirWithWorm(remoteResponseListener);
                break;
            case R.id.open_window_by_blue:
                ArielApplication.getCanBusManager().setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);
                show("发送成功");
                break;
            case R.id.open_sunroof_by_blue:
                ArielApplication.getCanBusManager().setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);
                show("发送成功");

                break;
            case R.id.open_air_by_blue:
                ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_POWER_SWITCH, AirConditionState.SWITCH_ON);
                show("发送成功");
                break;
            case R.id.query_tbox_status:
                if (!TextUtils.isEmpty(TspManager.getmPdsn()))
                TspManager.getInstance(mContext).getOnlineStatusByDeviceIds(new RestCallback<DeviceOnlineStatusInfo>() {
                    @Override
                    public void success(DeviceOnlineStatusInfo arg0, RestResponse response) {
                        queryTboxStatus.setText(arg0.getDataList().get(0).isOnline()?"查询Tbox状态（在线）":"查询Tbox状态（离线）");

                    }

                    @Override
                    public void failure(RestError restError) {
                        queryTboxStatus.setText("查询Tbox状态（失败）");

                    }
                });
                break;
            case R.id.wakeup_tbox:
                TspManager.getInstance(mContext).wakeupDevice(new RestCallback<BaseBean>() {
                    @Override
                    public void success(BaseBean arg0, RestResponse response) {
                        show("发送成功");

                    }

                    @Override
                    public void failure(RestError restError) {
                        show("唤醒失败");

                    }
                });

                break;
            case R.id.open_init:
                ArielApplication.getCanBusManager().initCanBusData();
                show("初始化成功");
                break;

            case R.id.open_set_by_blue:
                HashMap<VehicleState, Integer> cmdMap = new HashMap<>();
                cmdMap.put(VehicleState.SMM_MEMORY_ACCOUNT_SET, VehicleState.SMM_ACCOUNT1);
                cmdMap.put(VehicleState.SMM_MEMORY_FUNCTION_OPERATION_SET, VehicleState.SMM_TRANSFER);
                ArielApplication.getCanBusManager().setVehicleState(cmdMap);
                show("发送成功");
                break;
            case R.id.open_set_by_blue2:
                HashMap<VehicleState, Integer> cmdMap2 = new HashMap<>();
                cmdMap2.put(VehicleState.SMM_MEMORY_ACCOUNT_SET, VehicleState.SMM_ACCOUNT2);
                cmdMap2.put(VehicleState.SMM_MEMORY_FUNCTION_OPERATION_SET, VehicleState.SMM_TRANSFER);
                ArielApplication.getCanBusManager().setVehicleState(cmdMap2);
                show("发送成功");
                break;
            case R.id.get_aircondition:
                AirCondition airCondition = ArielApplication.getCanBusManager().getAirCondition();
                show("airCondition===" + airCondition);
                if (airCondition != null) {
                    get_aircondition_text.setText("airCondition===" + airCondition.toString());
                }
                break;
            case R.id.add_seat:
                boolean isadd = true;
                if (deviceSettingBeans != null && deviceSettingBeans.size() > 0) {
                    for (int i = 0; i < deviceSettingBeans.size(); i++) {
                        if (deviceSettingBeans.get(i).getUid() == ArielApplication.getmUserInfo().getUid() && deviceSettingBeans.get(i).getUid() != 0) {
                            isadd = false;
                        }
                    }
                }

                if (isadd) {
                    SeatBean seatBean = new SeatBean();
                    seatBean.setUid(ArielApplication.getmUserInfo().getUid());
                    seatBean.setSeatAccount(deviceSettingBeans.size());
                    deviceSettingBeans.add(seatBean);
                    String value = new Gson().toJson(deviceSettingBeans);
                    TspManager.getInstance(mContext).addSeatMemorySetting(value, new RestCallback<DeviceSettingResponse>() {
                        @Override
                        public void success(DeviceSettingResponse bean, RestResponse restResponse) {
                            if (null != bean && null != bean.getData() && !bean.getData().isEmpty()) {
                                DeviceSettingRequest settingRequest = bean.getData().get(0);
                                sid = settingRequest.getSid();
                                if (!TextUtils.isEmpty(settingRequest.getValue())) {
                                    deviceSettingBeans = new Gson().fromJson(settingRequest.getValue(), new TypeToken<List<SeatBean>>() {
                                    }.getType());
                                }
                            }
                        }

                        @Override
                        public void failure(RestError restError) {

                        }
                    });
                }

                break;
            case R.id.get_seat:
                TspManager.getInstance(mContext).getSeatMemorySetting(new RestCallback<DeviceSettingResponse>() {
                    @Override
                    public void success(DeviceSettingResponse bean, RestResponse restResponse) {
                        if (null != bean && null != bean.getData() && !bean.getData().isEmpty()) {
                            DeviceSettingRequest settingRequest = bean.getData().get(0);
                            sid = settingRequest.getSid();
                            if (!TextUtils.isEmpty(settingRequest.getValue())) {
                                deviceSettingBeans = new Gson().fromJson(settingRequest.getValue(), new TypeToken<List<SeatBean>>() {
                                }.getType());
                            }
                        }

                    }

                    @Override
                    public void failure(RestError restError) {

                    }
                });
                break;
            case R.id.update_seat:
//                DeviceSettingRequest bean = deviceSettingBeans.get(0);
//                for (DeviceSettingRequest deviceSettingBean : deviceSettingBeans) {
//                    String atributes = deviceSettingBean.getValue();
//                    if (!TextUtils.isEmpty(atributes)) {
//                        try {
//                            JSONObject object = new JSONObject(atributes);
//                            String user = object.optString("name");
//                            String seat = object.optString("seat");
//                            if (TextUtils.equals(ArielApplication.getmUserInfo().getUid() + "", user)) {
//                                JSONObject object1 = new JSONObject();
//
//                                try {
//                                    object1.put("name", user);
//                                    object1.put("seat", "2");
//                                    deviceSettingBean.setValue(object.toString());
//                                } catch (JSONException var8) {
//                                    var8.printStackTrace();
//                                }
//                                deviceSettingBean.setValue(object1.toString());
//                                bean = deviceSettingBean;
//                            }
//
//                        } catch (JSONException e) {
//                        }
//
//                    }
//                }
//                TspManager.getInstance(mContext).editSeatMemorySetting(bean, new RestCallback<BaseBean>() {
//                    @Override
//                    public void success(BaseBean baseBean, RestResponse restResponse) {
//
//                    }
//
//                    @Override
//                    public void failure(RestError restError) {
//
//                    }
//                });
                SeatControlManager.getInstance(mContext).transferSeatMemorySetting();
                break;
            case R.id.temp1:
                try {
                    int temp = (int) (ArielApplication.getCanBusManager().getAirCondition().getAirLeftTemperature() - 1);
                    ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_LEFT_TEMP, temp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.temp2:
                try {
                    int temp = (int) (ArielApplication.getCanBusManager().getAirCondition().getAirLeftTemperature() - 2);
                    ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_LEFT_TEMP, temp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.del_seat:
                TspManager.getInstance(mContext).delSeatMemorySetting(sid, new RestCallback<BaseBean>() {
                    @Override
                    public void success(BaseBean baseBean, RestResponse restResponse) {

                    }

                    @Override
                    public void failure(RestError restError) {

                    }
                });
                break;
            default:
                break;
        }

    }

    private void show(String str) {
        ToastUtil.show(str, mContext);
    }

    private void setUserInfo(AuthLoginResponseItem identifyCodeResp) {
        TspManager.getInstance(this).setGlobalToken(identifyCodeResp);
        UserInfo userInfo = new UserInfo();
        try {
            userInfo.setUid(identifyCodeResp.getUserInfo().getUid());
            userInfo.setMobilePhone(identifyCodeResp.getUserInfo().getMobilePhone());
            userInfo.setAvatar(identifyCodeResp.getUserInfo().getNickName());
            userInfo.setUserName(identifyCodeResp.getUserInfo().getUserName());
            userInfo.setUserSeq(identifyCodeResp.getUserInfo().getUserSeq());
            userInfo.setAuthLoginResponseItem(identifyCodeResp);
            Log.e(TAG, "setUserInfo() userInfo=" + userInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArielApplication.setmUserInfo(userInfo);

    }

}
