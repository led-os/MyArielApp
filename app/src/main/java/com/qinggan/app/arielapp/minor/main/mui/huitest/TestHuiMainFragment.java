package com.qinggan.app.arielapp.minor.main.mui.huitest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.capability.push.factory.model.HUIPushMode;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.push.bean.PushMessageBodyBean;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;

public class TestHuiMainFragment extends UIControlBaseFragment implements View.OnClickListener {
    private Button mBtnGoWork, mBtnBackHome, mBtnRainy, mBtnpollution, mBtnpollution2;
    private FragmentManager fragmentManager;
    private IntegrationCore integrationCore;
    private Context context;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hui_test, container, false);

        context = getActivity();

        mBtnGoWork = view.findViewById(R.id.btn_go_road_condition);
        mBtnBackHome = view.findViewById(R.id.btn_back_road_condition);
        mBtnRainy = view.findViewById(R.id.btn_rainy_weather);
        mBtnpollution = view.findViewById(R.id.btn_pollution_weather);
        mBtnpollution2 = view.findViewById(R.id.btn_pollution_weather2);

        mBtnGoWork.setOnClickListener(this);
        mBtnBackHome.setOnClickListener(this);
        mBtnRainy.setOnClickListener(this);
        mBtnpollution.setOnClickListener(this);
        mBtnpollution2.setOnClickListener(this);

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
    public void onClick(View view) {
        PushMessageBodyBean pushMessageBodyBean = new PushMessageBodyBean();

        switch (view.getId()) {
            case R.id.btn_go_road_condition:
                pushMessageBodyBean.setMsgTitle("上班交通情况");
                pushMessageBodyBean.setPushBody("上班交通情况");
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
                break;
            case R.id.btn_back_road_condition:
                pushMessageBodyBean.setMsgTitle("下班交通情况");
                pushMessageBodyBean.setPushBody("下班交通情况");
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
                break;
            case R.id.btn_rainy_weather:
                pushMessageBodyBean.setMsgTitle("下雨天气推送");
                pushMessageBodyBean.setPushBody("下雨天气推送");
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
                break;
            case R.id.btn_pollution_weather:
                pushMessageBodyBean.setMsgTitle("污染天气推送");
                pushMessageBodyBean.setPushBody("污染天气推送");
                LocalFragmentManager.getInstance().showSubFragment(fragmentManager, LocalFragmentManager.FragType.PUSHMESSAGE, R.id.main_content_view, pushMessageBodyBean);
                break;
            case R.id.btn_pollution_weather2:
                String pushBody = "{\"commonData\":{\"deviceId\":\"\",\"domain\":\"AI_OPOLLUTIONWEATHER\",\"domainDescribe\":\"污染天气提醒\",\"rulesPriority\":9,\"sceneTypeName\":\"消息提醒\",\"timeStamp\":1544078413284,\"userAccountId\":\"61e6b43d3ecc4758b11728425e89a9d3\"},\"serviceData\":\"当前空气质量较差，建议开启雾霾模式\"}";
                HUIPushMode pushMode = new HUIPushMode();
                pushMode.doService(pushBody);
                break;
        }


    }


}
