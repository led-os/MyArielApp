package com.qinggan.app.arielapp.minor.main.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.utils.WLog;
import com.qinggan.mobile.tsp.restmiddle.RestError;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 地图工具类
 * by zsq
 * ****/
public class MapUtils {
    private static final String TAG = MapUtils.class.getSimpleName();
    private static boolean isNaviInfoSyncing = false;
    private PoiSearch mPoiSearch = null;
    List<PoiInfo> allAddr = new ArrayList<PoiInfo>();//poi检索结果list
    private RoutePlanSearch mSearch;
    private Context mContect;

    public MapUtils(Context mContect) {
        this.mContect = mContect;
    }

    public static int TSP_COMMAND_TYPE_INSERT = 0;
    public static int TSP_COMMAND_TYPE_DELETE = 1;
    public static int TSP_COMMAND_TYPE_UPDATE = 2;
    public static int TSP_COMMAND_TYPE_QUERY  = 3;
    public static int TSP_COMMAND_TYPE_SYNC  = 4;

    public static int NAVIINFO_SYNC_FLAG_NORMAL = 0;
    public static int NAVIINFO_SYNC_FLAG_INSERT = 1;
    public static int NAVIINFO_SYNC_FLAG_UPDATE = 2;
    public static int NAVIINFO_SYNC_FLAG_DELETE = 3;

    public static String NAVIINFO_ADDRESS_TYPE_FAVOUR  = "0";
    public static String NAVIINFO_ADDRESS_TYPE_HOME    = "1";
    public static String NAVIINFO_ADDRESS_TYPE_COMPANY = "2";

    public static boolean IS_NOTIFY_UI_WHEN_SYNC = false;

    //获取规划路线
    public void setElement(LatLng st_point, LatLng end_point) {

        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(listener);

        PlanNode stNode = PlanNode.withLocation(st_point);
        PlanNode enNode = PlanNode.withLocation(end_point);
        mSearch.drivingSearch((new DrivingRoutePlanOption())
                .from(stNode)
                .to(enNode));
    }

    //根据关键字搜索
    public void searchByText(String str,LatLng cenLatLng) {
        //初始化搜索模块
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(str);
        option.sortType(PoiSortType.distance_from_near_to_far);
        option.pageNum(1);
        option.pageCapacity(30);
        option.radius(10000);
        option.location(cenLatLng);
        mPoiSearch.searchNearby(option);
    }


    OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }


        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }


        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }


        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult result) {

            if (result != null) {
                List<DrivingRouteLine> routeLineList = result.getRouteLines();
                if (routeLineList != null && !routeLineList.isEmpty() && routeLineList.size() != 0) {
                    long time=routeLineList.get(0).getDuration();//耗时
                    int len=routeLineList.get(0).getDistance();//距离
                    EventBus.getDefault().post(new EventBusBean("navTime",time+""));
                    Log.i("Alan","预计耗时=-=" +Tools.formatDuring(time*1000)+"，距离=-="+(Math.round( len / 100d) / 10d )+"km");
                    EventBus.getDefault().post(new EventBusBean("setElement",Tools.formatDuring(time*1000),(Math.round( len / 100d) / 10d )+"km"));
//                    String instruction = routeLineList.get(0).getAllStep().get(0).getInstructions();
//                    Toast.makeText(mContect, routeLineList.get(0).getAllStep().size()
//                            + "-------:" + instruction, Toast.LENGTH_SHORT).show();
                }
            }
        }


        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
            Toast.makeText(mContect, mContect.getString(R.string.no_locate), Toast.LENGTH_SHORT).show();
        }

        public void onGetBikingRouteResult(BikingRouteResult result) {

        }
    };


    //poi检索监听
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) { //获取POI检索结果
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(mContect, mContect.getString(R.string.no_result), Toast.LENGTH_SHORT).show();
                return;
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result != null) {
                    //获取poi检索结果
                    allAddr = result.getAllPoi();
                    EventBus.getDefault().post(new EventBusBean("poiList",allAddr));
                    return;
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            //获取Place详情页检索结果
            poiDetailResult.getAddress();
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

    };

    public static AddressBean naviInfo2AddressBean(NaviInfo naviInfo) {
        AddressBean addressBean = new AddressBean();

        addressBean.setAddress(naviInfo.getAddress());
        addressBean.setDisplayName(naviInfo.getDisplayName());
        addressBean.setIsFavour(naviInfo.getIsFavour());
        addressBean.setIsPreset(naviInfo.getIsPreset());
        addressBean.setLatitude(Double.valueOf(naviInfo.getPoiLat()));
        addressBean.setLongitude(Double.valueOf(naviInfo.getPoiLno()));
        addressBean.setName(naviInfo.getName());
        addressBean.setSid(naviInfo.getSid());
        addressBean.setType(Integer.valueOf(naviInfo.getAddressType()));
        addressBean.setUid(naviInfo.getUid());
        if (naviInfo.getLastModifiedDate() != null) {
            addressBean.setLastModifiedDate(naviInfo.getLastModifiedDate().getTime());
        }

        return addressBean;
    }


    public static NaviInfo addressBean2NaviInfo(AddressBean addressBean) {
        NaviInfo naviInfo = new NaviInfo();

        naviInfo.setAddress(addressBean.getAddress());
        naviInfo.setAddressType(addressBean.getType()+"");
        naviInfo.setDisplayName(addressBean.getDisplayName());
        naviInfo.setIsFavour(addressBean.getIsFavour());
        naviInfo.setIsPreset(addressBean.getIsPreset());
        naviInfo.setName(addressBean.getName());
        naviInfo.setPoiLat(addressBean.getLatitude()+"");
        naviInfo.setPoiLno(addressBean.getLongitude()+"");
        naviInfo.setSid(addressBean.getSid());
        naviInfo.setUid(addressBean.getUid());
        try {
            if (addressBean.getLastModifiedDate() != null) {
                naviInfo.setLastModifiedDate(new Date(addressBean.getLastModifiedDate()));
            }
        } catch (RuntimeException exception) {
            WLog.d(TAG, "parse address bean date exception:" + exception);
            exception.printStackTrace();
        }

        return naviInfo;
    }

    public static void releaseAddressBeanEvent(boolean isSuccess, int commandType, RestError error, List<AddressBean> module) {
        EventBusTSPInfo event = new EventBusTSPInfo<List<AddressBean>>();
        event.setSuccess(isSuccess);
        event.setBussinessType(EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO);
        event.setCommandType(commandType);
        if (!isSuccess) {
            event.setRestError(error);
        }
        event.setModule(module);
        EventBus.getDefault().post(event);
    }

    public static void syncNaviInfo(Context context) {
        IntegrationCore.getIntergrationCore(context).getTSPNaviInfo();
    }

    public static void clearNaviInfo(Context context) {
        IntegrationCore.getIntergrationCore(context).deleteAllNaviInfo(context, NaviInfo.class.getName());
    }

    public static void clearNaviSearchHistory(Context context) {
        IntegrationCore.getIntergrationCore(context).deleteAllNaviInfo(context, NaviSearchHistory.class.getName());
    }

    public static void syncNaviIfoAfterTSPRequest(Context context, EventBusTSPInfo event) {
        WLog.d(TAG, "syncNaviIfoAfterTSPRequest");
        if (isNaviInfoSyncing) {
            WLog.d(TAG, "skip this sync");
            return;
        }
        isNaviInfoSyncing = true;
        try {
            naviInfoSync(context, event);
        } catch (Exception e) {
            WLog.d(TAG, " navi info sync exception:" + e.getMessage());
            e.printStackTrace();
        } finally {
            isNaviInfoSyncing = false;
        }
    }

    private static void naviInfoSync(Context context, EventBusTSPInfo event) {
        List<AddressBean> addressBeanList = (List<AddressBean>) (List) event.getModule();
        List<NaviInfo> localNaviInfoList = (List<NaviInfo>) (List) IntegrationCore.getIntergrationCore(context).searchDbData(context, NaviInfo.class.getName());
        Map<String, NaviInfo> tspNaviInfoMap = new HashMap<>();
        Map<String, NaviInfo> localeNaviInfoMap = new HashMap<>();
        List<NaviInfo> tspNaviInfoList = new ArrayList<>();

        NaviInfo tspPresetNaviInfo = null;
        NaviInfo localPresetNaviInfo = null;
        NaviInfo tspHomeNaviInfo = null;
        NaviInfo localHomeNaviInfo = null;
        NaviInfo tspCompanyNaviInfo = null;
        NaviInfo localCompanyNaviInfo = null;

        if (addressBeanList != null) {
            for (AddressBean bean : addressBeanList) {
                WLog.d(TAG, "TSP AddressBean:" + bean);
                NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(bean);
                if (context.getString(R.string.preset).equals(naviInfo.getName()) && "true".equals(naviInfo.getIsPreset())) {
                    tspPresetNaviInfo = naviInfo;
                } else if(NAVIINFO_ADDRESS_TYPE_HOME.equals(naviInfo.getAddressType())) {
                    tspHomeNaviInfo = naviInfo;
                } else if(NAVIINFO_ADDRESS_TYPE_COMPANY.equals(naviInfo.getAddressType())){
                    tspCompanyNaviInfo = naviInfo;
                } else {
                    tspNaviInfoMap.put(naviInfo.getUid(), naviInfo);
                    tspNaviInfoList.add(naviInfo);
                }
            }
        }

        if (localNaviInfoList != null) {
            for (NaviInfo naviInfo : localNaviInfoList) {
                WLog.d(TAG, "local NaviInfo:" + naviInfo);
                if (context.getString(R.string.preset).equals(naviInfo.getName()) && "true".equals(naviInfo.getIsPreset())) {
                    localPresetNaviInfo = naviInfo;
                } else if(NAVIINFO_ADDRESS_TYPE_HOME.equals(naviInfo.getAddressType())) {
                    localHomeNaviInfo = naviInfo;
                } else if(NAVIINFO_ADDRESS_TYPE_COMPANY.equals(naviInfo.getAddressType())){
                    localCompanyNaviInfo = naviInfo;
                } else {
                    localeNaviInfoMap.put(naviInfo.getUid(), naviInfo);
                }
            }

            if (localPresetNaviInfo != null) {
                localNaviInfoList.remove(localPresetNaviInfo);
            }
            if (localHomeNaviInfo != null) {
                localNaviInfoList.remove(localHomeNaviInfo);
            }
            if (localCompanyNaviInfo != null) {
                localNaviInfoList.remove(localCompanyNaviInfo);
            }
        }

        syncSingleNaviInfoData(context, localPresetNaviInfo, tspPresetNaviInfo);
        syncSingleNaviInfoData(context, localHomeNaviInfo, tspHomeNaviInfo);
        syncSingleNaviInfoData(context, localCompanyNaviInfo, tspCompanyNaviInfo);

        syncTSPExistData(context, localeNaviInfoMap, tspNaviInfoList);
        syncLocalExistData(context, localNaviInfoList, tspNaviInfoMap);
    }

    private static void syncTSPExistData(Context context, Map<String, NaviInfo> localeNaviInfoMap, List<NaviInfo> tspNaviInfoList) {
        if (tspNaviInfoList != null) {
            for (NaviInfo naviInfo : tspNaviInfoList) {
                WLog.d(TAG, "tsp NaviInfo:" + naviInfo);
                NaviInfo temp = localeNaviInfoMap.get(naviInfo.getUid());
                if (temp != null) {
                    //云端有，本地也有
                    if (temp.getLastModifiedDate() == null || naviInfo.getLastModifiedDate() == null) {
                        WLog.d(TAG, "tsp invalidate skip NaviInfo:" + naviInfo);
                        continue;
                    }
                    if (temp.getLastModifiedDate().getTime() < naviInfo.getLastModifiedDate().getTime()) {
                        // update locale info
                        WLog.d(TAG, "syncTSPExistData update locale info:" + naviInfo);
                        temp.setName(naviInfo.getName());
                        temp.setIsPreset(naviInfo.getIsPreset());
                        temp.setDisplayName(naviInfo.getDisplayName());
                        temp.setAddressType(naviInfo.getAddressType());
                        temp.setAddress(naviInfo.getAddress());
                        temp.setLastModifiedDate(naviInfo.getLastModifiedDate());
                        temp.setSyncFlag(NAVIINFO_SYNC_FLAG_NORMAL);
                        IntegrationCore.getIntergrationCore(context).updatePresetDest(temp, context, NaviInfo.class.getName());
                    } else if (temp.getLastModifiedDate().getTime() > naviInfo.getLastModifiedDate().getTime()) {
                        // update TSP info
                        WLog.d(TAG, "syncTSPExistData update TSP info:" + naviInfo);
                        naviInfo.setName(temp.getName());
                        naviInfo.setIsPreset(temp.getIsPreset());
                        naviInfo.setDisplayName(temp.getDisplayName());
                        naviInfo.setAddressType(temp.getAddressType());
                        naviInfo.setAddress(temp.getAddress());
                        naviInfo.setLastModifiedDate(temp.getLastModifiedDate());
                        // 同步数据到云端，同时更新本地数据库同步标识
                        if (temp.getSyncFlag() == NAVIINFO_SYNC_FLAG_DELETE) {
                            IntegrationCore.getIntergrationCore(context).deleteTSPNaviInfo(naviInfo, IS_NOTIFY_UI_WHEN_SYNC);
                        } else {
                            IntegrationCore.getIntergrationCore(context).updateTSPNaviInfo(naviInfo, IS_NOTIFY_UI_WHEN_SYNC);
                        }
                    }

                } else {
                    //云端有，本地没有
                    //非本机上传数据,insert locale info
                    WLog.d(TAG, "syncTSPExistData insert locale info:" + naviInfo);
                    naviInfo.setSyncFlag(NAVIINFO_SYNC_FLAG_NORMAL);
                    IntegrationCore.getIntergrationCore(context).savePresetDest(naviInfo, context, NaviInfo.class.getName());
                }
            }
        }
    }

    private static void syncLocalExistData(Context context, List<NaviInfo> localNaviInfoList, Map<String, NaviInfo> tspNaviInfoMap) {
        if (localNaviInfoList != null) {
            for (NaviInfo naviInfo : localNaviInfoList) {
                NaviInfo temp = tspNaviInfoMap.get(naviInfo.getUid());
                if (temp == null) {
                    //云端没有,本地有
                    //delete locale info
                    if (naviInfo.getSyncFlag() == NAVIINFO_SYNC_FLAG_DELETE) {
                        WLog.d(TAG, "syncLocalExistData delete locale info:" + naviInfo);
                        IntegrationCore.getIntergrationCore(context).deleteNaviInfo(naviInfo, context, NaviInfo.class.getName());
                    } else {
                        WLog.d(TAG, "syncLocalExistData saveTSPNaviInfo:" + naviInfo);
                        IntegrationCore.getIntergrationCore(context).saveTSPNaviInfo(naviInfo, IS_NOTIFY_UI_WHEN_SYNC);
                    }
                }
            }
        }
    }


    private static void syncSingleNaviInfoData(Context context, NaviInfo localNaviInfo, NaviInfo tspNaviInfo) {
        if (localNaviInfo != null && tspNaviInfo != null) {
            //云端有，本地也有
            if (localNaviInfo.getLastModifiedDate().getTime() < tspNaviInfo.getLastModifiedDate().getTime()) {
                WLog.d(TAG, "syncSingleNaviInfoData  update locale info:" + tspNaviInfo);
                updateNaviInfoByTSP(localNaviInfo, tspNaviInfo);
                IntegrationCore.getIntergrationCore(context).updatePresetDest(localNaviInfo, context, NaviInfo.class.getName());
            } else if (localNaviInfo.getLastModifiedDate().getTime() > tspNaviInfo.getLastModifiedDate().getTime()) {
                WLog.d(TAG, "syncSingleNaviInfoData update TSP info:" + tspNaviInfo);
                updateNaviInfo(tspNaviInfo, localNaviInfo);
                if (localNaviInfo.getSyncFlag() == NAVIINFO_SYNC_FLAG_DELETE) {
                    IntegrationCore.getIntergrationCore(context).deleteTSPNaviInfo(tspNaviInfo, IS_NOTIFY_UI_WHEN_SYNC);
                } else {
                    IntegrationCore.getIntergrationCore(context).updateTSPNaviInfo(tspNaviInfo, IS_NOTIFY_UI_WHEN_SYNC);
                }
            } else {
                WLog.d(TAG, "syncSingleNaviInfoData  need not update localNaviInfo:" + localNaviInfo );
            }
        } else if (localNaviInfo == null && tspNaviInfo != null) {
            //云端有，本地没有
            WLog.d(TAG, "syncSingleNaviInfoData insert locale info:" + tspNaviInfo);
            tspNaviInfo.setSyncFlag(NAVIINFO_SYNC_FLAG_NORMAL);
            IntegrationCore.getIntergrationCore(context).savePresetDest(tspNaviInfo, context, NaviInfo.class.getName());
        } else if (localNaviInfo != null && tspNaviInfo == null) {
            //云端没有,本地有
            if (localNaviInfo.getSyncFlag() == NAVIINFO_SYNC_FLAG_DELETE) {
                //delete locale info
                WLog.d(TAG, "syncSingleNaviInfoData delete locale info:" + localNaviInfo);
                IntegrationCore.getIntergrationCore(context).deleteNaviInfo(localNaviInfo, context, NaviInfo.class.getName());
            } else {
                //upload to TSP
                WLog.d(TAG, "syncSingleNaviInfoData saveTSPNaviInfo:" + localNaviInfo);
                IntegrationCore.getIntergrationCore(context).saveTSPNaviInfo(localNaviInfo, IS_NOTIFY_UI_WHEN_SYNC);
            }
        } else {
            WLog.d(TAG, "syncSingleNaviInfoData all navi info is null" );
        }
    }

    private static void updateNaviInfoByTSP(NaviInfo localNaviInfo, NaviInfo tspNaviInfo) {
        updateNaviInfo(localNaviInfo, tspNaviInfo);
        localNaviInfo.setSyncFlag(NAVIINFO_SYNC_FLAG_NORMAL);
    }

    private static void updateNaviInfo(NaviInfo targetNaviInfo, NaviInfo orginNaviInfo) {
        targetNaviInfo.setName(orginNaviInfo.getName());
        targetNaviInfo.setIsPreset(orginNaviInfo.getIsPreset());
        targetNaviInfo.setDisplayName(orginNaviInfo.getDisplayName());
        targetNaviInfo.setAddressType(orginNaviInfo.getAddressType());
        targetNaviInfo.setAddress(orginNaviInfo.getAddress());
        targetNaviInfo.setPoiLat(orginNaviInfo.getPoiLat());
        targetNaviInfo.setPoiLno(orginNaviInfo.getPoiLno());
        targetNaviInfo.setUid(orginNaviInfo.getUid());

        targetNaviInfo.setLastModifiedDate(orginNaviInfo.getLastModifiedDate());
    }

    public static List<NaviInfo> queryAllPresetNaviInfo(Context context){
        List<NaviInfo> naviInfos = new ArrayList<>();
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsPreset("true");
        naviInfo.setName(context.getString(R.string.preset));
        List<BasicInfo> basicInfos = IntegrationCore.getIntergrationCore(context).queryDestInfo(naviInfo, context, NaviInfo.class.getName());

        if(basicInfos != null
                && basicInfos.size() >0){
            for(BasicInfo basicInfo : basicInfos){
                if(basicInfo instanceof  NaviInfo){
                    naviInfos.add((NaviInfo) basicInfo);
                }
            }
        }
        return naviInfos;
    }

    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    public void getLocation() {
        mLocClient = new LocationClient(mContect);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    /**
     * 获取当前位置
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            location.getLocType();
            double CurrentLat = location.getLatitude();
            double CurrentLon = location.getLongitude();
//           location.getCity();
            mLocClient.stop();
            EventBus.getDefault().post(new EventBusBean("location", location));
        }

    }

    public double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }
    public void onReceivePoi(BDLocation poiLocation) {
    }
}
