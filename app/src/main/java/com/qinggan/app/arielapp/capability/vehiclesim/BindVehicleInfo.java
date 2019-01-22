package com.qinggan.app.arielapp.capability.vehiclesim;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.certification.CertificationStatusRsp;
import com.qinggan.mobile.tsp.models.safecode.AddSafecodeRsp;
import com.qinggan.mobile.tsp.models.tbox.BindVehicleDetailRsp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import java.util.ArrayList;

/**
 * <获取车机sim卡相关信息>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-28]
 * @see [相关类/方法]
 * @since [V1]
 */
public class BindVehicleInfo {
    private static String TAG = BindVehicleInfo.class.getSimpleName();
    //车机相关信息
    private static String iccid;
    private static String tpdsn;
    private static String pdsn;
    private static String imsi;

    private static SharedPreferences sharedPreferences = ArielApplication.getApp().getSharedPreferences("vehicle_tbox", Context.MODE_PRIVATE);

    public static String getIccid() {
        if (TextUtils.isEmpty(iccid))
            iccid = sharedPreferences.getString("iccid", "");
        Log.d(TAG, "getIccid:" + iccid);
        return iccid;
    }

    public static void setIccid(String iccid) {
        Log.d(TAG, "setIccid:" + iccid);
        BindVehicleInfo.iccid = iccid;
        sharedPreferences.edit().putString("iccid", iccid).commit();
    }

    public static String getTpdsn() {
        if (TextUtils.isEmpty(tpdsn))
            tpdsn = sharedPreferences.getString("tpdsn", "");
        Log.d(TAG, "getTpdsn:" + tpdsn);
        return tpdsn;
    }

    public static void setTpdsn(String tpdsn) {
        Log.d(TAG, "setTpdsn:" + tpdsn);
        BindVehicleInfo.tpdsn = tpdsn;
        sharedPreferences.edit().putString("tpdsn", tpdsn).commit();
        if (!TextUtils.isEmpty(tpdsn)) {
            UserInfo userInfo = ArielApplication.getmUserInfo();
            userInfo.setTpdsn(tpdsn);
            ArielApplication.setmUserInfo(userInfo);
        }

    }

    public static void setPdsn(String pdsn) {
        Log.d(TAG, "setPdsn:" + pdsn);
        BindVehicleInfo.pdsn = pdsn;
        sharedPreferences.edit().putString("pdsn", pdsn).commit();
    }

    public static String getPdsn() {
        if (TextUtils.isEmpty(pdsn))
            pdsn = sharedPreferences.getString("pdsn", "");
        Log.d(TAG, "getPdsn:" + pdsn);
        return pdsn;
    }

    public static String getImsi() {
        if (TextUtils.isEmpty(imsi))
            imsi = sharedPreferences.getString("imsi", "");
        Log.d(TAG, "getImsi:" + imsi);
        return imsi;
    }

    public static void setImsi(String imsi) {
        Log.d(TAG, "setImsi:" + imsi);
        BindVehicleInfo.imsi = imsi;
        sharedPreferences.edit().putString("imsi", imsi).commit();
    }

    public static boolean isHasPin() {
        return sharedPreferences.getBoolean("pin", false);
    }

    public static void setHasPin(boolean hasPin) {
        sharedPreferences.edit().putBoolean("pin", hasPin).commit();
    }

    /**
     * 实名认证
     *
     * @param auth
     */
    public static void setAuth(boolean auth) {
        sharedPreferences.edit().putBoolean("auth", auth).commit();
    }

    public static boolean isAuth() {
        return sharedPreferences.getBoolean("auth", false);
    }

    /**
     * 清除
     */
    public static void clear() {
        sharedPreferences.edit().clear().commit();
        tpdsn = null;
        iccid = null;
        imsi = null;
    }


    /**
     * 登录之后获取绑定车辆tbox及sim的相关信息
     */
    public static void getVehicleListDetail() {
        TspManager.getInstance(ArielApplication.getApp()).getVehicleListDetail(new RestCallback<BindVehicleDetailRsp>() {
            @Override
            public void success(BindVehicleDetailRsp bindVehicleDetailRsp, RestResponse restResponse) {
                if (null != bindVehicleDetailRsp && "0".equals(bindVehicleDetailRsp.getStatusCode())) {
                    ArrayList<BindVehicleDetailRsp.BindVehicleAllTypesRes> data = bindVehicleDetailRsp.getData();
                    if (null != data && !data.isEmpty()) {
                        BindVehicleDetailRsp.BindVehicleAllTypesRes res = data.get(0);
                        BindVehicleDetailRsp.VehicleInfo vehicleInfo = res.getVehicleInfo();
                        if (null != vehicleInfo) {
                            BindVehicleDetailRsp.DeviceTboxRes deviceTboxRes = vehicleInfo.getTboxDetail();
                            if (null != deviceTboxRes) {
                                String tpdsn = deviceTboxRes.getDeviceId();
                                setTpdsn(tpdsn);
//                                if (null != deviceTboxRes.getSimDetail()) {
//                                    setIccid(deviceTboxRes.getSimDetail().getIccid());
//                                    setImsi(deviceTboxRes.getSimDetail().getDeviceId());
//                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void failure(RestError restError) {

            }
        });
    }

    /**
     * 校验是否设置过安全码
     */
    public static void checkPin(String vin) {
        TspManager.getInstance(ArielApplication.getApp()).checkPin(vin, "", new RestCallback<AddSafecodeRsp>() {
            @Override
            public void success(AddSafecodeRsp addSafecodeRsp, RestResponse restResponse) {
                if (null != addSafecodeRsp) {
                    String status = addSafecodeRsp.getStatusCode();
                    if ("1".equals(status)) {
                        setHasPin(true);
                    } else if ("2".equals(status)) {
                        setHasPin(false);
                    }
                }
            }

            @Override
            public void failure(RestError restError) {
            }
        });
    }

    /**
     * 检查实名认证状态
     */
    public static void findCertificationStatus(String vin, String accountId) {
        TspManager.getInstance(ArielApplication.getApp()).findCertificationStatus(vin, accountId, new RestCallback<CertificationStatusRsp>() {
            @Override
            public void success(CertificationStatusRsp certificationStatusRsp, RestResponse restResponse) {
                if (null != certificationStatusRsp && null != certificationStatusRsp.getData()) {
                    String status = certificationStatusRsp.getData().getStatus();
                    if ("2".equals(status)) {
                        //实名认证成功
                        setAuth(true);
                    } else {
                        //未实名认证
                        setAuth(false);
                    }
                } else {
                    //数据异常
                }
            }

            @Override
            public void failure(RestError restError) {
                //异常
            }
        });
    }

    //    private static volatile VehicleSIM instance;
//
//    public static VehicleSIM getInstance() {
//        if (null == instance) {
//            synchronized (VehicleSIM.class) {
//                if (null == instance)
//                    instance = new VehicleSIM();
//            }
//        }
//        return instance;
//    }
//
//    OperatorManager operatorManager;
//
//    //车机sim卡信息
//    private String iccid;
//    private String imsi;
//
//    private VehicleSIM() {
//        Log.d(TAG, "init VehicleSIM start");
//        OnInitListener onInitListener = new OnInitListener() {
//            @Override
//            public void onConnectStatusChange(boolean b) {
//
//            }
//        };
//
//        OnConnectListener onConnectListener = new OnConnectListener() {
//            @Override
//            public void onConnect(boolean b) {
//                Log.d(TAG, "onConnectListener,onConnect:" + b);
//                if (b)
//                    operatorManager.sendGetSimcardInfo();
//            }
//        };
//        operatorManager = OperatorManager.getInstance(ArielApplication.getApp(), onInitListener, onConnectListener);
//        operatorManager.registerListener(new OperatorListener() {
//            @Override
//            public void onGetSimcardInfo() {
//                Log.d(TAG, "OperatorListener,onGetSimcardInfo");
//            }
//
//            @Override
//            public void onSimcardInfo(String iccid1, String imei1, String imsi1) {
//                Log.d(TAG, "OperatorListener,onSimcardInfo:iccid:" + iccid1 + ",imei:" + imei1 + ",imsi:" + imsi1);
//                iccid = iccid1;
//                imsi = imsi1;
//            }
//        });
//        Log.d(TAG, "init VehicleSIM end");
//    }
//
//    public String getIccid() {
//        return iccid;
//    }
//
//    public String getImsi() {
//        return imsi;
//    }
}
