package com.qinggan.app.arielapp.ui.nav;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.adpater.NavAddressAdapter;
import com.qinggan.app.voiceapi.analyse.UMAnalyse;
import com.qinggan.app.voiceapi.analyse.UMDurationEvent;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.nav.NavConditionBean;
import com.qinggan.app.voiceapi.bean.nav.NavPOIBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;

import java.util.ArrayList;
import java.util.List;

import static com.qinggan.app.voiceapi.DataTypeConstant.ROUTE_SEARCH_TYPE;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;
import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_LIST_SELECT;

/**
 * <导航地址列表>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class NavAddressListFragment extends UIControlBaseFragment implements NavAddressAdapter.OnItemClickListener {
    private RecyclerView mRcvAddressList;
    private String from = "石榴财智中心";
    private LatLng origin = new LatLng(32.058784, 118.757749); //车的位置
    private LatLng dest;
    private IntegrationCore integrationCore;
    private Context context;
    private ImageView mIvBack;
    private TextView mSpeakGuide;
    private TextView mTvDest;
    private LinearLayout mLlConfirmDest;
    private static final int MSG_PLAN_ROUTE = 1;
    private FragmentManager fragmentManager;
    private LinearLayout mLlSetPosition;
    private LinearLayout mLlCompany;
    private LinearLayout mLlHome;
    private LinearLayout mLlCollection;
    private String mDestName;
    private String mDestLat;
    private String mDestLng;
    private List<BasicInfo> naviInfos;

    public static NavAddressListFragment newInstance(DcsDataWrapper wrapper, ArrayList<DcsBean> poiBeans) {
        Bundle args = new Bundle();
        args.putParcelable("wrapper", wrapper);
        args.putParcelableArrayList("address", poiBeans);
        NavAddressListFragment fragment = new NavAddressListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSelectItemPosition(int position) {
        //用户语音选中了某个地址
        confirmDest(position);
    }

    @Override
    public void onItemClick(int position) {
        //用户手动选中了某个地址
        confirmDest(position);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.ll_company:
                savePresetDestInfo("公司");
                ToastUtil.show("点击公司", context);
                break;
            case R.id.ll_home:
                savePresetDestInfo("家");
                ToastUtil.show("点击家", context);
                break;
            case R.id.ll_collection:
                ToastUtil.show("点击收藏", context);
                break;
        }
    }

    //预设目的地信息
    public void savePresetDestInfo(String name) {
        boolean isUpdate = false;

        NaviInfo naviInfo = new NaviInfo();
        naviInfo.setPoiLat(mDestLat);
        naviInfo.setPoiLno(mDestLng);
        naviInfo.setDisplayName(mDestName);
        naviInfo.setName(name);
        naviInfo.setIsPreset("true");

        naviInfos = integrationCore.searchDbData(context, NaviInfo.class.getName());

        for(int i = 0; i < naviInfos.size(); i++){
            String presetName = ((NaviInfo)naviInfos.get(i)).getName();
            String isPreset = ((NaviInfo)naviInfos.get(i)).getIsPreset();
            Log.i(TAG, "presetName = " + presetName + " isPreset = " + isPreset);

            if(presetName.equals(name) && isPreset.equals("true")){
                isUpdate = true;
            }
        }

        if(isUpdate){
            integrationCore.updatePresetDest(naviInfo, context, NaviInfo.class.getName());
        }else {
            integrationCore.savePresetDest(naviInfo, context, NaviInfo.class.getName());
        }
    }

    private boolean out_to_nav = false;

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

    public void confirmDest(int position) {
        double lat = ((NavPOIBean) poiBeans.get(position)).getLat();
        double lng = ((NavPOIBean) poiBeans.get(position)).getLng();
        mDestName =  ((NavPOIBean) poiBeans.get(position)).getName();

        mDestLat = String.valueOf(lat);
        mDestLng = String.valueOf(lng);

        dest = new LatLng(lat, lng);
        showConfirmDestUi(mDestName);

        mHandler.sendEmptyMessageDelayed(MSG_PLAN_ROUTE, 3000);
    }

    public void showConfirmDestUi(String name){
        mSpeakGuide.setVisibility(View.GONE);
        mLlConfirmDest.setVisibility(View.VISIBLE);
        mRcvAddressList.setVisibility(View.GONE);
        mLlSetPosition.setVisibility(View.VISIBLE);
        mTvDest.setText(name);
    }

    private void startPlanRoute(){
        if(PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE){
            savePresetDestInfo("预设");
            Toast.makeText(context, "预设目的地成功", Toast.LENGTH_SHORT).show();
            fragmentManager.popBackStack();
        }else {
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
    public void onResume() {
        super.onResume();
        if (out_to_nav) {
            //导航开启时间统计
            UMAnalyse.stopTime(UMDurationEvent.NAV);
            fragmentManager.popBackStack();
            return;
        }
        integrationCore = IntegrationCore.getIntergrationCore(ArielApplication.getApp());
        fragmentManager = getFragmentManager();
        addAddressListUIControl();

    }

    DcsDataWrapper wrapper;
    //地址数据list 使用的时候,DcsBean需要强转成NavPOIBean
    ArrayList<DcsBean> poiBeans;
    NavConditionBean conditionBean;
    ArrayList<NavAddressBean> navAddressBeans = new ArrayList<>();
    /**
     * TRAFFIC_CONDITION_TYPE ,
     * ROUTE_SEARCH_TYPE ,
     * POI_SEARCH_TYPE,
     * ETA_TYPE
     **/
    int type;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nav_address_list, container, false);
        context = getActivity();
        mIvBack = rootView.findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });
        mRcvAddressList = rootView.findViewById(R.id.recycle_nav_address);
        mRcvAddressList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRcvAddressList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        mSpeakGuide = rootView.findViewById(R.id.tv_speak_guide);
        mLlConfirmDest = rootView.findViewById(R.id.ll_confirm_dest);
        mTvDest = rootView.findViewById(R.id.tv_dest);
        mLlSetPosition = rootView.findViewById(R.id.ll_setting_position);
        mLlCompany = rootView.findViewById(R.id.ll_company);
        mLlHome = rootView.findViewById(R.id.ll_home);
        mLlCollection = rootView.findViewById(R.id.ll_collection);

        mLlCompany.setOnClickListener(this);
        mLlHome.setOnClickListener(this);
        mLlCollection.setOnClickListener(this);

        wrapper = getArguments().getParcelable("wrapper");
        poiBeans = getArguments().getParcelableArrayList("address");
        type = wrapper.getType();
        conditionBean = (NavConditionBean) wrapper.getDcsBean();
        if (type == ROUTE_SEARCH_TYPE) {
            //起点
            String origin = conditionBean.getOrigin();
            //途经点
            String passpoint = conditionBean.getPassingPoint();
            //终点
            String destina = conditionBean.getDestination();

            Log.i(TAG, "origin = " + origin + " destina = " + destina);
        }

        navAddressBeans.clear();
        for (DcsBean dcsBean : poiBeans) {
            String address = ((NavPOIBean) dcsBean).getAddress();
            String name = ((NavPOIBean) dcsBean).getName();
            NavAddressBean navAddressBean = new NavAddressBean(address, name);
            navAddressBeans.add(navAddressBean);
        }
        NavAddressAdapter navAddressAdapter = new NavAddressAdapter(navAddressBeans);
        mRcvAddressList.setAdapter(navAddressAdapter);
        navAddressAdapter.setItemClickListener(this);
        return rootView;
    }

    /**
     * 添加地址列表UIControl
     */
    private void addAddressListUIControl() {
        //添加UIControl
        mUiControlItems.clear();
        mUIControlElements.clear();
        int size = poiBeans.size();
        for (int i = 0; i < size; i++) {
            String address = ((NavPOIBean) poiBeans.get(i)).getAddress();
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
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }
}
