package com.qinggan.app.arielapp.minor.main.mui.navitest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;

public class TestNaviMainFragment extends UIControlBaseFragment implements View.OnClickListener, IntegrationCore.NaviCancleCallBack {
    Button mBtnPreset, mBtnIncarNavi, mBtnLeavecar;
    private FragmentManager fragmentManager;
    private IntegrationCore integrationCore;
    private List<BasicInfo> naviInfos;
    private Context context;
    private LatLng origin = new LatLng(32.058784, 118.757749);
    private String from = "石榴财智中心";
    private LatLng dest;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navi_test, container, false);

        context = getActivity();
        mBtnPreset = view.findViewById(R.id.btn_preset);
        mBtnIncarNavi = view.findViewById(R.id.btn_incar_navi);
        mBtnLeavecar = view.findViewById(R.id.btn_leavecar_navi);

        mBtnPreset.setOnClickListener(this);
        mBtnIncarNavi.setOnClickListener(this);
        mBtnLeavecar.setOnClickListener(this);

        integrationCore = IntegrationCore.getIntergrationCore(getActivity());
        fragmentManager = getFragmentManager();
        return view;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_preset:
                PhoneStateManager.getInstance(ArielApplication.getApp()).setPhoneState(PhoneState.OUT_CAR_MODE);
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.POISEARCH, R.id.main_content_view);
                break;
            case R.id.btn_incar_navi:
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.SEARCHADRESS, R.id.main_content_view);
                mHandler.sendEmptyMessageDelayed(1, 2000);
                break;
            case R.id.btn_leavecar_navi:
                if(dest != null){
                    //取消导航
                    try {
                        openAppByPackageName(context, "com.baidu.BaiduMap");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    integrationCore.setNaviCallBack(this);
                    integrationCore.cancelNav();
                }else {
                    Toast.makeText(context, "请先预设目的地", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private static void openAppByPackageName(Context context, String packageName) throws PackageManager.NameNotFoundException {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent("android.intent.action.MAIN", (Uri)null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List<ResolveInfo> apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo)apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                System.out.println("---alvin----packageName---" + packageName);
                String className = ri.activityInfo.name;
                if (packageName.equals("com.autonavi.minimap")) {
                    className = "com.autonavi.map.activity.SplashActivity";
                }

                System.out.println("---alvin----className---" + className);
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(337641472);
                intent.addCategory("android.intent.category.LAUNCHER");
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException var10) {
            var10.printStackTrace();
        }

    }


    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    startNavi();
                    fragmentManager.popBackStack();
                    break;
                case 2:
                    integrationCore.findCar(context, origin, dest);
                    break;
            }
        }
    };

    private void startNavi(){
        boolean isHasPreset = false;

        naviInfos = integrationCore.searchDbData(context, NaviInfo.class.getName());

        for(int i = 0; i < naviInfos.size(); i++){
            String presetName = ((NaviInfo)naviInfos.get(i)).getName();
            String isPreset = ((NaviInfo)naviInfos.get(i)).getIsPreset();
            Log.i(TAG, "presetName = " + presetName + " isPreset = " + isPreset);

            if(presetName.equals("预设") && isPreset.equals("true")){
                isHasPreset = true;
                double lat = Double.parseDouble(((NaviInfo)naviInfos.get(i)).getPoiLat());
                double lng = Double.parseDouble(((NaviInfo)naviInfos.get(i)).getPoiLno());
                Log.i(TAG, "minos startNavi lat = " + lat + " lng = " + lng);
                dest = new LatLng(lat, lng);
                integrationCore.planRoute(context, from, dest, origin);

                mUiControlItems.clear();
                mUIControlElements.clear();

                //跳转到导航之后,重新注册UIControl 开始导航,结束导航,第一个到第三个
                for (int j = 0; j < 3; j++) {
                    UIControlItem uiItem = new UIControlItem();
                    uiItem.setLabel(j + "");
                    uiItem.setIndex(j);
                    String url = mFragmentHashCode + "-" + NAV_IN_ROUTE_SELECT + ":" + j;
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
                //导航开启时间统计
                UMAnalyse.startTime(UMDurationEvent.NAV);
            }
        }

        if(!isHasPreset){
            Toast.makeText(context, "请先预设目的地", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNaviCancle() {
        mHandler.sendEmptyMessageDelayed(2, 1000);
    }
}
