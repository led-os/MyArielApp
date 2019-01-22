package com.qinggan.app.arielapp.minor.main.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.adapter.SearchHistoryAdapter;
import com.qinggan.app.arielapp.minor.main.navigation.adapter.SearchResultAdapter;
import com.qinggan.app.arielapp.minor.main.navigation.bean.NavAddressInfoBean;
import com.qinggan.app.arielapp.minor.main.navigation.poisearch.ISearchCallBack;
import com.qinggan.app.arielapp.minor.main.navigation.poisearch.PoiSearchUtil;
import com.qinggan.app.arielapp.minor.main.navigation.poisearch.SearchParameter;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.adpater.NavAddressAdapter;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.restmiddle.RestError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_LIST_SELECT;

public class NavPoiSearchFragment extends UIControlBaseFragment implements NavAddressAdapter.OnItemClickListener,
        View.OnClickListener, SearchResultAdapter.OnClickListener, ISearchCallBack, SearchHistoryAdapter.OnClickListener {
    private final int search_type_his = 0;
    private final int search_type_eat = 1;
    private final int search_type_park = 2;
    private final int search_type_oil = 3;
    private final int search_type_scenic = 4;
    private PoiSearch mPoiSearch = null;
    private Context context;
    private RecyclerView mSearchHistoryRclView;
    private RecyclerView mSearchResultRclView;
    private List<PoiInfo> mSearchResultAddrs = new ArrayList<>();
    private EditText mSearchEt;
    private ArrayList<NavAddressInfoBean> mHistoryAddressInfoBeans = new ArrayList<>();
    private ArrayList<NavAddressInfoBean> mSearchAddressInfoBeans = new ArrayList<>();
    private ArrayList<NavAddressInfoBean> mPopupAddressInfoBeans = new ArrayList<>();
    private LatLng origin = new LatLng(32.058784, 118.757749);
    private String from = "石榴财智中心";
    private ImageView mIvBack;
    private FragmentManager fragmentManager;
    private IntegrationCore integrationCore = null;
    private SearchResultAdapter mSearchResultAdapter;
    private SearchResultAdapter mSearchPopupAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private static final int MSG_PLAN_ROUTE = 1;
    private RelativeLayout mRlSearchLay;
    private LatLng dest;
    private List<BasicInfo> mNaviInfos;
    private RelativeLayout mRlCollection;
    private ImageView mIvHis;
    private ImageView mIvEat;
    private ImageView mIvPark;
    private ImageView mIvOil;
    private ImageView mIvScenic;
    private ImageView mIvVoice;
    private LinearLayout mNoResultLy;
    private ImageView mNoResultIv;
    private TextView mNoResultResion;
    private TextView mNoResultTip;
    private Button mResearchBtn;
    private ImageView mLoadingView;
    private LinearLayout mSearchClassify;
    private LinearLayout mSettingHomeAndCompany;
    private String mDistance;
    private int mSelectPos;
    private RelativeLayout mHomeSetting;
    private RelativeLayout mCompanySetting;
    private static final int NORMAL_TYPE = 0;
    private static final int HOME_TYPE = 1;
    private static final int COMPANY_TYPE = 2;
    private static final int POPUP_TYPE = 4;
    private TextView mTvDestHome;
    private TextView mTvDestCompany;
    private View rootView;
    private int mType;
    private PoiSearchUtil mPoiSearchUtil;
    private boolean isOutOilSearch;
    private NavAddressInfoBean mNavAddressInfoBean;
    private double mCurrentLng = 0.0;
    private double mCurrentLat = 0.0;
    private String mCurrentCity = "南京";
    private boolean out_to_nav = false;
    private Button mClearHistoryBtn;
    private NavDataManager mNavDataManager;
    private NaviInfo mHomeInfo, mCompanyInfo;
    private int mOperateType, mSearchType;


    @Override
    public void onSelectItemPosition(int position) {
        Log.d(TAG, "onSelectItemPosition:" + position);
        mSelectPos = position;
        if (mType == HOME_TYPE || mType == COMPANY_TYPE) {
            presetHomeAndCompany(position);
        } else if (mType == NORMAL_TYPE) {
            if (mSearchAddressInfoBeans != null
                    && mSearchAddressInfoBeans.size() > 0) {
                String lat = mSearchAddressInfoBeans.get(position).getmLat();
                String lng = mSearchAddressInfoBeans.get(position).getmLng();
                dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                startPlanRoute();
            }
        }
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG, "onSelectOtherOC:" + action);
        if (TextUtils.isEmpty(action)) return;
        if (ConstantNavUc.NAV_CANCEL.equals(action)) {
            //取消导航
            integrationCore.cancelNav();
        } else if (ConstantNavUc.NAV_START.equals(action)) {
            //开始导航
            integrationCore.startNav();
        } else if (action.startsWith(NAV_IN_ROUTE_SELECT)) {
            String[] strs = action.split(":");
            if (strs.length == 2) {
                //已经跳转到导航中,处理用户说的第一个第二个第三个
                int index = Integer.parseInt(strs[1]);
                Log.d(TAG, "onSelectOtherOC:index:" + index);
                integrationCore.onNavRouteSelect(index);
            }
        }
    }

    public static NavPoiSearchFragment newInstance(boolean isOilSearch) {
        NavPoiSearchFragment navPoiSearchFragment = new NavPoiSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isOilSearch", isOilSearch);
        navPoiSearchFragment.setArguments(bundle);
        return navPoiSearchFragment;
    }

    ;

    @Override
    public void onResume() {
        Log.i(TAG, " onResume");
        super.onResume();
        if (out_to_nav) {
            //导航开启时间统计
            UMAnalyse.stopTime(UMDurationEvent.NAV);
            out_to_nav = false;
            fragmentManager.popBackStack();
            return;
        }

        initPresetHomeAndCompany();
        if (isOutOilSearch) {
            mOperateType = search_type_oil;
            startSearchOilStation();
            isOutOilSearch = false;
        } else if (mOperateType != search_type_his) {
            mSearchHistoryRclView.setVisibility(View.GONE);
            mSearchResultRclView.setVisibility(View.VISIBLE);
        } else {
            mOperateType = search_type_his;
            displayHistorySearchData();
        }
        new MapUtils(context).getLocation();
        addAddressListUIControl();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_navi_poi_search, container, false);
        context = getActivity();
        mSearchHistoryRclView = rootView.findViewById(R.id.recycle_history_address);

        mSearchEt = rootView.findViewById(R.id.et_search);
        mIvBack = rootView.findViewById(R.id.iv_back);
        mRlSearchLay = rootView.findViewById(R.id.search_lay);
        mRlCollection = rootView.findViewById(R.id.rl_collection);
        mIvHis = rootView.findViewById(R.id.iv_his);
        mIvEat = rootView.findViewById(R.id.iv_eat);
        mIvOil = rootView.findViewById(R.id.iv_oil);
        mIvPark = rootView.findViewById(R.id.iv_park);
        mIvScenic = rootView.findViewById(R.id.iv_scenic);
        mIvVoice = rootView.findViewById(R.id.iv_navi_voice_icon);
        mLoadingView = rootView.findViewById(R.id.navi_search_loading);
        Glide.with(this).load(R.drawable.loadinggif).diskCacheStrategy(DiskCacheStrategy.SOURCE).
                into(mLoadingView);
        mNoResultLy = rootView.findViewById(R.id.navi_no_search_ly);
        mNoResultIv = rootView.findViewById(R.id.navi_no_search_iv);
        mNoResultResion = rootView.findViewById(R.id.navi_no_result);
        mNoResultTip = rootView.findViewById(R.id.navi_no_result_tip);
        mResearchBtn = rootView.findViewById(R.id.navi_research_btn);
        mSearchClassify = rootView.findViewById(R.id.ll_search_classify);
        mSettingHomeAndCompany = rootView.findViewById(R.id.ll_home_company);
        mHomeSetting = rootView.findViewById(R.id.rl_setting_home);
        mCompanySetting = rootView.findViewById(R.id.rl_setting_company);
        mTvDestHome = rootView.findViewById(R.id.tv_dest_home);
        mTvDestCompany = rootView.findViewById(R.id.tv_dest_company);
        mSearchResultRclView = rootView.findViewById(R.id.recycle_search_result);
        mClearHistoryBtn = rootView.findViewById(R.id.btn_clear_history);

        mIvBack.setOnClickListener(this);
        mRlCollection.setOnClickListener(this);
        mIvHis.setOnClickListener(this);
        mIvEat.setOnClickListener(this);
        mIvOil.setOnClickListener(this);
        mIvPark.setOnClickListener(this);
        mIvScenic.setOnClickListener(this);
        mIvVoice.setOnClickListener(this);
        mHomeSetting.setOnClickListener(this);
        mCompanySetting.setOnClickListener(this);
        mClearHistoryBtn.setOnClickListener(this);
        mResearchBtn.setOnClickListener(this);
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mCurrentLat == 0.0
                            || mCurrentLng == 0.0) {
                        ToastUtil.show("不能定位当前位置", context);
                        return true;
                    }

                    ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if (mSearchEt.getText() != null) {
                        if (!mSearchEt.getText().toString().isEmpty()) {
                            if (mType != HOME_TYPE && mType != COMPANY_TYPE) {
                                mType = NORMAL_TYPE;
                            }
                            mNavDataManager.saveSearchHistoryDestInfo(mSearchEt.getText().toString(), "", "",
                                    "", "","", Constants.SERACH_TYPE);
                            searchByText(mSearchEt.getText().toString());
                            hideSettingHomeAndCompany();
                        } else {
                            ToastUtil.show("请输入目的地", context);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        mHomeSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mHomeInfo != null) {
                    deleteHomeAndCompany(mHomeInfo.getName());
                    mHomeInfo = null;
                    mTvDestHome.setText("去设置>");
                    return true;
                }
                return false;
            }
        });


        mCompanySetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mCompanyInfo != null) {
                    deleteHomeAndCompany(mCompanyInfo.getName());
                    mCompanyInfo = null;
                    mTvDestCompany.setText("去设置>");
                    return true;
                }
                return false;
            }
        });

        integrationCore = IntegrationCore.getIntergrationCore(ArielApplication.getApp());
        fragmentManager = getFragmentManager();
        initVehiclePositionInfo();
        //初始化搜索模块
        initPoiSearch();

        //初始化导航数据管理类
        mNavDataManager = NavDataManager.getInstance();
        mNavDataManager.init(context);

        mIvHis.setSelected(true);
        mSearchResultAdapter = new SearchResultAdapter(mSearchAddressInfoBeans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        mSearchResultRclView.setLayoutManager(linearLayoutManager);
        mSearchResultRclView.setAdapter(mSearchResultAdapter);
        mSearchResultRclView.setVisibility(View.GONE);
        mSearchResultAdapter.setClickListener(NavPoiSearchFragment.this);

        mSearchHistoryAdapter = new SearchHistoryAdapter(mHistoryAddressInfoBeans);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        mSearchHistoryRclView.setLayoutManager(linearLayoutManager1);
        mSearchHistoryRclView.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryRclView.setVisibility(View.VISIBLE);
        mSearchHistoryAdapter.setClickListener(this);

        if (getArguments() != null) {
            isOutOilSearch = getArguments().getBoolean("isOilSearch");
        }

        return rootView;
    }

    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
        mPoiSearchUtil = new PoiSearchUtil();
        mPoiSearchUtil.initPresent(this);
    }

    private void initVehiclePositionInfo() {
        EventBus.getDefault().register(this);
        integrationCore.getVehicleDetailInfo();//获取车辆信息
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "location"://选择好预设目的地
                BDLocation location = event.getLocation();

                mCurrentLat = location.getLatitude();
                mCurrentLng = location.getLongitude();
                mCurrentCity = location.getCity();
                origin = new LatLng(mCurrentLat, mCurrentLng);
                Log.i(TAG, " mCurrentLat = " + mCurrentLat + " mCurrentLng = " + mCurrentLng
                        + " mCurrentCity = " + mCurrentCity);
                break;
        }
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (event.isSuccess()) {
            if (event.getModule() instanceof VehicleDetailInfo) {
                //拿到车辆信息
                VehicleDetailInfo vehicleDetailInfo = (VehicleDetailInfo) event.getModule();
                if (vehicleDetailInfo != null) {
//                    mCurrentLat = Double.valueOf(vehicleDetailInfo.getData().getLat());
//                    mCurrentLng = Double.valueOf(vehicleDetailInfo.getData().getLon());
//
//                    origin = new LatLng(mCurrentLat, mCurrentLng);
//                    Log.i(TAG,"mCurrentLng = " + mCurrentLng + " mCurrentLat = " + mCurrentLat);
                    return;
                }
            }

            Log.i(TAG, "event.getCommandType() = " + event.getCommandType());
            if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT
                    || event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();
                if (addressBeans != null) {
                    if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT) {
                        NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));
                        Log.i(TAG, " NaviInfo1 naviInfo = " + naviInfo.toString());
                        integrationCore.savePresetDest(naviInfo, context, NaviInfo.class.getName());
                    } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                        NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));

                        String addressType = naviInfo.getAddressType();
                        Log.i(TAG, " addressType = " + addressType);
                        if (!TextUtils.isEmpty(addressType)) {
                            NaviInfo naviInfo1 = new NaviInfo();
                            naviInfo1.setAddressType(addressType);

                            mNaviInfos = integrationCore.queryDestInfo(naviInfo1, context, NaviInfo.class.getName());
                            if (mNaviInfos != null) {
                                for (BasicInfo basicInfo : mNaviInfos) {
                                    if (basicInfo instanceof NaviInfo) {
                                        ((NaviInfo) basicInfo).setAddress(naviInfo.getAddress());
                                        ((NaviInfo) basicInfo).setDisplayName(naviInfo.getDisplayName());
                                        ((NaviInfo) basicInfo).setPoiLat(naviInfo.getPoiLat());
                                        ((NaviInfo) basicInfo).setPoiLno(naviInfo.getPoiLno());
                                        integrationCore.updatePresetDest((NaviInfo) basicInfo, context, NaviInfo.class.getName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Log.i(TAG, "event is fail");
            RestError error = event.getRestError();
            if (error != null) {
                Log.i(TAG, "error:" + error.getMessage());
                Log.i(TAG, "error:" + error);
            }
//            mCurrentLat = 32.058784;
//            mCurrentLng = 118.757749;
//            origin = new LatLng(mCurrentLat, mCurrentLng);
        }
    }

    private void initPresetHomeAndCompany() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsPreset("true");
        mNaviInfos = integrationCore.queryDestInfo(naviInfo, context, NaviInfo.class.getName());

        if (mNaviInfos != null) {
            for (int i = 0; i < mNaviInfos.size(); i++) {
                String presetName = ((NaviInfo) mNaviInfos.get(i)).getName();
                String destName = ((NaviInfo) mNaviInfos.get(i)).getDisplayName();
                String lng = ((NaviInfo) mNaviInfos.get(i)).getPoiLno();
                String lat = ((NaviInfo) mNaviInfos.get(i)).getPoiLat();

                if (presetName.equals("家")) {
                    Log.i("minos", " presetName = " + presetName + " destName = " + destName
                            + " lng = " + lng + " lat = " + lat);
                    mTvDestHome.setText(destName);
                    mHomeInfo = ((NaviInfo) mNaviInfos.get(i));
                } else if (presetName.equals("公司")) {
                    mTvDestCompany.setText(destName);
                    mCompanyInfo = ((NaviInfo) mNaviInfos.get(i));
                }
            }
        }
    }


    private void deleteHomeAndCompany(String name) {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setName(name);
        List<BasicInfo> list = integrationCore.queryDestInfo(naviInfo, context, NaviInfo.class.getName());

        if (list != null) {
            for (BasicInfo basicInfo : list) {
                integrationCore.deleteNaviInfo((NaviInfo) basicInfo, context, NaviInfo.class.getName());
            }
        }
    }


    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
//        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
//                .hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
//                        InputMethodManager.HIDE_NOT_ALWAYS);
        switch (view.getId()) {
            case R.id.iv_back:
                integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                fragmentManager.popBackStack();
                break;
            case R.id.rl_collection:
                naviDestPopup(R.string.my_collection, POPUP_TYPE);
                setAllSeletedFalse();
                displayCollectionData();
                mSearchHistoryRclView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
                mNoResultLy.setVisibility(View.GONE);
                break;
            case R.id.iv_his:
                mOperateType = search_type_his;
                setAllSeletedFalse();
                mIvHis.setSelected(true);
                mSearchEt.setText("你想去哪？");
                displayHistorySearchData();
                break;
            case R.id.iv_eat:
                mOperateType = search_type_eat;
                setAllSeletedFalse();
                mIvEat.setSelected(true);
                mSearchEt.setText(getString(R.string.delicacy));
                naviDestPopup(R.string.nearby_delicacy, POPUP_TYPE);
                startPoiSearch(R.string.nearby_delicacy);
                break;
            case R.id.iv_oil:
                mOperateType = search_type_oil;
                startSearchOilStation();
                break;
            case R.id.iv_park:
                mOperateType = search_type_park;
                setAllSeletedFalse();
                mIvPark.setSelected(true);
                mSearchEt.setText(getString(R.string.parking));
                naviDestPopup(R.string.nearby_parking, POPUP_TYPE);
                startPoiSearch(R.string.nearby_parking);
                break;
            case R.id.iv_scenic:
                mOperateType = search_type_scenic;
                setAllSeletedFalse();
                mIvScenic.setSelected(true);
                mSearchEt.setText(getString(R.string.scenery));
                naviDestPopup(R.string.nearby_scenery, POPUP_TYPE);
                startPoiSearch(R.string.nearby_scenery);
                break;
            case R.id.iv_navi_voice_icon:
                naviHintPopup();
                VoicePolicyManage.getInstance().record(true);
                break;
            case R.id.rl_setting_home:
                if (mHomeInfo == null || mHomeInfo.getDisplayName().equals("")) {
                    settingHomeAndCompany(HOME_TYPE);
                } else {
                    dest = new LatLng(Double.valueOf(mHomeInfo.getPoiLat()),
                            Double.valueOf(mHomeInfo.getPoiLno()));
                    startPlanRoute();
                }
                break;
            case R.id.rl_setting_company:
                if (mCompanyInfo == null || mCompanyInfo.getDisplayName().equals("")) {
                    settingHomeAndCompany(COMPANY_TYPE);
                } else {
                    dest = new LatLng(Double.valueOf(mCompanyInfo.getPoiLat()),
                            Double.valueOf(mCompanyInfo.getPoiLno()));
                    startPlanRoute();
                }
                break;
            case R.id.btn_clear_history:
                mNavDataManager.deleteNaviSearchHistory(context);
                break;
        }
    }

    private void startSearchOilStation() {
        setAllSeletedFalse();
        mIvOil.setSelected(true);
        mSearchEt.setText(getString(R.string.oil_station));
        naviDestPopup(R.string.nearby_oil_station, POPUP_TYPE);
        startPoiSearch(R.string.nearby_oil_station);
    }

    private void displayHistorySearchData() {
        List<NaviSearchHistory> naviInfos = mNavDataManager.querySearchHistoryDestInfo();
        mHistoryAddressInfoBeans.clear();
        for (NaviSearchHistory naviInfo : naviInfos) {
            if (naviInfo.getType().equals("0")) {
                String contents = naviInfo.getContents();
                NavAddressInfoBean navAddressBean = new NavAddressInfoBean("", contents, "");
                navAddressBean.setType(naviInfo.getType());
                mHistoryAddressInfoBeans.add(navAddressBean);
            } else if (naviInfo.getType().equals("1")) {
                String name = naviInfo.getName();
                String address = naviInfo.getAddress();
                String lat = naviInfo.getPoiLat();
                String lng = naviInfo.getPoiLno();
                NavAddressInfoBean navAddressInfoBean = new NavAddressInfoBean(address, name, "");
                navAddressInfoBean.setmLat(lat);
                navAddressInfoBean.setmLng(lng);
                navAddressInfoBean.setType(naviInfo.getType());
                mHistoryAddressInfoBeans.add(navAddressInfoBean);
            }
        }
        mSearchHistoryRclView.setVisibility(View.VISIBLE);
        mSearchResultRclView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    private void displayCollectionData() {
        List<NaviInfo> naviInfos = mNavDataManager.queryCollectionDestInfo();
        mPopupAddressInfoBeans.clear();
        for (NaviInfo naviInfo : naviInfos) {
            String address = naviInfo.getAddress();
            String name = naviInfo.getDisplayName();
            String lat = naviInfo.getPoiLat();
            String lng = naviInfo.getPoiLno();
            String uid = naviInfo.getUid();
            NavAddressInfoBean navAddressBean = new NavAddressInfoBean(address, name, "");
            navAddressBean.setmLat(lat);
            navAddressBean.setmLng(lng);
            navAddressBean.setUid(uid);
            mPopupAddressInfoBeans.add(navAddressBean);
        }
        mSearchPopupAdapter.notifyDataSetChanged();
    }

    private void settingHomeAndCompany(int settingType) {
        mType = settingType;
        hideSettingHomeAndCompany();
    }

    private void setAllSeletedFalse() {
        mIvHis.setSelected(false);
        mIvEat.setSelected(false);
        mIvOil.setSelected(false);
        mIvPark.setSelected(false);
        mIvScenic.setSelected(false);
    }

    //预设目的地popup
    private void presetDestPop(final String name, String detail, final int type, final String lat, final String lng, final String uid) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.prset_detail_pop, null);

        TextView tvName = view.findViewById(R.id.tv_address_name);
        TextView tvDetail = view.findViewById(R.id.tv_address_detail);
        Button btnConfirmDest = view.findViewById(R.id.btn_confirm_dest);

        tvName.setText(name);
        tvDetail.setText(detail);
        if (type == HOME_TYPE) {
            btnConfirmDest.setText(getString(R.string.preset_home));
        } else if (type == COMPANY_TYPE) {
            btnConfirmDest.setText(getString(R.string.preset_company));
        } else if (type == NORMAL_TYPE || type == POPUP_TYPE) {
            btnConfirmDest.setText(getString(R.string.preset_dest));
        }

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x30000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(mSearchEt, Gravity.BOTTOM, 0, 0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (window.isShowing()) {
                    window.dismiss();
                }
            }
        });

        btnConfirmDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("设置为家", context);
                if (window.isShowing()) {
                    window.dismiss();
                }
                if (type == 1) {
                    mNavDataManager.savePresetDestInfo("家", Constants.PRESET_HOME_TYPE, lat, lng, name, uid);
                } else if (type == 2) {
                    mNavDataManager.savePresetDestInfo("公司", Constants.PRESET_COMPANY_TYPE, lat, lng, name, uid);
                } else if (type == 0) {
                    mNavDataManager.savePresetDestInfo("预设", Constants.PRSET_NORMAL_TYPE, lat, lng, name, uid);
                }
            }
        });
    }

    //地址详情popup
    private void addressDetailsPop(final PoiDetailResult poiDetailResult) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.address_detail_pop, null);

        ImageView ivCollect = view.findViewById(R.id.iv_detail_collect);
        ImageView ivNavi = view.findViewById(R.id.iv_detail_navi);
        final TextView tvName = view.findViewById(R.id.tv_address_name);
        RelativeLayout rlPhoneCall = view.findViewById(R.id.rl_phone_call);
        TextView tvAddress = view.findViewById(R.id.tv_address_detail);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        TextView tvDistance = view.findViewById(R.id.tv_distance);
        // poi url
        final ImageView iv_address = view.findViewById(R.id.iv_address);
        StreetUrl.StreetCallback callback = new StreetUrl.StreetCallback() {
            @Override
            public void onStreetUrl(String street_id, String url) {
                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.default_loading).
                        into(iv_address);
            }
        };
        if (!TextUtils.isEmpty(poiDetailResult.getUid())) {
            StreetUrl task = new StreetUrl(poiDetailResult.getUid(), callback);
            task.execute();
        } else {
            StreetUrl task = new StreetUrl(poiDetailResult.getName(), (int) poiDetailResult.getLocation().latitudeE6, (int) poiDetailResult.getLocation().longitudeE6, callback);
            task.execute();
        }

        final String address = poiDetailResult.getAddress();
        final String name = poiDetailResult.getName();
        String price = String.valueOf(poiDetailResult.getPrice());
        final String phone = poiDetailResult.getTelephone();
        final LatLng latLng = poiDetailResult.getLocation();

        tvName.setText(name);
        tvAddress.setText(address);
        if (!TextUtils.isEmpty(price)) {
            tvPrice.setText("人均" + price + "元");
        } else {
            tvPrice.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(phone)) {
            // ToastUtil.show("拨打电话", context);
            tvPhone.setText(phone);
        } else {
            rlPhoneCall.setVisibility(View.GONE);
        }

        // add call phone
        rlPhoneCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(phone)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        // add call phone
        rlPhoneCall.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(phone)) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        tvDistance.setText("距离 " + mDistance);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        window.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x30000000);
        window.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        window.setAnimationStyle(R.style.mypopwindow_anim_style);
        // 在底部显示
        window.showAtLocation(mSearchEt, Gravity.BOTTOM, 0, 0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (window.isShowing()) {
                    window.dismiss();
                }
            }
        });

        ivCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("已收藏", context);
                
                mNavDataManager.saveCollectionDestInfo(poiDetailResult);
            }
        });

        ivNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("点击导航", context);
                if (window.isShowing()) {
                    window.dismiss();
                }
                mNavDataManager.saveSearchHistoryDestInfo("", name, address, String.valueOf(latLng.latitude),
                        String.valueOf(latLng.longitude),"", Constants.NAVI_TYPE);
                dest = latLng;
                mHandler.sendEmptyMessageDelayed(MSG_PLAN_ROUTE, 1000);
            }
        });
    }

    //语言输入提示popup
    private void naviHintPopup() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.navi_hint_pop, null);

        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        int[] location = new int[2];
        mRlSearchLay.getLocationOnScreen(location);
        int popHight = getResources().getDisplayMetrics().heightPixels - location[1] - mRlSearchLay.getHeight() - 5;

        final PopupWindow window = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                popHight);

        window.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x30000000);
        window.setBackgroundDrawable(dw);
        window.showAsDropDown(mRlSearchLay, 0, 10);
    }

    //附近美食等popup
    private void naviDestPopup(int resId, int type) {
        // 利用layoutInflater获得View
//        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.navi_pop, null);
//
//        RecyclerView recyclerView = view.findViewById(R.id.rlv_collection);
//        RelativeLayout sqRelative = view.findViewById(R.id.rl_sq);
//        TextView textView = view.findViewById(R.id.tv_classify);
//
//        textView.setText(getString(resId));
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
//        recyclerView.setLayoutManager(linearLayoutManager);
//
//        mType = type;
//        mSearchPopupAdapter = new SearchResultAdapter(mPopupAddressInfoBeans);
//        recyclerView.setAdapter(mSearchPopupAdapter);
//        mSearchPopupAdapter.notifyDataSetChanged();
//        mSearchPopupAdapter.setClickListener(NavPoiSearchFragment.this);
//
//        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
//        int[] location = new int[2];
//        mIvEat.getLocationOnScreen(location);
//        int popHight = getResources().getDisplayMetrics().heightPixels - location[1] - mIvEat.getHeight() - 5;
//
//        final PopupWindow window = new PopupWindow(view,
//                WindowManager.LayoutParams.MATCH_PARENT,
//                popHight);
//
//        window.setFocusable(true);
//        ColorDrawable dw = new ColorDrawable(0x30000000);
//        window.setBackgroundDrawable(dw);
//        // 设置popWindow的显示和消失动画
//        window.setAnimationStyle(R.style.mypopwindow_anim_style);
//        // 在底部显示
//
//        window.showAsDropDown(mIvEat, 0, 10);
//        sqRelative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (window.isShowing()) {
//                    window.dismiss();
//                }
//            }
//        });

        mType = type;
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.VISIBLE);
        mSearchPopupAdapter = new SearchResultAdapter(mPopupAddressInfoBeans);
        mSearchResultRclView.setAdapter(mSearchPopupAdapter);
        mSearchPopupAdapter.notifyDataSetChanged();
        mSearchPopupAdapter.setClickListener(NavPoiSearchFragment.this);
    }

    private void hideSettingHomeAndCompany() {
        mSearchClassify.setVisibility(View.GONE);
        mSettingHomeAndCompany.setVisibility(View.GONE);
    }

    private void startPoiSearch(int resId) {
//        if (mCurrentLat == 0.0
//                || mCurrentLng == 0.0) {
//            ToastUtil.show("不能获得车辆当前位置", context);
//            return;
//        }

        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption().keyword(getString(resId))
                .location(origin)
                .radius(100000);
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setNearbySearchOption(poiNearbySearchOption);
        if (!isNetworkConnected()) {
            searchNoNet();
        } else {
            mPoiSearchUtil.searchNearBy(searchParameter);
        }
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int position) {
        if (mHistoryAddressInfoBeans != null) {
            NavAddressInfoBean navAddressInfoBean = mHistoryAddressInfoBeans.get(position);
            if (navAddressInfoBean == null) {
                return;
            }
            if (navAddressInfoBean.getType().equals(Constants.SERACH_TYPE)) {
                String content = navAddressInfoBean.getmName();
                mSearchEt.setText(content);
                if (!isNetworkConnected()) {
                    searchNoNet();
                } else {
                    searchByText(content);
                    hideSettingHomeAndCompany();
                }

            } else if (navAddressInfoBean.getType().equals(Constants.NAVI_TYPE)) {
                String lat = navAddressInfoBean.getmLat();
                String lng = navAddressInfoBean.getmLng();
                dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                startPlanRoute();
//                PoiInfo poiInfo = new PoiInfo();
//                poiInfo.setName(navAddressInfoBean.getmName());
//                poiInfo.setAddress(navAddressInfoBean.getmAddress());
//                double dlat = Double.parseDouble(navAddressInfoBean.getmLat());
//                double dlng = Double.parseDouble(navAddressInfoBean.getmLng());
//                poiInfo.setLocation(new LatLng(dlat, dlng));
//                mPoiSearchUtil.searchByPoiInfo(poiInfo);
            }
        }

    }

    @Override
    public void gotoNavi(int position,boolean preset) {
        if (mHistoryAddressInfoBeans != null) {
            NavAddressInfoBean navAddressInfoBean = mHistoryAddressInfoBeans.get(position);
            if (navAddressInfoBean == null || navAddressInfoBean.getType().equals(Constants.SERACH_TYPE)) {
                return;
            }
            String lat = navAddressInfoBean.getmLat();
            String lng = navAddressInfoBean.getmLng();
            dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            startPlanRoute();
        }
    }

    @Override
    public void deleteHis(int position) {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAN_ROUTE:
                    startPlanRoute();
                    break;
            }
        }
    };

    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    public void startPlanRoute() {
        if (mCurrentLat == 0.0
                || mCurrentLng == 0.0) {
            ToastUtil.show("不能获得车辆当前位置", context);
            return;
        }

        integrationCore.planRoute(context, from, dest, origin);

        mUiControlItems.clear();
        mUIControlElements.clear();

        //跳转到导航之后,重新注册UIControl 开始导航,结束导航,第一个到第三个
        for (int i = 0; i < 3; i++) {
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(i + "");
            uiItem.setIndex(i);
            String url = mFragmentHashCode + "-" + NAV_IN_ROUTE_SELECT + ":" + i;
            uiItem.setUrl(url);
            mUiControlItems.add(uiItem);
        }

        UIControlElementItem elementItem = new UIControlElementItem();
        elementItem.addWord(getString(R.string.nav_exit));
        elementItem.addWord(getString(R.string.nav_end));
        elementItem.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_CANCEL);
        UIControlElementItem elementItem1 = new UIControlElementItem();
        elementItem1.addWord(getString(R.string.nav));
        elementItem1.addWord(getString(R.string.nav_start));
        elementItem1.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START);
        mUIControlElements.add(elementItem);
        mUIControlElements.add(elementItem1);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
        out_to_nav = true;
        //导航开启时间统计
        UMAnalyse.startTime(UMDurationEvent.NAV);
    }

    //根据关键字搜索
    private void searchByText(String str) {
        showLoading();
        PoiCitySearchOption option = new PoiCitySearchOption();
        option.city(mCurrentCity);
        option.keyword(str);
        option.pageNum(0);
        mPoiSearch.searchInCity(option);
    }

    //poi检索监听
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) { //获取POI检索结果
            Log.i(TAG, "onGetPoiResult result = " + result);
            mLoadingView.setVisibility(View.GONE);
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
//                Toast.makeText(context, "未找到结果", Toast.LENGTH_SHORT).show();
//                return;
                searchNoResult();
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result != null) {
                    //获取poi检索结果
                    mSearchResultAddrs = result.getAllPoi();
                    mSearchAddressInfoBeans.clear();
                    mNoResultLy.setVisibility(View.GONE);
                    mSearchHistoryRclView.setVisibility(View.GONE);
                    mSearchResultRclView.setVisibility(View.VISIBLE);
                    for (PoiInfo poiInfo : mSearchResultAddrs) {
                        String address = poiInfo.address;
                        String name = poiInfo.name;
                        String lat = String.valueOf(poiInfo.location.latitude);
                        String lng = String.valueOf(poiInfo.location.longitude);
                        String uid = poiInfo.getUid();
                        String distance = mPoiSearchUtil.getDistance(mCurrentLat, mCurrentLng,
                                poiInfo.getLocation().latitude, poiInfo.getLocation().longitude);

                        NavAddressInfoBean navAddressInfoBean = new NavAddressInfoBean(address, name, distance);
                        navAddressInfoBean.setmLat(lat);
                        navAddressInfoBean.setmLng(lng);
                        navAddressInfoBean.setUid(uid);
                        navAddressInfoBean.setPoiInfo(poiInfo);

                        Log.i(TAG, " navAddressInfoBean = " + navAddressInfoBean);
                        mSearchAddressInfoBeans.add(navAddressInfoBean);
                    }
                    mSearchResultAdapter.upDataList(mSearchAddressInfoBeans);
                    mSearchResultRclView.setAdapter(mSearchResultAdapter);
                    addAddressListUIControl();
                    return;
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            Log.i(TAG, "  onGetPoiDetailResult PoiDetailResult");
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            Log.i(TAG, "  onGetPoiDetailResult PoiDetailSearchResult");
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

    };

    /**
     * 添加地址列表UIControl
     */
    private void addAddressListUIControl() {
        //添加UIControl
        mUiControlItems.clear();
        mUIControlElements.clear();

        int size = mSearchResultAddrs.size();
        for (int i = 0; i < size; i++) {
            String address = mSearchResultAddrs.get(i).name;
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(address);
            uiItem.setIndex(i);
            String url = mFragmentHashCode + "-" + NAV_LIST_SELECT + ":" + i;
            uiItem.setUrl(url);
            mUiControlItems.add(uiItem);
        }

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    @Override
    public void onNaviIconClick(int position,boolean preset) {
        if (mType == POPUP_TYPE) {
            mNavAddressInfoBean = mPopupAddressInfoBeans.get(position);
        } else if (mType == NORMAL_TYPE) {
            mNavAddressInfoBean = mSearchAddressInfoBeans.get(position);
        }

        if (mNavAddressInfoBean != null) {
            String slat = mNavAddressInfoBean.getmLat();
            String slng = mNavAddressInfoBean.getmLng();
            String name = mNavAddressInfoBean.getmName();
            String address = mNavAddressInfoBean.getmAddress();
            String uid = mNavAddressInfoBean.getUid();
            if (!TextUtils.isEmpty(slat) && !TextUtils.isEmpty(slng)) {
                mNavDataManager.saveSearchHistoryDestInfo("", name, address, slat, slng,uid, Constants.NAVI_TYPE);
                double dlat = Double.parseDouble(slat);
                double dlng = Double.parseDouble(slng);
                dest = new LatLng(dlat, dlng);
                startPlanRoute();
            }
        }
    }

    @Override
    public void onAddressItemClick(int position) {
        if (mType == HOME_TYPE || mType == COMPANY_TYPE) {
            presetHomeAndCompany(position);
        } else {
            showAddressDetailPop(position);
        }
    }


    @Override
    public void onItemLongClick(int position) {

    }

    private void presetHomeAndCompany(int position) {
        if (mSearchAddressInfoBeans != null
                && mSearchAddressInfoBeans.size() > 0) {
            String name = mSearchAddressInfoBeans.get(position).getmName();
            String address = mSearchAddressInfoBeans.get(position).getmAddress();
            String lng = mSearchAddressInfoBeans.get(position).getmLng();
            String lat = mSearchAddressInfoBeans.get(position).getmLat();
            String uid = mSearchAddressInfoBeans.get(position).getUid();
            presetDestPop(name, address, mType, lat, lng, uid);
        }
    }

    private void showAddressDetailPop(int position) {
        String name = "";
        String address = "";
        String lng = "";
        String lat = "";
        String uid = "";
        String distance = "";
        if (mType == POPUP_TYPE) {
            if (mPopupAddressInfoBeans != null
                    && mPopupAddressInfoBeans.size() > 0) {
                name = mPopupAddressInfoBeans.get(position).getmName();
                address = mPopupAddressInfoBeans.get(position).getmAddress();
                lng = mPopupAddressInfoBeans.get(position).getmLng();
                lat = mPopupAddressInfoBeans.get(position).getmLat();
                uid = mPopupAddressInfoBeans.get(position).getUid();
                distance = mPopupAddressInfoBeans.get(position).getmDistance();
            }
        } else {
            if (mSearchAddressInfoBeans != null
                    && mSearchAddressInfoBeans.size() > 0) {
                name = mSearchAddressInfoBeans.get(position).getmName();
                address = mSearchAddressInfoBeans.get(position).getmAddress();
                lng = mSearchAddressInfoBeans.get(position).getmLng();
                lat = mSearchAddressInfoBeans.get(position).getmLat();
                uid = mSearchAddressInfoBeans.get(position).getUid();
                distance = mSearchAddressInfoBeans.get(position).getmDistance();
            }
        }


//        if(PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
//                    == PhoneState.OUT_CAR_MODE){
//                presetDestPop(name, address, mType, lat, lng);
//         }else{
        mSelectPos = position;
        mDistance = distance;

        PoiInfo poiInfo = null;
        if (mType == POPUP_TYPE) {
            poiInfo = mPopupAddressInfoBeans.get(position).getPoiInfo();
        } else if (mType == NORMAL_TYPE) {
            poiInfo = mSearchAddressInfoBeans.get(position).getPoiInfo();
        }
        if (poiInfo != null) {
            mPoiSearchUtil.searchByPoiInfo(poiInfo);
        }
    }

    @Override
    public void showSearchResult(PoiResult result) {
        if (mOperateType != mSearchType) {
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.VISIBLE);
        List<PoiInfo> poiInfos = result.getAllPoi();
        if (poiInfos != null) {
            mPopupAddressInfoBeans.clear();
            for (PoiInfo poiInfo : poiInfos) {
                String address = poiInfo.getAddress();
                String name = poiInfo.getName();

                String distance = mPoiSearchUtil.getDistance(mCurrentLat, mCurrentLng,
                        poiInfo.getLocation().latitude, poiInfo.getLocation().longitude);
                NavAddressInfoBean navAddressBean = new NavAddressInfoBean(address, name, distance);
                navAddressBean.setmLng(String.valueOf(poiInfo.getLocation().longitude));
                navAddressBean.setmLat(String.valueOf(poiInfo.getLocation().latitude));
                navAddressBean.setPoiInfo(poiInfo);
                mPopupAddressInfoBeans.add(navAddressBean);
            }
            mSearchPopupAdapter.upDataList(mPopupAddressInfoBeans);
            mSearchResultRclView.setAdapter(mSearchPopupAdapter);
        }
    }

    @Override
    public void searchFail(String errMsg) {
    }

    @Override
    public void searchStart() {
        mSearchType = mOperateType;
        showLoading();
    }

    @Override
    public void searchNoResult() {
        if (mSearchType != mOperateType) {
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.VISIBLE);
        mNoResultIv.setImageDrawable(getResources().getDrawable(R.drawable.navi_no_search_resault));
        mNoResultResion.setText(R.string.navi_no_result);
        mNoResultTip.setText(R.string.navi_no_result_tip);
        mResearchBtn.setVisibility(View.VISIBLE);
    }

    public void searchNoNet() {
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.VISIBLE);
        mNoResultIv.setImageDrawable(getResources().getDrawable(R.drawable.navi_no_net));
        mNoResultResion.setText(R.string.navi_not_net);
        mNoResultTip.setText(R.string.navi_not_net_tip);
        mResearchBtn.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.GONE);

    }

    @Override
    public void searchTimeOut() {
        if (mSearchType != mOperateType) {
            return;
        }
    }

    @Override
    public void invalidSearch() {
        if (mSearchType != mOperateType) {
            return;
        }
    }

    @Override
    public void showPoiDetail(PoiDetailResult poiDetailResult) {
        Log.i(TAG, "searchByPoiInfo showPoiDetail PoiDetailResult  ");
        if (poiDetailResult != null) {
            addressDetailsPop(poiDetailResult);
        }
    }

    @Override
    public void showPoiDetail(PoiDetailSearchResult poiDetailSearchResult) {
        Log.i(TAG, "searchByPoiInfo showPoiDetail poiDetailSearchResult = " + poiDetailSearchResult);
//        if(poiDetailSearchResult != null){
//            List<PoiDetailInfo> poiDetailInfos = poiDetailSearchResult.getPoiDetailInfoList();
//            if(poiDetailInfos != null){
//                PoiDetailInfo poiDetailInfo = poiDetailInfos.get(0);
//                String address = poiDetailInfo.getAddress();
//                String name = poiDetailInfo.getName();
//                String price = String.valueOf(poiDetailInfo.getPrice());
//                String telephone = poiDetailInfo.getTelephone();
//                LatLng latLng = poiDetailInfo.getLocation();
//                Log.i(TAG, " address  = " + address + " name = " + name + " price = " + price + " telephone = " + telephone);
//                addressDetailsPop(name, address, price, telephone, latLng);
//            }
//        }
    }
}
