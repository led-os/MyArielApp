package com.qinggan.app.arielapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.entity.EventBusTSPInfo;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.navigation.NavDataManager;
import com.qinggan.app.arielapp.minor.main.navigation.NavPoiSearchActivity;
import com.qinggan.app.arielapp.minor.main.utils.MapUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.user.Bean.AddressBean;
import com.qinggan.app.arielapp.user.adapter.AddressAdapter;
import com.qinggan.mobile.tsp.restmiddle.RestError;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.qinggan.app.arielapp.minor.utils.Constants.DELETE_ADDRESS_EVENT;

/**
 * Created by Yorashe on 18-11-23.
 */

public class AddressActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.back_btn)
    RelativeLayout backBtn;
    @BindView(R.id.add_icon)
    RelativeLayout addIcon;
    @BindView(R.id.recyclerview)
    SwipeMenuRecyclerView mRecyclerView;

    private View mHeadView;
    private ImageView addHome;
    private RelativeLayout addHomeLay;
    private ImageView addComp;
    private RelativeLayout addCompLay;
    private ImageView addNew;
    private RelativeLayout addNewLay;
    private TextView mCompAddressText;
    private TextView mHomeAddressText;
    private TextView mTvHome;
    private TextView mTvCompany;

    private AddressAdapter adapter;
    List<AddressBean> addressList = new ArrayList<>();
    private List<BasicInfo> mNaviInfos;
    private NavDataManager mNavDataManager;
    private IntegrationCore mIntegrationCore;

    @Override
    protected void initView() {
        mHeadView=getLayoutInflater().inflate(R.layout.address_headview,null);
        adapter=new AddressAdapter(this,addressList,mRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addHeaderView(mHeadView);

        addHome=mHeadView.findViewById(R.id.add_home);
        mTvHome = mHeadView.findViewById(R.id.tv_home);
        addHomeLay=mHeadView.findViewById(R.id.add_home_lay);

        addComp=mHeadView.findViewById(R.id.add_comp);
        mTvCompany = mHeadView.findViewById(R.id.tv_company);
        addCompLay=mHeadView.findViewById(R.id.add_comp_lay);

        addNew=mHeadView.findViewById(R.id.add_new);
        addNewLay=mHeadView.findViewById(R.id.add_new_lay);

        mCompAddressText=mHeadView.findViewById(R.id.comp_address_text);
        mHomeAddressText=mHeadView.findViewById(R.id.home_address_text);
        addNewLay.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mNavDataManager = NavDataManager.getInstance();
        mNavDataManager.init(this);
        mIntegrationCore = IntegrationCore.getIntergrationCore(this);
    }

    private void initPresetDestination(){
        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setIsPreset("true");
        mNaviInfos = IntegrationCore.getIntergrationCore(this).queryDestInfo(naviInfo, this, NaviInfo.class.getName());

        Log.i(TAG, "mNaviInfos = " + mNaviInfos);
        if(mNaviInfos != null){
            for(int i = 0; i < mNaviInfos.size(); i++){
                String presetName = ((NaviInfo)mNaviInfos.get(i)).getName();
                String destName = ((NaviInfo)mNaviInfos.get(i)).getDisplayName();
                String address = ((NaviInfo)mNaviInfos.get(i)).getAddress();
                String lng = ((NaviInfo)mNaviInfos.get(i)).getPoiLno();
                String lat = ((NaviInfo)mNaviInfos.get(i)).getPoiLat();
                String sid = ((NaviInfo)mNaviInfos.get(i)).getSid();

                Log.i(TAG, "presetName = " + presetName);
                if(presetName.equals(mContext.getString(R.string.home))){
                    Log.i("minos", " presetName = " + presetName + " destName = " + destName
                            + " lng = " + lng + " lat = " + lat);
                    mHomeAddressText.setText(destName);
                    addHome.setVisibility(View.GONE);
                    setTextViewMarginLeftZero(mTvHome);
                    setTextViewMarginLeftZero(mHomeAddressText);
                }else if(presetName.equals(mContext.getString(R.string.company))){
                    mCompAddressText.setText(destName);
                    addComp.setVisibility(View.GONE);
                    setTextViewMarginLeftZero(mTvCompany);
                    setTextViewMarginLeftZero(mCompAddressText);
                }else if(presetName.equals(mContext.getString(R.string.preset))){
//                    AddressBean addressBean = new AddressBean();
//                    addressBean.setDisplayName(destName);
//                    addressBean.setAddress(address);
//                    addressBean.setLatitude(Double.parseDouble(lat));
//                    addressBean.setLongitude(Double.parseDouble(lng));
//                    addressBean.setSid(sid);
//                    addressList.add(addressBean);
                }
            }
        }
    }

    private void setTextViewMarginLeftZero(TextView textView) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        lp.leftMargin = 0;
        textView.setLayoutParams(lp);
    }

    private void initCollectionData(){
        addressList.clear();
        List<NaviInfo> naviInfos = mNavDataManager.queryCollectionDestInfo();
        for(NaviInfo naviInfo : naviInfos){
            AddressBean addressBean = new AddressBean();
            addressBean.setDisplayName(naviInfo.getDisplayName());
            addressBean.setAddress(naviInfo.getAddress());
            addressBean.setLatitude(Double.parseDouble(naviInfo.getPoiLat()));
            addressBean.setLongitude(Double.parseDouble(naviInfo.getPoiLno()));
            addressBean.setSid(naviInfo.getSid());
            addressList.add(addressBean);
        }
        adapter.setDataList(addressList);
        addNewLay.setVisibility((adapter.getDataList()!=null && adapter.getDataList().size()>0)?View.GONE:View.VISIBLE);
    }

    //删除收藏地址event
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBusMsg(EventBusBean event) {
        String type = event.getType();
        switch (type) {
            case DELETE_ADDRESS_EVENT:
                 delFavoriteData(event.getMsg());
                 addNewLay.setVisibility((adapter.getDataList()!=null && adapter.getDataList().size()>0)?View.GONE:View.VISIBLE);
                 break;
            default:
                 break;
        }
    }

    //接收eventsbus消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getVehicleEvent(EventBusTSPInfo event) {
        if (event.isSuccess()) {
            Log.i(TAG, "event.isSuccess() event.getCommandType() = " + event.getCommandType());
            deleteLocalCollectNaviInfo(event, MapUtils.NAVIINFO_SYNC_FLAG_NORMAL);
        }else {
            Log.i(TAG, "event is fail");
            RestError error = event.getRestError();
            if(error!=null){
                Log.i(TAG, "error:"+error.getMessage());
                Log.i(TAG, "error:"+error);
            }
            deleteLocalCollectNaviInfo(event, MapUtils.NAVIINFO_SYNC_FLAG_DELETE);
        }
    }

    private void deleteLocalCollectNaviInfo(EventBusTSPInfo event, int syncFlag){
        if (event.getCommandType() == MapUtils.TSP_COMMAND_TYPE_DELETE) {
            List<AddressBean> addressBeans = (List<AddressBean>) event.getModule();
            if (addressBeans != null) {
                NaviInfo naviInfo = MapUtils.addressBean2NaviInfo(addressBeans.get(0));

                NaviInfo queryNaviInfo = new NaviInfo();
                queryNaviInfo.setSid(naviInfo.getSid());

                List<BasicInfo> basicInfos = mIntegrationCore.queryDestInfo(queryNaviInfo, this, NaviInfo.class.getName());
                for(BasicInfo basicInfo : basicInfos){
                    if(basicInfo instanceof NaviInfo){
                        Log.i(TAG, " delete basicInfo = " + basicInfo);
                        mNavDataManager.deleteLocalPresetNaviInfo((NaviInfo) basicInfo, syncFlag);
                    }
                }
            }
        }
    }

    private void delFavoriteData(final String sid){
        mNavDataManager.deleteCloundNaviInfo(sid);
    }

    @Override
    protected void initListener() {
        addHome.setOnClickListener(this);
        addHomeLay.setOnClickListener(this);
        addComp.setOnClickListener(this);
        addCompLay.setOnClickListener(this);
        addNew.setOnClickListener(this);
        addNewLay.setOnClickListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.address_lay;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPresetDestination();
        initCollectionData();
    }

    /**
     * @param activity
     */
    public static void startAction(Context activity) {
        Intent intent = new Intent(activity, AddressActivity.class);
        activity.startActivity(intent);
    }

    @OnClick({R.id.back_btn,R.id.add_icon})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.add_icon:
            case R.id.add_new:
            case R.id.add_new_lay:
                addAddress();
                break;
            case R.id.add_home_lay:
            case R.id.add_home:
                setHomeAddress();
                break;
            case R.id.add_comp_lay:
            case R.id.add_comp:
                setCompAddreess();
                break;
        }
    }

    private void addAddress(){
        Log.i(TAG, "addAddress ");
        startPoiSearchActivity(NavPoiSearchActivity.NORMAL_TYPE);
    }

    private void setHomeAddress(){
        Log.i(TAG, "setHomeAddress ");
        startPoiSearchActivity(NavPoiSearchActivity.HOME_TYPE);
    }

    private void startPoiSearchActivity(int setType){
        Intent intent = new Intent();
        intent.putExtra("setType", setType);
        intent.setClass(this, NavPoiSearchActivity.class);
        startActivity(intent);
    }

    private void setCompAddreess(){
        Log.i(TAG, "setCompAddreess ");
        startPoiSearchActivity(NavPoiSearchActivity.COMPANY_TYPE);
    }

}
