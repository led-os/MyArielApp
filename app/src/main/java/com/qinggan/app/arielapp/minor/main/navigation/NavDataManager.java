package com.qinggan.app.arielapp.minor.main.navigation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.utils.TokenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class NavDataManager {
    private static NavDataManager instance;
    private Context mContext;
    private List<BasicInfo> mNaviInfos;
    private IntegrationCore integrationCore;
    private static final String TAG = "NavDataManager";

    public static NavDataManager getInstance() {
        if (instance == null) {
            synchronized (NavDataManager.class) {
                if (instance == null) {
                    instance = new NavDataManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
    }

    //预设目的地信息
    public void savePresetDestInfo(String name, String addressType, String destLat, String destLng, String destName, String uid) {
        boolean isUpdate = false;

        NaviInfo naviInfo = new NaviInfo();
        updatePresetNaviInfo(name, addressType, destLat, destLng, destName, uid, naviInfo);

        mNaviInfos = integrationCore.searchDbData(mContext, NaviInfo.class.getName());

        if (mNaviInfos == null) {
            integrationCore.saveTSPNaviInfo(naviInfo);
            return;
        }

        for (int i = 0; i < mNaviInfos.size(); i++) {
            NaviInfo tempNaviInfo =  (NaviInfo) mNaviInfos.get(i);

            String presetName = tempNaviInfo.getName();
            String isPreset = tempNaviInfo.getIsPreset();

            Log.i(TAG, " presetName = " + presetName + " isPreset = " + isPreset);
            if (!TextUtils.isEmpty(presetName)
                    && !TextUtils.isEmpty(isPreset)) {
                if (presetName.equals(name) && isPreset.equals("true")) {
                    isUpdate = true;
                    /*((NaviInfo) mNaviInfos.get(i)).setDisplayName(destName);
                    ((NaviInfo) mNaviInfos.get(i)).setPoiLat(destLat);
                    ((NaviInfo) mNaviInfos.get(i)).setPoiLno(destLng);*/
                    updatePresetNaviInfo(name, addressType, destLat, destLng, destName, uid, tempNaviInfo);
                    Log.i("minos", "presetName = " + presetName + " isPreset = " + isPreset);
                    integrationCore.updateTSPNaviInfo(tempNaviInfo);
                }
            }
        }

        if (!isUpdate) {
            Log.i(TAG, "naviInfo = " + naviInfo + "isUpdate = " + isUpdate);
            integrationCore.saveTSPNaviInfo(naviInfo);
        }
    }

    private void updatePresetNaviInfo(String name, String addressType, String destLat, String destLng, String destName, String uid, NaviInfo naviInfo) {
        naviInfo.setPoiLat(destLat);
        naviInfo.setPoiLno(destLng);
        naviInfo.setDisplayName(destName);
        naviInfo.setName(name);
        naviInfo.setAddressType(addressType);
        naviInfo.setIsPreset("true");
        naviInfo.setUid(uid);
        naviInfo.setLastModifiedDate(new Date());
    }

    //保存搜索历史数据
    public void saveSearchHistoryDestInfo(String content, String name, String address,
                                          String lat, String lng, String uid, String type) {
        NaviSearchHistory naviSearchHistory = new NaviSearchHistory();
        if (type.equals(Constants.SERACH_TYPE)) {
            naviSearchHistory.setContents(content);
        } else if (type.equals(Constants.NAVI_TYPE)) {
            naviSearchHistory.setName(name);
        }

        List<BasicInfo> searchHistoryList =
                integrationCore.querySearchHistory(naviSearchHistory, mContext, NaviSearchHistory.class.getName());

        if (searchHistoryList != null
                && searchHistoryList.size() > 0) {
            return;
        }

        naviSearchHistory.setCreateDate(new Date());
        naviSearchHistory.setName(name);
        naviSearchHistory.setAddress(address);
        naviSearchHistory.setPoiLat(lat);
        naviSearchHistory.setPoiLno(lng);
        naviSearchHistory.setType(type);
        naviSearchHistory.setUid(uid);
        naviSearchHistory.setLastModifiedDate(new Date());
        Log.i(TAG, "naviSearchHistory =  " + naviSearchHistory);
        integrationCore.saveSearchText(naviSearchHistory, mContext, NaviSearchHistory.class.getName());
    }

    //删除导航搜索历史
    public void deleteNaviSearchHistory(Context context) {
        List<BasicInfo> naviSearchHistoryList = IntegrationCore.getIntergrationCore(context).searchDbData(context, NaviSearchHistory.class.getName());

        for (BasicInfo basicInfo : naviSearchHistoryList) {
            if (basicInfo instanceof NaviSearchHistory) {
                IntegrationCore.getIntergrationCore(context).deleteSearchHistoryNaviInfo((NaviSearchHistory) basicInfo, context, NaviSearchHistory.class.getName());
            }
        }
    }

    //查询搜索历史信息
    public List<NaviSearchHistory> querySearchHistoryDestInfo() {
        List<NaviSearchHistory> naviSearchHistoryList = new ArrayList<>();
        NaviSearchHistory naviSearchHistory = new NaviSearchHistory();
        mNaviInfos = integrationCore.queryAllSearchHistory(naviSearchHistory, mContext, NaviSearchHistory.class.getName());

        Log.i(TAG, "querySearchHistoryDestInfo mNaviInfos = " + mNaviInfos);
        if (mNaviInfos != null) {
            for (BasicInfo basicInfo : mNaviInfos) {
                if (basicInfo instanceof NaviSearchHistory) {
                    naviSearchHistoryList.add((NaviSearchHistory) basicInfo);
                }
            }
        }

        Collections.sort(naviSearchHistoryList, new Comparator<NaviSearchHistory>() {
            @Override
            public int compare(NaviSearchHistory naviSearchHistory1, NaviSearchHistory naviSearchHistory2) {
                return (int) (naviSearchHistory2.getCreateDate().getTime()
                        - naviSearchHistory1.getCreateDate().getTime());
            }
        });
        return naviSearchHistoryList;
    }

    //查询收藏地址信息
    public List<NaviInfo> queryCollectionDestInfo() {
        List<NaviInfo> naviInfos = new ArrayList<>();
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsFavour("true");
        mNaviInfos = integrationCore.queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (mNaviInfos != null) {
            for (BasicInfo basicInfo : mNaviInfos) {
                if (basicInfo instanceof NaviInfo) {
                    Log.i(TAG, "(NaviInfo) basicInfo = " + ((NaviInfo) basicInfo).toString());
                    naviInfos.add((NaviInfo) basicInfo);
                }
            }
        }
        return naviInfos;
    }

    //保存个人中心收藏地址
    public void savePersonCenterCollectionDest(String lat, String lng, String displayName,
                                               String address, String uid) {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setPoiLat(lat);
        naviInfo.setPoiLno(lng);
        naviInfo.setDisplayName(displayName);
        naviInfo.setName(mContext.getString(R.string.collection));
        naviInfo.setAddress(address);
        naviInfo.setUid(uid);
        naviInfo.setAddressType("0");
        naviInfo.setIsFavour("true");
        integrationCore.saveTSPNaviInfo(naviInfo);
    }

    //保存收藏数据
    public boolean saveCollectionDestInfo(PoiDetailResult poiDetailResult) {
        if (ArielApplication.getmUserInfo() == null || TokenUtils.getInstance(mContext).needLogin()) {
            LoginActivity.startAction(mContext);
            return false;
        }

        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setPoiLat(String.valueOf(poiDetailResult.getLocation().latitude));
        naviInfo.setPoiLno(String.valueOf(poiDetailResult.getLocation().longitude));
        naviInfo.setDisplayName(poiDetailResult.getName());
        naviInfo.setName(mContext.getString(R.string.collection));
        naviInfo.setAddress(poiDetailResult.getAddress());
        naviInfo.setUid(poiDetailResult.getUid());
        naviInfo.setAddressType("0");
        naviInfo.setIsFavour("true");
        naviInfo.setLastModifiedDate(new Date());
        integrationCore.saveTSPNaviInfo(naviInfo);
        return true;
    }

    public void deleteCloundNaviInfo(String sid) {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setSid(sid);

        integrationCore.deleteTSPNaviInfo(naviInfo);
    }

    public void saveLocalPresetDest(List<AddressBean> addressBeans, int syncFlag){
        Log.i(TAG, "saveLocalPresetDest  addressBeans = " + addressBeans);
        if(addressBeans != null) {
            NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));

            NaviInfo queryNavi = new NaviInfo();
            queryNavi.setUid(naviInfo.getUid());

            mNaviInfos = integrationCore.queryDestInfoByFilter(queryNavi, mContext, NaviInfo.class.getName(), false);

            if(mNaviInfos != null
                    && mNaviInfos.size() > 0){
                NaviInfo tempNavi = (NaviInfo) mNaviInfos.get(0);
                if(tempNavi.getSyncFlag() == MapUtils.NAVIINFO_SYNC_FLAG_DELETE){
                    updateTempNaviInfo(tempNavi, naviInfo);
                    integrationCore.updatePresetDest(tempNavi, mContext, NaviInfo.class.getName());
                }
            }else {
                naviInfo.setSyncFlag(syncFlag);
                Log.i(TAG, "saveLocalPresetDest  naviInfo = " + naviInfo.toString());
                integrationCore.savePresetDest(naviInfo, mContext, NaviInfo.class.getName());
            }

            integrationCore.getCardController().reloadNaviCard();
        }
    }

    public void updateLocalPresetDest(List<AddressBean> addressBeans, int syncFlag){
        Log.i(TAG, "updateLocalPresetDest  addressBeans = " + addressBeans);
        if(addressBeans != null){
            NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));
            String addressType = naviInfo.getAddressType();
            Log.i(TAG, " addressType = " + addressType);
            if (!TextUtils.isEmpty(addressType)) {
                NaviInfo naviInfo1 = new NaviInfo();
                naviInfo1.setAddressType(addressType);

                mNaviInfos = integrationCore.queryDestInfo(naviInfo1, mContext, NaviInfo.class.getName());
                if (mNaviInfos != null) {
                    for (BasicInfo basicInfo : mNaviInfos) {
                        if (basicInfo instanceof NaviInfo) {
                            if(((NaviInfo) basicInfo).getName().equals(naviInfo.getName())){
                                ((NaviInfo) basicInfo).setAddress(naviInfo.getAddress());
                                ((NaviInfo) basicInfo).setDisplayName(naviInfo.getDisplayName());
                                ((NaviInfo) basicInfo).setPoiLat(naviInfo.getPoiLat());
                                ((NaviInfo) basicInfo).setPoiLno(naviInfo.getPoiLno());
                                ((NaviInfo) basicInfo).setLastModifiedDate(naviInfo.getLastModifiedDate());
                                ((NaviInfo) basicInfo).setUid(naviInfo.getUid());
                                ((NaviInfo) basicInfo).setSyncFlag(syncFlag);
                                integrationCore.updatePresetDest((NaviInfo) basicInfo, mContext, NaviInfo.class.getName());

                                integrationCore.getCardController().reloadNaviCard();
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateTempNaviInfo(NaviInfo tempNavi, NaviInfo orginNavi){
        tempNavi.setAddress(orginNavi.getAddress());
        tempNavi.setDisplayName(orginNavi.getDisplayName());
        tempNavi.setPoiLat(orginNavi.getPoiLat());
        tempNavi.setPoiLno(orginNavi.getPoiLno());
        tempNavi.setLastModifiedDate(orginNavi.getLastModifiedDate());
        tempNavi.setUid(orginNavi.getUid());
        tempNavi.setSyncFlag(MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
    }

    public void deleteHomeAndCompany(String name, int syncFlag) {
        Log.i(TAG, "deleteHomeAndCompany  name = " + name);
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setName(name);
        List<BasicInfo> list = integrationCore.queryDestInfo(naviInfo, mContext, NaviInfo.class.getName());

        if (list != null) {
            for (BasicInfo basicInfo : list) {
                if(syncFlag == MapUtils.NAVIINFO_SYNC_FLAG_NORMAL){
                    NaviInfo naviInfo1 = (NaviInfo) basicInfo;
                    Log.i(TAG, "deleteHomeAndCompany normal naviInfo1 = " + naviInfo1);
                    integrationCore.deleteNaviInfo((NaviInfo) basicInfo, mContext, NaviInfo.class.getName());
                    integrationCore.getCardController().reloadNaviCard();
                }else if (syncFlag == MapUtils.NAVIINFO_SYNC_FLAG_DELETE) {
                    ((NaviInfo) basicInfo).setSyncFlag(syncFlag);
                    ((NaviInfo) basicInfo).setLastModifiedDate(new Date());
                    NaviInfo naviInfo2 = ((NaviInfo) basicInfo);
                    Log.i(TAG, "deleteHomeAndCompany delete naviInfo2 =" + naviInfo2);
                    integrationCore.updatePresetDest((NaviInfo) basicInfo, mContext, NaviInfo.class.getName());
                    integrationCore.getCardController().reloadNaviCard();
                }
            }
        }
    }

    public void deleteLocalPresetNaviInfo(NaviInfo naviInfo, int syncFlag){
        Log.i(TAG, "deleteLocalPresetNaviInfo start");
        if(syncFlag == MapUtils.NAVIINFO_SYNC_FLAG_NORMAL){
            Log.i(TAG, "deleteLocalPresetNaviInfo normal naviInfo = " + naviInfo);
            integrationCore.deleteNaviInfo(naviInfo, mContext, NaviInfo.class.getName());
            integrationCore.getCardController().reloadNaviCard();
        }else if (syncFlag == MapUtils.NAVIINFO_SYNC_FLAG_DELETE) {
            naviInfo.setSyncFlag(syncFlag);
            naviInfo.setLastModifiedDate(new Date());
            Log.i(TAG, "deleteHomeAndCompany delete naviInfo = " + naviInfo);
            integrationCore.updatePresetDest(naviInfo, mContext, NaviInfo.class.getName());
            integrationCore.getCardController().reloadNaviCard();
        }
    }
}
