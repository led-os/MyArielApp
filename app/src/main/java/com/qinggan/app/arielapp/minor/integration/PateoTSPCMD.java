package com.qinggan.app.arielapp.minor.integration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.core.TSPInterface;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.utils.NetUtils;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.utils.WLog;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.models.userfavorite.AddFavoriteResp;
import com.qinggan.mobile.tsp.models.userfavorite.CancelFavoriteResp;
import com.qinggan.mobile.tsp.restmiddle.RestCallback;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by brian on 18-11-3.
 */

public class PateoTSPCMD implements TSPInterface {

    private static final String TAG = "PateoTSPCMD";
    private static PateoTSPCMD instance;
    private final Context mContext;


    public PateoTSPCMD(Context context) {
        mContext = context;
    }

    @Override
    public void getVehicleDetailInfo() {

        if (!checkNetworkConnection(EventBusTSPInfo.BUSSINESS_TYPE_VEHICLE_INFO, -1)) {
            return;
        }

        if (ArielApplication.getmUserInfo() != null && !ArielApplication.getmUserInfo().getVin().equals("")) {
            String vin = ArielApplication.getmUserInfo().getVin();
            //获取车辆信息
            TspManager.getInstance(mContext).getStatusAndAlerts(vin, new RestCallback<VehicleDetailInfo>() {
                @Override
                public void success(VehicleDetailInfo vehicleDetailInfo, RestResponse restResponse) {
                    releaseVehicleDetailInfoEvent(true, null, vehicleDetailInfo);
                }

                @Override
                public void failure(RestError restError) {
                    releaseVehicleDetailInfoEvent(false, restError, null);
                }
            });
        } else {
            RestError restError = new RestError();
            restError.setMessage("user has not been login , or there is no vehicle has been binded");
            releaseVehicleDetailInfoEvent(false, restError, null);
        }
    }

    @Override
    public void getFavorAddressList() {

        if (!checkNetworkConnection(EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO, MapUtils.TSP_COMMAND_TYPE_QUERY)) {
            return;
        }

        TspManager.getInstance(mContext).getAddressFavoriteByType(new RestCallback<AddFavoriteResp>() {
            @Override
            public void success(AddFavoriteResp addFavoriteRequest, RestResponse restResponse) {
                List<AddressBean> addressList = new ArrayList<>();
                if (addFavoriteRequest.getData() != null && !addFavoriteRequest.getData().isEmpty()) {
                    for (AddFavoriteResp.DataBean dataBean : addFavoriteRequest.getData()) {
                        AddressBean bean = new Gson().fromJson(dataBean.getContent(), AddressBean.class);
                        bean.setSid(dataBean.getSid());
                        addressList.add(bean);
                    }
                }
                MapUtils.releaseAddressBeanEvent(true, MapUtils.TSP_COMMAND_TYPE_QUERY, null, addressList);

            }

            @Override
            public void failure(RestError restError) {
                MapUtils.releaseAddressBeanEvent(false, MapUtils.TSP_COMMAND_TYPE_QUERY, restError, null);

            }
        });
    }

    @Override
    public void addFavorAddress(AddressBean addressBean) {
        addFavorAddress(addressBean, true);
    }

    @Override
    public void addFavorAddress(final AddressBean addressBean, final boolean isNotifyUI) {
        if (!checkNetworkConnection(EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO, MapUtils.TSP_COMMAND_TYPE_INSERT, addressBean)) {
            return;
        }
        final String tempUID = addressBean.getUid();
        Log.d(TAG, "addFavorAddress tempUID: " + tempUID);
        if (!isNotifyUI) {
            addressBean.setSid("");
        }
        String address = new Gson().toJson(addressBean);
        TspManager.getInstance(mContext).addAddressFavoriteByType(address, new RestCallback<AddFavoriteResp>() {
            @Override
            public void success(AddFavoriteResp addFavoriteRequest, RestResponse restResponse) {
                List<AddressBean> addressList = new ArrayList<>();
                try {
                    for (AddFavoriteResp.DataBean dataBean : addFavoriteRequest.getData()) {
                        AddressBean bean = new Gson().fromJson(dataBean.getContent(), AddressBean.class);
                        bean.setSid(dataBean.getSid());
                        addressList.add(bean);
                        Log.d(TAG, "addFavorAddress addressBean: " + bean);
                        break;
                    }
                    if (isNotifyUI) {
                        MapUtils.releaseAddressBeanEvent(true, MapUtils.TSP_COMMAND_TYPE_INSERT, null, addressList);
                    } else {
                        //本地数据同步云端后回调,更新本地数据的同步状态,sid and syncFlag
                        if (addressList.size() > 0) {
                            AddressBean tempAddress = new AddressBean();
                            tempAddress.setUid(tempUID);
                            NaviInfo naviInfo = getTargetNaviInfo(tempAddress);
                            Log.d(TAG, "addFavorAddress naviInfo: " + naviInfo);
                            if (naviInfo != null) {
                                naviInfo.setSid(addressList.get(0).getSid());
                                naviInfo.setSyncFlag(MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);

                                IntegrationCore.getIntergrationCore(mContext).updatePresetDest(naviInfo, mContext, NaviInfo.class.getName());
                            }
                        }
                    }
                } catch (Exception e) {
                    if(isNotifyUI) {
                        List<AddressBean> tspFailList = new ArrayList<>();
                        tspFailList.add(addressBean);
                        MapUtils.releaseAddressBeanEvent(false, MapUtils.TSP_COMMAND_TYPE_INSERT, null, tspFailList);
                    }
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RestError restError) {
                if(isNotifyUI) {
                    List<AddressBean> tspFailList = new ArrayList<>();
                    tspFailList.add(addressBean);
                    MapUtils.releaseAddressBeanEvent(false, MapUtils.TSP_COMMAND_TYPE_INSERT, restError, tspFailList);
                }
            }
        });
    }

    @Override
    public void updateFavorAddress(AddressBean addressBean) {
        updateFavorAddress(addressBean,true);
    }

    @Override
    public void updateFavorAddress(final AddressBean addressBean, final boolean isNotifyUI) {
        if (!checkNetworkConnection(EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO, MapUtils.TSP_COMMAND_TYPE_UPDATE, addressBean)) {
            return;
        }

        String address = new Gson().toJson(addressBean);
        TspManager.getInstance(mContext).updateAddressFavoriteByType(address, addressBean.getSid(), new RestCallback<AddFavoriteResp>() {
            @Override
            public void success(AddFavoriteResp addFavoriteRequest, RestResponse restResponse) {
                WLog.d(TAG, "updateFavorAddress AddressBean success:" + addressBean);
                List<AddressBean> addressList = new ArrayList<>();
                addressList.add(addressBean);
                if(isNotifyUI) {
                    MapUtils.releaseAddressBeanEvent(true, MapUtils.TSP_COMMAND_TYPE_UPDATE, null, addressList);
                } else {
                    //本地数据同步云端后回调,更新本地数据的同步状态
                    NaviInfo naviInfo = getTargetNaviInfo(addressBean);
                    if (naviInfo != null) {
                        naviInfo.setSyncFlag(MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);

                        IntegrationCore.getIntergrationCore(mContext).updatePresetDest(naviInfo, mContext, NaviInfo.class.getName());
                    }
                }
            }

            @Override
            public void failure(RestError restError) {
                WLog.d(TAG, "updateFavorAddress AddressBean fail:" + addressBean);
                if(isNotifyUI) {
                    List<AddressBean> tspFailList = new ArrayList<>();
                    tspFailList.add(addressBean);
                    MapUtils.releaseAddressBeanEvent(false, MapUtils.TSP_COMMAND_TYPE_UPDATE, restError, tspFailList);
                }
            }
        });

    }

    @Override
    public void delFavorAddress(final String sid) {
        delFavorAddress(sid, true);
    }

    @Override
    public void delFavorAddress(final String sid, final boolean isNotifyUI) {

        final AddressBean addressBean = new AddressBean();
        addressBean.setSid(sid);
        if (!checkNetworkConnection(EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO, MapUtils.TSP_COMMAND_TYPE_DELETE,addressBean)) {
            return;
        }

        TspManager.getInstance(mContext).cancelAddressFavoriteByType(sid, new RestCallback<CancelFavoriteResp>() {
            @Override
            public void success(CancelFavoriteResp addFavoriteRequest, RestResponse restResponse) {
                WLog.d(TAG, "delete AddressBean success sid:" + sid);
                List<AddressBean> addressList = new ArrayList<>();
                addressList.add(addressBean);
                if (isNotifyUI) {
                    MapUtils.releaseAddressBeanEvent(true, MapUtils.TSP_COMMAND_TYPE_DELETE, null, addressList);
                } else {
                    //本地数据同步云端后回调,更新本地数据
                    NaviInfo naviInfo = getTargetNaviInfo(addressBean);
                    if (naviInfo != null && naviInfo.getSyncFlag() == MapUtils.NAVIINFO_SYNC_FLAG_DELETE) {
                        IntegrationCore.getIntergrationCore(mContext).deleteNaviInfo(naviInfo, mContext, NaviInfo.class.getName());
                    }
                }
            }

            @Override
            public void failure(RestError restError) {
                WLog.d(TAG, "delete AddressBean failure sid:" + sid);
                if(isNotifyUI) {
                    List<AddressBean> tspFailList = new ArrayList<>();
                    tspFailList.add(addressBean);
                    MapUtils.releaseAddressBeanEvent(false, MapUtils.TSP_COMMAND_TYPE_DELETE, restError, tspFailList);
                }
            }
        });
    }

    private NaviInfo getTargetNaviInfo(AddressBean addressBean) {
        NaviInfo naviInfo = new NaviInfo();
        if (addressBean.getUid() != null && !addressBean.getUid().equals("")) {
            naviInfo.setUid(addressBean.getUid());
        } else if (addressBean.getSid() != null && !addressBean.getSid().equals("")) {
            naviInfo.setSid(addressBean.getSid());
        } else {
            return null;
        }

        List<NaviInfo> naviInfos = (List<NaviInfo>) (List) IntegrationCore.getIntergrationCore(mContext)
                .queryDestInfoByFilter(naviInfo, mContext, NaviInfo.class.getName(),false);
        if (naviInfos != null && naviInfos.size() > 0) {
            naviInfo = naviInfos.get(0);
        }
        return naviInfo;
    }

    private void releaseVehicleDetailInfoEvent(boolean isSuccess, RestError error, VehicleDetailInfo vehicleDetailInfo) {
        EventBusTSPInfo event = new EventBusTSPInfo<VehicleDetailInfo>();
        event.setSuccess(isSuccess);
        event.setBussinessType(EventBusTSPInfo.BUSSINESS_TYPE_VEHICLE_INFO);
        if (isSuccess) {
            event.setModule(vehicleDetailInfo);
        } else {
            event.setRestError(error);
        }
        EventBus.getDefault().post(event);
    }

    public static PateoTSPCMD getInstance(Context context) {
        if (instance == null) {
            instance = new PateoTSPCMD(context);
        }
        return instance;
    }

    private boolean checkNetworkConnection(int businessType, int commandType) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager == null || NetUtils.NETWORK_NONE == NetUtils.getNetWorkState(mConnectivityManager)) {
            Log.d("checkNetworkConnection", "NetWorkState: " + NetUtils.getNetWorkState(mConnectivityManager));
            RestError restError = new RestError();
            restError.setMessage("network is not connected,failed to connect");
            if (EventBusTSPInfo.BUSSINESS_TYPE_VEHICLE_INFO == businessType) {
                releaseVehicleDetailInfoEvent(false, restError, null);
            } else if (EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO == businessType) {
                MapUtils.releaseAddressBeanEvent(false, commandType, restError, null);
            }
            return false;
        }
        return true;
    }

    private boolean checkNetworkConnection(int businessType, int commandType, AddressBean addressBean) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (mConnectivityManager == null || NetUtils.NETWORK_NONE == NetUtils.getNetWorkState(mConnectivityManager)) {
            Log.d("checkNetworkConnection", "NetWorkState: " + NetUtils.getNetWorkState(mConnectivityManager));
            RestError restError = new RestError();
            restError.setMessage("network is not connected,failed to connect");

            List<AddressBean> tspFailList = new ArrayList<>();
            tspFailList.add(addressBean);
            MapUtils.releaseAddressBeanEvent(false, commandType, restError, tspFailList);

            return false;
        }
        return true;
    }

}