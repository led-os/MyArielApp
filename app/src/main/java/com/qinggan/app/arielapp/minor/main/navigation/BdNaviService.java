package com.qinggan.app.arielapp.minor.main.navigation;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.NaviInterface;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.integration.BaiduNaviCMD;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.bean.BaiduUiControlEvent;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.utils.AppManager;
import com.qinggan.app.virtualclick.utils.AppNameConstants;
import com.qinggan.app.virtualclick.utils.VirtualControlManager;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.mobile.tsp.models.device.VehicleDetailInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class BdNaviService extends AccessibilityService implements PhoneStateManager.PhoneStateChangeListener,
        LastKilometreDialog.confirmListener {


    private String TAG = BdNaviService.class.getSimpleName();

    private String baiduNaviAct = "com.baidu.baidumaps.MapsActivity";


    private int currVoiceCmd;
    private final int VOICE_NOT_EFFECTIVE = 0;//不响应语音
    private final int VOICE_AVOIDTRAFFICJAM = 1;//避开拥堵　
    private final int VOICE_AVOIDEXPRESSWAY = 2;//不走高速
    private final int VOICE_FIRSTEXPRESSWAY = 3;//高速优先
    private final int VOICE_QUITNAVI = 6;//退出导航
    private final int VOICE_LESS_TIME = 7;//时间优先
    private final int VOICE_LESS_FEE = 8;//少收费
    private final int VOICE_2d = 9;
    private final int VOICE_3d = 10;


    private boolean needStarWalk;


    //目的地、人的位置、车的位置
    private LatLng dest;
    private LatLng origin;
    private LatLng carOrigin;


    private int tagetExist, calculateTaget, wtaget;

    private NaviInterface mNaviCMD;

    private LastKilometreDialog dialog;


    private String channelId = "ModeChange";

    private long lasttime;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //this code for wechat virtual click
        VirtualControlManager.getInstance().dispatchEvent(event, getRootInActiveWindow(), this);
        if (event == null || event.getPackageName() == null ||
                event.getPackageName() != null && !event.getPackageName().equals(AppNameConstants.BAIDU_APP_PACKAGE_NAME)) {
            return;
        }
        int eventType = event.getEventType();
        findTaget(event);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;

            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (event.getText().equals(getString(R.string.start_nav))) {
                    needStarWalk = false;
                }
                break;

            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                if (lasttime == 0) {
                    lasttime = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - lasttime < 500) {
                    lasttime = System.currentTimeMillis();
                    return;
                }
                if (needStarWalk && currVoiceCmd == VOICE_NOT_EFFECTIVE) {
                    gotoWalk();
                } else if (currVoiceCmd != VOICE_NOT_EFFECTIVE && inDrivingNavi()) {
                    switch (currVoiceCmd) {
                        case VOICE_LESS_TIME:
                            openPrefSetting(event.getSource());
                            clickBaiduUISetting(event.getSource(), 1);
                            break;
                        case VOICE_LESS_FEE:
                            openPrefSetting(event.getSource());
                            clickBaiduUISetting(event.getSource(), 2);
                            break;
                        case VOICE_AVOIDTRAFFICJAM:
                            openPrefSetting(event.getSource());
                            clickTrafficJam(event.getSource());
                            break;
                        case VOICE_AVOIDEXPRESSWAY:
                            openPrefSetting(event.getSource());
                            clickAvoidExpressWay(event.getSource());
                            break;
                        case VOICE_FIRSTEXPRESSWAY:
                            openPrefSetting(event.getSource());
                            clickFirstExpressWay(event.getSource());
                            break;
                        case VOICE_2d:
                        case VOICE_3d:
                            open3D(event.getSource());
                            break;
                        case VOICE_QUITNAVI:
                            quitBaidu();
                            break;
                    }

                }

                break;

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                if (needStarWalk && currVoiceCmd == VOICE_NOT_EFFECTIVE) {
                    gotoWalk();
                }
                break;

        }
    }


    /**
     * 点击时间优先、少收费
     * 重置currVoiceCmd
     *
     * @param rootNode
     */
    private void clickBaiduUISetting(AccessibilityNodeInfo rootNode, int item) {
        String lastStep_1 = "com.baidu.BaiduMap:id/nsdk_route_sort_gv";
        List<AccessibilityNodeInfo> lastNode_1 = rootNode.findAccessibilityNodeInfosByViewId(lastStep_1);
        if (lastNode_1 != null && lastNode_1.size() != 0) {
            AccessibilityNodeInfo listNode = lastNode_1.get(0);
            int childCont = listNode.getChildCount();
            Log.d(TAG, "clickBaiduUISetting childCont:" + childCont);
            if (childCont != 0 && childCont == 6) {
                listNode.getChild(item).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            currVoiceCmd = VOICE_NOT_EFFECTIVE;
        }
        rootNode.recycle();
    }

    /**
     * 点击高速优先按钮
     * 重置currVoiceCmd
     *
     * @param rootNode
     */
    private void clickFirstExpressWay(AccessibilityNodeInfo rootNode) {
        String lastStep_1 = "com.baidu.BaiduMap:id/nsdk_route_sort_gv";
        List<AccessibilityNodeInfo> lastNode_1 = rootNode.findAccessibilityNodeInfosByViewId(lastStep_1);
        if (lastNode_1 != null && lastNode_1.size() != 0) {
            AccessibilityNodeInfo listNode = lastNode_1.get(0);
            int childCont = listNode.getChildCount();
            Log.d(TAG, "clickFirstExpressWay childCont:" + childCont);
            if (childCont != 0 && childCont == 6) {
                listNode.getChild(5).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            currVoiceCmd = VOICE_NOT_EFFECTIVE;
        }
        rootNode.recycle();
    }

    /**
     * 点击不走高速
     * 重置currVoiceCmd
     *
     * @param rootNode
     */
    private void clickAvoidExpressWay(AccessibilityNodeInfo rootNode) {
        String lastStep_1 = "com.baidu.BaiduMap:id/nsdk_route_sort_gv";
        List<AccessibilityNodeInfo> lastNode_1 = rootNode.findAccessibilityNodeInfosByViewId(lastStep_1);
        if (lastNode_1 != null && lastNode_1.size() != 0) {
            AccessibilityNodeInfo listNode = lastNode_1.get(0);
            int childCont = listNode.getChildCount();
            Log.d(TAG, "clickAvoidExpressWay childCont:" + childCont);
            if (childCont != 0 && childCont == 6) {
                listNode.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            currVoiceCmd = VOICE_NOT_EFFECTIVE;
        }
        rootNode.recycle();
    }

    /**
     * 点击躲避拥堵
     * 重置currVoiceCmd
     *
     * @param rootNode
     */
    private void clickTrafficJam(AccessibilityNodeInfo rootNode) {
        String lastStep_1 = "com.baidu.BaiduMap:id/nsdk_route_sort_gv";
        List<AccessibilityNodeInfo> lastNode_1 = rootNode.findAccessibilityNodeInfosByViewId(lastStep_1);
        if (lastNode_1 != null && lastNode_1.size() != 0) {
            AccessibilityNodeInfo listNode = lastNode_1.get(0);
            int childCont = listNode.getChildCount();
            Log.d(TAG, "clickTrafficJam childCont:" + childCont);
            if (childCont != 0 && childCont == 6) {
                listNode.getChild(3).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            currVoiceCmd = VOICE_NOT_EFFECTIVE;
        }
        rootNode.recycle();
    }

    /**
     * 全程预览、退出全程预览
     */
    private void fullView(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            return;
        }
        String oneStep_1 = "com.baidu.BaiduMap:id/bnav_rg_cp_fullview_mode_btn";//全览
        List<AccessibilityNodeInfo> oneNode_1 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_1);
        if (oneNode_1 != null && oneNode_1.size() > 0) {
            AccessibilityNodeInfo info = oneNode_1.get(0);
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        //TODO 添加类型
        Log.e(TAG, "fullView: " + rootNode.toString());
        rootNode.recycle();
    }


    /**
     * 退出百度地图回到铃机界面
     */
    private void quitBaidu() {
        if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING || BdMapUIcontrol.walkNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        } else {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
        BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
        BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
        AppManager.getAppManager().returnToActivity(MainActivity.class);
        currVoiceCmd = VOICE_NOT_EFFECTIVE;

    }

    /**
     * 3d
     *
     * @return
     */
    private void open3D(AccessibilityNodeInfo rootNode) {
        //第一步点击满足条件
        String oneStep_1 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_open_close_ly";
        String oneStep_2 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_quit_ly";
        //第二步满足条件
        String twoStep_1 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_settings";
        String twoStep_２ = "com.baidu.BaiduMap:id/bnav_rg_toolbox_car3d_mode";//　3d

        List<AccessibilityNodeInfo> oneNode_1 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_1);
        List<AccessibilityNodeInfo> oneNode_2 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_2);


        List<AccessibilityNodeInfo> twoNode_1 = rootNode.findAccessibilityNodeInfosByViewId(twoStep_1);
        List<AccessibilityNodeInfo> twoNode_2 = rootNode.findAccessibilityNodeInfosByViewId(twoStep_２);

        if (oneNode_1 != null && oneNode_1.size() != 0 && oneNode_2 != null && oneNode_2.size() != 0) {
            oneNode_1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else if (twoNode_1 != null && twoNode_1.size() != 0 && twoNode_2 != null && twoNode_2.size() != 0) {
            twoNode_2.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        currVoiceCmd = VOICE_NOT_EFFECTIVE;
        rootNode.recycle();
    }

    /**
     * 打开路线偏好
     * 重置currVoiceCmd
     *
     * @return
     */
    private void openPrefSetting(AccessibilityNodeInfo rootNode) {
        //第一步点击满足条件
        String oneStep_1 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_open_close_ly";
        String oneStep_2 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_quit_ly";
        //第二步满足条件
        String twoStep_1 = "com.baidu.BaiduMap:id/bnav_rg_toolbox_settings";
        String twoStep_２ = "com.baidu.BaiduMap:id/bnav_rg_toolbox_route_sort";

        List<AccessibilityNodeInfo> oneNode_1 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_1);
        List<AccessibilityNodeInfo> oneNode_2 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_2);


        List<AccessibilityNodeInfo> twoNode_1 = rootNode.findAccessibilityNodeInfosByViewId(twoStep_1);
        List<AccessibilityNodeInfo> twoNode_2 = rootNode.findAccessibilityNodeInfosByViewId(twoStep_２);

        if (oneNode_1 != null && oneNode_1.size() != 0 && oneNode_2 != null && oneNode_2.size() != 0) {
            oneNode_1.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else if (twoNode_1 != null && twoNode_1.size() != 0 && twoNode_2 != null && twoNode_2.size() != 0) {
            twoNode_2.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        rootNode.recycle();
    }

    /**
     * 先退出驾车导航，再发起步行导航
     */
    private void gotoWalk() {
        if (inDrivingNavi()) {
            quitDringNavi();
        } else if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL) {
            LatLng latLng = carOrigin == null ? origin : carOrigin;
            if (needStarWalk && calculateTaget > 2) {
                showNotification();
                mNaviCMD.startWalkNavi(ArielApplication.getApp(), latLng, dest);
            }
        }
    }


    @Override
    public void onInterrupt() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        VirtualControlManager.getInstance().onCancel();
    }


    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "服务已连接");
        addVehicleStateChangeListener();
        mNaviCMD = new BaiduNaviCMD();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, getString(R.string.channelname), importance);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        new MapUtils(this).getLocation();
        super.onServiceConnected();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "服务已断开");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        return super.onUnbind(intent);
    }


    @Override
    public void onPhoneStateChange(PhoneState phoneState) {

        if (phoneState == PhoneState.OUT_CAR_MODE) {
            Log.d(TAG, "PhoneState.OUT_CAR_MODE");
            if ((origin == null && carOrigin == null) || dest == null) {
                Log.d(TAG, "条件不满足");
                return;
            }

            if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_FINISHED ||
                    BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL) {
                Log.d(TAG, "驾车导航已经结束，不再发起步行导航");
                return;
            }

            LatLng latLng = carOrigin == null ? origin : carOrigin;

            double distance = getDistance(latLng.latitude, latLng.longitude,
                    dest.latitude, dest.longitude);
//            dialog = new LastKilometreDialog(ActivityLifecycleListener.currentActivity, this);
//            dialog.show();
            if (distance > 10 || distance < 0.01) {
                Log.d(TAG, "距离超过步行规划距离");
                return;
            }
            needStarWalk = true;
            BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
            currVoiceCmd = VOICE_NOT_EFFECTIVE;
            if (!BdMapUIcontrol.baiduIsForeground) {
                startActivityForPackage();
            }

        }
    }

    /**
     * 通过虚拟点击 响应语音命令
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(BaiduUiControlEvent event) {
        if (event == null)
            return;
        if (event.item.equals(ConstantNavUc.BAIDU_SHOW_NAVI)) {
            startActivityForPackage();
            return;
        }
        voiceCondition();
        if (event.item.equals(ConstantNavUc.BAIDU_UI_TIME_FAST)) {
            currVoiceCmd = VOICE_LESS_TIME;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_LESS_FEE)) {
            currVoiceCmd = VOICE_LESS_FEE;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_AVOID_HIGHT)) {
            currVoiceCmd = VOICE_AVOIDEXPRESSWAY;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_AVOID_TRAFFIC)) {
            currVoiceCmd = VOICE_AVOIDTRAFFICJAM;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_HIGHWAY)) {
            currVoiceCmd = VOICE_FIRSTEXPRESSWAY;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_2D)) {
            currVoiceCmd = VOICE_2d;
        } else if (event.item.equals(ConstantNavUc.BAIDU_UI_3D)) {
            currVoiceCmd = VOICE_3d;
        } else if (event.item.equals(ConstantNavUc.BAIDU_STOP_NAVI)) {
            currVoiceCmd = VOICE_QUITNAVI;
        }
        Log.d(TAG, "currVoiceCmd==" + currVoiceCmd);

    }

    public void startActivityForPackage() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.baidu.BaiduMap");
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 根据界面或者通知栏消息判断
     *
     * @return
     */
    private boolean inDrivingNavi() {
        return (tagetExist > 2 || BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING);
    }


    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case "location":
                BDLocation location = event.getLocation();
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                origin = new LatLng(lat, lng);
                Log.i(TAG, "originlat = " + lat + " originLat = " + lng);
                break;
        }
    }


    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (event.isSuccess() && event.getModule() instanceof VehicleDetailInfo) {
            VehicleDetailInfo vehicleDetailInfo = (VehicleDetailInfo) event.getModule();
            if (vehicleDetailInfo != null && vehicleDetailInfo.getData() != null) {
                String latstr = vehicleDetailInfo.getData().getLat();
                String lonstr = vehicleDetailInfo.getData().getLon();
                if (latstr == null || latstr.equals("") || lonstr == null || lonstr.equals("")) {
                    return;
                }
                double lat = Double.valueOf(vehicleDetailInfo.getData().getLat());
                double lng = Double.valueOf(vehicleDetailInfo.getData().getLon());
                carOrigin = new LatLng(lat, lng);
                Log.i(TAG, "carOriginlat = " + lat + " carOriginlon = " + lng);
                return;
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventLatLng(LatLng latLng) {
        if (latLng != null) {
            dest = latLng;
            needStarWalk = false;
            currVoiceCmd = VOICE_NOT_EFFECTIVE;
            Log.d(TAG, "目的地设置");
        }


    }


    /**
     * 单击地图
     */
    private void clickBaiduMap(AccessibilityNodeInfo rootNode) {
        if (rootNode == null) {
            Log.e("dingqb", "clickBaiduMap: null");
            return;
        }
        String oneStep_1 = "com.baidu.BaiduMap:id/bnav_rg_control_panel";//mapview
        List<AccessibilityNodeInfo> oneNode_1 = rootNode.findAccessibilityNodeInfosByViewId(oneStep_1);
        if (oneNode_1 != null && oneNode_1.size() > 0) {
            AccessibilityNodeInfo info = oneNode_1.get(0);
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        rootNode.recycle();
    }


    /**
     * 执行语音命令的前置条件
     */
    private void voiceCondition() {
        needStarWalk = false;
        BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
        BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
        if (!BdMapUIcontrol.baiduIsForeground) {
            startActivityForPackage();
        }
    }


    @Override
    public void onConfirm() {
        needStarWalk = true;
        BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
        BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
        currVoiceCmd = VOICE_NOT_EFFECTIVE;
        if (!BdMapUIcontrol.baiduIsForeground) {
            startActivityForPackage();
        }
    }

    @Override
    public void onCancle() {
        needStarWalk = false;
        resetAll();
    }


    /**
     * 获取当前位置和目的地的距离,默认单位为千米
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    private double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double radLon1 = rad(lng1);
        double radLon2 = rad(lng2);
        double a = radLat1 - radLat2;
        double b = radLon1 - radLon2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6371.393;
        return s;
    }


    private double rad(double d) {
        return d * Math.PI / 180.0;
    }

    private void addVehicleStateChangeListener() {
        PhoneStateManager.getInstance(ArielApplication.getApp()).addPhoneStateChangeListener(this);
    }


    /**
     * 查找驾车导航界面匹配控件
     *
     * @param rootNode
     */
    private void findNaviTargetWidget(AccessibilityNodeInfo rootNode) {
        String[] targetId = new String[]{

                //这部分包含在“bnav_rg_control_panel 中”　
//                "com.baidu.BaiduMap:id/bnav_rg_cp_fullview_mode_btn",//全览
//                "com.baidu.BaiduMap:id/bnav_rg_cp_map_switch",//小地图
//                "com.baidu.BaiduMap:id/bnav_rg_cp_traffic_panel",//路况
//                "com.baidu.BaiduMap:id/bnav_rg_cp_voice_mode_btn",//导航播报

                "com.baidu.BaiduMap:id/nav_guide_info_layout",//引导信息
                "com.baidu.BaiduMap:id/bnav_rg_enlarge_road_map",//路口放大图
                "com.baidu.BaiduMap:id/bnavi_hw_service_area_top_layout", //收费站
                "com.baidu.BaiduMap:id/bnavi_hw_service_area_bottom_layout",//服务区
                "com.baidu.BaiduMap:id/bnav_rg_road_name_tv",//当前路名
                "com.baidu.BaiduMap:id/bnav_mini_layout_root",//高速方向控制
                "com.baidu.BaiduMap:id/bnav_rg_lane_info_rr",//车道面板
                "com.baidu.BaiduMap:id/bnav_rg_simpleguide_open",//拐弯方向控制


                "com.baidu.BaiduMap:id/bnav_rg_control_panel",//导航控制面板
                "com.baidu.BaiduMap:id/bnav_rg_toolbox_scroollview",//底部退出按钮
                "com.baidu.BaiduMap:id/bnav_rg_cp_anolog_rl",//导航暂停按钮（模拟导航）
                "com.baidu.BaiduMap:id/bnav_rg_cp_anolog_quit_icon",//退出导航按钮（模拟导航）

        };

        tagetExist = 0;
        if (rootNode == null) {
            return;
        }
        for (int i = 0; i < targetId.length; i++) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(targetId[i]);
            if (list != null && list.size() != 0) {
                tagetExist = tagetExist + 1;
            }
        }
        rootNode.recycle();

    }


    /**
     * 查找步行导航界面的匹配控件
     *
     * @param rootNode
     */
    private void findWNaviTargetWidget(AccessibilityNodeInfo rootNode) {
        wtaget = 0;
        if (rootNode == null) {
            return;
        }
        String[] targetId = new String[]{"com.baidu.BaiduMap:id/ar_entry",//实景模式图标
                "com.baidu.BaiduMap:id/walk_calorie_btn　",//卡路里消耗
                "com.baidu.BaiduMap:id/user_npc",//根布局
                "com.baidu.BaiduMap:id/bnav_rg_location_layout", //方向按钮,
                "com.baidu.BaiduMap:id/route_report_btn",//上报
                "com.baidu.BaiduMap:id/ar_end_focus_layout",//终点
                "com.baidu.BaiduMap:id/bnav_rg_bar_layout",//底部（退出、设置）
        };

        for (int i = 0; i < targetId.length; i++) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(targetId[i]);
            if (list != null && list.size() != 0) {
                wtaget = wtaget + 1;
            }
        }


        rootNode.recycle();

    }


    /**
     * 查找首页的匹配控件
     *
     * @param rootNode
     */
    private void findCalculationTargetWidget(AccessibilityNodeInfo rootNode) {
        String[] targetId = new String[]{
                "com.baidu.BaiduMap:id/ll_location_buttons",//整体按钮控制控件
                "com.baidu.BaiduMap:id/searchbox",//搜索
                "com.baidu.BaiduMap:id/duhelper_flysaucer" //百度首页底部（发现周边等）
        };
        calculateTaget = 0;
        if (rootNode == null) {
            return;
        }

        for (int i = 0; i < targetId.length; i++) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(targetId[i]);
            if (list != null && list.size() != 0) {
                calculateTaget = calculateTaget + 1;
            }
        }
        rootNode.recycle();
    }

    /**
     * 根据获取到的控件信息判断百度应用当前界面
     *
     * @param event
     */
    private void findTaget(AccessibilityEvent event) {
        //驾车导航界面
        findNaviTargetWidget(event.getSource());
        if (tagetExist > 2) {
            wtaget = 0;
            BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_ING;
            calculateTaget = 0;
            BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
            return;
        }

        //首页界面
        findCalculationTargetWidget(event.getSource());
        if (calculateTaget > 2) {
            tagetExist = 0;
            wtaget = 0;
            if (BdMapUIcontrol.isVirtualBack && BdMapUIcontrol.walkNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
                BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
            } else if (BdMapUIcontrol.walkNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
                BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHED;
            } else if (BdMapUIcontrol.isVirtualBack && BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
                BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHEDBYVIRTUAL;
            } else if (BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
                BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_FINISHED;
            }
            currVoiceCmd = VOICE_NOT_EFFECTIVE;
            return;
        }
        //步行导航界面
        findWNaviTargetWidget(event.getSource());
        if (wtaget > 3) {
            resetAll();
        }
    }

    /**
     * 通知栏弹出离车通知
     */
    private void showNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.channelname))
                .setContentText(getString(R.string.walkstart))
                .setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true).build();
        manager.notify(1, notification);
    }

    /**
     * 进入步行导航后，需要重置的变量
     */
    private void resetAll() {
        BdMapUIcontrol.walkNaviStatus = BdMapUIcontrol.NAVI_STATUS_ING;
        BdMapUIcontrol.dringNaviStatus = BdMapUIcontrol.NAVI_STATUS_RESET;
        needStarWalk = false;
        dest = null;
        currVoiceCmd = VOICE_NOT_EFFECTIVE;
    }


    /**
     * 如果当前百度在驾车导航模式，连续点击点击两次就退出该模式
     */
    private void quitDringNavi() {
        if (needStarWalk && BdMapUIcontrol.dringNaviStatus == BdMapUIcontrol.NAVI_STATUS_ING) {
            BdMapUIcontrol.isVirtualBack = true;
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        }
    }


}
