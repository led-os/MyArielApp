package com.qinggan.app.arielapp.minor.scenario;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.controller.StageController;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.integration.PateoVehicleControlCMD;
import com.qinggan.app.arielapp.minor.main.mui.adapter.ProfileAdpter;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.bluekey.BindCarActivity;
import com.qinggan.app.arielapp.utils.VehicleUtils;
import com.qinggan.app.arielapp.voiceview.VoicePolicyManage;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.mobile.tsp.manager.TspManager;
import com.qinggan.mobile.tsp.util.NetUtil;
import com.qinggan.qinglink.api.OnConnectListener;
import com.qinggan.qinglink.api.OnInitListener;
import com.qinggan.app.widget.voiceLinePulse.LinePulseView;
import com.qinggan.qinglink.api.md.HotwordListener;
import com.qinggan.qinglink.api.md.HotwordManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang
 * Time on 2018/11/6.
 * Function  情景模式
 */


public class ProfilesFragment extends UIControlBaseFragment implements RefreshUICallback{

    private final String TAG = "scene_mode";
    View mView;
    private RecyclerView recyclerView_profile;
    private Context mContext = ArielApplication.getApp();
    ProfileAdpter profileAdpter = new ProfileAdpter(mContext);
    List<String> list1 = new ArrayList<>();
    ImageView mImageView;
    LinePulseView wakeup;
    IntegrationCore integrationCore = null;
    String title;
    PateoVehicleControlCMD vehicleControlCMD;
    private boolean vehicleControlResult;
    public static final int SNOW_NUM = 2;
    public static final int SMOKE_NUM = 3;
    public static final int COLD_NUM = 0;
    public static final int SUN_NUM = 1;
    private static final String MODULE_NAME = "vehicle";
    private HotwordManager mHotwordManager;
    private boolean isOnpause = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.scene_mode_layout, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileAdpter.setItemClickListener(new ProfileAdpter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                if (!canUseVehiClecontrol()) {
                    return;
                }
                if (!VehicleUtils.isACCOn()){
                    return;
                }
                Log.d("sasuke","item click");
                lastSelected = curSelected;
                curSelected = recyclerView_profile.getChildAdapterPosition(view);
                Log.d(TAG,"curSelected : "+curSelected);
                Log.d(TAG,"lastSelected : "+lastSelected);
                startProfile(curSelected);
                if (lastSelected != -1){
                    recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.INVISIBLE);
                    recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.setBackground(null);
                }
                if (curSelected != -1){
                    recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.VISIBLE);
                    recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.setBackgroundResource(R.drawable.scene_mode_taget);
                }
            }

            @Override
            public void onItemLongClick(View view) {

            }
        });
        init();
    }

    /**
     * 登录后才可以使用
     */

    private boolean canUseVehiClecontrol() {
        if (!NetUtil.isNetworkConnected(mContext)){
//            VoicePolicyManage.getInstance().speak(mContext.getResources().getString(R.string.no_network_tips));
            ToastUtil.show(R.string.no_network_tips,mContext);
            return false;
        } else if (ArielApplication.getmUserInfoWithLogin() == null) {
            VoicePolicyManage.getInstance().speak("请先登录并绑定车辆后再使用车控");
            return false;
        } else if (TextUtils.isEmpty(TspManager.getmPdsn())) {
            Context context = ArielApplication.getApp();
            Intent intent = new Intent(context, BindCarActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            VoicePolicyManage.getInstance().speak("请先绑定车辆后再使用车控");
            return false;
        }
        return true;
    }


    private void init() {
        initView();
        initData();
        integrationCore = IntegrationCore.getIntergrationCore(mContext);
        vehicleControlCMD = PateoVehicleControlCMD.getInstance();
        vehicleControlCMD.setUiCallback(this);
    }

    private void initView() {
        recyclerView_profile = mView.findViewById(R.id.recycle_profile);
        mImageView = mView.findViewById(R.id.delete_image);
        wakeup = mView.findViewById(R.id.music_icon);
        wakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VoicePolicyManage.getInstance().record(true);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_profile.setLayoutManager(new GridLayoutManager(mContext, 2));
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                integrationCore.changeStage(StageController.Stage.MAIN_IN_CAR);
                getFragmentManager().popBackStack();
                getActivity().finish();
            }
        });
    }

    private void initData() {
        list1.add(getString(R.string.cold));
        list1.add(getString(R.string.sun_mode));
        list1.add(getString(R.string.snow_mode));
        list1.add(getString(R.string.smoke));
        profileAdpter.setList(list1);
        recyclerView_profile.setAdapter(profileAdpter);
        mHotwordManager = HotwordManager.getInstance(getContext(), new OnInitListener() {
            @Override
            public void onConnectStatusChange(boolean b) {

            }
        }, new OnConnectListener() {
            @Override
            public void onConnect(boolean b) {
                if (b && !isOnpause) {
                    addWakeupElements();
                } else {
                    if (null != mHotwordManager) {
                        mHotwordManager.clearElementUCWords(MODULE_NAME);
                    }
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isOnpause = false;
        addWakeupElements();
    }


    @Override
    public void onPause() {
        super.onPause();
        isOnpause = true;
        if (null != mHotwordManager) {
            mHotwordManager.clearElementUCWords(MODULE_NAME);
        }
    }


    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }




    private int curSelected = -1;
    private int lastSelected = -1;

    public void startProfile(int item){
        if (item == SNOW_NUM){
            integrationCore.executeScenario(SNOW_NUM,this);
        }else if (item == SMOKE_NUM){
            integrationCore.executeScenario(SMOKE_NUM,this);
        }else if (item == COLD_NUM){
            integrationCore.executeScenario(COLD_NUM,this);
        }else {
            integrationCore.executeScenario(SUN_NUM,this);
        }
    }

    @Override
    public void refreshUI(boolean success) {
        Log.d(TAG,"vehicel result : "+success);
        vehicleControlResult = success;
        if (success){
            if (lastSelected != -1){
                recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.INVISIBLE);
                recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.setBackground(null);
            }
            if (curSelected != -1){
                recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.VISIBLE);
                recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.setBackgroundResource(R.drawable.scene_mode_taget);
            }
        }

    }

    private void updateUISelected(){
        if (lastSelected != -1){
            recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.INVISIBLE);
            recyclerView_profile.findViewHolderForAdapterPosition(lastSelected).itemView.setBackground(null);
        }
        if (curSelected != -1){
            recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.findViewById(R.id.mode_opened).setVisibility(View.VISIBLE);
            recyclerView_profile.findViewHolderForAdapterPosition(curSelected).itemView.setBackgroundResource(R.drawable.scene_mode_taget);
        }
    }

    private void addWakeupElements() {
        ArrayList<com.qinggan.qinglink.bean.UIControlElementItem> mWakeUpElements = new ArrayList<>();
        com.qinggan.qinglink.bean.UIControlElementItem smartWormElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartWormElement.setWord(getString(R.string.sun));
        smartWormElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_WORM);
        mWakeUpElements.add(smartWormElement);

        com.qinggan.qinglink.bean.UIControlElementItem smartWormElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartWormElement1.setWord(getString(R.string.worm_mode));
        smartWormElement1.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_WORM+"1");
        mWakeUpElements.add(smartWormElement1);

        com.qinggan.qinglink.bean.UIControlElementItem smartClodElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartClodElement.setWord(getString(R.string.cold));
        smartClodElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_CLOD);
        mWakeUpElements.add(smartClodElement);
        com.qinggan.qinglink.bean.UIControlElementItem smartClodElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartClodElement1.setWord(getString(R.string.cool_mode1));
        smartClodElement1.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_CLOD+"1");
        mWakeUpElements.add(smartClodElement1);

        com.qinggan.qinglink.bean.UIControlElementItem smartSmokElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartSmokElement.setWord(getString(R.string.wakeup43));
        smartSmokElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_SMOK);
        mWakeUpElements.add(smartSmokElement);

        com.qinggan.qinglink.bean.UIControlElementItem smartRainElement = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartRainElement.setWord(getString(R.string.rain_mode));
        smartRainElement.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN);
        mWakeUpElements.add(smartRainElement);
        com.qinggan.qinglink.bean.UIControlElementItem smartRainElement1 = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartRainElement1.setWord(getString(R.string.rain_mode1));
        smartRainElement1.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN+"1");
        mWakeUpElements.add(smartRainElement1);
        com.qinggan.qinglink.bean.UIControlElementItem smartRainElement2 = new com.qinggan.qinglink.bean.UIControlElementItem();
        smartRainElement2.setWord(getString(R.string.snow_mode));
        smartRainElement2.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN+"2");
        mWakeUpElements.add(smartRainElement2);
        mHotwordManager.setElementUCWords(MODULE_NAME, mWakeUpElements);
        mHotwordManager.registerListener(MODULE_NAME, new HotwordListener() {
            @Override
            public void onItemSelected(final String action) {
                mImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!canUseVehiClecontrol()) {
                            return;
                        }
                        if (!VehicleUtils.isACCOn()){
                            return;
                        }
                        //一键温暖
                        if ( !TextUtils.isEmpty(action) && action.contains(ConstantNavUc.VEHICLECONTROL_OPEN_SMART_WORM)) {
                            startProfile(SUN_NUM);
                            lastSelected = curSelected;
                            curSelected  = SUN_NUM;
                            updateUISelected();
                            return;
                        }
                        //清凉
                        if ( !TextUtils.isEmpty(action) && action.contains(ConstantNavUc.VEHICLECONTROL_OPEN_SMART_CLOD)) {
                            startProfile(COLD_NUM);
                            lastSelected = curSelected;
                            curSelected = COLD_NUM;
                            updateUISelected();
                            return;
                        }
                        //雨雪模式
                        if ( !TextUtils.isEmpty(action) && action.contains(ConstantNavUc.VEHICLECONTROL_OPEN_SMART_RAIN)) {
                            startProfile(SNOW_NUM);
                            lastSelected = curSelected;
                            curSelected = SNOW_NUM;
                            updateUISelected();
                            return;
                        }
                        //抽烟模式
                        if ( !TextUtils.isEmpty(action) && action.contains(ConstantNavUc.VEHICLECONTROL_OPEN_SMART_SMOK)) {
                            startProfile(SMOKE_NUM);
                            lastSelected = curSelected;
                            curSelected = SMOKE_NUM;
                            updateUISelected();
                            return;
                        }
                    }
                });

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onSwitchPage(int i) {

            }
        });
    }

}
