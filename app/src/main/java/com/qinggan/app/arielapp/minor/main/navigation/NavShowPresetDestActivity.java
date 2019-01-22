package com.qinggan.app.arielapp.minor.main.navigation;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.UIControlBaseActivity;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.wechat.MessageEvent;
import com.qinggan.app.arielapp.minor.phone.view.MultiDirectionSlidingDrawer;
import com.qinggan.app.arielapp.minor.wechat.WeChatSendMsgActivity;
import com.qinggan.app.arielapp.minor.wechat.inter.SlidingDrawerCallBack;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.virtualclick.utils.ActionCode;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.voiceapi.nluresult.NluResultManager;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;

/**
 * wechat send activity
 */

public class NavShowPresetDestActivity extends UIControlBaseActivity implements View.OnClickListener, SlidingDrawerCallBack {
    private final String TAG = NavShowPresetDestActivity.class.getSimpleName();
    private MultiDirectionSlidingDrawer mDrawer;
    private static final int MSG_PLAN_ROUTE = 1;
    private static final int MSG_START_RECORDING = 3;
    private static final int MSG_CANCLE_PRESET = 4;
    private static final int MSG_TIME_OUT = 5;
    private String from = "起点";
    private LatLng presetDest;
    private Double mCarLat;
    private Double mCarLng;
    private LatLng mCarOrigin =  new LatLng(32.058784, 118.757749);
    private IntegrationCore integrationCore;
    private Button mBtnConfirm;
    private Button mBtnCancle;
    private TextView mTvDestName;
    private TextView mTvDestDistance;
    private TextView mTvDestTime;
    private boolean isOnResume = false;
    private static final int PRESET_DEST_TIME_OUT = 10 * 1000;
    private boolean isOutToNavi = false;
    private HotwordManager mHotwordManager;
    private boolean isOnstop;
    private String MODULE_NAME = "nav_preset";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    private int[] voiceCmd = new int[]{R.string.navi_confirm, R.string.navi_yes, R.string.navi_ok,
                                         R.string.nav, R.string.navi_ensure, R.string.navi_cancle};

    private String[] navUc = new String[]{ConstantNavUc.NAV_CONFIRM, ConstantNavUc.NAV_CONFIRM, ConstantNavUc.NAV_CONFIRM,
            ConstantNavUc.NAV_CONFIRM, ConstantNavUc.NAV_CONFIRM, ConstantNavUc.NAV_CANCLE_PRESET};

    private int[] numbers =  new int[]{R.string.navi_first, R.string.navi_second, R.string.navi_third};

    private String[] numberUcs = new String[]{ConstantNavUc.NAV_FIRST, ConstantNavUc.NAV_SECOND, ConstantNavUc.NAV_THIRD};

    private ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> mWakeUpElements = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        isOnstop = false;
        addDismissWakeupItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        if(isOutToNavi) {
            NavShowPresetDestActivity.this.finish();
            return;
        }

        addNaviConfirmUIControl();
        mHandler.removeMessages(MSG_TIME_OUT);
        mHandler.sendEmptyMessageDelayed(MSG_TIME_OUT, PRESET_DEST_TIME_OUT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnstop = true;
        if (null != mHotwordManager) {
            Log.i(TAG, "onStop clearElementUCWords");
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.i(TAG, " onSelectOtherOC action = " + action);
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
            case ConstantNavUc.NAV_START_NAV:
                //开始导航
                integrationCore.startNav();
                break;
            case ConstantNavUc.NAV_CONFIRM:
                 mHandler.sendEmptyMessage(MSG_PLAN_ROUTE);
                 break;
            case ConstantNavUc.NAV_CANCLE_PRESET:
                 mHandler.sendEmptyMessage(MSG_CANCLE_PRESET);
                 break;
            case ConstantNavUc.NAV_FIRST:
                if (isOutToNavi) {
                    integrationCore.onNavRouteSelect(0);
                }
                break;
            case ConstantNavUc.NAV_SECOND:
                if (isOutToNavi) {
                    integrationCore.onNavRouteSelect(1);
                }
                break;
            case ConstantNavUc.NAV_THIRD:
                if (isOutToNavi) {
                    integrationCore.onNavRouteSelect(2);
                }
                break;
        }

    }

    protected void initView() {
        mDrawer = (MultiDirectionSlidingDrawer) findViewById(R.id.drawer);
        mDrawer.animateOpen();
        mDrawer.setCallBack(this);

        mBtnConfirm = (Button) findViewById(R.id.btn_navi_confirm);
        mBtnCancle = (Button) findViewById(R.id.btn_navi_cancle);
        mTvDestName = (TextView) findViewById(R.id.tv_dest_name);
        mBtnConfirm.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);

        mTvDestDistance = (TextView) findViewById(R.id.tv_distance);
        mTvDestTime = (TextView) findViewById(R.id.tv_time);

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
        //确认，取消免唤醒词添加
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

        //跳转到百度地图后免唤醒词添加
        com.qinggan.qinglink.bean.UIControlElementItem startNaviElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        startNaviElement1.setWord(getString(R.string.start_nav));
        startNaviElement1.setIdentify(ConstantNavUc.NAV_START_NAV);
        mWakeUpElements.add(startNaviElement1);
    }

    @Override
    protected void initData() {
        queryPresetNavDest();
        integrationCore = IntegrationCore.getIntergrationCore(ArielApplication.getApp());
        initVehiclePositionInfo();

        VoicePolicyManage.getInstance().speak(getString(R.string.navi_dialog_message));
        VoicePolicyManage.getInstance().addTtsStatusListeners(mTtsStateChangeListener);
    }

    private void initVehiclePositionInfo() {
        EventBus.getDefault().register(this);
        integrationCore.getVehicleDetailInfo();//获取车辆信息
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_navi_show_preset;
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "setElement":
                mTvDestDistance.setText(event.getLen());
                mTvDestTime.setText(event.getElapsed_time());
                break;
        }
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (EventBusTSPInfo.BUSSINESS_TYPE_VEHICLE_INFO == event.getBussinessType()) {
            if (event.isSuccess()) {
                if (event.getModule() instanceof VehicleDetailInfo) {
                    //拿到车辆信息
                    VehicleDetailInfo vehicleDetailInfo = (VehicleDetailInfo) event.getModule();
                    if (null != vehicleDetailInfo) {
                        double gd_lon_db = Double.valueOf(vehicleDetailInfo.getData().getLon());
                        double gd_lat_db = Double.valueOf(vehicleDetailInfo.getData().getLat());
                        Log.i("Alan", "获取到的高德车辆位置信息" + gd_lat_db + "=-=" + gd_lon_db);
                        //高德坐标转百度坐标
                        double[] bd_lat_lon = new MapUtils(this).gaoDeToBaidu(gd_lon_db, gd_lat_db);
                        mCarLng = bd_lat_lon[0];
                        mCarLat = bd_lat_lon[1];
                        Log.i("Alan", "转换后的百度车辆位置信息" + mCarLat + "=-=" + mCarLng);
                        mCarOrigin = new LatLng(mCarLat, mCarLng);

                        new MapUtils(this).setElement(mCarOrigin, presetDest);
                    }
                }
            }
        }
    }

    @Override
    public void openCallBack() {
    }

    @Override
    public void closeCallBack() {

    }

    @Override
    public void onClick(View view) {
        final int viewId = view.getId();
        switch (viewId) {
            case R.id.btn_navi_confirm:
                 startPlanRoute();
                 break;
            case R.id.btn_navi_cancle:
                 NavShowPresetDestActivity.this.finish();
                 break;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_PLAN_ROUTE:
                    startPlanRoute();
                    break;
                case MSG_START_RECORDING:
                    VoicePolicyManage.getInstance().removeTtsStatusListener(mTtsStateChangeListener);
                    VoicePolicyManage.getInstance().record(true);
                    break;
                case MSG_CANCLE_PRESET:
                    NavShowPresetDestActivity.this.finish();
                    break;
                case MSG_TIME_OUT:
                     Log.i(TAG, "isOnResume = " + isOnResume);
                     if(isOnResume){
                         startPlanRoute();
                     }
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
            Log.i(TAG, "speak done");
            mHandler.sendEmptyMessage(MSG_START_RECORDING);
        }
    };

    private void queryPresetNavDest() {
        List<NaviInfo> routeList= MapUtils.queryAllPresetNaviInfo(this);
        if(routeList != null
                && routeList.size() > 0){
            String lat = routeList.get(0).getPoiLat();
            String lng = routeList.get(0).getPoiLno();
            String name = routeList.get(0).getDisplayName();

            from = getString(R.string.origin);
            presetDest = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            if(mTvDestName != null){
                mTvDestName.setText(name);
            }
        }
    }

    private void addNaviConfirmUIControl() {
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

        UIControlElementItem cancleNavElement = new UIControlElementItem();
        cancleNavElement.addWord(getString(R.string.navi_cancle));
        cancleNavElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_CANCLE_PRESET);
        mUIControlElements.add(cancleNavElement);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    public void startPlanRoute() {
        Log.i(TAG, " NAVI startPlanRoute ");
        isOutToNavi = true;
        VoicePolicyManage.getInstance().interrupt();
        integrationCore.planRoute(this, from, presetDest, mCarOrigin);

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
    }

}
