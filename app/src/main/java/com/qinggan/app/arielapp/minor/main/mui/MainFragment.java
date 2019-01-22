package com.qinggan.app.arielapp.minor.main.mui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.qinggan.app.arielapp.ActivityLifecycleListener;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.QueryBuildInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.app.MyConstants;
import com.qinggan.app.arielapp.minor.main.driving.adapter.PageFragmentAdapter;
import com.qinggan.app.arielapp.minor.main.driving.view.IntelligenceFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.OrdinaryFragment;
import com.qinggan.app.arielapp.minor.main.driving.view.SimpleFragment;
import com.qinggan.app.arielapp.minor.main.entity.Adresss;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.mui.adapter.CollectAdapter;
import com.qinggan.app.arielapp.minor.main.mui.adapter.HistoryAdapter;
import com.qinggan.app.arielapp.minor.main.mui.adapter.PoiAdapter;
import com.qinggan.app.arielapp.minor.main.navigation.NavDataManager;
import com.qinggan.app.arielapp.minor.main.navigation.NavigationActivity;
import com.qinggan.app.arielapp.minor.main.navigation.dialog.ConfirmStartNaviDialog;
import com.qinggan.app.arielapp.minor.main.utils.GetAddressUtil;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.main.utils.Tools;
import com.qinggan.app.arielapp.minor.main.welcome.WelcomeActivity;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.BleKeyHelper;
import com.qinggan.app.arielapp.ui.bluekey.MyCarActivity;
import com.qinggan.app.arielapp.ui.bluekey.VerfyCarIdentityActivity;
import com.qinggan.app.arielapp.ui.widget.LVCircularZoom;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.user.Bean.UserInfo;
import com.qinggan.app.arielapp.user.activity.LoginActivity;
import com.qinggan.app.arielapp.vehiclecontrol.VehcleControlManager;
import com.qinggan.app.arielapp.voiceview.VoiceFloatViewService;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.dcs.location.ILocation;
import com.qinggan.app.dcs.location.LocationImpl;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.voiceapi.control.UIControlMgr;
import com.qinggan.bluekey.manager.BleKeyManager;
import com.qinggan.bluekey.service.BleCarKey;
import com.qinggan.bluekey.service.BlueKeyService;
import com.qinggan.mobile.tsp.bean.CarCtrlRespBean;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.models.vhlcontrol.VhlCtlResult;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.mobile.tsp.restmiddle.RestResponse;
import com.qinggan.mobile.tsp.service.remotecontrol.RemoteResponseListener;
import com.qinggan.mobile.tsp.util.NetUtil;
import com.qinggan.qinglink.enumeration.AirConditionState;
import com.qinggan.qinglink.enumeration.VehicleState;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.qinggan.app.arielapp.minor.utils.Constants.LOGIN_EVENT;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;

public class MainFragment extends UIControlBaseFragment implements BaiduMap.OnMarkerClickListener, View.OnClickListener, ViewPager.OnPageChangeListener,
        IntegrationCore.VoiceChangeModeCallback, PhoneStateManager.PhoneStateChangeListener {

    protected String TAG = UIControlBaseFragment.class.getSimpleName();

    private TextureMapView mMapView = null;
    private BaiduMap mBaiduMap;
    //当前车辆的位置
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private double mCurrentAccracy = 0.0;

    //车辆位置
    private double lon_db = 0.0;
    private double lat_db = 0.0;

    private UiSettings mUiSettings;
    private boolean isFirstLoc = true;
    private MyLocationData locData;
    private ImageView _user;
    private View ble_back_door;
    private RelativeLayout search_lay;
    private RecyclerView collect_view;
    private RecyclerView history_view;
    private List<Adresss> dataList; //常用地址列表
    private List<BasicInfo> dataList_db;
    private List<NaviSearchHistory> dataList_h; //搜索历史列表
    private AlertDialog dialog;//搜索弹窗
    private LinearLayout _car;
    private EditText seach_where;
    private LinearLayout car_lock_btn;
    private LocalStorageTools localStorageTools;
    private Context context;
    private View mainview;
    private FragmentManager fragmentManager;
    private IFragmentStatusListener mFragmentStatusListener;
    private ViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private PageFragmentAdapter adapter = null;
    private TranslateAnimation mCloseAction;
    private TranslateAnimation mShowAction;
    private LatLng point;//当前人的位置
    private LatLng curPoint = new LatLng(32.05969736, 118.7572604);//中心位置 给个默认值
    private IntegrationCore integrationCore;
    private LatLng end_point; //车辆位置
    private ImageView search_btn;
    private TextView ms_text;
    private TextView car_number;
    List<PoiInfo> allAddr = new ArrayList<PoiInfo>();//poi检索结果list
    private RecyclerView poi_view;
    private PoiAdapter poiAdapter;
    private OrdinaryFragment ordinaryFragment;
    private IntelligenceFragment intelligenceFragment;
    private SimpleFragment simpleFragment;
    private List<UIControlElementItem> openElementItems = new ArrayList<>();
    private List<BasicInfo> cardList = new ArrayList<>();
    //    private static String PATH = "custom_config_dark.json";
    private ProgressBar remaining_pb;
    private LinearLayout hot_car;
    private LinearLayout trunk;
    private LinearLayout warmth;
    private LinearLayout cool;
    private LinearLayout window;
    private ImageView car_lock_img;
    private ImageView hot_car_img;
    private ImageView trunk_img;
    private ImageView warmth_img;
    private ImageView cool_img;
    private ImageView window_img;

    private TextView update_txt;
    private int carDoor;
    private int hotCar;
    private int carTrunk;
    private int carWarmth;
    private int carCool;
    private int carWinsow;
    private LinearLayout notice_lay;
    private LinearLayout nav_lay;
    private LinearLayout oil_lay;
    private TextView car_lock_txt;
    private TextView hot_car_txt;
    private TextView trunk_txt;
    private TextView warmth_txt;
    private TextView cool_txt;
    private TextView window_txt;
    private LVCircularZoom car_lock_loading;
    private LVCircularZoom hot_car_loading;
    private LVCircularZoom trunk_loading;
    private LVCircularZoom warmth_loading;
    private LVCircularZoom cool_loading;
    private LVCircularZoom window_loading;

//    private LinearLayout map_lay;

    private LinearLayout ble_car_unlock_btn;
    private LinearLayout ble_lock_car_btn;
    private LinearLayout ble_trunk;

    private LinearLayout blue_window;
    private LinearLayout blue_air;
    private LinearLayout blue_sky;

    private int cx;
    private int cy;
    private int radius;

    private LinearLayout journey_lay;
    private TextView iv_route;
    private String from;
    private LatLng dest;
    private NavDataManager mNavDataManager;
    private TextView car_adress;
    private TextView car_distance;
    private LinearLayout car_lay;
    private static final int MSG_PLAN_ROUTE = 1;
    private static final int MSG_START_NAVI_DIALOG = 2;
    private static final int MSG_START_RECORDING = 3;
    private static final int MSG_START_WELCOME = 4;
    private ConfirmStartNaviDialog mConfirmDialog;
    private TextView remote_ca_txt;
    private TextView buletooth_car_txt;
    private LinearLayout buletooth_car_lay;

    private LinearLayout bleContainer;
    private TextView bleTipTxt;
    private TextView bleTipClose;
    private PopupWindows popupWindows;


    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        this.mFragmentStatusListener = fragmentStatus;
    }


    @Override
    public void init(IASRSession session) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NewApi")
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("xiaohf", "inflaterView: s");
        fragmentManager = getFragmentManager();
        if (null != getActivity()) {
            context = getActivity();
        } else {
            Log.i("Alan", "getActivity()为null");
        }
        integrationCore = IntegrationCore.getIntergrationCore(context);
        integrationCore.changeStage(StageController.Stage.MAIN_LEAVE);//默认离车模式
        integrationCore.setVoiceCallback(this);
        /**
         * MapView (TextureMapView)的
         * {@link MapView.setCustomMapStylePath(String customMapStylePath)}
         * 方法一定要在MapView(TextureMapView)创建之前调用。
         * 如果是setContentView方法通过布局加载MapView(TextureMapView), 那么一定要放置在
         * MapView.setCustomMapStylePath方法之后执行，否则个性化地图不会显示
         */
//        setMapCustomFile(context, PATH);
        mainview = inflater.inflate(R.layout.activity_main_map, container, false);
        initCarStatus();//初始化车控状态
        initView();//初始化控件
        initView_(mainview);
        initMap();//初始化地图
        setAnimation();//设置动画
//        MapView.setMapCustomEnable(true);


        localStorageTools = new LocalStorageTools(context);
        viewPager = (ViewPager) mainview.findViewById(R.id.driver);
        ArielApplication.getApp().setDrViewPager(viewPager);
        viewPager.setOnPageChangeListener(this);
        fragmentList.clear();
        ordinaryFragment = new OrdinaryFragment();
        intelligenceFragment = new IntelligenceFragment();

        //integrationCore.initDbCarInfo();
        simpleFragment = new SimpleFragment();
//
        fragmentList.add(ordinaryFragment);
        fragmentList.add(intelligenceFragment);
        fragmentList.add(simpleFragment);

        adapter = new PageFragmentAdapter(fragmentManager, fragmentList);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//设置缓存页数
//        //获取上一次选中的下标
        int index = localStorageTools.getInteger("pageIndex");

        if (index == -1) {//默认值是-1
            index = 1;
        }

        viewPager.setCurrentItem(index);//设置显示哪个位置的页面

        addUIControlItems();

        notice_lay = (LinearLayout) mainview.findViewById(R.id.notice_lay);
        nav_lay = (LinearLayout) mainview.findViewById(R.id.nav_lay);
        oil_lay = (LinearLayout) mainview.findViewById(R.id.oil_lay);

        notice_lay.setOnClickListener(this);
        nav_lay.setOnClickListener(this);
        oil_lay.setOnClickListener(this);
        initUserDate();
        return mainview;
    }


    private void initCarStatus() {
        carDoor = MyConstants.LOCK_CAR;
        hotCar = MyConstants.NO_HOT_CAR;
        carTrunk = MyConstants.TRUNK_CLOSE;
        carWarmth = MyConstants.CLOSE_WARMTH;
        carCool = MyConstants.CLOSE_COOL;
        carWinsow = MyConstants.CLOSE_WINDOW;
//        queryVehicleStatus();

    }


    //初始化控件
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        updateStateBar(true);

        EventBus.getDefault().register(this);
        _user = (ImageView) mainview.findViewById(R.id._user);//个人中心
        ble_back_door = (View) mainview.findViewById(R.id.ble_back_door);//BLE演示后门
        _user.setOnClickListener(this);
        ble_back_door.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Toast.makeText(getActivity(),"长按后门",Toast.LENGTH_SHORT).show();
                if (null != getActivity()) {
                    BleKeyManager.getInstance(getActivity()).popUnlock();
                }
                return false;
            }
        });
        _car = (LinearLayout) mainview.findViewById(R.id._car);//驾驶模式
        _car.setOnClickListener(this);
        _car.setSelected(true);
        PhoneStateManager.getInstance(getContext()).setConnectionToCarStateChangeListener(new PhoneStateManager.ConnectionToCarStateChangeListener() {
            @Override
            public void onConnectionChanged(boolean connected) {
                _car.setSelected(!connected);
            }
        });
        search_lay = (RelativeLayout) mainview.findViewById(R.id.search_lay);//搜索栏
        search_lay.setOnClickListener(this);
        remaining_pb = (ProgressBar) mainview.findViewById(R.id.remaining_pb);
        remaining_pb.post(new Runnable() {
            @Override
            public void run() {
                remaining_pb.setProgress(40);
            }
        });

//        map_lay = (LinearLayout) mainview.findViewById(R.id.map_lay);


        car_lock_btn = (LinearLayout) mainview.findViewById(R.id.car_lock_btn);//解锁车辆
        car_lock_btn.setOnClickListener(this);
        hot_car = (LinearLayout) mainview.findViewById(R.id.hot_car);
        hot_car.setOnClickListener(this);
        trunk = (LinearLayout) mainview.findViewById(R.id.trunk);
        trunk.setOnClickListener(this);
        warmth = (LinearLayout) mainview.findViewById(R.id.warmth);
        warmth.setOnClickListener(this);
        cool = (LinearLayout) mainview.findViewById(R.id.cool);
        cool.setOnClickListener(this);
        window = (LinearLayout) mainview.findViewById(R.id.window);
        window.setOnClickListener(this);

        car_lock_img = (ImageView) mainview.findViewById(R.id.car_lock_img);
        hot_car_img = (ImageView) mainview.findViewById(R.id.hot_car_img);
        trunk_img = (ImageView) mainview.findViewById(R.id.trunk_img);
        warmth_img = (ImageView) mainview.findViewById(R.id.warmth_img);
        cool_img = (ImageView) mainview.findViewById(R.id.cool_img);
        window_img = (ImageView) mainview.findViewById(R.id.window_img);

        car_lock_txt = (TextView) mainview.findViewById(R.id.car_lock_txt);
        hot_car_txt = (TextView) mainview.findViewById(R.id.hot_car_txt);
        trunk_txt = (TextView) mainview.findViewById(R.id.trunk_txt);
        warmth_txt = (TextView) mainview.findViewById(R.id.warmth_txt);
        cool_txt = (TextView) mainview.findViewById(R.id.cool_txt);
        window_txt = (TextView) mainview.findViewById(R.id.window_txt);

        journey_lay = (LinearLayout) mainview.findViewById(R.id.journey_lay);
        journey_lay.setOnClickListener(this);
        iv_route = (TextView) mainview.findViewById(R.id.iv_route);
        car_number = (TextView) mainview.findViewById(R.id.car_number);

        ble_car_unlock_btn = (LinearLayout) mainview.findViewById(R.id.ble_car_unlock_btn);//解锁车辆
        ble_car_unlock_btn.setOnClickListener(this);
        ble_lock_car_btn = (LinearLayout) mainview.findViewById(R.id.ble_lock_car_btn);//解锁车辆
        ble_lock_car_btn.setOnClickListener(this);
        ble_trunk = (LinearLayout) mainview.findViewById(R.id.ble_trunk);//解锁车辆
        ble_trunk.setOnClickListener(this);


        blue_window = (LinearLayout) mainview.findViewById(R.id.blue_window);//打开车窗
        blue_window.setOnClickListener(this);
        blue_air = (LinearLayout) mainview.findViewById(R.id.blue_air);//打开空调
        blue_air.setOnClickListener(this);
        blue_sky = (LinearLayout) mainview.findViewById(R.id.blue_sky);//打开天窗
        blue_sky.setOnClickListener(this);

        PhoneStateManager.getInstance(context).addPhoneStateChangeListener(this);
        mNavDataManager = NavDataManager.getInstance();
        mNavDataManager.init(context);

        car_lay = (LinearLayout) mainview.findViewById(R.id.car_lay);
        car_adress = (TextView) mainview.findViewById(R.id.car_adress);
        car_distance = (TextView) mainview.findViewById(R.id.car_distance);
        remote_ca_txt = (TextView) mainview.findViewById(R.id.remote_ca_txt);
        initLoadView();
        buletooth_car_txt = (TextView) mainview.findViewById(R.id.buletooth_car_txt);
        buletooth_car_lay = (LinearLayout) mainview.findViewById(R.id.buletooth_car_lay);

        update_txt=(TextView)mainview.findViewById(R.id.update_txt);
        update_txt.setVisibility(View.GONE);

        bleContainer = (LinearLayout) mainview.findViewById(R.id.ble_key_container);
        bleTipTxt = (TextView) mainview.findViewById(R.id.ble_key_tip);
        bleTipTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == ArielApplication.getmUserInfo()) {
                    Log.d(TAG, "bleTipTxt  OnClickListener not login");
                    //没有临时钥匙,或者临时钥匙过期
                    if (null == BleKeyHelper.getBleCarKey()) {
                        Log.d(TAG, "bleTipTxt  OnClickListener  bleCarKey null");
                        BindCarActivity.startAction(getActivity());
                    } else {
                        Log.d(TAG, "bleTipTxt  OnClickListener  bleCarKey type:" + BleKeyHelper.getBleCarKey().keyType);
                        if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP) {
                            LoginActivity.startAction(getActivity());
                        }
                    }
                } else {
                    Log.d(TAG, "bleTipTxt  OnClickListener  has login");
                    //没有临时钥匙,或者临时钥匙过期
                    // 显示  我已上车,立刻用车
                    if (null == BleKeyHelper.getBleCarKey()) {
                        Log.d(TAG, "bleTipTxt  OnClickListener : bleCarKey null");
                        BindCarActivity.startAction(getActivity());
                    } else {
                        Log.d(TAG, "bleTipTxt  OnClickListener :  bleCarKey type:" + BleKeyHelper.getBleCarKey().keyType);
                        if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP) {
                            LoginActivity.startAction(getActivity());
                        } else if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_FOREVER) {
                            VerfyCarIdentityActivity.startAction(getActivity());
                        }
                    }
                }
            }
        });
        bleTipClose = (TextView) mainview.findViewById(R.id.ble_key_tip_close);
        bleTipClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHideKeyTipByType(false);
            }
        });
        /*PhoneStateManager.getInstance(getContext()).addVehicleStateChangeListener(new PhoneStateManager.PhoneStateChangeListener() {
            @Override
            public void onPhoneStateChange(PhoneState phoneState) {
                if (PhoneState.IN_CAR_MODE == phoneState) {
                    _car.performClick();
                }
            }
        });*/

        showOrHideKeyTipByType(true);
    }
    //修改状态栏颜色
    private void updateStateBar(boolean isLightColor) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取窗口区域
            Window window = getActivity().getWindow();
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



    private void delPresetDestInfo(final String sid) {
        mNavDataManager.deleteCloundNaviInfo(sid);
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO == event.getBussinessType()) {
            if (event.isSuccess()) {
                Log.i(TAG, "event.isSuccess() event.getCommandType() = " + event.getCommandType());
                deleteLocalPresetDestInfo(event, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
            } else {
                Log.i(TAG, "event is fail");
                RestError error = event.getRestError();
                if (error != null) {
                    Log.i(TAG, "error:" + error.getMessage());
                    Log.i(TAG, "error:" + error);
                }
                deleteLocalPresetDestInfo(event, MapUtils.NAVIINFO_SYNC_FLAG_DELETE);
            }
        }
    }

    private void deleteLocalPresetDestInfo(EventBusTSPInfo event, int syncFlag) {
        if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_DELETE) {
            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();
            if (addressBeans != null) {
                NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));

                NaviInfo queryNaviInfo = new NaviInfo();
                queryNaviInfo.setSid(naviInfo.getSid());

                List<BasicInfo> basicInfos = integrationCore.queryDestInfo(queryNaviInfo, context, NaviInfo.class.getName());
                for (BasicInfo basicInfo : basicInfos) {
                    if (basicInfo instanceof NaviInfo) {
                        Log.i(TAG, " delete basicInfo = " + basicInfo);
                        mNavDataManager.deleteLocalPresetNaviInfo((NaviInfo) basicInfo, syncFlag);
                    }
                }
            }
        }
    }

    public void setAnimation() {
        //设置显示时的动画
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        //设置隐藏时的动画，监听动画结束后隐藏选择框
        mCloseAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mCloseAction.setDuration(500);
        mCloseAction.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //这个selectTime包含选择框
                viewPager.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void addUIControlItems() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        addNormalUIControl();
        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void addNormalUIControl() {
        UIControlElementItem openMusicElement = new UIControlElementItem();
        openMusicElement.addWord(getString(R.string.car_open_music));
        openMusicElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.CAR_OPEN_MUSIC);
        mUIControlElements.add(openMusicElement);

        UIControlElementItem openModeElement = new UIControlElementItem();
        openModeElement.addWord(getString(R.string.car_open_contextual_model));
        openModeElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.CAR_OPEN_MODE);
        mUIControlElements.add(openModeElement);

        //打开驾驶模式
        UIControlElementItem onCar = new UIControlElementItem();
        onCar.addWord(getString(R.string.car_state1));
        onCar.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.OPEN_CAR_ON);
        mUIControlElements.add(onCar);
        //退出驾驶模式
        UIControlElementItem exitCar = new UIControlElementItem();
        exitCar.addWord(getString(R.string.car_state2));
        exitCar.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.EXIT_CAR_ON);
        mUIControlElements.add(exitCar);
        //切换到离车模式
        UIControlElementItem offCar = new UIControlElementItem();
        offCar.addWord(getString(R.string.car_state3));
        offCar.addWord(getString(R.string.car_state4));
        offCar.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.CAR_OFF);
        mUIControlElements.add(offCar);
    }

    /**
     * fragment首次创建的时候,已经在inflaterView中添加过uicontrol
     * 加该标记,防止重复注册
     */
    private boolean firstResume = true;

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Alan", "MainFragment onResume()..");
        //恢复首页语音uicontrol
        if (!firstResume)
            resumeUIControl();
        firstResume = false;
        //初始化本地存储
        mMapView.onResume();
        localStorageTools = new LocalStorageTools(context);
        integrationCore.getVehicleDetailInfo();//获取车辆信息
        //VoiceFloatView.getInstance().setMainFragment(true);
        //VoiceFloatView.getInstance().updateVoiceFloatLayout(true);
        intelligenceFragment.rgisterEventListener();
        List<NaviInfo> routeList = MapUtils.queryAllPresetNaviInfo(context);
        Log.i(TAG, " routeList = " + routeList);
        if (null != routeList) {
            if (routeList.size() > 0) {
                Log.i("Alan", "预设目的地=-=" + routeList.get(0).getDisplayName());
                iv_route.setText(routeList.get(0).getDisplayName());
                update_txt.setVisibility(View.VISIBLE);

            } else {
                Log.i("Alan", "预设目的地列表为空");
                iv_route.setText(getString(R.string.main_have_num));
                update_txt.setVisibility(View.GONE);
            }
        } else {
            Log.i("Alan", "预设目的地列表为空");
            iv_route.setText(getString(R.string.main_have_num));
            update_txt.setVisibility(View.GONE);
        }
        carControlUI();
        queryVehicleStatus();

    }

    private void resumeUIControl() {
        UIControlMgr.getInstance().addElementAndListContent(mUIControlElements, mUiControlItems);
        UIControlMgr.getInstance().updateContentToAsr();
        UIControlMgr.getInstance().registerUIControlCallback(this, mFragmentHashCode);
    }

    private void carControlUI() {
        boolean isCarControl = localStorageTools.getBoolean("isCarControl");
        if (isCarControl) {
            remote_ca_txt.setVisibility(View.VISIBLE);
            buletooth_car_txt.setVisibility(View.VISIBLE);
            buletooth_car_lay.setVisibility(View.VISIBLE);
        } else {
            remote_ca_txt.setVisibility(View.GONE);
            buletooth_car_txt.setVisibility(View.GONE);
            buletooth_car_lay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //VoiceFloatView.getInstance().updateVoiceFloatLayout(false);
        //VoiceFloatView.getInstance().setMainFragment(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        LocationImpl.getInstance(context).delLocationListener(mLocationListener);
        PhoneStateManager.getInstance(context).removePhoneStateChangeListener(this);
    }

    @Override
    public void onSelectOtherOC(String action) {
        super.onSelectOtherOC(action);
        if (ConstantNavUc.CAR_OPEN_MUSIC.equals(action)) {
            //打开音乐
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.MUSIC);
        } else if (ConstantNavUc.CAR_OPEN_MODE.equals(action)) {
            //打开情景模式
            IntegrationCore.getIntergrationCore(ArielApplication.getApp()).VoiceJump(StageController.Stage.SCENARIO);
        } else if (ConstantNavUc.OPEN_CAR_ON.equals(action)) {
            //打开驾驶模式
            if (IntegrationCore.getIntergrationCore(ArielApplication.getApp()).voiceCtrl(true))
                VoicePolicyManage.getInstance().speak(getString(R.string.car_state_open));
        } else if (ConstantNavUc.EXIT_CAR_ON.equals(action)) {
            //退出驾驶模式
            if (IntegrationCore.getIntergrationCore(ArielApplication.getApp()).voiceCtrl(false))
                VoicePolicyManage.getInstance().speak(getString(R.string.car_state_exit));
        } else if (ConstantNavUc.CAR_OFF.equals(action)) {
            //切换到离车模式
            if (IntegrationCore.getIntergrationCore(ArielApplication.getApp()).voiceCtrl(false))
                VoicePolicyManage.getInstance().speak(getString(R.string.car_state_off));
        } else if (action.startsWith(NAV_IN_ROUTE_SELECT)) {
            String[] strs = action.split(":");
            if (strs.length == 2) {
                //已经跳转到导航中,处理用户说的第一个第二个第三个
                int index = Integer.parseInt(strs[1]);
                Log.d(TAG, "onSelectOtherOC:index:" + index);
                integrationCore.onNavRouteSelect(index);
            }
        } else if (ConstantNavUc.NAV_CANCEL.equals(action)) {
            integrationCore.cancelNav();
        } else if (ConstantNavUc.NAV_START.equals(action)) {
            integrationCore.startNav();
        } else if (ConstantNavUc.NAV_CONFIRM.equals(action)) {
            mHandler.sendEmptyMessage(MSG_PLAN_ROUTE);
        }
    }

    //控件监听
    @Override
    public void onClick(final View v) {
        if (null == getActivity()) {
            Log.i("Alan", "getActivity()为null");
            return;
        } else {
            Log.i("Alan", "getActivity()不为null");
        }
        switch (v.getId()) {
            case R.id._mycar:
                UserInfo mUser = ArielApplication.getmUserInfo();
                if (mUser != null) {
                    Intent intent = new Intent(getActivity(), MyCarActivity.class);
                    getActivity().startActivity(intent);
                }
                break;
            case R.id._user:
                Intent intent = new Intent(getActivity(), MyCenterActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_jingzhi);
                break;
            case R.id._car:
//                integrationCore.voiceCtrl(true);
                //PhoneStateManager.getInstance(context).setPhoneStateForTest(PhoneState.IN_CAR_MODE);
                //模式切换的时候对自动弹出欢迎界面，加入延时的以避免智能推荐界面早于欢迎界面先显示，造成错乱
                /**mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {*/
                try {
                    updateStateBar(false);
                    viewPager.setVisibility(View.VISIBLE);
                    radius = Math.max(viewPager.getWidth(), viewPager.getHeight());
                    if (MainFragment.this.isVisible()) {
                        Animator animator = ViewAnimationUtils.createCircularReveal(viewPager, viewPager.getWidth(), 0, 0, radius);
                        animator.setDuration(500);
                        animator.start();
                    }
                    VoiceFloatViewService voiceFloatViewService = MainActivity.getVoiceFloatViewService();
                    if (null != voiceFloatViewService)
                        voiceFloatViewService.changeInCarMode(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /**}
                 }, 2500);*/
                break;

            case R.id.journey_lay://预设目的地
                if(update_txt.getVisibility()==View.GONE){
                    return;
                }
                popupWindows = new PopupWindows(context, mainview);
                break;
            case R.id.search_lay://搜索
//                double[] bd_lat_lon= new MapUtils(getActivity()).gaoDeToBaidu(118.750666,32.053996);
//
//            Log.i("Alan","=-=="+bd_lat_lon[0]+","+bd_lat_lon[1]);
                //                getAdress(mCurrentLon,mCurrentLat);
//                //测试用
//                LatLng  st_point = new LatLng(  31.066225,118.762341);
//                LatLng  end_point = new LatLng(  32.066225,118.762341);
//                new MapUtils(context).setElement(st_point,end_point);
                gotoNavigationActivity(false, true);
                break;
            case R.id.search_btn:
                String search_text = seach_where.getText().toString();
                if (search_text.equals("")) {
                    Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //将搜索界面保存到本地
                NaviSearchHistory naviSearchHistory = new NaviSearchHistory();
                naviSearchHistory.setContents(search_text);
                naviSearchHistory.setCreateDate(new Date());
                integrationCore.saveSearchText(naviSearchHistory, context, NaviSearchHistory.class.getName());
                new MapUtils(context).searchByText(search_text, new LatLng(mCurrentLat, mCurrentLon));//poi检索

                break;

//            case R.id.adress_ys_lay://点击预设目的地时
//                new PopupWindows(context, adress_ys_lay);
//                break;
            case R.id.car_lock_btn://解锁车辆
                if (carDoor == MyConstants.LOCK_CAR) {
                    VehcleControlManager.getInstance(getActivity()).vehicleUnlocking(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);
                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            ToastUtil.show("指令发送失败", context);
                            hideLoadView(v);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (null != vhlCtlResult && vhlCtlResult.getRemoteDoorResult() != null &&
                                    TextUtils.equals("0", vhlCtlResult.getRemoteDoorResult().getRemoteDoorResult())) {
                                doorStatusChange(false);
                                ToastUtil.show(R.string.door_unlocked, context);

                            }else{
                                ToastUtil.show("解锁失败", context);
                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                } else {
                    VehcleControlManager.getInstance(getActivity()).vehiclelocking(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
//                            if (null != vhlCtlResult && vhlCtlResult.getRemoteDoorResult() != null &&
//                                    TextUtils.equals("0", vhlCtlResult.getRemoteDoorResult().getRemoteDoorResult())) {
                                doorStatusChange(true);
                                ToastUtil.show(R.string.door_locked, context);
//
//                            }else{
//                                ToastUtil.show("锁门失败", context);
//                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                }
//                new MapUtils(context).setElement();
//                showCarLock();
                break;
            case R.id.hot_car://热车
                if (hotCar == MyConstants.NO_HOT_CAR) {
                    VehcleControlManager.getInstance(getActivity()).startEnine(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                hotCarStatusChange(true);
                                ToastUtil.show(R.string.main_is_hot_car, context);
                            }else{
                                ToastUtil.show("热车失败，请稍后尝试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                } else {
                    VehcleControlManager.getInstance(getActivity()).closeEnine(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                hotCarStatusChange(false);
                                ToastUtil.show(R.string.engine_has_been_shut_down, context);

                            }else{
                                ToastUtil.show("熄火失败，请稍后尝试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                }
                break;
            case R.id.trunk://后备箱
                VehcleControlManager.getInstance(getActivity()).openTrunk(new RemoteResponseListener() {
                    @Override
                    public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                        showLoadView(v);

                    }

                    @Override
                    public void onSendFailure(RestError restError) {
                        hideLoadView(v);
                        ToastUtil.show("指令发送失败", getActivity());

                    }

                    @Override
                    public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                        hideLoadView(v);
                        if (vhlCtlResult!=null && vhlCtlResult.getRemoteTrunkResult()!=null
                                && TextUtils.equals("0",vhlCtlResult.getRemoteTrunkResult().getRemoteTrunkResult())) {
                            ToastUtil.show("尾门已解锁", getActivity());
                        }else {
                            ToastUtil.show("尾门解锁失败,请稍候重试", getActivity());

                        }
                    }

                    @Override
                    public void onCmdTimeout() {
                        hideLoadView(v);
                        ToastUtil.show("命令执行超时", context);


                    }
                });
                break;
            case R.id.warmth://温暖
                if (carWarmth == MyConstants.CLOSE_WARMTH) {
                    VehcleControlManager.getInstance(getActivity()).setAirWithWorm(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                warmStatusChange(true);
                                ToastUtil.show(R.string.main_is_warm, context);
                            }else{
                                ToastUtil.show("打开温暖模式失败，请稍后重试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                } else {
                    VehcleControlManager.getInstance(getActivity()).closeEnine(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                warmStatusChange(false);
                                ToastUtil.show(R.string.engine_has_been_shut_down, context);

                            }else{
                                ToastUtil.show("熄火失败，请稍后尝试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                }

                break;
            case R.id.cool://清凉
                if (carCool == MyConstants.CLOSE_COOL) {
                    VehcleControlManager.getInstance(getActivity()).setAirWithCold(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                coolStatusChange(true);
                                ToastUtil.show(R.string.main_is_cool, context);
                            }else{
                                ToastUtil.show("打开清凉模式失败，请稍后重试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                } else {
                    VehcleControlManager.getInstance(getActivity()).closeEnine(new RemoteResponseListener() {

                        @Override
                        public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                            showLoadView(v);

                        }

                        @Override
                        public void onSendFailure(RestError restError) {
                            hideLoadView(v);
                            ToastUtil.show("指令发送失败", context);
                        }

                        @Override
                        public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                            hideLoadView(v);
                            if (vhlCtlResult!=null && vhlCtlResult.getResultList()!=null && vhlCtlResult.getResultList().size()>0
                                    && TextUtils.equals("0",vhlCtlResult.getResultList().get(0).getRemoteObjectResult())){
                                coolStatusChange(false);
                                ToastUtil.show(R.string.engine_has_been_shut_down, context);

                            }else{
                                ToastUtil.show("熄火失败，请稍后尝试", context);

                            }
                        }

                        @Override
                        public void onCmdTimeout() {
                            hideLoadView(v);
                            ToastUtil.show("命令执行超时", context);
                        }
                    });
                }

                break;
            case R.id.window://开窗
                VehcleControlManager.getInstance(getActivity()).openWindow(false, new RemoteResponseListener() {

                    @Override
                    public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                        showLoadView(v);

                    }

                    @Override
                    public void onSendFailure(RestError restError) {
                        hideLoadView(v);
                        ToastUtil.show("指令发送失败", context);
                    }

                    @Override
                    public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                        hideLoadView(v);
                        if (vhlCtlResult!=null && vhlCtlResult.getRemoteWindowsResult()!=null) {
                            String errorWindows="";
//                            if (TextUtils.equals("0",vhlCtlResult.getRemoteWindowsResult().getLeftFrontWindowResult())){
//                                ToastUtil.show("车窗已关闭", getActivity());
//                            }else{
//                                ToastUtil.show("车窗关闭失败,请稍候重试", getActivity());
//
//                            }
                            if (TextUtils.equals("1",vhlCtlResult.getRemoteWindowsResult().getLeftFrontWindowResult())){
                                errorWindows=errorWindows+"左前窗,";
                            }
                            if (TextUtils.equals("1",vhlCtlResult.getRemoteWindowsResult().getRightFrontWindowResult())){
                                errorWindows=errorWindows+"右前窗,";
                            }
                            if (TextUtils.equals("1",vhlCtlResult.getRemoteWindowsResult().getLeftRearWindowResult())){
                                errorWindows=errorWindows+"左后窗,";
                            }
                            if (TextUtils.equals("1",vhlCtlResult.getRemoteWindowsResult().getRightRearWindowResult())){
                                errorWindows=errorWindows+"右后窗,";
                            }
//                            if (TextUtils.equals("1",vhlCtlResult.getRemoteSunroofResult().getRemoteSunroofResult())){
//                                errorWindows=errorWindows+"天窗,";
//                            }
                            if (TextUtils.isEmpty(errorWindows)){
                                ToastUtil.show("车窗已关闭", getActivity());
                            }else{
                                ToastUtil.show(errorWindows+"关闭失败,请稍候重试", getActivity());

                            }
                        }else {
                            ToastUtil.show("车窗关闭失败,请稍候重试", getActivity());

                        }

                    }

                    @Override
                    public void onCmdTimeout() {
                        hideLoadView(v);
                        ToastUtil.show("命令执行超时", context);
                    }
                });
                break;
            case R.id.notice_lay:
                VehcleControlManager.getInstance(getActivity()).findCar(new RemoteResponseListener() {
                    @Override
                    public void onSendSuccess(CarCtrlRespBean resp, RestResponse restResponse) {
                        showLoadView(v);

                    }

                    @Override
                    public void onSendFailure(RestError restError) {
                        hideLoadView(v);
                        ToastUtil.show("指令发送失败", getActivity());

                    }

                    @Override
                    public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                        hideLoadView(v);
                        if (vhlCtlResult!=null && vhlCtlResult.getRemoteDangerWarningLampResult()!=null
                                && TextUtils.equals("0",vhlCtlResult.getRemoteDangerWarningLampResult().getRemoteDangerWarningLampResult())) {
                            ToastUtil.show("鸣笛闪灯已打开", getActivity());
                        }else{
                            ToastUtil.show("打开鸣笛闪灯失败，请稍后再试", getActivity());

                        }

                    }

                    @Override
                    public void onCmdTimeout() {
                        hideLoadView(v);
                        ToastUtil.show("命令执行超时", context);

                    }
                });

                break;
            case R.id.nav_lay:
                navToCar();

                break;
            case R.id.oil_lay:
                gotoNavigationActivity(true, false);
                break;
            case R.id.ble_car_unlock_btn:
                if (BleKeyManager.getInstance(getActivity()).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(getActivity()).setDoor(1);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", getActivity());
                }
                break;
            case R.id.ble_lock_car_btn:
                if (BleKeyManager.getInstance(getActivity()).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(getActivity()).setDoor(0);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", getActivity());
                }
                break;
            case R.id.ble_trunk:
                if (BleKeyManager.getInstance(getActivity()).getBlueKeyServiceState() == BlueKeyService.SERVICE_STATE_AUTH_SUCCESS) {
                    BleKeyManager.getInstance(getActivity()).setTrunk(1);
                } else {
                    ToastUtil.show("蓝牙钥匙还未连接通过认证", getActivity());
                }
                break;
            case R.id.blue_window:
                ArielApplication.getCanBusManager().setVehicleState(VehicleState.DRIVER_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);

                blue_window.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArielApplication.getCanBusManager().setVehicleState(VehicleState.PASSENGER_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);

                    }
                }, 100);
                blue_window.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArielApplication.getCanBusManager().setVehicleState(VehicleState.REAR_LEFT_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);

                    }
                }, 200);
                blue_window.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ArielApplication.getCanBusManager().setVehicleState(VehicleState.REAR_RIGHT_POWER_WINDOW_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);
                    }
                }, 300);

                break;
            case R.id.blue_air:
                ArielApplication.getCanBusManager().setAirConditionState(AirConditionState.AC_POWER_SWITCH, AirConditionState.SWITCH_ON);
                break;
            case R.id.blue_sky:
                ArielApplication.getCanBusManager().setVehicleState(VehicleState.POWER_SUNROOF_CONTROL_SWITCH, VehicleState.SWITCH_OPEN);

                break;
            default:

                break;

        }
    }

    //导航到车
    private void navToCar() {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        if (lat_db == 0.0 && lon_db == 0.0) {
            ToastUtil.show(getString(R.string.main_no_search_car), context);
        } else {
            boolean isInstalled = integrationCore.getCardController().isBaiduMapInstalled();
            if (isInstalled) {
                Log.i("Alan", "已安装百度地图");
                Log.i("Alan", "寻车导航车辆位置信息" + lat_db + "=-=" + lon_db);
                Log.i("Alan", "寻车导航人位置信息" + mCurrentLat + "=-=" + mCurrentLon);
                end_point = new LatLng(lat_db, lon_db);
                point = new LatLng(mCurrentLat, mCurrentLon);
                integrationCore.findCar(context, point, end_point);
            } else {
                Log.i("Alan", "未安装百度地图");
                integrationCore.getCardController().goToInstallSomeApp(integrationCore.getCardController().PACKAGE_NAME_BAIDU_MAP);
            }
        }
    }

    private MyLocationListenner mLocationListener = new MyLocationListenner();

    //初始化地图
    private void initMap() {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        //获取地图控件引用
        mMapView = mainview.findViewById(R.id.bmapView);
        mMapView.getLayoutParams().height  = getScreenHeight();
        mMapView.requestLayout();

        mBaiduMap = mMapView.getMap();
        mUiSettings = mBaiduMap.getUiSettings();
        // 开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        //地图上比例尺
        mMapView.showScaleControl(false);
        // 隐藏缩放控件
        mMapView.showZoomControls(false);
        mMapView.setEnabled(false);

        // 定位初始化
//        mLocClient = new LocationClient(getActivity().getApplicationContext());
//        mLocClient.registerLocationListener(new MyLocationListenner());
//        LocationClientOption option = new LocationClientOption();
//        option.setOpenGps(true); // 打开gps
//        option.setCoorType("bd09ll"); // 设置坐标类型
//        option.setScanSpan(60000);// 设置定位间隔时间
//        mLocClient.setLocOption(option);
//        mLocClient.start();

        LocationImpl location = LocationImpl.getInstance(context);
        ILocation.LocationInfo locationInfo = location.getLocationInfo();
        if (locationInfo != null) {
            MapStatus.Builder builder = new MapStatus.Builder();
            LatLng center = new LatLng(locationInfo.latitude, locationInfo.longitude);
            builder.target(center).zoom(17);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
        location.addLocationListener(mLocationListener);

//        if (cbAllGestures.isChecked()) {
        mUiSettings.setAllGesturesEnabled(false);
//        } else {
//        mUiSettings.setZoomGesturesEnabled(true);
//        mUiSettings.setScrollGesturesEnabled(true);
//        mUiSettings.setRotateGesturesEnabled(true);
//        mUiSettings.setOverlookingGesturesEnabled(false);
//        }
        mBaiduMap.setOnMarkerClickListener(this);
        // add
        if (mGestureDetector == null) {
            mGestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    switchMap();
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {

                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });
        }
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i(TAG, "onMapClick: ");
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (ll_bottom.getVisibility() == View.GONE || ll_head.getVisibility() == View.GONE) {
                        scrollview.requestDisallowInterceptTouchEvent(true);
                        scrollview.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return true;
                            }
                        });
                    }
                } else {
                    if (ll_bottom.getVisibility() == View.VISIBLE || ll_head.getVisibility() == View.VISIBLE) {
                        scrollview.requestDisallowInterceptTouchEvent(false);
                        scrollview.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                return false;
                            }
                        });
                    }
                }


                if (ll_bottom.getVisibility() == View.VISIBLE || ll_head.getVisibility() == View.VISIBLE) {
                    if (mGestureDetector != null) {
                        mGestureDetector.onTouchEvent(event);
                    }
                }

            }
        });
        MapUtils.syncNaviInfo(context);
    }


    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCarEvent(EventBusTSPInfo event) {
        if (null == getActivity()) {
            Log.i("Alan", "getActivity()为null");
            return;
        }
        if (EventBusTSPInfo.BUSSINESS_TYPE_VEHICLE_INFO == event.getBussinessType()) {
            if (event.isSuccess()) {
                //拿到车辆信息

                if (null != event.getModule()) {
                    VehicleDetailInfo vehicleDetailInfo = (VehicleDetailInfo) event.getModule();
                    Log.i("Alan", "获取到的车辆数据：" + vehicleDetailInfo.getData());
                    Log.i("Alan", "获取到的原始坐标：" +
                            vehicleDetailInfo.getData().getLat() + "=-=" + vehicleDetailInfo.getData().getLon());
                    Log.i("Alan", "获取到的剩余里程：" + vehicleDetailInfo.getData().getAutonomie());
                    Log.i("Alan", "获取到的总里程：" + vehicleDetailInfo.getData().getKmTotal());

                    if (null != vehicleDetailInfo.getData() && null != vehicleDetailInfo.getData().getLat()
                            && null != vehicleDetailInfo.getData().getLon()) {
                        double gd_lon_db = Double.valueOf(vehicleDetailInfo.getData().getLon());
                        double gd_lat_db = Double.valueOf(vehicleDetailInfo.getData().getLat());
                        Log.i("Alan", "获取到的高德车辆位置信息" + gd_lat_db + "=-=" + gd_lon_db);
                        //高德坐标转百度坐标
                        double[] bd_lat_lon = new MapUtils(getActivity()).gaoDeToBaidu(gd_lon_db, gd_lat_db);
                        lon_db = bd_lat_lon[0];
                        lat_db = bd_lat_lon[1];
                        Log.i("Alan", "转换后的百度车辆位置信息" + lat_db + "=-=" + lon_db);

                        end_point = new LatLng(lat_db, lon_db);
                        curPoint = end_point;
                        int icion = R.drawable.carplace;
                        updateMap(icion);
                        getAdress(lon_db, lat_db);
                        //计算当前位置到车的距离
                        LatLng end_point = new LatLng(lat_db, lon_db);
                        //测试
//                    LatLng  end_point = new LatLng(  32.066225,118.762341);
                        new MapUtils(getActivity()).setElement(point, end_point);
                    } else {
                        Log.i("Alan", "返回的车辆的坐标为null");
                        car_lay.setVisibility(View.GONE);
                        int icion1 = R.drawable.home_icon_me;
                        updateMap(icion1);
                    }
                } else {
                    Log.i("Alan", "返回的车辆数据为null");
                    car_lay.setVisibility(View.GONE);
                    int icion2 = R.drawable.home_icon_me;
                    updateMap(icion2);
                }
            } else {
                Log.i("Alan", "未登录或者未绑定车辆");
                car_lay.setVisibility(View.GONE);
                int icion3 = R.drawable.home_icon_me;
                updateMap(icion3);
            }
        }

    }

    //刷新地图标注以及中心点
    private void updateMap(int icion) {
        //定义Maker坐标点
        mBaiduMap.clear();
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(icion);
        //构建MarkerOption，用于在地图上添加Marker

        OverlayOptions option = new MarkerOptions()
                .position(curPoint)
                .icon(bitmap);
        mBaiduMap.addOverlay(option);
        //设定中心点坐标//定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder() //要移动的点
                .target(curPoint) //放大地图到20倍
                .zoom(17).build(); //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus); //改变地图状态
        mBaiduMap.setMapStatus(mMapStatusUpdate);

    }

    //根据经纬度转位置文字描述
    private void getAdress(double lon, double lat) {
        GetAddressUtil.reverseGeoParse(lon, lat, new OnGetGeoCoderResultListener() {
            //获取正向解析结果时执行函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult arg0) {
            }

            //获取反向解析结果时执行函数
            @Override
            public void onGetReverseGeoCodeResult(final ReverseGeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
//                    Toast.makeText(getActivity(), "抱歉，未能找到结果!", Toast.LENGTH_LONG);
                } else {////得到结果后处理方法
//                    Toast.makeText(getActivity(), "地址为："+result.getAddress(), Toast.LENGTH_LONG);
                    if (null != getActivity()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                car_lay.setVisibility(View.VISIBLE);
                                car_adress.setText(result.getAddress());
                                Log.i("Alan", "解析的车辆位置" + result.getAddress());
                            }
                        });
                    } else {
                        Log.i("Alan", "getActivity()为null");
                    }

                }
            }

        });

    }


    //接收EventBusTSPInfo消息,异步处理
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void asyncHandleTSPInfoEvent(EventBusTSPInfo event) {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        Log.d(TAG, "asyncHandleTSPInfoEvent EventBusTSPInfo:" + event);
        if (event.isSuccess()) {
            if (EventBusTSPInfo.BUSSINESS_TYPE_NAVI_INFO == event.getBussinessType()
                    && MapUtils.TSP_COMMAND_TYPE_QUERY == event.getCommandType()) {
                MapUtils.syncNaviIfoAfterTSPRequest(context, event);
            } else {
                //skip
            }
        } else {
            if (event.getRestError() != null) {
                Log.e(TAG, "RestError:" + event.getRestError().getMessage());
            }
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //将当前选中的下标保存下来
        localStorageTools.setInteger("pageIndex", position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //模式切换
    @Override
    public boolean voiceChangeMode(boolean isIncar) {
        boolean isSuccess = false;
        if (isIncar) {//离车切驾驶
            updateStateBar(false);
            //判断当前是否是离车模式
//            if (integrationCore.getStage() == StageController.Stage.MAIN_LEAVE) {
            integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
            viewPager.setVisibility(View.VISIBLE);
            viewPager.startAnimation(mShowAction);
            isSuccess = true;

//            } else {
//                isSuccess = false;
//                Toast.makeText(context, "当前页面不能切换离车模式", Toast.LENGTH_SHORT).show();
//            }
        } else {//驾驶切离车
            updateStateBar(true);
            //判断当前是否是驾驶模式
//            if (integrationCore.getStage() == StageController.Stage.MAIN_IN_CAR) {
            integrationCore.changeStage(StageController.Stage.MAIN_LEAVE);
            viewPager.setVisibility(View.GONE);
            viewPager.startAnimation(mCloseAction);
            isSuccess = true;
//            } else {
//                isSuccess = false;
//                Toast.makeText(context, "当前页面不能切换离车模式", Toast.LENGTH_SHORT).show();
//            }
        }
        VoiceFloatViewService voiceFloatViewService = MainActivity.getVoiceFloatViewService();
        if (null != voiceFloatViewService) voiceFloatViewService.changeInCarMode(isIncar);
        return isSuccess;
    }


    //地图上标注监听
    @Override
    public boolean onMarkerClick(Marker marker) {
        navToCar();
        return true;
    }

    @Override
    public void onPhoneStateChange(PhoneState phoneState) {
        Log.d(TAG, "onPhoneStateChange phoneState : " + phoneState);
        if (phoneState == PhoneState.IN_CAR_MODE) {
            if (popupWindows != null && popupWindows.isShowing()) {
                popupWindows.dismiss();
            }
            _car.performClick();
            updateStateBar(false);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_WELCOME), 2000);
        }
    }

    private void startWelcome() {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        Intent startWelcome = new Intent(context, WelcomeActivity.class);
        startActivity(startWelcome);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAN_ROUTE:
                    dismissDialog();
                    startPlanRoute();
                    break;
                case MSG_START_NAVI_DIALOG:
                    showConfirmStartNaviDialog();
                    break;
                case MSG_START_RECORDING:
                    VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
                    VoicePolicyManage.getInstance().record(true);
                    break;
                case MSG_START_WELCOME:
                    startWelcome();
                    break;
            }
        }
    };

    private VoicePolicyManage.TtsStateChangeListener mTtsStateChangeListener = new VoicePolicyManage.TtsStateChangeListener() {
        @Override
        public void onStart() {
        }

        @Override
        public void onDone() {
            Log.i(TAG, "speak done");
            mHandler.sendEmptyMessage(MSG_START_RECORDING);
        }

        @Override
        public void onError() {
        }
    };

    private void showConfirmStartNaviDialog() {
        VoicePolicyManage.getInstance().speak(getString(R.string.navi_dialog_message));
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);

        mConfirmDialog = new ConfirmStartNaviDialog(ActivityLifecycleListener.getInstance().currentActivity);
        mConfirmDialog.setOnclickListener(new ConfirmStartNaviDialog.OnClickListener() {
            @Override
            public void onConfirmBtnClick() {
                startPlanRoute();
            }

            @Override
            public void onCancleBtnClick() {
//                VoicePolicyManage.getInstance().interrupt(true);
            }
        });
        /*if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            mConfirmDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            mConfirmDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }*/
        mConfirmDialog.show();
        addDialogConfirmUIControl();
    }

    private void addDialogConfirmUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem confirmNavElement = new UIControlElementItem();
        confirmNavElement.addWord(getString(R.string.navi_confirm));
        confirmNavElement.addWord(getString(R.string.navi_yes));
        confirmNavElement.addWord(getString(R.string.navi_ok));
        confirmNavElement.addWord(getString(R.string.nav));
        confirmNavElement.addWord(getString(R.string.navi_ensure));
        confirmNavElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_CONFIRM);
        mUIControlElements.add(confirmNavElement);

        addNormalUIControl();
        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void dismissDialog() {
        if (mConfirmDialog != null
                && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
        }
    }

    public void startPlanRoute() {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        Log.i(TAG, " NAVI startPlanRoute ");
        integrationCore.planRoute(context, from, dest, point);

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

        addNormalUIControl();
        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements ILocation.LocationListener {

        @Override
        public void onError(int errCode) {

        }

        @Override
        public void onReceiveLocation(ILocation.LocationInfo location) {
//            Log.i("xiaohf", "onReceiveLocation: ");
//        }
//
//        @Override
//        public void onReceiveLocation(BDLocation location) { // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mBaiduMap.clear();//清空之前的标注
            mCurrentLat = location.latitude;
            mCurrentLon = location.longitude;
            mCurrentAccracy = location.radius;
            int icion = R.drawable.carplace;
            point = new LatLng(mCurrentLat, mCurrentLon);
            curPoint = point;
            //定义Maker坐标点
            if (lat_db != 0.0 && lon_db != 0.0) {//获取到车辆的位置
                end_point = new LatLng(lat_db, lon_db);
                curPoint = end_point;
                icion = R.drawable.carplace;
            } else {//未获取到车辆位置则将中心移动到人的位置
                icion = R.drawable.home_icon_me;
            }

            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(icion);
            //构建MarkerOption，用于在地图上添加Marker

            OverlayOptions option = new MarkerOptions()
                    .position(curPoint)
                    .icon(bitmap);
            mBaiduMap.addOverlay(option);
            //设定中心点坐标//定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder() //要移动的点
                    .target(curPoint) //放大地图到20倍
                    .zoom(17).build(); //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus); //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);


//        locData = new MyLocationData.Builder() .accuracy(location.getRadius()) // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(1).latitude(location.getLatitude()) .longitude(location.getLongitude()).build();
//        mBaiduMap.setMyLocationData(locData);
//            if (isFirstLoc) {
//                isFirstLoc = false;
//                LatLng ll = new LatLng(location.getLatitude(),
//                        location.getLongitude());
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(ll).zoom(17.0f);
//                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }


    private byte[] InputStreamToByte(InputStream is) throws IOException {
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            bytestream.write(ch);
        }
        byte imgdata[] = bytestream.toByteArray();
        bytestream.close();
        return imgdata;

    }

    private void gotoNavigationActivity(boolean isOilSearch, boolean homePageSearch) {
        if (null == getActivity()) {
            Log.i("Alan", "getActivity()为null");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), NavigationActivity.class);
        if (isOilSearch) {
            intent.putExtra("isOilSearch", isOilSearch);
        }

        if (homePageSearch) {
            intent.putExtra("homePageSearch", homePageSearch);
        }

        startActivity(intent);
    }


    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        if (null == context) {
            Log.i("Alan", "context为null");
            return;
        }
        String type = event.getType();
        switch (type) {
            case "setElement":
                car_distance.setText(event.getLen());
                break;
            case "PresetAdress"://选择好预设目的地
                if (dialog != null) {//关闭搜索弹窗
                    dialog.dismiss();
                }
                Toast.makeText(context, "您选择了" + event.getAdresss().getRemark(), Toast.LENGTH_SHORT).show();
                //隐藏搜索狂，显示预设目的
//                search_lay.setVisibility(View.GONE);
                break;
            case "downClose":
                updateStateBar(true);
                integrationCore.changeStage(StageController.Stage.MAIN_LEAVE);
                viewPager.setVisibility(View.GONE);
                viewPager.startAnimation(mCloseAction);
                VoiceFloatViewService voiceFloatViewService = MainActivity.getVoiceFloatViewService();
                if (null != voiceFloatViewService) voiceFloatViewService.changeInCarMode(false);
                break;

            case "poiList":
                allAddr = event.getAllAddr();
                ms_text.setVisibility(View.GONE);
                collect_view.setVisibility(View.GONE);
                history_view.setVisibility(View.GONE);
                poi_view.setVisibility(View.VISIBLE);
                poiAdapter = new PoiAdapter(context, allAddr);
                LinearLayoutManager linearLayoutManager_p = new LinearLayoutManager(context);
                linearLayoutManager_p.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
                poi_view.setLayoutManager(linearLayoutManager_p);
                //添加分割线
                poi_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
                poi_view.setAdapter(poiAdapter);
                break;
            case LOGIN_EVENT:
                initUserDate();
                break;
            default:
                break;
        }
    }


    //显示操作目的地弹窗
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);
            if (null == context) {
                Log.i("Alan", "context为null");
                return;
            }
            View view = View.inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in_2));

            setWidth(LinearLayout.LayoutParams.FILL_PARENT);
            setHeight(LinearLayout.LayoutParams.FILL_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            TextView bt1 = (TextView) view.findViewById(R.id.updateadress);
            TextView bt2 = (TextView) view.findViewById(R.id.deleteadress);
            TextView bt3 = (TextView) view.findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    showDeclare();
                    gotoNavigationActivity(false, false);
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    adress_ys_lay.setVisibility(View.GONE);
                    List<NaviInfo> routeList = MapUtils.queryAllPresetNaviInfo(context);
                    if (routeList != null
                            && routeList.size() > 0) {
                        NaviInfo naviInfo = routeList.get(0);
                        delPresetDestInfo(naviInfo.getSid());
                    }
                    iv_route.setText(getString(R.string.main_have_num));
                    update_txt.setVisibility(View.GONE);
                    //刷新导航卡片
                    integrationCore.getCardController().reloadNaviCard();
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    /**
     * 搜索弹窗
     */
    public void showDeclare() {
        initData(0);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.search_layout, null);
        seach_where = (EditText) v.findViewById(R.id.seach_where);
        seach_where.setHint("你想去哪儿？");
        search_btn = (ImageView) v.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);
        ms_text = (TextView) v.findViewById(R.id.ms_text);

        //常用地址列表-----------
        collect_view = (RecyclerView) v.findViewById(R.id.collect_view);
        CollectAdapter collectAdapter = new CollectAdapter(context, dataList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);//设置水平滑动
        collect_view.setLayoutManager(linearLayoutManager);
        collect_view.setAdapter(collectAdapter);

        //搜索历史列表-----------
        initData(1);
        history_view = (RecyclerView) v.findViewById(R.id.history_view);
        HistoryAdapter historyAdapter = new HistoryAdapter(context, dataList_h);
        LinearLayoutManager linearLayoutManager_h = new LinearLayoutManager(context);
        linearLayoutManager_h.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        history_view.setLayoutManager(linearLayoutManager_h);

        //添加分割线
        history_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        history_view.setAdapter(historyAdapter);

        //poi列表--------.
        poi_view = (RecyclerView) v.findViewById(R.id.poi_view);
        poiAdapter = new PoiAdapter(context, allAddr);
        LinearLayoutManager linearLayoutManager_p = new LinearLayoutManager(context);
        linearLayoutManager_p.setOrientation(LinearLayoutManager.VERTICAL);//设置竖直滑动
        poi_view.setLayoutManager(linearLayoutManager_p);
        //添加分割线
        poi_view.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        poi_view.setAdapter(poiAdapter);

        dialog = new AlertDialog.Builder(context)
                .setView(v)
                .setCancelable(false).create();
        dialog.show();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (340 * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
        params.height = (540 * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ImageView close_btn = (ImageView) v.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    //初始化数据
    private void initData(int type) {
        dataList = new ArrayList<Adresss>();
        dataList_h = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Adresss adresss = new Adresss();
            if (type == 1) {
                QueryBuildInfo queryBuildInfo = new QueryBuildInfo();
                queryBuildInfo.setDistinct(true);
                adresss.setRemark("上海虹桥机场" + i);
                dataList_db = integrationCore.searchDbData(context, NaviSearchHistory.class.getName());
                //类型转换
                for (int j = 0; j < dataList_db.size(); j++) {
                    dataList_h.add((NaviSearchHistory) dataList_db.get(j));
                }
            } else {
                adresss.setRemark("公司" + i);
                dataList.add(adresss);
            }


        }
    }
    //添加布局控件点击效果
//    private View.OnTouchListener voiceButtonListener = new View.OnTouchListener() {
//
//        @SuppressLint("ResourceAsColor")
//        public boolean onTouch(View arg0, MotionEvent event) {
//            // TODO Auto-generated method stub
//            int action = event.getAction();
//            if (action == MotionEvent.ACTION_DOWN) { // 按下
//                notice_img.setBackgroundResource(R.drawable.home_icon_notice_press);
//                notice_txt.setTextColor(context.getResources().getColor(R.color.click_bg));
//            } else if (action == MotionEvent.ACTION_UP) { // 松开
//                notice_img.setBackgroundResource(R.drawable.home_icon_notice);
//                notice_txt.setTextColor(context.getResources().getColor(R.color.no_click_bg));
//            }
//            return true;
//
//        }
//    };
//    private View.OnTouchListener oilButtonListener = new View.OnTouchListener() {
//
//        @SuppressLint("ResourceAsColor")
//        public boolean onTouch(View arg0, MotionEvent event) {
//            // TODO Auto-generated method stub
//            int action = event.getAction();
//            if (action == MotionEvent.ACTION_DOWN) { // 按下
//                oil_img.setBackgroundResource(R.drawable.home_icon_oil_press);
//                oil_txt.setTextColor(context.getResources().getColor(R.color.click_bg));
//            } else if (action == MotionEvent.ACTION_UP) { // 松开
//                oil_img.setBackgroundResource(R.drawable.home_icon_oil);
//                oil_txt.setTextColor(context.getResources().getColor(R.color.no_click_bg));
//
//               gotoNavigationActivity(true);
//            }
//            return true;
//
//        }
//    };


//    private View.OnTouchListener navButtonListener = new View.OnTouchListener() {
//
//        @SuppressLint("ResourceAsColor")
//        public boolean onTouch(View arg0, MotionEvent event) {
//            // TODO Auto-generated method stub
//            int action = event.getAction();
//            if (action == MotionEvent.ACTION_DOWN) { // 按下
//                nav_img.setBackgroundResource(R.drawable.home_icon_walk_press);
//                nav_txt.setTextColor(context.getResources().getColor(R.color.click_bg));
//            } else if (action == MotionEvent.ACTION_UP) { // 松开
//                nav_img.setBackgroundResource(R.drawable.home_icon_walk);
//                nav_txt.setTextColor(context.getResources().getColor(R.color.no_click_bg));
//                //测试用
//                if(lat_db==0.0&&lon_db==0.0){
//                    ToastUtil.show(getString(R.string.main_no_search_car),context);
//                }else {
//                    end_point = new LatLng(lat_db + 1, lon_db);
//                    integrationCore.findCar(context, point, end_point);
//                }
//            }
//            return true;
//
//        }
//    };

    // 设置个性化地图config文件路径
//    private void setMapCustomFile(Context context, String PATH) {
//        FileOutputStream out = null;
//        InputStream inputStream = null;
//        String moduleName = null;
//        try {
//            inputStream = context.getAssets()
//                    .open("customConfigdir/" + PATH);
//            byte[] b = new byte[inputStream.available()];
//            inputStream.read(b);
//
//            moduleName = context.getFilesDir().getAbsolutePath();
//            File f = new File(moduleName + "/" + PATH);
//            if (f.exists()) {
//                f.delete();
//            }
//            f.createNewFile();
//            out = new FileOutputStream(f);
//            out.write(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        MapView.setCustomMapStylePath(moduleName + "/" + PATH);
//
//    }

    private LinearLayout ll_head;// head
    //    LinearLayout map_lay;// middle
    private LinearLayout ll_bottom;// bottom
    private FrameLayout rf_map;
    private LinearLayout ll_back;
    private ScrollView scrollview;

    private GestureDetector mGestureDetector;
    private void initView_(View view) {
        ll_head = view.findViewById(R.id.ll_head);
        ll_bottom = view.findViewById(R.id.ll_bottom);
        rf_map = view.findViewById(R.id.rf_map);
        ll_back = view.findViewById(R.id.ll_back);
        ll_back.setVisibility(View.GONE);
        scrollview=view.findViewById(R.id.scrollview);
        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMap();
            }
        });
        if (mMapView == null) {
            mMapView = view.findViewById(R.id.bmapView);
        }
        ll_head.setOnClickListener(this);// 防止事件传递到地图
    }

    private boolean isAnim = false;

    private void doResizeMapAnimal(final View v, final int from, final int to) {
        isAnim = true;
        ValueAnimator va = ValueAnimator.ofInt(from, to);
        va.setDuration(200L);
        va.setInterpolator(new DecelerateInterpolator());
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator vva) {
                ViewGroup.LayoutParams lp = v.getLayoutParams();
                lp.height = (int) vva.getAnimatedValue();
                v.setLayoutParams(lp);
            }
        });
        va.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                if (from > to) {
                    v.setVisibility(View.GONE);

                    ll_back.setVisibility(View.VISIBLE);

                    FrameLayout.LayoutParams car_params = (FrameLayout.LayoutParams) car_lay.getLayoutParams();
                    car_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int dp80 = (int) getResources().getDimension(R.dimen.dp80);
                    car_params.height = dp80;
                    car_params.gravity = Gravity.BOTTOM;
                    int dp32 = (int) getResources().getDimension(R.dimen.dp32);
                    car_params.bottomMargin = dp32;
                    car_lay.setLayoutParams(car_params);
                    bleContainer.setVisibility(View.GONE);
                } else {
                    ll_bottom.setVisibility(View.VISIBLE);
                    ll_back.setVisibility(View.GONE);

                    FrameLayout.LayoutParams car_params = (FrameLayout.LayoutParams) car_lay.getLayoutParams();
                    car_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    int dp80 = (int) getResources().getDimension(R.dimen.dp80);
                    car_params.height = dp80;
                    car_params.gravity = Gravity.TOP;
                    car_lay.setLayoutParams(car_params);
                    bleContainer.setVisibility(View.VISIBLE);
                }

                isAnim = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                isAnim = false;
            }
        });
        va.start();
    }

    private void switchMap() {
        Log.i(TAG, "switchMap: ");
        if(isAnim) {
            return;
        }

        if (ll_bottom.getVisibility() == View.VISIBLE || ll_head.getVisibility() == View.VISIBLE) {
            ll_bottom.setVisibility(View.GONE);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            int dp130 = (int) getResources().getDimension(R.dimen.dp130);
            doResizeMapAnimal(ll_head, dp130,0);

            mUiSettings.setAllGesturesEnabled(true);
        } else {
            int dp130 = (int) getResources().getDimension(R.dimen.dp130);
            doResizeMapAnimal(ll_head, 0, dp130);
            ll_head.setVisibility(View.VISIBLE);

            mUiSettings.setAllGesturesEnabled(false);
            scrollview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
    }

    int getScreenHeight() {
        //2、通过Resources获取
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heigth = dm.heightPixels;
        int width = dm.widthPixels;
        return heigth;
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBleKeyTypeUpdate(String update) {
        if ("BleKeyTypeUpdate".equals(update)) {
            Log.d(TAG, "onBleKeyTypeUpdate:" + update);
            showOrHideKeyTipByType(true);
        }
    }

    private void showOrHideKeyTipByType(boolean show) {
        Log.d(TAG, "showOrHideKeyTipByType: show:" + show);
        if (show) {
            bleContainer.setVisibility(View.VISIBLE);
        } else {
            bleContainer.setVisibility(View.GONE);
        }
        if (null == ArielApplication.getmUserInfo()) {
            Log.d(TAG, "showKeyTipByType not login");
            //没有临时钥匙,或者临时钥匙过期
            //显示  我已上车,立刻用车
            if (null == BleKeyHelper.getBleCarKey()) {
                Log.d(TAG, "showOrHideKeyTipByType: bleCarKey null");
                bleTipTxt.setText("我已上车,立刻用车");
            } else {
                Log.d(TAG, "showOrHideKeyTipByType:  bleCarKey type:" + BleKeyHelper.getBleCarKey().keyType);
                if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP) {
                    bleTipTxt.setText("快速注册,获取永久钥匙");
                }
            }
        } else {
            Log.d(TAG, "showKeyTipByType has login");
            //没有临时钥匙,或者临时钥匙过期
            // 显示  我已上车,立刻用车
            if (null == BleKeyHelper.getBleCarKey()) {
                Log.d(TAG, "showOrHideKeyTipByType: bleCarKey null");
                bleTipTxt.setText("我已上车,立刻用车");
            } else {
                Log.d(TAG, "showOrHideKeyTipByType:  bleCarKey type:" + BleKeyHelper.getBleCarKey().keyType);
                if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_TEMP) {
                    bleTipTxt.setText("快速注册,获取永久钥匙");
                } else if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_FOREVER) {
                    bleTipTxt.setText("升级手机钥匙,获取更多权限");
                } else if (BleKeyHelper.getBleCarKey().keyType == BleCarKey.KEY_TYPE_OWNER) {
                    bleContainer.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void initUserDate() {
        if (ArielApplication.getmUserInfo() != null && !TextUtils.isEmpty(ArielApplication.getmUserInfo().getMobilePhone())) {
            car_number.setVisibility(View.VISIBLE);
            _user.setImageResource(R.drawable.user_s);
        } else {
            car_number.setVisibility(View.GONE);
            _user.setImageResource(R.drawable.user_s_default);

        }
    }


    private void initLoadView(){
        car_lock_loading = mainview.findViewById(R.id.car_lock_loading);
        hot_car_loading = mainview.findViewById(R.id.hot_car_loading);
        trunk_loading = mainview.findViewById(R.id.trunk_loading);
        warmth_loading = mainview.findViewById(R.id.warmth_loading);
        cool_loading = mainview.findViewById(R.id.cool_loading);
        window_loading = mainview.findViewById(R.id.window_loading);
    }
//    private TextView car_lock_txt;
//    private TextView hot_car_txt;
//    private TextView trunk_txt;
//    private TextView warmth_txt;
//    private TextView cool_txt;
//    private TextView window_txt;
//    private LVCircularZoom car_lock_loading;
//    private LVCircularZoom hot_car_loading;
//    private LVCircularZoom trunk_loading;
//    private LVCircularZoom warmth_loading;
//    private LVCircularZoom cool_loading;
//    private LVCircularZoom window_loading;


    private void showLoadView(final View view){
        car_lock_loading.post(new Runnable() {
            @Override
            public void run() {
                switch (view.getId()){
                    case R.id.car_lock_btn:
                        car_lock_loading.startAnim();
                        car_lock_loading.setVisibility(View.VISIBLE);
                        car_lock_txt.setVisibility(View.GONE);
                        break;
                    case R.id.hot_car:
                        hot_car_loading.startAnim();
                        hot_car_loading.setVisibility(View.VISIBLE);
                        hot_car_txt.setVisibility(View.GONE);
                        break;

                    case R.id.trunk:
                        trunk_loading.startAnim();
                        trunk_loading.setVisibility(View.VISIBLE);
                        trunk_txt.setVisibility(View.GONE);
                        break;

                    case R.id.warmth:
                        warmth_loading.startAnim();
                        warmth_loading.setVisibility(View.VISIBLE);
                        warmth_txt.setVisibility(View.GONE);
                        break;

                    case R.id.cool:
                        cool_loading.startAnim();
                        cool_loading.setVisibility(View.VISIBLE);
                        cool_txt.setVisibility(View.GONE);
                        break;

                    case R.id.window:
                        window_loading.startAnim();
                        window_loading.setVisibility(View.VISIBLE);
                        window_txt.setVisibility(View.GONE);
                        break;

                }
            }
        });


    }

    private void hideLoadView(final View view){
        VehcleControlManager.getInstance(getActivity()).setRemoting(false);
        car_lock_loading.post(new Runnable() {
            @Override
            public void run() {
                switch (view.getId()){
                    case R.id.car_lock_btn:
                        car_lock_loading.stopAnim();
                        car_lock_loading.setVisibility(View.GONE);
                        car_lock_txt.setVisibility(View.VISIBLE);
                        break;

                    case R.id.hot_car:
                        hot_car_loading.stopAnim();
                        hot_car_loading.setVisibility(View.GONE);
                        hot_car_txt.setVisibility(View.VISIBLE);
                        break;

                    case R.id.trunk:
                        trunk_loading.stopAnim();
                        trunk_loading.setVisibility(View.GONE);
                        trunk_txt.setVisibility(View.VISIBLE);
                        break;

                    case R.id.warmth:
                        warmth_loading.stopAnim();
                        warmth_loading.setVisibility(View.GONE);
                        warmth_txt.setVisibility(View.VISIBLE);
                        break;

                    case R.id.cool:
                        cool_loading.stopAnim();
                        cool_loading.setVisibility(View.GONE);
                        cool_txt.setVisibility(View.VISIBLE);
                        break;

                    case R.id.window:
                        window_loading.stopAnim();
                        window_loading.setVisibility(View.GONE);
                        window_txt.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });


    }



    private void queryVehicleStatus(){
        if (NetUtil.isNetworkConnected(getActivity()) && ArielApplication.getmUserInfo() != null &&! TextUtils.isEmpty(TspManager.getmPdsn())
                && !ArielApplication.isRfcomConnect()){
            VehcleControlManager.getInstance(getActivity()).getVehicleStatus(new RemoteResponseListener() {
                @Override
                public void onSendSuccess(CarCtrlRespBean carCtrlRespBean, RestResponse restResponse) {
                    showAllLoadView(true);

                }

                @Override
                public void onSendFailure(RestError restError) {
                    showAllLoadView(false);
                }

                @Override
                public void onCmdResult(VhlCtlResult vhlCtlResult, RestResponse restResponse) {
                    showAllLoadView(false);
                    if (null!=vhlCtlResult){
                        //更新门锁状态0-closed,1-open
                        doorStatusChange(TextUtils.equals("0",vhlCtlResult.getDoorLockState()));
//                    vhlCtlResult.geteachMileage
                        //判断是当前是热车还是清凉还是温暖acState0-closed;engineStatus引擎状态 0-OFF,1-ON
                        if (TextUtils.equals("0",vhlCtlResult.getEngineStatus())){
                            acStatusChange(0);
                        }else if (TextUtils.equals("1",vhlCtlResult.getEngineStatus())
                                && TextUtils.equals("0",vhlCtlResult.getAcState())){
                            acStatusChange(1);
                        }else if (TextUtils.equals("1",vhlCtlResult.getEngineStatus())
                                && TextUtils.equals("1",vhlCtlResult.getAcState())){
                            if (TextUtils.equals("16",vhlCtlResult.getAcTemperature())){
                                acStatusChange(2);
                            }else if(TextUtils.equals("24",vhlCtlResult.getAcTemperature())){
                                acStatusChange(3);

                            }

                        }
                    }

                }

                @Override
                public void onCmdTimeout() {
                    showAllLoadView(false);

                }
            },10);
        }

    }




    /**
     *
     * @param isLock true上锁，false解锁
     */
    private void doorStatusChange(boolean isLock){
        carDoor=isLock?MyConstants.LOCK_CAR:MyConstants.OPEN_CAR;
        if (carDoor != MyConstants.LOCK_CAR) {
            car_lock_txt.setText(getString(R.string.main_door_unlocked));
        } else {
            car_lock_txt.setText(getString(R.string.main_door_locked));
        }
    }

    /**
     *
     * @param isHot true 热车，false熄火
     */
    private void hotCarStatusChange(boolean isHot){
        hotCar=isHot?MyConstants.HOT_CAR:MyConstants.NO_HOT_CAR;
        if (hotCar != MyConstants.NO_HOT_CAR) {
            hot_car_txt.setText(getString(R.string.main_is_hot_car));
        } else {
            hot_car_txt.setText(getString(R.string.main_hot_car));

        }
    }

    /**
     *
     * @param isWarm true 温暖，false熄火
     */
    private void warmStatusChange(boolean isWarm){
        carWarmth=isWarm?MyConstants.OPEN_WARMTH:MyConstants.CLOSE_WARMTH;
        if (carWarmth != MyConstants.CLOSE_WARMTH) {
            warmth_txt.setText(getString(R.string.main_is_warm));
        } else {
            warmth_txt.setText(getString(R.string.main_warmth));

        }
    }


    /**
     *
     * @param isCool true 清凉，false熄火
     */
    private void coolStatusChange(boolean isCool){
        carCool=isCool?MyConstants.OPEN_COOL:MyConstants.CLOSE_COOL;
        if (carCool != MyConstants.CLOSE_COOL) {
            cool_txt.setText(getString(R.string.main_is_cool));
        } else {
            cool_txt.setText(getString(R.string.main_cool));
        }
    }


    /**
     *
     * @param type 0:引擎没有启动，1:热车，2:清凉，3:温暖
     */

    private void acStatusChange(int type){
        switch (type){
            case 0:
                hotCarStatusChange(false);
                warmStatusChange(false);
                coolStatusChange(false);
                break;
            case 1:
                hotCarStatusChange(true);
                break;
            case 2:
                coolStatusChange(true);
                break;
            case 3:
                warmStatusChange(true);
                break;
                default:
                    break;
        }

    }

    private void showAllLoadView(final boolean isShow){
        hot_car.post(new Runnable() {
            @Override
            public void run() {
                if (isShow){
                    showLoadView(hot_car);
                    showLoadView(cool);
                    showLoadView(warmth);
                    showLoadView(car_lock_btn);
                    showLoadView(window);
                    showLoadView(trunk);
                }else{
                    hideLoadView(hot_car);
                    hideLoadView(cool);
                    hideLoadView(warmth);
                    hideLoadView(car_lock_btn);
                    hideLoadView(window);
                    hideLoadView(trunk);
                }
            }
        });


    }


}

