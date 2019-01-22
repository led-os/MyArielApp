package com.qinggan.app.arielapp.minor.main.navigation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.adapter.NaviPoiSearchAdapter;
import com.qinggan.app.arielapp.minor.main.navigation.adapter.SearchResultAdapter;
import com.qinggan.app.arielapp.minor.main.navigation.bean.NavAddressInfoBean;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.mobile.tsp.restmiddle.RestError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class NavPoiSearchActivity extends BaseActivity implements NaviPoiSearchAdapter.OnClickListener, View.OnClickListener {
    private static final String TAG = "NavPoiSearchActivity";
    private PoiSearch mPoiSearch = null;
    private RecyclerView mSearchResultRclView;
    private List<PoiInfo> mSearchResultAddrs = new ArrayList<>();
    private EditText mSearchEt;
    private ArrayList<NavAddressInfoBean> mSearchAddressInfoBeans = new ArrayList<>();
    private RelativeLayout mRlBack;
    private IntegrationCore integrationCore = null;
    private NaviPoiSearchAdapter mSearchResultAdapter;
    public static final int NORMAL_TYPE = 0;
    public static final int HOME_TYPE = 1;
    public static final int COMPANY_TYPE = 2;
    public static final int POPUP_TYPE = 4;
    private int mType = NORMAL_TYPE;
    private NavDataManager mNavDataManager;
    private List<BasicInfo> mNaviInfos;
    private String mCurrentCity = "南京";

    @Override
    protected void initView() {
        mSearchResultRclView = (RecyclerView) findViewById(R.id.recycle_search_result);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mRlBack = (RelativeLayout) findViewById(R.id.rl_back);

        integrationCore = IntegrationCore.getIntergrationCore(ArielApplication.getApp());
        EventBus.getDefault().register(this);
        //初始化导航数据管理类
        mNavDataManager = NavDataManager.getInstance();
        mNavDataManager.init(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("setType", 0);
//        updateStateBar(false);
    }

    @Override
    protected void initListener() {
        initPoiSearch();

        mRlBack.setOnClickListener(this);
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                    if (mSearchEt.getText() != null) {
                        if (!mSearchEt.getText().toString().isEmpty()) {
                            if (mType != HOME_TYPE && mType != COMPANY_TYPE) {
                                mType = NORMAL_TYPE;
                            }
                            searchByText(mSearchEt.getText().toString());
                        } else {
                            ToastUtil.show("请输入目的地", NavPoiSearchActivity.this);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        mSearchResultAdapter = new NaviPoiSearchAdapter(mSearchAddressInfoBeans);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        mSearchResultRclView.setLayoutManager(linearLayoutManager);
        mSearchResultRclView.setAdapter(mSearchResultAdapter);
        mSearchResultAdapter.setClickListener(NavPoiSearchActivity.this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navi_poi_search;
    }

    //根据关键字搜索
    private void searchByText(String str) {
        PoiCitySearchOption option = new PoiCitySearchOption();
        option.city(mCurrentCity);
        option.keyword(str);
        option.pageNum(0);
        mPoiSearch.searchInCity(option);
    }

    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
    }

    //poi检索监听
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) { //获取POI检索结果
            Log.i(TAG, "onGetPoiResult result = " + result);
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(NavPoiSearchActivity.this, "未找到结果", Toast.LENGTH_SHORT).show();
                return;
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                if (result != null) {
                    //获取poi检索结果
                    mSearchResultAddrs = result.getAllPoi();
                    mSearchAddressInfoBeans.clear();

                    for (PoiInfo poiInfo : mSearchResultAddrs) {
                        String address = poiInfo.address;
                        String name = poiInfo.name;
                        String lat = String.valueOf(poiInfo.location.latitude);
                        String lng = String.valueOf(poiInfo.location.longitude);
                        String uid = poiInfo.getUid();

                        NavAddressInfoBean navAddressInfoBean = new NavAddressInfoBean(address, name, "");
                        navAddressInfoBean.setmLat(lat);
                        navAddressInfoBean.setmLng(lng);
                        navAddressInfoBean.setUid(uid);
                        navAddressInfoBean.setPoiInfo(poiInfo);

                        Log.i(TAG, " navAddressInfoBean = " + navAddressInfoBean);
                        mSearchAddressInfoBeans.add(navAddressInfoBean);
                    }
                    mSearchResultAdapter.notifyDataSetChanged();
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

    @Override
    protected void onResume() {
        super.onResume();
        new MapUtils(this).getLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private String from = "起点";
    private LatLng origin = new LatLng(32.058784, 118.757749);
    private LatLng dest;

    @Override
    public void onNaviIconClick(int position, boolean preset) {
        String name = mSearchAddressInfoBeans.get(position).getmName();
        String address = mSearchAddressInfoBeans.get(position).getmAddress();
        String lng = mSearchAddressInfoBeans.get(position).getmLng();
        String lat = mSearchAddressInfoBeans.get(position).getmLat();
        String uid = mSearchAddressInfoBeans.get(position).getUid();
        if (PhoneStateManager.getInstance(this).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            if (mType == HOME_TYPE) {
                ToastUtil.show(getString(R.string.preset_home), NavPoiSearchActivity.this);
                mNavDataManager.savePresetDestInfo(getString(R.string.home), Constants.PRESET_HOME_TYPE, lat, lng, name, uid);
            } else if (mType == COMPANY_TYPE) {
                ToastUtil.show(getString(R.string.preset_company), NavPoiSearchActivity.this);
                mNavDataManager.savePresetDestInfo(getString(R.string.company), Constants.PRESET_COMPANY_TYPE, lat, lng, name, uid);
            } else if (mType == NORMAL_TYPE) {
                ToastUtil.show(getString(R.string.collection), NavPoiSearchActivity.this);
                mNavDataManager.savePersonCenterCollectionDest(lat, lng, name, address, uid);
            }

        } else {
            dest = new LatLng(Double.valueOf(mSearchAddressInfoBeans.get(position).getmLat()),
                    Double.valueOf(mSearchAddressInfoBeans.get(position).getmLng()));
            integrationCore.planRoute(this, from, dest, origin);
        }
    }

    @Override
    public void onAddressItemClick(int position) {
        presetDestination(position);
    }

    @Override
    public void onItemLongClick(int position) {

    }

    private void presetDestination(int position) {
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

    //预设目的地popup
    private void presetDestPop(final String name, final String address, final int type,
                               final String lat, final String lng, final String uid) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.prset_detail_pop, null);

        TextView tvName = view.findViewById(R.id.tv_address_name);
        TextView tvDetail = view.findViewById(R.id.tv_address_detail);
        Button btnConfirmDest = view.findViewById(R.id.btn_confirm_dest);
        final ImageView iv_prset_address=view.findViewById(R.id.iv_prset_address);
        StreetUrl.StreetCallback callback = new StreetUrl.StreetCallback() {
            @Override
            public void onStreetUrl(String street_id, String url) {
                Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.default_loading).
                        into(iv_prset_address);
            }
        };
        if (!TextUtils.isEmpty(uid)) {
            StreetUrl task = new StreetUrl(uid, callback);
            task.execute();
        } else {
//            (int) poiDetailResult.getLocation().longitudeE6
            StreetUrl task = new StreetUrl(name, (int)Double.parseDouble(lat)*1000000, (int)Double.parseDouble(lng)*1000000, callback);
            task.execute();
        }
        tvName.setText(name);
        tvDetail.setText(address);
        if (type == HOME_TYPE) {
            btnConfirmDest.setText(getString(R.string.preset_home));
        } else if (type == COMPANY_TYPE) {
            btnConfirmDest.setText(getString(R.string.preset_company));
        } else if (type == NORMAL_TYPE || type == POPUP_TYPE) {
            btnConfirmDest.setText(getString(R.string.collection_dest));
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
                if (type == HOME_TYPE) {
                    ToastUtil.show(getString(R.string.preset_home), NavPoiSearchActivity.this);
                    mNavDataManager.savePresetDestInfo(getString(R.string.home), Constants.PRESET_HOME_TYPE, lat, lng, name, uid);
                } else if (type == COMPANY_TYPE) {
                    ToastUtil.show(getString(R.string.preset_company), NavPoiSearchActivity.this);
                    mNavDataManager.savePresetDestInfo(getString(R.string.company), Constants.PRESET_COMPANY_TYPE, lat, lng, name, uid);
                } else if (type == NORMAL_TYPE) {
                    ToastUtil.show(getString(R.string.collection), NavPoiSearchActivity.this);
                    mNavDataManager.savePersonCenterCollectionDest(lat, lng, name, address, uid);
//                    mNavDataManager.savePresetDestInfo("预设",Constants.PRSET_NORMAL_TYPE,lat, lng, name);
                }
                if (window.isShowing()) {
                    window.dismiss();
                }
            }
        });
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "location"://选择好预设目的地
                BDLocation location = event.getLocation();
                mCurrentCity = location.getCity();
                origin = new LatLng(location.getLatitude(), location.getLongitude());
                break;
        }
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (event.isSuccess()) {
            Log.i(TAG, "event.getCommandType() = " + event.getCommandType());
            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();

            if (addressBeans != null) {
                if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT) {
                    mNavDataManager.saveLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
                } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                    mNavDataManager.updateLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
                }
                this.finish();
            }
        } else {
            Log.i(TAG, "event is fail");
            RestError error = event.getRestError();
            if (error != null) {
                Log.i(TAG, "error:" + error.getMessage());
                Log.i(TAG, "error:" + error);
            }
            ToastUtil.show(error.getMessage(), this);

            Log.i(TAG, "fail save event.getCommandType() = " + event.getCommandType());
            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();

            if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT) {
                mNavDataManager.saveLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_INSERT);
            } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                mNavDataManager.updateLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_UPDATE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
        }
    }

    //修改状态栏颜色
    private void updateStateBar(boolean isLightColor) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取窗口区域
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // 如果亮色，设置状态栏文字为黑色
            if (isLightColor) {
                window.setStatusBarColor(Color.WHITE);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                window.setStatusBarColor(Color.BLACK);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }

        }
    }
}
