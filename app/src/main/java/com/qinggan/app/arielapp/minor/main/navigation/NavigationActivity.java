package com.qinggan.app.arielapp.minor.main.navigation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
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
import com.qinggan.app.arielapp.minor.main.navigation.poisearch.SearchType;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.ui.adpater.NavAddressAdapter;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.utils.TokenUtils;
import com.qinggan.app.arielapp.voiceview.BaseFloatView;
import com.qinggan.app.arielapp.voiceview.NavHintFloatView;
import com.qinggan.app.arielapp.voiceview.SelectHintFloatView;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.nav.NavPOIBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_LIST_SELECT;

public class NavigationActivity extends UIControlBaseActivity implements NavAddressAdapter.OnItemClickListener,
        View.OnClickListener, SearchResultAdapter.OnClickListener, ISearchCallBack, SearchHistoryAdapter.OnClickListener,
        PhoneStateManager.PhoneStateChangeListener{
    private final int search_type_his = 0;
    private final int search_type_eat = 1;
    private final int search_type_park = 2;
    private final int search_type_oil = 3;
    private final int search_type_scenic = 4;
    private final int preset_home = 5;
    private final int preset_compeny = 6;
    private final int preset_normal = 7;
    private final int opertate_type_collecte = 8;
    private final int VOICE_SEARCH_TYPE = 9;
    private final int MSG_PLAN_ROUTE = 10;
    private final int MSG_SHOW_HINT = 11;
    private final int MSG_HANDLE_VOICE_ACTION = 12;
    private SuggestionSearch mSuggestionSearch = null;
    private PoiSearch mPoiSearch = null;
    private PoiCitySearchOption mSearchOption;
    private RecyclerView mSearchHistoryRclView;
    private RecyclerView mSearchResultRclView;
    private EditText mSearchEt;
    private ArrayList<NavAddressInfoBean> mAddressResultBeans = new ArrayList<>();
    private List<NaviInfo> mColleteNaviInfos;
    private LatLng origin = new LatLng(32.058784, 118.757749);
    private LatLng carOrigin;
    private String from = "起点";
    private ImageView mIvBack;
    private IntegrationCore integrationCore = null;
    private SearchResultAdapter mSearchResultAdapter;
    private SearchHistoryAdapter mSearchHistoryAdapter;
    private RelativeLayout mRlSearchLay;
    private LatLng dest;
    private List<BasicInfo> mNaviInfos;
    private RelativeLayout mRlCollection;
    private ImageView mIvHis;
    private ImageView mIvEat;
    private ImageView mIvPark;
    private ImageView mIvOil;
    private ImageView mIvScenic;
    private LinePulseView mIvVoice;
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
    private TextView mTvDestHome;
    private TextView mTvDestCompany;
    //    private int mType;
    private PoiSearchUtil mPoiSearchUtil;
    private boolean isOutOilSearch;
    private boolean homePageSearch;
    private boolean voiceStartPoi;
    private double mCurrentLng = 0.0;
    private double mCurrentLat = 0.0;
    private String mCurrentCity = "南京";
    private boolean out_to_nav = false;
    private NavDataManager mNavDataManager;
    private NaviInfo mHomeInfo, mCompanyInfo;
    private int mOperateType, mSearchType;
    private DcsDataWrapper mWrapper;
    private int mOpertateItem;
    private ArrayList<DcsBean> mPoiBeans; //地址数据list 使用的时候,DcsBean需要强转成NavPOIBean

    private BdMapUIcontrol mBdMapUIcontrol;

    private int[] voiceCmd = new int[]{R.string.goHome, R.string.goHome1,
            R.string.gotoCompany, R.string.navi_open_collection,
            R.string.navi_search_history, R.string.navi_history,
            R.string.navi_search_delicacy, R.string.navi_search_restaurant, R.string.navi_search_restaurants,
            R.string.navi_nearby_delicacy, R.string.navi_nearby_restaurant, R.string.navi_nearby_restaurants,
            R.string.navi_search_park, R.string.navi_nearby_park, R.string.navi_park,
            R.string.navi_search_oilstation, R.string.navi_nearby_oilstation, R.string.navi_oilstation,
            R.string.navi_search_scenic, R.string.navi_nearby_scenic, R.string.navi_scenic,
            R.string.last_page, R.string.next_page,
            R.string.navi_close_list,
            R.string.navi_back, R.string.navi_close_list1,
    };

    private String[] navUc = new String[]{ConstantNavUc.NAV_GO_HOME, ConstantNavUc.NAV_GO_HOME,
            ConstantNavUc.NAV_GO_COMPANY, ConstantNavUc.NAV_COLLECTION,
            ConstantNavUc.NAV_HISTORY, ConstantNavUc.NAV_HISTORY,
            ConstantNavUc.NAV_DELICACY, ConstantNavUc.NAV_DELICACY, ConstantNavUc.NAV_DELICACY,
            ConstantNavUc.NAV_DELICACY, ConstantNavUc.NAV_DELICACY, ConstantNavUc.NAV_DELICACY,
            ConstantNavUc.NAV_PARKING, ConstantNavUc.NAV_PARKING, ConstantNavUc.NAV_PARKING,
            ConstantNavUc.NAV_OIL_STATION, ConstantNavUc.NAV_OIL_STATION, ConstantNavUc.NAV_OIL_STATION,
            ConstantNavUc.NAV_SCENERY, ConstantNavUc.NAV_SCENERY, ConstantNavUc.NAV_SCENERY,
            ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM, ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM,
            ConstantNavUc.NAV_BACK,
            ConstantNavUc.NAV_BACK, ConstantNavUc.NAV_BACK,
    };

    private int[] detailVoiceCmd = new int[]{R.string.navi_add_collection,R.string.navi_phone_call, R.string.navi_phone_order,
            R.string.nav_start, R.string.navi_go_now};

    private String[] detailNavUc = new String[]{ConstantNavUc.NAV_START_COLLECTION, ConstantNavUc.NAV_START_CALL, ConstantNavUc.NAV_START_CALL,
            ConstantNavUc.NAV_START_NAV, ConstantNavUc.NAV_START_NAV};

    private int[] numbers =  new int[]{R.string.navi_first, R.string.navi_second, R.string.navi_third, R.string.navi_fourth,
            R.string.navi_five, R.string.navi_six, R.string.navi_seven, R.string.navi_eight, R.string.navi_nine, R.string.navi_ten };

    private String[] numberUcs = new String[]{ConstantNavUc.NAV_FIRST, ConstantNavUc.NAV_SECOND, ConstantNavUc.NAV_THIRD,  ConstantNavUc.NAV_FOURTH,
            ConstantNavUc.NAV_FIVE, ConstantNavUc.NAV_SIX, ConstantNavUc.NAV_SEVEN, ConstantNavUc.NAV_EIGHT, ConstantNavUc.NAV_NINE, ConstantNavUc.NAV_TEN};

    private boolean isSearching;
    private boolean isSearchNext;
    private boolean isNearby;
    private int mCurrentPage = 0;
    private int mTotalCount = 0;
    private int mTotalPage = 0;
    private int mShowStartPos = -1; // 列表可见第一个项序号
    private LinearLayoutManager llm;
    private LinearLayoutManager historyLlm;
    private String mDetailPhone;
    private PoiDetailResult mPoiDetailResult;
    private String mDetailAddress;
    private String mDetailName;
    private LatLng mDetailLatLng;
    private String mDetailUid;
    private PopupWindow mDetailPopWindow;
    private int firstPositon;
    private int endPositon;
    private int mListSize;
    private boolean mIsUp = false;
    private boolean isVoiceScroll = false;
    private List<PoiInfo> mPoiInfos;
    private String MODULE_NAME = "navi";
    private NavHintFloatView mNavHintFloatView;
    private SelectHintFloatView mSelectHintFloatView;
    private HotwordManager mHotwordManager;
    private boolean isOnstop;
    private int mMoveToPos;
    private boolean mIsClickSearch=false;// 离车模式下，点击操作不语音提供播报


    private ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> mWakeUpElements = new ArrayList<>();
    private int[] hintString = new int[] {R.string.nav_hint_gohome, R.string.nav_hint_gopark,
            R.string.nav_hint_eat, R.string.nav_hint_oil};
    /**
     * 搜索周边滑动列表加载下一页
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int totalItemCount = llm.getItemCount();
            int visibleItemCount = llm.getChildCount();
            hideInput();
            if (mOperateType == opertate_type_collecte) {
                return;
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && (visibleItemCount > 0)) {
                isVoiceScroll = false;
                int lastPostion = llm.findLastVisibleItemPosition();
                int firstPostion = llm.findFirstVisibleItemPosition();
                if (lastPostion == (totalItemCount - 1) && (totalItemCount < mTotalCount)) {
                    searchNext();
                }
                mShowStartPos = firstPostion;
                addContentItemList(false);
//                addAddressListUIControl();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isVoiceScroll) {
                return;
            }

            int distance = 0;
            if (mIsUp) {
                int n = llm.findLastVisibleItemPosition() - firstPositon;
                Log.i(TAG, "mSearchResultRclView.getChildCount() = " + mSearchResultRclView.getChildCount());
                if (0 <= n && n < mSearchResultRclView.getChildCount()) {
                    distance = -mSearchResultRclView.getChildAt(0).getBottom();
                }
            } else {
                int n = endPositon - llm.findFirstVisibleItemPosition();
                if (0 <= n && n < mSearchResultRclView.getChildCount()) {
                    distance = mSearchResultRclView.getChildAt(n).getTop();
                    Log.i(TAG, "mSearchResultRclView.getChildAt(n).getTop() " + mSearchResultRclView.getChildAt(n).getTop());
                }
            }

//            mSearchResultRclView.scrollBy(0, distance);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mMapView!=null){
            mMapView.onCreate(mContext, savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "Activity onStart");
        super.onStart();
        isOnstop = false;
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "Activity onStop");
        super.onStop();
        isOnstop = true;
    }

    @Override
    protected void initView() {
        mSearchHistoryRclView = (RecyclerView) findViewById(R.id.recycle_history_address);

        mSearchEt = (EditText) findViewById(R.id.et_search);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mRlSearchLay = (RelativeLayout) findViewById(R.id.search_lay);
        mRlCollection = (RelativeLayout) findViewById(R.id.rl_collection);
        mIvHis = (ImageView) findViewById(R.id.iv_his);
        mIvEat = (ImageView) findViewById(R.id.iv_eat);
        mIvOil = (ImageView) findViewById(R.id.iv_oil);
        mIvPark = (ImageView) findViewById(R.id.iv_park);
        mIvScenic = (ImageView) findViewById(R.id.iv_scenic);
        mIvVoice = (LinePulseView) findViewById(R.id.iv_navi_voice_icon);
        mLoadingView = (ImageView) findViewById(R.id.navi_search_loading);
        Glide.with(this).load(R.drawable.loadinggif).diskCacheStrategy(DiskCacheStrategy.SOURCE).
                into(mLoadingView);
        mNoResultLy = (LinearLayout) findViewById(R.id.navi_no_search_ly);
        mNoResultIv = (ImageView) findViewById(R.id.navi_no_search_iv);
        mNoResultResion = (TextView) findViewById(R.id.navi_no_result);
        mNoResultTip = (TextView) findViewById(R.id.navi_no_result_tip);
        mResearchBtn = (Button) findViewById(R.id.navi_research_btn);
        mSearchClassify = (LinearLayout) findViewById(R.id.ll_search_classify);
        mSettingHomeAndCompany = (LinearLayout) findViewById(R.id.ll_home_company);
        mHomeSetting = (RelativeLayout) findViewById(R.id.rl_setting_home);
        mCompanySetting = (RelativeLayout) findViewById(R.id.rl_setting_company);
        mTvDestHome = (TextView) findViewById(R.id.tv_dest_home);
        mTvDestCompany = (TextView) findViewById(R.id.tv_dest_company);
        mSearchResultRclView = (RecyclerView) findViewById(R.id.recycle_search_result);

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
        mResearchBtn.setOnClickListener(this);
        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mCurrentLat == 0.0
                            || mCurrentLng == 0.0) {
                        ToastUtil.show(getString(R.string.no_current_lng), NavigationActivity.this);
                        return true;
                    }
                    hideInput();
                    if (mSearchEt.getText() != null) {
                        if (!mSearchEt.getText().toString().isEmpty()) {
//                            if (mType != HOME_TYPE && mType != COMPANY_TYPE) {
//                                mType = NORMAL_TYPE;
//                            }
                            mNavDataManager.saveSearchHistoryDestInfo(mSearchEt.getText().toString(), "", "",
                                    "", "", "", Constants.SERACH_TYPE);
                            searchByText(mSearchEt.getText().toString());
                            mIsClickSearch = true;
                            hideSettingHomeAndCompany();
                        } else {
                            displayHistorySearchData(true);
                            ToastUtil.show(getString(R.string.input_dest), NavigationActivity.this);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        // add 点击输入框后取消语音图标的显示
        mSearchEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    exitTtsAndFloatView();
                }
                return false;
            }
        });
        mHomeSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mHomeInfo != null) {
                    mOperateType = preset_home;
                    integrationCore.deleteTSPNaviInfo(mHomeInfo);
                    return true;
                }
                return false;
            }
        });


        mCompanySetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mCompanyInfo != null) {
                    mOperateType = preset_compeny;
                    integrationCore.deleteTSPNaviInfo(mCompanyInfo);
                    return true;
                }
                return false;
            }
        });

        mSearchHistoryRclView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int visibleItemCount = historyLlm.getChildCount();
                hideInput();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && (visibleItemCount > 0)) {
                    isVoiceScroll = false;
                    Log.i(TAG, "mSearchHistoryRclView SCROLL_STATE_IDLE");
                    addContentItemList(true);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isVoiceScroll) {
                    return;
                }

                int distance = 0;
                if (mIsUp) {
                    int n = historyLlm.findLastVisibleItemPosition() - firstPositon;
                    Log.i(TAG, "mSearchHistoryRclView.getChildCount() = " + mSearchHistoryRclView.getChildCount());
                    if (0 <= n && n < mSearchHistoryRclView.getChildCount()) {
                        distance = -mSearchHistoryRclView.getChildAt(0).getBottom();
                    }
                } else {
                    int n = endPositon - historyLlm.findFirstVisibleItemPosition();
                    if (0 <= n && n < mSearchHistoryRclView.getChildCount()) {
                        distance = mSearchHistoryRclView.getChildAt(n).getTop();
                        Log.i(TAG, "mSearchHistoryRclView.getChildAt(n).getTop() " + mSearchHistoryRclView.getChildAt(n).getTop());
                    }
                }

//                mSearchHistoryRclView.scrollBy(0, distance);
            }
        });

        integrationCore = IntegrationCore.getIntergrationCore(ArielApplication.getApp());
        initVehiclePositionInfo();
        //初始化搜索模块
        initPoiSearch();

        //初始化导航数据管理类
        mNavDataManager = NavDataManager.getInstance();
        mNavDataManager.init(this);

        mIvHis.setSelected(true);
        mSearchResultAdapter = new SearchResultAdapter(mAddressResultBeans);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchResultRclView.setLayoutManager(llm);
        mSearchResultRclView.setAdapter(mSearchResultAdapter);
        mSearchResultRclView.setVisibility(View.GONE);
        mSearchResultRclView.addOnScrollListener(mOnScrollListener);
        mSearchResultAdapter.setClickListener(this);
        mSearchHistoryAdapter = new SearchHistoryAdapter(mAddressResultBeans);
        historyLlm = new LinearLayoutManager(this);
        historyLlm.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        mSearchHistoryRclView.setLayoutManager(historyLlm);
        mSearchHistoryRclView.setAdapter(mSearchHistoryAdapter);
        mSearchHistoryRclView.setVisibility(View.VISIBLE);
        mSearchHistoryAdapter.setClickListener(this);

        mNavHintFloatView = NavHintFloatView.getInstance(this);
        mSelectHintFloatView = SelectHintFloatView.getInstance(this);

        mHotwordManager = HotwordManager.getInstance(this, new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                Log.i(TAG, "onConnect b = " + b  +  " isOnstop = " + isOnstop);
                if (b && !isOnstop) {
                    addDismissWakeupItems();
                } else {
                    if (null != mHotwordManager) {
                        Log.i(TAG, "onConnect clearElementUCWords");
                        mHotwordManager.clearElementUCWords(MODULE_NAME);
                    }
                }
            }
        });

        onChangeListener();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent.hasExtra("isOilSearch")) {
            isOutOilSearch = intent.getBooleanExtra("isOilSearch", false);
        }

        if (intent.hasExtra("homePageSearch")) {
            homePageSearch = intent.getBooleanExtra("homePageSearch", false);
        }

        if (intent.hasExtra("wrapper")) {
            mWrapper = intent.getParcelableExtra("wrapper");
            voiceStartPoi = true;
        }

        if (intent.hasExtra("address")) {
            mPoiBeans = intent.getParcelableArrayListExtra("address");
            voiceStartPoi = true;
        }

        if (voiceStartPoi) {
            voiceStarNavi();
            return;
        }

        if (!homePageSearch) {
            // 离车模式下不提示播报内容
            if (PhoneStateManager.getInstance(getApplicationContext()).getPhoneState() == PhoneState.IN_CAR_MODE) {

                speakNaviGuide(getString(R.string.navi_guide));
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            updateStateBar(true);
            return R.layout.activity_navigation_white_bg;
        } else {
            updateStateBar(false);
            return R.layout.activity_navigation;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMapView!=null){
            mMapView.onDestroy();
        }
        if (null != mHotwordManager) {
            Log.i(TAG, "onDestroy clearElementUCWords");
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }

        if (mSearchResultRclView != null) {
            mSearchResultRclView.removeOnScrollListener(mOnScrollListener);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("wrapper")) {
            mWrapper = intent.getParcelableExtra("wrapper");
        }

        if (intent.hasExtra("address")) {
            mPoiBeans = intent.getParcelableArrayListExtra("address");
        }

        voiceStarNavi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, " onResume");
        if (mMapView!=null){
            mMapView.onResume();
        }
        addDismissWakeupItems();
        addNaviVoiceCmdUIControl();
        if (out_to_nav) {
            //导航开启时间统计
            UMAnalyse.stopTime(UMDurationEvent.NAV);
            out_to_nav = false;
            return;
        }

        new MapUtils(this).getLocation();

        if (voiceStartPoi) {
            return;
        }
        mSearchEt.setText("");
        initPresetHomeAndCompany();
        if (isOutOilSearch) {
//            mOperateType = search_type_oil;
//            startSearchOilStation();
//            isOutOilSearch = false;
            mOperateType = search_type_oil;
            showLoading();
        } else if (mOperateType != search_type_his) {
            mSearchHistoryRclView.setVisibility(View.GONE);
            mSearchResultRclView.setVisibility(View.VISIBLE);
        } else {
            mOperateType = search_type_his;
            displayHistorySearchData(false);
        }

//        addAddressListUIControl();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView!=null){
            mMapView.onPause();
        }
        exitTtsAndFloatView();
    }

    private void speakNaviGuide(String guide) {
        Log.i(TAG, "NAV VOICE speakNaviGuide start");
        exitTtsAndFloatView();
        VoicePolicyManage.getInstance().speak(guide);
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);
        Log.i(TAG, "NAV VOICE speakNaviGuide end");
    }

    private VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener() {
        @Override
        public void onStart() {
            Log.i(TAG, "NAV VOICE TtsStateChangeListener onStart");
        }

        @Override
        public void onDone() {
            Log.i(TAG, "NAV VOICE TtsStateChangeListener onDone");
            mHandler.removeMessages(MSG_SHOW_HINT);
            mHandler.sendEmptyMessage(MSG_SHOW_HINT);
        }

        @Override
        public void onError() {
            Log.i(TAG, "NAV VOICE TtsStateChangeListener onError");
            mHandler.removeMessages(MSG_SHOW_HINT);
            mHandler.sendEmptyMessage(MSG_SHOW_HINT);
        }
    };

    private void speakSelectWhichNaviGuide() {
        if (mSearchClassify.getVisibility() == View.GONE
                 && !isSearchNext
                 && !out_to_nav) {
//            speakNaviGuide(getString(R.string.please_select_which));
            if (PhoneStateManager.getInstance(mContext).getPhoneState()==PhoneState.OUT_CAR_MODE){
                if (mIsClickSearch){
                    Log.d(TAG, "onGetPoiResult: click");
                }else{
                    speakNaviGuide(getString(R.string.please_select_which));
                }
            }else{
                speakNaviGuide(getString(R.string.please_select_which));
            }

        }
    }

    private boolean voiceStarNavi() {
        boolean isVoice = false;
        if (mWrapper != null
                && mPoiBeans != null) {
            mOperateType = VOICE_SEARCH_TYPE;
            hideSettingHomeAndCompany();
            mAddressResultBeans.clear();
            mNoResultLy.setVisibility(View.GONE);
            mSearchHistoryRclView.setVisibility(View.GONE);
            mSearchResultRclView.setVisibility(View.VISIBLE);
            speakSelectWhichNaviGuide();
            mSearchResultRclView.smoothScrollToPosition(0);
            for (DcsBean dcsBean : mPoiBeans) {
                String address = ((NavPOIBean) dcsBean).getAddress();
                String name = ((NavPOIBean) dcsBean).getName();
                String lat = String.valueOf(((NavPOIBean) dcsBean).getLat());
                String lng = String.valueOf(((NavPOIBean) dcsBean).getLng());

                NavAddressInfoBean navAddressInfoBean = new NavAddressInfoBean(address, name, "");
                navAddressInfoBean.setmLat(lat);
                navAddressInfoBean.setmLng(lng);

                Log.i(TAG, " minos navAddressInfoBean = " + navAddressInfoBean);
                mAddressResultBeans.add(navAddressInfoBean);
            }
            mSearchResultAdapter.upDataList(mAddressResultBeans);
            addVoiceAddressListUIControl();
            mSearchResultRclView.post(new Runnable() {
                @Override
                public void run() {
                    addContentItemList(false);
                }
            });
            isVoice = true;
        }
        return isVoice;
    }


    @Override
    public void onClick(View view) {
        mCurrentPage = 0;
        hideInput();
        switch (view.getId()) {
            case R.id.iv_back:
                handleBackEvent();
                break;
            case R.id.rl_collection:
                showCollection();
                break;
            case R.id.iv_his:
                showHistory();
                break;
            case R.id.iv_eat:
                showEat();
                break;
            case R.id.iv_oil:
                showOilStation();
                break;
            case R.id.iv_park:
                showPark();
                break;
            case R.id.iv_scenic:
                showScenic();
                break;
            case R.id.iv_navi_voice_icon:
                //naviHintPopup();
               showNaviHintAndStartRecord();
                break;
            case R.id.rl_setting_home:
                showGoHome();
                break;
            case R.id.rl_setting_company:
                showGoCompany();
                break;
            case R.id.btn_clear_history:
                mNavDataManager.deleteNaviSearchHistory(this);
                mAddressResultBeans.clear();
                mSearchHistoryAdapter.notifyDataSetChanged();
                break;

            case R.id.navi_research_btn:
                doReSearch();
                break;
        }
    }

    private void doReSearch() {
        switch (mOperateType) {
            case search_type_eat:
                showEat();
                break;
            case search_type_park:
                showPark();
                break;
            case search_type_oil:
                showOilStation();
                break;
            case search_type_scenic:
                showScenic();
                break;
        }
    }

    private void handleBackEvent() {
        if (mSearchClassify.getVisibility() == View.GONE) {
            backToNavHomePage();
        } else {
            exitNavModule();
        }
    }

    private void exitNavModule() {
        this.finish();
    }

    private void exitTtsAndFloatView() {
        if (mTtsStateChangeListener != null) {
            VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
        }

        VoicePolicyManage.getInstance().interrupt();
        disMissHintFloatView();
    }

    private void backToNavHomePage() {
        mSearchClassify.setVisibility(View.VISIBLE);
        mSettingHomeAndCompany.setVisibility(View.VISIBLE);
        showHistory();
    }

    private void showNaviHintAndStartRecord() {
        Log.i(TAG, "NAV VOICE showNaviHintAndStartRecord record start");
        VoicePolicyManage.getInstance().record(true);
        Log.i(TAG, "NAV VOICE showNaviHintAndStartRecord record end");
        if(mSearchResultRclView.getVisibility() == View.VISIBLE){
            mSelectHintFloatView.show();
            Log.i(TAG, "NAV VOICE mSelectHintFloatView show");
        }else {
            mNavHintFloatView.show();
            Log.i(TAG, "NAV VOICE mNavHintFloatView show");
            setRandomHint();
        }
    }

    private void disMissHintFloatView() {
        if(mSelectHintFloatView != null
                && mSelectHintFloatView.isShown()) {
            mSelectHintFloatView.dismiss();
        }

        if(mNavHintFloatView != null
                && mNavHintFloatView.isShown()) {
            mNavHintFloatView.dismiss();
        }
    }

    private void setRandomHint(){
        Random random = new Random();
        int hintValue = random.nextInt(4);
        switch (hintValue) {
            case 0:
                mNavHintFloatView.setHintTextView(getString(hintString[0]));
                break;
            case 1:
                mNavHintFloatView.setHintTextView(getString(hintString[1]));
                break;
            case 2:
                mNavHintFloatView.setHintTextView(getString(hintString[2]));
                break;
            case 3:
                mNavHintFloatView.setHintTextView(getString(hintString[3]));
                break;
        }
    }

    private void showHistory() {
        mOperateType = search_type_his;
        mSearchType = -1;
        setAllSeletedFalse();
        mIvHis.setSelected(true);
        mSearchEt.setText("");
        revertPresetType();
        displayHistorySearchData(false);
    }

    private void showCollection() {
        mSearchEt.setText("");
        mOperateType = opertate_type_collecte;
        mSearchType = -1;
        revertPresetType();
        naviDestPopup(R.string.my_collection);
        setAllSeletedFalse();
        displayCollectionData();
    }

    private void showEat() {
        mOperateType = search_type_eat;
        setAllSeletedFalse();
        mIvEat.setSelected(true);
        mSearchEt.setText(getString(R.string.delicacy));
        mSearchEt.setSelection(mSearchEt.getText().length());
        revertPresetType();
        naviDestPopup(R.string.nearby_delicacy);
        startPoiSearch(getString(R.string.nearby_delicacy));
    }

    private void showOilStation() {
        mOperateType = search_type_oil;
        startSearchOilStation();
    }

    private void showPark() {
        mOperateType = search_type_park;
        setAllSeletedFalse();
        mIvPark.setSelected(true);
        mSearchEt.setText(getString(R.string.parking));
        mSearchEt.setSelection(mSearchEt.getText().length());
        revertPresetType();
        naviDestPopup(R.string.nearby_parking);
        startPoiSearch(getString(R.string.nearby_parking));
    }

    private void showScenic() {
        mOperateType = search_type_scenic;
        setAllSeletedFalse();
        mIvScenic.setSelected(true);
        mSearchEt.setText(getString(R.string.scenery));
        mSearchEt.setSelection(mSearchEt.getText().length());
        revertPresetType();
        naviDestPopup(R.string.nearby_scenery);
        startPoiSearch(getString(R.string.nearby_scenery));
    }

    /**
     * 导航到公司
     */
    private void showGoHome() {
        if (mHomeInfo == null || mHomeInfo.getDisplayName().equals("")) {
            mOperateType = preset_home;
            settingHomeAndCompany();
        } else if (PhoneStateManager.getInstance(this).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            setPreDestination(mHomeInfo.getPoiLat(), mHomeInfo.getPoiLno(), mHomeInfo.getName(), mHomeInfo.getUid());
        } else {
            dest = new LatLng(Double.valueOf(mHomeInfo.getPoiLat()),
                    Double.valueOf(mHomeInfo.getPoiLno()));
            startPlanRoute();
        }
    }

    /**
     * 导航到公司
     */
    private void showGoCompany() {
        mOperateType = preset_compeny;
        if (mCompanyInfo == null || mCompanyInfo.getDisplayName().equals("")) {
            settingHomeAndCompany();
        } else if (PhoneStateManager.getInstance(this).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            setPreDestination(mCompanyInfo.getPoiLat(), mCompanyInfo.getPoiLno(), mCompanyInfo.getName(), mCompanyInfo.getUid());
        } else {
            dest = new LatLng(Double.valueOf(mCompanyInfo.getPoiLat()),
                    Double.valueOf(mCompanyInfo.getPoiLno()));
            startPlanRoute();
        }
    }

    private void handleVoiceAction(String action) {
        Log.d(TAG, "onSelectOtherOC action = " + action);
        if (TextUtils.isEmpty(action)) return;

        if (action.startsWith(NAV_IN_ROUTE_SELECT)) {
            String[] strs = action.split(":");
            if (strs.length == 2) {
                //已经跳转到导航中,处理用户说的第一个第二个第三个
                int index = Integer.parseInt(strs[1]);
                Log.d(TAG, "onSelectOtherOC:index:" + index);
                integrationCore.onNavRouteSelect(index);
            }
            return;
        }

        switch (action) {
            case ConstantNavUc.NAV_CANCEL:
                //取消导航
                integrationCore.cancelNav();
                break;
            case ConstantNavUc.NAV_START:
                //开始导航
                integrationCore.startNav();
                break;
            case ConstantNavUc.NAV_GO_HOME:
                showGoHome();
                break;
            case ConstantNavUc.NAV_GO_COMPANY:
                showGoCompany();
                break;
            case ConstantNavUc.NAV_COLLECTION:
                showCollection();
                break;
            case ConstantNavUc.NAV_DELICACY:
                showEat();
                break;
            case ConstantNavUc.NAV_PARKING:
                showPark();
                break;
            case ConstantNavUc.NAV_OIL_STATION:
                showOilStation();
                break;
            case ConstantNavUc.NAV_SCENERY:
                showScenic();
                break;
            case ConstantNavUc.NAV_START_COLLECTION:
                if (mDetailPopWindow != null
                        && mDetailPopWindow.isShowing()) {
                    startDetailCollection(mPoiDetailResult);
                }
                break;
            case ConstantNavUc.NAV_START_CALL:
                if (mDetailPopWindow != null
                        && mDetailPopWindow.isShowing()) {
                    startDetailPhoneCall();
                }
                break;
            case ConstantNavUc.NAV_START_NAV:
                if (out_to_nav) {
                    integrationCore.startNav();
                }

                if (mDetailPopWindow != null
                        && mDetailPopWindow.isShowing()) {
                    startDetailNavi();
                }
                break;
            case ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM:
                Log.i(TAG, " endPositon = " + endPositon + " mListSize = " + mListSize);
                if (endPositon < mListSize - 1) {
                    isVoiceScroll = true;
                    mIsUp = false;

                    if (mOperateType == search_type_his
                            && mSearchClassify.getVisibility() == View.VISIBLE) {
//                        int item = historyLlm.findLastVisibleItemPosition() - historyLlm.findFirstVisibleItemPosition();
//                        int moveToPos = historyLlm.findLastVisibleItemPosition() + item;
//                        mMoveToPos = moveToPos > (mListSize - 1) ? (mListSize - 1) : moveToPos;
//                        Log.i(TAG, "PAGE_NEXT moveToPosH = " + mMoveToPos);
//                        mSearchHistoryRclView.smoothScrollToPosition(mMoveToPos);
                        int moveToPos = historyLlm.findLastVisibleItemPosition();
                        mMoveToPos = moveToPos > (mListSize - 1) ? (mListSize - 1) : moveToPos;
                        Log.i(TAG, "PAGE_NEXT moveToPosH = " + mMoveToPos);
                        mSearchHistoryRclView.smoothScrollToPosition(mMoveToPos);
                    } else {
//                        int item = llm.findLastVisibleItemPosition() - llm.findFirstVisibleItemPosition();
//                        int moveToPos = llm.findLastVisibleItemPosition() + item;
//                        mMoveToPos = moveToPos > (mListSize - 1) ? (mListSize - 1) : moveToPos;
//                        Log.i(TAG, "PAGE_NEXT moveToPosR = " + mMoveToPos);
//                        mSearchResultRclView.smoothScrollToPosition(mMoveToPos);
                        // add
                        int moveToPos = llm.findLastVisibleItemPosition();
                        mMoveToPos = moveToPos > (mListSize - 1) ? (mListSize - 1) : moveToPos;
                        Log.i(TAG, "PAGE_NEXT moveToPosR = " + mMoveToPos);
                        llm.scrollToPositionWithOffset(mMoveToPos, 0);
                    }
                }
                break;
            case ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM:
                Log.i(TAG, " firstPositon = " + firstPositon + " mListSize = " + mListSize);
                if (firstPositon >= 0) {
                    isVoiceScroll = true;
                    mIsUp = true;
                    if (mOperateType == search_type_his
                            && mSearchClassify.getVisibility() == View.VISIBLE) {
                        int item = historyLlm.findLastVisibleItemPosition() - historyLlm.findFirstVisibleItemPosition();
                        int moveToPos = historyLlm.findFirstVisibleItemPosition() - item;
                        mMoveToPos = moveToPos >= 0 ? moveToPos : 0;
                        Log.i(TAG, "PAGE_NEXT moveToPosH = " + mMoveToPos);
                        mSearchHistoryRclView.smoothScrollToPosition(mMoveToPos);
                    } else {
                        int item = llm.findLastVisibleItemPosition() - llm.findFirstVisibleItemPosition();
                        int moveToPos = llm.findFirstVisibleItemPosition() - item;
                        mMoveToPos = moveToPos >= 0 ? moveToPos : 0;
                        Log.i(TAG, "PAGE_NEXT moveToPosR = " + mMoveToPos);
                        mSearchResultRclView.smoothScrollToPosition(mMoveToPos);
                    }
                }
                break;
            case ConstantNavUc.NAV_BACK:
                if (mSearchClassify.getVisibility() == View.GONE) {
                    backToNavHomePage();
                }
                break;
            case ConstantNavUc.NAV_HISTORY:
                showHistory();
                break;
            case ConstantNavUc.NAV_FIRST:
                if (out_to_nav) {
                    integrationCore.onNavRouteSelect(0);
                } else {
                    selectResultItemPosition(0);
                }
                break;
            case ConstantNavUc.NAV_SECOND:
                if (out_to_nav) {
                    integrationCore.onNavRouteSelect(1);
                } else {
                    selectResultItemPosition(1);
                }
                break;
            case ConstantNavUc.NAV_THIRD:
                if (out_to_nav) {
                    integrationCore.onNavRouteSelect(2);
                } else {
                    selectResultItemPosition(2);
                }
                break;
            case ConstantNavUc.NAV_FOURTH:
                selectResultItemPosition(3);
                break;
            case ConstantNavUc.NAV_FIVE:
                selectResultItemPosition(4);
                break;
            case ConstantNavUc.NAV_SIX:
                selectResultItemPosition(5);
                break;
            case ConstantNavUc.NAV_SEVEN:
                selectResultItemPosition(6);
                break;
            case ConstantNavUc.NAV_EIGHT:
                selectResultItemPosition(7);
                break;
            case ConstantNavUc.NAV_NINE:
                selectResultItemPosition(8);
                break;
            case ConstantNavUc.NAV_TEN:
                selectResultItemPosition(9);
                break;
            default:
                if (mBdMapUIcontrol != null) {
                    mBdMapUIcontrol.handleBdVoiceAction(action);
                }
                break;
        }

    }

    @Override
    public void onSelectOtherOC(String action) {
         Message message = mHandler.obtainMessage(MSG_HANDLE_VOICE_ACTION, action);
         mHandler.sendMessage(message);
    }

    @SuppressLint("MissingPermission")
    private void startDetailPhoneCall() {
        if (!TextUtils.isEmpty(mDetailPhone)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + mDetailPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            ToastUtil.show(getString(R.string.no_phone_num), this);
        }
    }

    private void startDetailCollection(PoiDetailResult poiDetailResult) {
        boolean isCollect = false;
        // add
        mColleteNaviInfos = mNavDataManager.queryCollectionDestInfo();
        if (mIvCollect.getTag() == null) {
            isCollect = false;
        } else {
            isCollect = (boolean) mIvCollect.getTag();
        }
        if (!isCollect) {
            if (mNavDataManager.saveCollectionDestInfo(poiDetailResult)) {
                ToastUtil.show(getString(R.string.collected), NavigationActivity.this);
                mIvCollect.setTag(true);
                mIvCollect.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_pressed));
            }
        } else {
            mIvCollect.setTag(false);
            ToastUtil.show(getString(R.string.cancelcollected), NavigationActivity.this);
//                    mNavDataManager.deleteCollectionDestInfo(poiDetailResult.getUid());
            if (mColleteNaviInfos != null && mOpertateItem < mColleteNaviInfos.size()) {
                integrationCore.deleteTSPNaviInfo(mColleteNaviInfos.get(mOpertateItem));
            }
            mIvCollect.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_normal));
        }
    }

    private void startDetailNavi() {
        if (mDetailPopWindow.isShowing()) {
            mDetailPopWindow.dismiss();
        }
        mNavDataManager.saveSearchHistoryDestInfo("", mDetailName, mDetailAddress, String.valueOf(mDetailLatLng.latitude),
                String.valueOf(mDetailLatLng.longitude), mDetailUid, Constants.NAVI_TYPE);
        dest = mDetailLatLng;
//                mHandler.sendEmptyMessageDelayed(MSG_PLAN_ROUTE, 1000);
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            if (ArielApplication.getmUserInfo() == null || TokenUtils.getInstance(this).needLogin()) {
                LoginActivity.startAction(NavigationActivity.this);
                return;
            }

            mNavDataManager.savePresetDestInfo(getString(R.string.preset), Constants.PRSET_NORMAL_TYPE, String.valueOf(mDetailLatLng.latitude),
                    String.valueOf(mDetailLatLng.longitude), mDetailName, mDetailUid);
            ToastUtil.show(getString(R.string.finishpreset), NavigationActivity.this);
        } else {
            mHandler.sendEmptyMessageDelayed(MSG_PLAN_ROUTE, 1000);
        }
    }

    @Override
    public void onSelectItemPosition(int position) {
        selectResultItemPosition(position);
    }

    private void selectResultItemPosition(int position) {
        Log.d(TAG, "selectResultItemPosition position = " + position);

        if (out_to_nav) {
            return;
        }

        mSelectPos = position;
        if (mOperateType == preset_home || mOperateType == preset_compeny) {
            showAddressDetailPop(position);
        } else if (mAddressResultBeans != null
                && mAddressResultBeans.size() > 0) {
            if (mOperateType == search_type_his
                    && mSearchClassify.getVisibility() == View.VISIBLE) {
                clickHistoryListItem(position);
                mIsClickSearch = false;
            } else {
                String lat = mAddressResultBeans.get(position).getmLat();
                String lng = mAddressResultBeans.get(position).getmLng();
                dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
//                startPlanRoute();
                //　login
                if (ArielApplication.getmUserInfo() == null || TokenUtils.getInstance(this).needLogin()) {
                    LoginActivity.startAction(NavigationActivity.this);
                    return;
                }
                if (PhoneStateManager.getInstance(this).getPhoneState()
                        == PhoneState.OUT_CAR_MODE) {
                    setPreDestination(lat, lng, mAddressResultBeans.get(position).getmName(), mAddressResultBeans.get(position).getUid());
                } else {
                    startPlanRoute();
                }
            }
        }
    }

    /**
     * 添加地址列表UIControl
     */
    private void addAddressListUIControl() {
        int showPos = mShowStartPos;
        if (showPos < 0) {
            showPos = 0;
        }

        //添加UIControl
        mUiControlItems.clear();
        mUIControlElements.clear();

        ArrayList<NavAddressInfoBean> result = isNearby ? mAddressResultBeans : mAddressResultBeans;
        if (result != null && result.size() > 0) {
            int max_pos = Math.min(showPos + 10, result.size());
            for (int i = showPos; i < max_pos; i++) {
                String address = result.get(i).getmName();
                int index = i - showPos;
                UIControlItem uiItem = new UIControlItem();
                uiItem.setLabel(address);
                uiItem.setIndex(index);
                String url = mFragmentHashCode + "-" + NAV_LIST_SELECT + ":" + index;
                uiItem.setUrl(url);
//                Log.i("UIControlMgr", "addAddressListUIControl: " + address);
                mUiControlItems.add(uiItem);
            }
        }

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }

    private void addNaviVoiceCmdUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        addNaviNormalCmdUIControl();
        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void addBdCmdUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();
        mBdMapUIcontrol = new BdMapUIcontrol(this);
        mUIControlElements = mBdMapUIcontrol.getBdCmdUIControl(mFragmentHashCode);
        addElementAndListContent();
    }

    private void addNaviNormalCmdUIControl() {
        for (int i = 0; i < voiceCmd.length; i++) {
            UIControlElementItem controlElementItem = new UIControlElementItem();
            controlElementItem.addWord(getString(voiceCmd[i]));
            controlElementItem.setIdentify(mFragmentHashCode + "-" + navUc[i]);
            mUIControlElements.add(controlElementItem);
        }
    }

    private void addAddressDetailUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem startNaviElement = new UIControlElementItem();
        startNaviElement.addWord(getString(R.string.nav_start));
        startNaviElement.addWord(getString(R.string.navi_go_now));
        startNaviElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_NAV);
        mUIControlElements.add(startNaviElement);

        UIControlElementItem startCallElement = new UIControlElementItem();
        startCallElement.addWord(getString(R.string.navi_phone_call));
        startCallElement.addWord(getString(R.string.navi_phone_order));
        startCallElement.addWord(getString(R.string.navi_phone_order1));
        startCallElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_CALL);
        mUIControlElements.add(startCallElement);

        UIControlElementItem startCollectElement = new UIControlElementItem();
        startCollectElement.addWord(getString(R.string.navi_add_collection));
        startCollectElement.addWord(getString(R.string.navi_add_collection1));
        startCollectElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_COLLECTION);
        mUIControlElements.add(startCollectElement);

        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();
    }


    private void addDismissWakeupItems() {
        Log.i(TAG, "addDismissWakeupItems");
        if (mHotwordManager == null) {
            return;
        }

        addNavDismissWakeupWord();
        mHotwordManager.setElementUCWords(MODULE_NAME, mWakeUpElements);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {
            @Override
            public void onItemSelected(String action) {
                Log.i(TAG, "addDismissWakeupItems  onItemSelected action = " + action);
                onSelectOtherOC(action);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }

    public void addNavDismissWakeupWord(){
        Log.i(TAG, "addNavDismissWakeupWord");
        mWakeUpElements.clear();
        //导航首页免唤醒词添加
        for (int i = 0; i < voiceCmd.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem naviElementItem = new com.qinggan.qinglink.bean.UIControlElementItem();
            naviElementItem.setWord(getString(voiceCmd[i]));
            naviElementItem.setIdentify(navUc[i]);
            mWakeUpElements.add(naviElementItem);
        }

        //第N个免唤醒词添加
        for(int i = 0; i < numbers.length; i++){
            com.qinggan.qinglink.bean.UIControlElementItem numberNaviElement = new com.qinggan.qinglink.bean.UIControlElementItem();
            numberNaviElement.setWord(getString(numbers[i]));
            numberNaviElement.setIdentify(numberUcs[i]);
            mWakeUpElements.add(numberNaviElement);
        }


        //导航详情页免唤醒词添加
        for(int i = 0; i < detailVoiceCmd.length; i++) {
            com.qinggan.qinglink.bean.UIControlElementItem naviDetailElementItem = new com.qinggan.qinglink.bean.UIControlElementItem();
            naviDetailElementItem.setWord(getString(detailVoiceCmd[i]));
            naviDetailElementItem.setIdentify(detailNavUc[i]);
            mWakeUpElements.add(naviDetailElementItem);
        }

        //跳转到百度地图后免唤醒词添加
        com.qinggan.qinglink.bean.UIControlElementItem startNaviElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        startNaviElement1.setWord(getString(R.string.start_nav));
        startNaviElement1.setIdentify(ConstantNavUc.NAV_START_NAV);
        mWakeUpElements.add(startNaviElement1);
    }

    /**
     * 添加语音搜素地址列表UIControl
     */
    private void addVoiceAddressListUIControl() {
        //添加UIControl
        mUiControlItems.clear();
        mUIControlElements.clear();

        int size = mPoiBeans.size();
        for (int i = 0; i < size; i++) {
            String address = ((NavPOIBean) mPoiBeans.get(i)).getAddress();
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

    private OnGetSuggestionResultListener mSugListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            Log.i(TAG, "onGetSuggestionResult result = " + res);
            if (res == null || res.error == SearchResult.ERRORNO.RESULT_NOT_FOUND
                    || res.getAllSuggestions() == null) {// 没有找到检索结果
                mLoadingView.setVisibility(View.GONE);
                searchNoResult();
            } else if (res.error == SearchResult.ERRORNO.NO_ERROR) {
                String city = mCurrentCity;
                for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                    if (info != null && !TextUtils.isEmpty(info.city)) {
                        city = info.city;
                        break;
                    }
                }

                if (mSearchOption == null) {
                    mSearchOption = new PoiCitySearchOption();
                }
                mSearchOption.city(city);
                mSearchOption.keyword(mSearchEt.getText().toString());
                mSearchOption.pageCapacity(20);
                mSearchOption.pageNum(mCurrentPage);
                mPoiSearch.searchInCity(mSearchOption);
            } else {
                mLoadingView.setVisibility(View.GONE);
                searchNoResult();
            }
        }
    };

    private void initPoiSearch() {
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(mSugListener);
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
                if (isOutOilSearch && carOrigin == null && mOperateType == search_type_oil) {
                    startSearchOilStation();
                    isOutOilSearch = false;
                }
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
                    mCurrentLat = Double.valueOf(vehicleDetailInfo.getData().getLat());
                    mCurrentLng = Double.valueOf(vehicleDetailInfo.getData().getLon());

                    carOrigin = new LatLng(mCurrentLat, mCurrentLng);
                    Log.i(TAG, "mCurrentLng = " + mCurrentLng + " mCurrentLat = " + mCurrentLat);
                    if (isOutOilSearch && mOperateType == search_type_oil) {
                        setAllSeletedFalse();
                        mSearchEt.setText(getString(R.string.car_nearby_oil_station));
                        mSearchEt.setSelection(mSearchEt.getText().length());
                        naviDestPopup(R.string.nearby_oil_station);
                        startPoiSearch(getString(R.string.nearby_oil_station));
                    }
                    return;
                }
            }

            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();

            Log.i(TAG, "event.getCommandType() = " + event.getCommandType());
            if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT) {
                if (addressBeans != null) {
                    NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));
                    if (mOperateType == preset_home || mOperateType == preset_compeny) {
                        showSetUpedCompenyHome(naviInfo);
                    }
                    mNavDataManager.saveLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
                }
            } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                mNavDataManager.updateLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
            } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_DELETE) {
                deletePresetDestInfo(MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
            }

        } else {
            Log.i(TAG, "event is fail");
            RestError error = event.getRestError();
            if (error != null) {
                Log.i(TAG, "error:" + error.getMessage());
                Log.i(TAG, "error:" + error);
            }


            Log.i(TAG, "fail save event.getCommandType() = " + event.getCommandType());
            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();

            if (mNavDataManager == null) {
                return;
            }

            if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_INSERT) {
                mNavDataManager.saveLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_INSERT);
            } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_UPDATE) {
                mNavDataManager.updateLocalPresetDest(addressBeans, MapUtils.NAVIINFO_SYNC_FLAG_UPDATE);
            } else if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_DELETE) {
                deletePresetDestInfo(MapUtils.NAVIINFO_SYNC_FLAG_DELETE);
            }


            if (mOperateType == preset_compeny || mOperateType == preset_home) {
                showHistory();
            }
        }
    }

    private void deletePresetDestInfo(int syncFlag) {
        if (mOperateType == preset_compeny) {
            mNavDataManager.deleteHomeAndCompany(mCompanyInfo.getName(), syncFlag);
            revertPresetType();
            mCompanyInfo = null;
            mTvDestCompany.setText(getString(R.string.gotosetting));
            mTvDestCompany.setTextColor(getResources().getColor(R.color.blue));
            if (mSearchType != -1) {
                mOperateType = mSearchType;
            } else {
                showHistory();
            }

        } else if (mOperateType == preset_home) {
            mNavDataManager.deleteHomeAndCompany(mHomeInfo.getName(), syncFlag);
            revertPresetType();
            mHomeInfo = null;
            mTvDestHome.setText(getString(R.string.gotosetting));
            mTvDestHome.setTextColor(getResources().getColor(R.color.blue));
            if (mSearchType != -1) {
                mOperateType = mSearchType;
            } else {
                showHistory();
            }
        } else if (mOperateType == opertate_type_collecte) {
            if (mColleteNaviInfos != null && mColleteNaviInfos.size() != 0) {
                mNavDataManager.deleteLocalPresetNaviInfo(mColleteNaviInfos.get(mOpertateItem), syncFlag);
//                integrationCore.deleteNaviInfo(mColleteNaviInfos.get(mOpertateItem),
//                        this, NaviInfo.class.getName());
                mAddressResultBeans.remove(mOpertateItem);
                if (mAddressResultBeans.size() == 0) {
                    showNodata();
                }
            }
        }
    }


    /**
     * 设置家，公司后，adapter item图标替换
     */
    private void revertPresetType() {
        if (mSearchResultAdapter != null) {
            mSearchResultAdapter.setPresetType("");
        }

        if (mSearchHistoryAdapter != null) {
            mSearchHistoryAdapter.setPresetType("");
        }

    }


    /**
     * 设置家，公司后，显示家公司名称
     */
    private void showSetUpedCompenyHome(NaviInfo info) {
        if (mOperateType == preset_home) {
            mHomeInfo = info;
            mTvDestHome.setText(info.getDisplayName());
            setHomeAddressColor();
        } else if (mOperateType == preset_compeny) {
            mCompanyInfo = info;
            mTvDestCompany.setText(info.getDisplayName());
            setCompanyAddressColor();
        }
        mSearchClassify.setVisibility(View.VISIBLE);
        mSettingHomeAndCompany.setVisibility(View.VISIBLE);
        showHistory();
    }

    private void initPresetHomeAndCompany() {
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsPreset("true");
        mNaviInfos = integrationCore.queryDestInfo(naviInfo, this, NaviInfo.class.getName());

        if (mNaviInfos != null) {
            for (int i = 0; i < mNaviInfos.size(); i++) {
                Log.i(TAG, "home naviInfo2 = " + ((NaviInfo) mNaviInfos.get(i)));
                String presetName = ((NaviInfo) mNaviInfos.get(i)).getName();
                String destName = ((NaviInfo) mNaviInfos.get(i)).getDisplayName();
                String lng = ((NaviInfo) mNaviInfos.get(i)).getPoiLno();
                String lat = ((NaviInfo) mNaviInfos.get(i)).getPoiLat();

                if (presetName.equals(getString(R.string.home))) {
                    Log.i("minos", " presetName = " + presetName + " destName = " + destName
                            + " lng = " + lng + " lat = " + lat);
                    mTvDestHome.setText(destName);
                    mHomeInfo = ((NaviInfo) mNaviInfos.get(i));
                    setHomeAddressColor();
                } else if (presetName.equals(getString(R.string.company))) {
                    mTvDestCompany.setText(destName);
                    setCompanyAddressColor();
                    mCompanyInfo = ((NaviInfo) mNaviInfos.get(i));
                }
            }
        }
    }


    private void startSearchOilStation() {
        setAllSeletedFalse();
        mIvOil.setSelected(true);
        mSearchEt.setText(getString(R.string.oil_station));
        mSearchEt.setSelection(mSearchEt.getText().length());
        revertPresetType();
        naviDestPopup(R.string.nearby_oil_station);
        startPoiSearch(getString(R.string.nearby_oil_station));
    }

    private void displayHistorySearchData(boolean revert) {
        List<NaviSearchHistory> naviInfos = mNavDataManager.querySearchHistoryDestInfo();
        mAddressResultBeans.clear();
        for (NaviSearchHistory naviInfo : naviInfos) {
            if (naviInfo.getType().equals("0")) {
                String contents = naviInfo.getContents();
                NavAddressInfoBean navAddressBean = new NavAddressInfoBean("", contents, "");
                navAddressBean.setType(naviInfo.getType());
                mAddressResultBeans.add(navAddressBean);
            } else if (naviInfo.getType().equals("1")) {
                String name = naviInfo.getName();
                String address = naviInfo.getAddress();
                String lat = naviInfo.getPoiLat();
                String lng = naviInfo.getPoiLno();
                NavAddressInfoBean navAddressInfoBean = new NavAddressInfoBean(address, name, "");
                navAddressInfoBean.setmLat(lat);
                navAddressInfoBean.setmLng(lng);
                navAddressInfoBean.setType(naviInfo.getType());
                navAddressInfoBean.setUid(naviInfo.getUid());
                mAddressResultBeans.add(navAddressInfoBean);
            }
        }


        if (mAddressResultBeans.size() == 0) {
            showNodata();
            return;

        } else if (mAddressResultBeans.size() > 0) {
            if (mOperateType == preset_home && !revert) {
                mSearchHistoryAdapter.setPresetType(Constants.PRESET_HOME_TYPE);
            } else if (mOperateType == preset_compeny && !revert) {
                mSearchHistoryAdapter.setPresetType(Constants.PRESET_COMPANY_TYPE);
            }
            mAddressResultBeans.add(new NavAddressInfoBean("", "", ""));
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.VISIBLE);
        mSearchHistoryRclView.post(new Runnable() {
            @Override
            public void run() {
               addContentItemList(true);
            }
        });
        mSearchResultRclView.setVisibility(View.GONE);
        mSearchHistoryAdapter.notifyDataSetChanged();
    }

    private void displayCollectionData() {
        mColleteNaviInfos = mNavDataManager.queryCollectionDestInfo();
        mAddressResultBeans.clear();
        for (NaviInfo naviInfo : mColleteNaviInfos) {
            String address = naviInfo.getAddress();
            String name = naviInfo.getDisplayName();
            String lat = naviInfo.getPoiLat();
            String lng = naviInfo.getPoiLno();
            String uid = naviInfo.getUid();
            NavAddressInfoBean navAddressBean = new NavAddressInfoBean(address, name, "");
            navAddressBean.setmLat(lat);
            navAddressBean.setmLng(lng);
            navAddressBean.setUid(uid);
            mAddressResultBeans.add(navAddressBean);
        }
        if (mAddressResultBeans.size() == 0) {
            showNodata();
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.VISIBLE);
        mSearchResultAdapter.notifyDataSetChanged();

    }

    private void settingHomeAndCompany() {
        if (ArielApplication.getmUserInfo() == null || TokenUtils.getInstance(this).needLogin()) {
            LoginActivity.startAction(NavigationActivity.this);
            return;
        }
        hideSettingHomeAndCompany();
        mSearchEt.setFocusable(true);
        mSearchEt.setFocusableInTouchMode(true);
        mSearchEt.requestFocus();
        mSearchEt.setText("");
        displayHistorySearchData(false);
    }

    private void setAllSeletedFalse() {
        mIvHis.setSelected(false);
        mIvEat.setSelected(false);
        mIvOil.setSelected(false);
        mIvPark.setSelected(false);
        mIvScenic.setSelected(false);
    }

    //预设目的地popup
    private void presetDestPop(PoiDetailResult poiDetailResult) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.prset_detail_pop, null);

        TextView tvName = view.findViewById(R.id.tv_address_name);
        TextView tvDetail = view.findViewById(R.id.tv_address_detail);
        Button btnConfirmDest = view.findViewById(R.id.btn_confirm_dest);

        final ImageView imageView = view.findViewById(R.id.iv_prset_address);
        StreetUrl.StreetCallback callback = new StreetUrl.StreetCallback() {
            @Override
            public void onStreetUrl(String street_id, String url) {
                Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.default_loading).
                        into(imageView);
            }
        };
        if (!TextUtils.isEmpty(poiDetailResult.getUid())) {
            StreetUrl task = new StreetUrl(poiDetailResult.getUid(), callback);
            task.execute();
        } else {
            StreetUrl task = new StreetUrl(poiDetailResult.getName(), (int) poiDetailResult.getLocation().latitudeE6, (int) poiDetailResult.getLocation().longitudeE6, callback);
            task.execute();
        }
        mDetailAddress = poiDetailResult.getAddress();
        mDetailName = poiDetailResult.getName();
        String price = String.valueOf(poiDetailResult.getPrice());
        mDetailPhone = poiDetailResult.getTelephone();
        mDetailLatLng = poiDetailResult.getLocation();
        mDetailUid = poiDetailResult.uid;
        mPoiDetailResult = poiDetailResult;

        tvName.setText(mDetailName);
        tvDetail.setText(mDetailAddress);
        if (mOperateType == preset_home) {
            btnConfirmDest.setText(getString(R.string.preset_home));
        } else if (mOperateType == preset_compeny) {
            btnConfirmDest.setText(getString(R.string.preset_company));
        } else {
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
                if (window.isShowing()) {
                    window.dismiss();
                }
                if (mOperateType == preset_home) {
                    mNavDataManager.savePresetDestInfo(getString(R.string.home), Constants.PRESET_HOME_TYPE, String.valueOf(mDetailLatLng.latitude),
                            String.valueOf(mDetailLatLng.longitude), mDetailName, mDetailUid);
                } else if (mOperateType == preset_compeny) {
                    mNavDataManager.savePresetDestInfo(getString(R.string.company), Constants.PRESET_COMPANY_TYPE, String.valueOf(mDetailLatLng.latitude),
                            String.valueOf(mDetailLatLng.longitude), mDetailName, mDetailUid);
                } else if (mOperateType == preset_normal) {
                    mNavDataManager.savePresetDestInfo(getString(R.string.preset), Constants.PRSET_NORMAL_TYPE, String.valueOf(mDetailLatLng.latitude),
                            String.valueOf(mDetailLatLng.longitude), mDetailName, mDetailUid);
                }
            }
        });
    }

    private ImageView mIvCollect;
    private ImageView detailIvNavi;
    private NaviMapViewLine mNaviMapViewLine =new NaviMapViewLine();
    private  TextureMapView mMapView;
    //地址详情popup
    private void addressDetailsPop(final PoiDetailResult poiDetailResult) {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
         View view = null;
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            view = inflater.inflate(R.layout.address_detail_pop_whitebg, null);
        } else {
            view = inflater.inflate(R.layout.address_detail_pop, null);
        }


        mPoiDetailResult = poiDetailResult;
        mIvCollect = view.findViewById(R.id.iv_detail_collect);
        detailIvNavi = view.findViewById(R.id.iv_detail_navi);
        final TextView tvName = view.findViewById(R.id.tv_address_name);
        RelativeLayout rlPhoneCall = view.findViewById(R.id.rl_phone_call);
        TextView tvAddress = view.findViewById(R.id.tv_address_detail);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        TextView tvPhone = view.findViewById(R.id.tv_phone);
        final TextView tvDistance = view.findViewById(R.id.tv_distance);
        if (mOperateType == opertate_type_collecte) {
            mIvCollect.setTag(true);
            mIvCollect.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_pressed));
        }
        final ImageView iv_address = view.findViewById(R.id.iv_address);
        mMapView = view.findViewById(R.id.map);
        final TextView tv_time=view.findViewById(R.id.tv_time);

        mNaviMapViewLine.initMapView(mMapView, new LatLng(mCurrentLat, mCurrentLng), poiDetailResult.getLocation(), new NaviMapViewLine.DistanceTimeCallBack() {
            @Override
            public void getRouteLineData(RouteLine data) {
                if (data.getDuration() > 60) {
                    int hour = 0;
                    int min = 0;
                    if (data.getDistance() > 3600) {
                        hour = data.getDuration() / 3600;
                        min = (data.getDuration() - hour * 3600) / 60;
                    }else{
                        min=data.getDuration()/60;
                    }
                    tv_time.setText(getString(R.string.time) + (hour > 0 ? (hour + "小时") : "") + (min > 0 ? (min + "分钟") : ("")));
                } else {
                    tv_time.setText(getString(R.string.time) + data.getDuration() + getString(R.string.second));
                }
                int s = data.getDistance();
                DecimalFormat df = new DecimalFormat("#.00");
                String dis = null;
                if (s >=1000) {
                    dis = df.format(s / 1000.f) + "km";
                } else {
                    dis = s + "m";

                }
                tvDistance.setText( getString(R.string.destdistance)+"" + dis);
            }
        });
        StreetUrl.StreetCallback callback = new StreetUrl.StreetCallback() {
            @Override
            public void onStreetUrl(String street_id, String url) {
                Glide.with(mContext).load(url).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.default_loading).
                        into(iv_address);
            }
        };
        if (!TextUtils.isEmpty(poiDetailResult.getUid())) {
            StreetUrl task = new StreetUrl(poiDetailResult.getUid(), callback);
//            task.execute();
        } else {
            StreetUrl task = new StreetUrl(poiDetailResult.getName(), (int) poiDetailResult.getLocation().latitudeE6, (int) poiDetailResult.getLocation().longitudeE6, callback);
//            task.execute();
        }

        mDetailAddress = poiDetailResult.getAddress();
        mDetailName = poiDetailResult.getName();
        String price = String.valueOf(poiDetailResult.getPrice());
        mDetailPhone = poiDetailResult.getTelephone();
        mDetailLatLng = poiDetailResult.getLocation();
        mDetailUid = poiDetailResult.uid;

        tvName.setText(mDetailName);
        tvAddress.setText(mDetailAddress);
        if (!TextUtils.isEmpty(price)) {
            tvPrice.setText(getString(R.string.percapita) + price + getString(R.string.rmb));
        } else {
            tvPrice.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mDetailPhone)) {
            if (mDetailPhone.contains(",")) {// 多个电话时显示一个电话号码
                try {
                    mDetailPhone = mDetailPhone.split(",")[0];
                } catch (Exception e) {

                }

            }
            tvPhone.setText(mDetailPhone);
        } else {
            rlPhoneCall.setVisibility(View.GONE);
        }
        // add call phone
        rlPhoneCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDetailPhoneCall();
            }
        });
        // 下面是两种方法得到宽度和高度 getWindow().getDecorView().getWidth()
        mDetailPopWindow = new PopupWindow(view,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        mDetailPopWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0x30000000);
        mDetailPopWindow.setBackgroundDrawable(dw);
        // 设置popWindow的显示和消失动画
        mDetailPopWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        mDetailPopWindow.setClippingEnabled(false);
        // 在底部显示
//        mDetailPopWindow.showAtLocation(mSearchEt, Gravity.BOTTOM, 0, 0);'
        if (!isFinishing()) {
            mDetailPopWindow.showAsDropDown(mSearchEt, 0, 0, Gravity.BOTTOM);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDetailPopWindow.isShowing()) {
                    if (mOperateType == search_type_his) {
                        mSearchHistoryAdapter.notifyDataSetChanged();
                    }
                    mDetailPopWindow.dismiss();
                    mNaviMapViewLine.release();
                }
            }
        });

        mIvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailCollection(mPoiDetailResult);
            }
        });

        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            detailIvNavi.setImageResource(R.drawable.navi_poi_detail_gonavi_selector);
        } else {
            detailIvNavi.setImageResource(R.drawable.detail_navi_selector);
        }
        detailIvNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailNavi();
            }
        });

        addAddressDetailUIControl();
    }


    //附近美食等popup
    private void naviDestPopup(int resId) {
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.VISIBLE);
        if (mSearchResultAdapter == null) {
            mSearchResultAdapter = new SearchResultAdapter(mAddressResultBeans);
            mSearchResultRclView.setAdapter(mSearchResultAdapter);
            mSearchResultAdapter.setClickListener(this);
        } else {
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }

    private void hideSettingHomeAndCompany() {
        mSearchClassify.setVisibility(View.GONE);
        mSettingHomeAndCompany.setVisibility(View.GONE);
    }

    private void startPoiSearch(String type) {
//        if (mCurrentLat == 0.0
//                || mCurrentLng == 0.0) {
//            ToastUtil.show("不能获得车辆当前位置", NavigationActivity.this);
//            return;
//        }
        LatLng curLatLng;
        if (isOutOilSearch && mOperateType == search_type_oil && carOrigin != null) {
            curLatLng = carOrigin;
        } else {
            curLatLng = origin;
        }
        isNearby = true;
        mAddressResultBeans.clear();
        PoiNearbySearchOption poiNearbySearchOption = new PoiNearbySearchOption()
                .keyword(type)
                .location(curLatLng)
                .radius(100000)
                .pageNum(0)
                .pageCapacity(20);
        SearchParameter searchParameter = new SearchParameter();
        searchParameter.setNearbySearchOption(poiNearbySearchOption);
        if (!isNetworkConnected()) {
            searchNoNet();
        } else {
            if (mSearchResultAdapter == null) {
                mSearchResultAdapter = new SearchResultAdapter(mAddressResultBeans);
                mSearchResultRclView.setAdapter(mSearchResultAdapter);
                mSearchResultAdapter.setClickListener(this);

            }
            mPoiSearchUtil.searchNearBy(searchParameter);
        }
    }

    private void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.GONE);
        mSearchResultRclView.smoothScrollToPosition(0);
    }

    @Override
    public void onItemClick(int position) {
        hideInput();
        if (mAddressResultBeans != null && mAddressResultBeans.size() != 0) {
            mIsClickSearch=true;
            clickHistoryListItem(position);
        }
    }

    private void clickHistoryListItem(int position) {
        if (position == mAddressResultBeans.size() - 1) {
            mNavDataManager.deleteNaviSearchHistory(this);
            mAddressResultBeans.clear();
            mSearchHistoryAdapter.notifyDataSetChanged();
            showNodata();
            return;
        }
        NavAddressInfoBean navAddressInfoBean = mAddressResultBeans.get(position);
        if (navAddressInfoBean == null) {
            return;
        }
        updataHisTime(position);
        if (navAddressInfoBean.getType().equals(Constants.SERACH_TYPE)) {
            String content = navAddressInfoBean.getmName();
            mSearchEt.setText(content);
            mSearchEt.setSelection(mSearchEt.getText().length());
            if (!isNetworkConnected()) {
                searchNoNet();
            } else {
                searchByText(content);
                hideSettingHomeAndCompany();
            }

        } else if (navAddressInfoBean.getType().equals(Constants.NAVI_TYPE)) {
            if ((navAddressInfoBean != null) && (!TextUtils.isEmpty(navAddressInfoBean.getUid()))) {
                mPoiSearchUtil.searchByPoiUid(navAddressInfoBean.getUid());
            } else {
                ToastUtil.show(getString(R.string.navi_poi_bean_uid), NavigationActivity.this);
            }

        }
    }

    @Override
    public void gotoNavi(int position, boolean preset) {
        hideInput();
        if (mAddressResultBeans == null || mAddressResultBeans.size() == 0) {
            return;
        }
        NavAddressInfoBean navAddressInfoBean = mAddressResultBeans.get(position);
        if (navAddressInfoBean == null) {
            return;
        }
        if (preset) {
            cloudSaveHomeCom(navAddressInfoBean);
            mSearchClassify.setVisibility(View.VISIBLE);
            mSettingHomeAndCompany.setVisibility(View.VISIBLE);
            revertPresetType();
        } else {
            if (navAddressInfoBean == null || navAddressInfoBean.getType().equals(Constants.SERACH_TYPE)) {
                return;
            }
            goNaviOrPreset(navAddressInfoBean);

        }
        updataHisTime(position);


    }

    private void goNaviOrPreset(NavAddressInfoBean navAddressInfoBean) {
        String lat = navAddressInfoBean.getmLat();
        String lng = navAddressInfoBean.getmLng();
        dest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            if (ArielApplication.getmUserInfo() == null || TokenUtils.getInstance(this).needLogin()) {
                LoginActivity.startAction(NavigationActivity.this);
                return;
            }
             //刷新导航卡片
            integrationCore.getCardController().reloadNaviCard();
            setPreDestination(lat, lng, navAddressInfoBean.getmName(), navAddressInfoBean.getUid());
        } else {
            startPlanRoute();
        }

    }

    private void updataHisTime(int position) {
        if (mAddressResultBeans == null || mAddressResultBeans.size() == 0 || position == 0) {
            return;
        }
        NavAddressInfoBean navAddressInfoBean = mAddressResultBeans.get(position);
        mAddressResultBeans.remove(position);
        List<NaviSearchHistory> naviInfos = mNavDataManager.querySearchHistoryDestInfo();
        if (naviInfos != null && naviInfos.size() != 0) {
            integrationCore.deleteSearchHistoryNaviInfo(naviInfos.get(position),
                    this, NaviSearchHistory.class.getName());
        }
        mAddressResultBeans.add(0, navAddressInfoBean);
        // add 点击去设置-搜索框item交替点击 界面不刷新，数据刷新，引起不一致性。
        mSearchHistoryAdapter.notifyDataSetChanged();
        String name = navAddressInfoBean.getmName();
        if (navAddressInfoBean.getType().equals(Constants.NAVI_TYPE)) {
            String slat = navAddressInfoBean.getmLat();
            String slng = navAddressInfoBean.getmLng();
            String address = navAddressInfoBean.getmAddress();
            String uid = navAddressInfoBean.getUid();
            mNavDataManager.saveSearchHistoryDestInfo("", name,
                    address, slat, slng, uid, Constants.NAVI_TYPE);
        } else {
            mNavDataManager.saveSearchHistoryDestInfo(name, "", "",
                    "", "", "", Constants.SERACH_TYPE);
        }


    }

    @Override
    public void deleteHis(int position) {
        hideInput();
        List<NaviSearchHistory> naviInfos = mNavDataManager.querySearchHistoryDestInfo();
        if (naviInfos != null && naviInfos.size() != 0) {
            integrationCore.deleteSearchHistoryNaviInfo(naviInfos.get(position),
                    this, NaviSearchHistory.class.getName());
        }
        mAddressResultBeans.remove(position);
        if (mAddressResultBeans.size() == 1) {
            mAddressResultBeans.clear();
            showNodata();
        }
        mSearchHistoryAdapter.notifyDataSetChanged();

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAN_ROUTE:
                    startPlanRoute();
                    break;
                case MSG_SHOW_HINT:
                    VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
                    showNaviHintAndStartRecord();
                    break;
                case MSG_HANDLE_VOICE_ACTION:
                     String action = (String) msg.obj;
                     handleVoiceAction(action);
                     break;

            }
        }
    };

    public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }
    void addUiCon(){
        UIControlElementItem avoidexpressway = new UIControlElementItem();
        avoidexpressway.addWord(getString(R.string.avoidexpressway));// 不走高速
        avoidexpressway.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.BAIDU_UI_AVOID_HIGHT);
        defaultElementItems.add(avoidexpressway);
        mUIControlElements.addAll(defaultElementItems);
        addElementAndListContent();

    }
    public void startPlanRoute() {
        Log.i(TAG, " NAVI startPlanRoute ");
//        EventBus.getDefault().post(new BaiduUiControlEvent());
        Log.e("dingqb", "startPlanRoute: " );
//        addUiCon();
        if (mCurrentLat == 0.0
                || mCurrentLng == 0.0) {
            ToastUtil.show(getString(R.string.no_current_car_lng), this);
            return;
        }
        integrationCore.planRoute(this, from, dest, origin);
        addBdCmdUIControl();
        out_to_nav = true;
        //导航开启时间统计
        UMAnalyse.startTime(UMDurationEvent.NAV);
    }

    //根据关键字搜索
    private void searchByText(String str) {
        showLoading();
        isNearby = false;
        mAddressResultBeans.clear();
        Log.e("", mOperateType + "");
        mCurrentPage = 0;
        if (SearchType.isType(str)) {
            startPoiSearch(str);
        } else {
            if (!isNetworkConnected()) {
                searchNoNet();
                return;
            }else {
                try {
                    mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                            .keyword(str)
                            .city(mCurrentCity));
                }catch (IllegalArgumentException e) {
                    ToastUtil.show(getString(R.string.navi_city_null), this);
                    return;
                }catch (IllegalStateException e) {
                    ToastUtil.show(getString(R.string.navi_search_null), this);
                    return;
                }
            }
        }
        searchStart();
        isSearching = true;
    }

    private void searchNext() {
        Log.i(TAG, "searchNext isSearching = " + isSearching);
        if (isSearching) {
            return;
        }

        int currentPage = mCurrentPage;
        currentPage++;
        if (currentPage >= mTotalPage) {
            return;
        }

        if (isNearby) {
            mPoiSearchUtil.nextPage();
        } else {
            mSearchOption.pageNum(currentPage);
            mPoiSearch.searchInCity(mSearchOption);
        }
        isSearching = true;
        isSearchNext = true;
    }

    //poi检索监听
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        public void onGetPoiResult(PoiResult result) { //获取POI检索结果
            Log.i(TAG, "onGetPoiResult result = " + result.error);
            isSearching = false;
            mLoadingView.setVisibility(View.GONE);
            if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
//                Toast.makeText(context, "未找到结果", Toast.LENGTH_SHORT).show();
//                return;
                searchNoResult();
            } else if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                //获取poi检索结果
                mCurrentPage = result.getCurrentPageNum();
                mTotalPage = result.getTotalPageNum();
                mTotalCount = result.getTotalPoiNum();
                List<PoiInfo> allPois = result.getAllPoi();
//                mAddressResultBeans.clear();
                mNoResultLy.setVisibility(View.GONE);
                mSearchHistoryRclView.setVisibility(View.GONE);
                mSearchResultRclView.setVisibility(View.VISIBLE);
                speakSelectWhichNaviGuide();
                for (PoiInfo poiInfo : allPois) {
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
                    mAddressResultBeans.add(navAddressInfoBean);
                }
                mSearchResultAdapter.upDataList(mAddressResultBeans);
                if (mOperateType == preset_home) {
                    mSearchResultAdapter.setPresetType(Constants.PRESET_HOME_TYPE);
                } else if (mOperateType == preset_compeny) {
                    mSearchResultAdapter.setPresetType(Constants.PRESET_COMPANY_TYPE);
                }
//                addAddressListUIControl();
                mSearchResultRclView.post(new Runnable() {
                    @Override
                    public void run() {
                        addContentItemList(false);
                    }
                });
            } else {
                searchError(PoiSearchUtil.getErrorString(result.error));
            }

            isSearchNext = false;
            mIsClickSearch=false;
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
    public void onNaviIconClick(int position, boolean preset) {
        hideInput();
        NavAddressInfoBean navAddressInfoBean = null;
        if (mAddressResultBeans != null && mAddressResultBeans.size() != 0) {
            navAddressInfoBean = mAddressResultBeans.get(position);
        }
        if (navAddressInfoBean == null) {
            return;
        }

        if (preset) {
            cloudSaveHomeCom(navAddressInfoBean);
            mSearchClassify.setVisibility(View.VISIBLE);
            mSettingHomeAndCompany.setVisibility(View.VISIBLE);
            revertPresetType();
            displayHistorySearchData(true);
        } else {
            String slat = navAddressInfoBean.getmLat();
            String slng = navAddressInfoBean.getmLng();
            String name = navAddressInfoBean.getmName();
            String address = navAddressInfoBean.getmAddress();
            String uid = navAddressInfoBean.getUid();
            if (!TextUtils.isEmpty(slat) && !TextUtils.isEmpty(slng)) {
                mNavDataManager.saveSearchHistoryDestInfo("", name, address, slat, slng, uid, Constants.NAVI_TYPE);
                goNaviOrPreset(navAddressInfoBean);
            }
        }

    }

    private void cloudSaveHomeCom(NavAddressInfoBean navAddressInfoBean) {
        if (mOperateType == preset_home) {
            mNavDataManager.savePresetDestInfo(getString(R.string.home), Constants.PRESET_HOME_TYPE,
                    navAddressInfoBean.getmLat(), navAddressInfoBean.getmLng(), navAddressInfoBean.getmName(), navAddressInfoBean.getUid());
        } else if (mOperateType == preset_compeny) {
            mNavDataManager.savePresetDestInfo(getString(R.string.company), Constants.PRESET_COMPANY_TYPE,
                    navAddressInfoBean.getmLat(), navAddressInfoBean.getmLng(), navAddressInfoBean.getmName(), navAddressInfoBean.getUid());
        }
    }

    @Override
    public void onAddressItemClick(int position) {
        hideInput();
        if (mOperateType == opertate_type_collecte) {
            mOpertateItem = position;
        }
        showAddressDetailPop(position);
    }

    @Override
    public void onItemLongClick(int position) {
        hideInput();
        if (mColleteNaviInfos != null && mColleteNaviInfos.size() != 0
                && mOperateType == opertate_type_collecte) {
            integrationCore.deleteTSPNaviInfo(mColleteNaviInfos.get(position));
            mOpertateItem = position;
            return;
        }
    }


    private void showAddressDetailPop(int position) {
        String name = "";
        String address = "";
        String lng = "";
        String lat = "";
        String uid = "";
        String distance = "";
        if (mAddressResultBeans != null
                && mAddressResultBeans.size() > 0) {
            name = mAddressResultBeans.get(position).getmName();
            address = mAddressResultBeans.get(position).getmAddress();
            lng = mAddressResultBeans.get(position).getmLng();
            lat = mAddressResultBeans.get(position).getmLat();
            uid = mAddressResultBeans.get(position).getUid();
            distance = mAddressResultBeans.get(position).getmDistance();
        }
        mSelectPos = position;
        mDistance = distance;

        PoiInfo poiInfo = null;
        if (mOperateType == VOICE_SEARCH_TYPE) {
            if (mAddressResultBeans != null
                    && mAddressResultBeans.size() > 0) {
                goNaviOrPreset(mAddressResultBeans.get(position));
            }
            return;
        } else {
            poiInfo = mAddressResultBeans.get(position).getPoiInfo();
        }
        if (poiInfo != null) {
            mPoiSearchUtil.searchByPoiInfo(poiInfo);
        } else if (uid != null && !uid.equals("")) {
            mPoiSearchUtil.searchByPoiUid(uid);
        }
    }

    @Override
    public void showSearchResult(PoiResult result) {
        isSearching = false;
        if (mOperateType != mSearchType) {
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.VISIBLE);
        speakSelectWhichNaviGuide();
        mCurrentPage = result.getCurrentPageNum();
        mTotalPage = result.getTotalPageNum();
        mTotalCount = result.getTotalPoiNum();
        mPoiInfos = result.getAllPoi();
        LatLng curLatng;
        if (isOutOilSearch && mOperateType == search_type_oil && carOrigin != null) {
            curLatng = carOrigin;
            isOutOilSearch = false;
        } else {
            curLatng = origin;
        }

        if (mPoiInfos != null) {
//            mAddressResultBeans.clear();
            for (PoiInfo poiInfo : mPoiInfos) {
                String address = poiInfo.getAddress();
                String name = poiInfo.getName();
                String distance = mPoiSearchUtil.getDistance(curLatng.latitude, curLatng.longitude,
                        poiInfo.getLocation().latitude, poiInfo.getLocation().longitude);
                NavAddressInfoBean navAddressBean = new NavAddressInfoBean(address, name, distance);
                navAddressBean.setmLng(String.valueOf(poiInfo.getLocation().longitude));
                navAddressBean.setmLat(String.valueOf(poiInfo.getLocation().latitude));
                navAddressBean.setPoiInfo(poiInfo);
                navAddressBean.setUid(poiInfo.uid);
                mAddressResultBeans.add(navAddressBean);
            }
            mSearchResultAdapter.upDataList(mAddressResultBeans);
            if (mOperateType == preset_home) {
                mSearchResultAdapter.setPresetType(Constants.PRESET_HOME_TYPE);
            } else if (mOperateType == preset_compeny) {
                mSearchResultAdapter.setPresetType(Constants.PRESET_COMPANY_TYPE);
            }

//            addAddressListUIControl();
        }

        mSearchResultRclView.post(new Runnable() {
            @Override
            public void run() {
                addContentItemList(false);
            }
        });

        isSearchNext = false;
    }

    @Override
    public void searchFail(String errMsg) {
        searchError(errMsg);
    }

    @Override
    public void searchStart() {
        mSearchType = mOperateType;
        showLoading();
    }

    public void showNodata() {
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.VISIBLE);
        mNoResultIv.setImageDrawable(getResources().getDrawable(R.drawable.navi_icon_no_data));
        if (mOperateType == opertate_type_collecte) {
            mNoResultResion.setText(R.string.navi_no_coll_data);
        } else {
            mNoResultResion.setText(R.string.navi_no_his_data);
        }
        mNoResultTip.setText(R.string.navi_data_tip);
        mResearchBtn.setVisibility(View.GONE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.GONE);
    }

    public void searchNoNet() {
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.VISIBLE);
        mNoResultIv.setImageDrawable(getResources().getDrawable(R.drawable.navi_no_net));
        mNoResultResion.setText(R.string.navi_not_net);
        mNoResultTip.setText(R.string.navi_not_net_tip);
        mResearchBtn.setVisibility(View.VISIBLE);
        mSearchHistoryRclView.setVisibility(View.GONE);
        mSearchResultRclView.setVisibility(View.GONE);

    }

    public void searchNoResult() {
        if (isOutOilSearch) {
            isOutOilSearch = false;
        }
        isSearching = false;
        isSearchNext = false;
        if (mCurrentPage != 0) {
            return;
        }
        mLoadingView.setVisibility(View.GONE);
        mNoResultLy.setVisibility(View.VISIBLE);
        mNoResultIv.setImageDrawable(getResources().getDrawable(R.drawable.navi_no_search_resault));
        mNoResultResion.setText(R.string.navi_no_result);
        mNoResultTip.setText(R.string.navi_no_result_tip);
        mResearchBtn.setVisibility(View.GONE);
    }

    public void searchError(String msg) {
        searchNoResult();
        mNoResultResion.setText(msg);
    }

    @Override
    public void searchTimeOut() {
        if (isOutOilSearch) {
            isOutOilSearch = false;
        }
        if (mSearchType != mOperateType) {
            return;
        }
    }

    @Override
    public void invalidSearch() {
        if (isOutOilSearch) {
            isOutOilSearch = false;
        }
        if (mSearchType != mOperateType) {
            return;
        }
    }

    @Override
    public void showPoiDetail(PoiDetailResult poiDetailResult) {
        Log.i(TAG, "searchByPoiInfo showPoiDetail PoiDetailResult  ");
        if (poiDetailResult == null) {
            return;
        }
        if (mOperateType == preset_compeny || mOperateType == preset_home) {
            presetDestPop(poiDetailResult);
        } else {
            if (mDetailPopWindow==null){
                addressDetailsPop(poiDetailResult);
            } else if ((mDetailPopWindow!=null)&&(!mDetailPopWindow.isShowing())){
                addressDetailsPop(poiDetailResult);
            }
        }
    }

    @Override
    public void showPoiDetail(PoiDetailSearchResult poiDetailSearchResult) {
        Log.i(TAG, "searchByPoiInfo showPoiDetail poiDetailSearchResult = " + poiDetailSearchResult);
    }


    private void hideInput() {
        ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 离车模式
    private void setPreDestination(final String lat, final String lng, final String name, String uid) {
        mOperateType = preset_normal;
        mNavDataManager.savePresetDestInfo(getString(R.string.preset), Constants.PRSET_NORMAL_TYPE, lat, lng, name, uid);
        mSearchHistoryAdapter.notifyDataSetChanged();
        ToastUtil.show(getString(R.string.finishpreset), NavigationActivity.this);
    }

    private void addContentItemList(boolean isHistoryList) {
        Log.d(TAG, "addContentItemList");
        if (null == mAddressResultBeans || mAddressResultBeans.size() < 1) {
            Log.d(TAG, "addContentItemList return");
            return;
        }


        if(isHistoryList){
            firstPositon = historyLlm.findFirstVisibleItemPosition();
            endPositon = historyLlm.findLastVisibleItemPosition();
        }else {
            firstPositon = llm.findFirstVisibleItemPosition();
            endPositon = llm.findLastVisibleItemPosition();
        }

        mListSize = mAddressResultBeans.size();
        Log.d(TAG, "addContentItemList firstPositon : " + firstPositon + "endPositon : " + endPositon + "listSize : " + mListSize);

        if (firstPositon >= 0 && endPositon >= 0 && mListSize > 0 && firstPositon < mListSize && endPositon < mListSize) {
//            mUIControlList = mDcsBeanList.subList(firstPositon, listSize+1);
        } else {
            Log.d(TAG, "addContentItemList size return");
            return;
        }

        mUiControlItems.clear();
        mUIControlElements.clear();

        for (int i = firstPositon; i < endPositon + 1; i++) {
            NavAddressInfoBean navAddressInfoBean = (NavAddressInfoBean) mAddressResultBeans.get(i);
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(navAddressInfoBean.getmName());
            uiItem.setIndex(i);
            String url = mFragmentHashCode + "-" + NAV_LIST_SELECT + ":" + i;
            uiItem.setUrl(url);
            mUiControlItems.add(uiItem);
        }

        UIControlElementItem prePage = new UIControlElementItem();
        prePage.addWord(getString(R.string.last_page));
        prePage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(prePage);

        UIControlElementItem uiNextPage = new UIControlElementItem();
        uiNextPage.addWord(getString(R.string.next_page));
        uiNextPage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(uiNextPage);

        addNaviNormalCmdUIControl();
        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }


    @Override
    public void onPhoneStateChange(PhoneState phoneState) {
        if (phoneState == PhoneState.OUT_CAR_MODE && mDetailPopWindow.isShowing()) {
            detailIvNavi.setImageResource(R.drawable.detail_preinstall_selector);
        } else if (mDetailPopWindow.isShowing()) {
            detailIvNavi.setImageResource(R.drawable.detail_navi_selector);
        }
        if (mSearchResultAdapter != null) {
            mSearchResultAdapter.notifyDataSetChanged();
        }
        if (mSearchHistoryAdapter != null) {
            mSearchHistoryAdapter.notifyDataSetChanged();
        }

    }

    private boolean isInterrupt = false;//默认没有打断

    private void onChangeListener() {
        VoicePolicyManage.getInstance().addListeners(new VoicePolicyManage.VoiceStateChangeListener() {
            @Override
            public void onStateChange(VoicePolicyManage.VoiceMode voiceMode, VoicePolicyManage.VoiceState voiceState) {

            }

            @Override
            public void onContentChange(String content) {

            }

            @Override
            public void onUserClick(boolean userClick, boolean wakeUpOrInterrupt) {
                isInterrupt = true;
            }
        });
        // add 你可以说第几个，默认超时执行第一个
        if (mNavHintFloatView == null) {
            return;
        }
        mSelectHintFloatView.setFloatViewCallBack(mFloatViewCallBack);
    }

    private BaseFloatView.FloatViewCallBack mFloatViewCallBack = new BaseFloatView.FloatViewCallBack() {
        @Override
        public void onViewShow() {
//            isInterrupt = false;
        }

        @Override
        public void onViewDismiss() {
            if (!isInterrupt) {
                if (mSearchResultAdapter.getItemCount() > 0) {
                    if (!isFinishing()){
                        selectResultItemPosition(0);
                    }
                }
                isInterrupt = true;
            }

        }
    };
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

    private void setHomeAddressColor() {
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            mTvDestHome.setTextColor(Color.BLACK);
        } else {
            mTvDestHome.setTextColor(Color.WHITE);
        }

    }

    private void setCompanyAddressColor() {
        if (PhoneStateManager.getInstance(mContext).getPhoneState() == PhoneState.OUT_CAR_MODE) {
            mTvDestCompany.setTextColor(Color.BLACK);
        } else {
            mTvDestCompany.setTextColor(Color.WHITE);
        }

    }
}
