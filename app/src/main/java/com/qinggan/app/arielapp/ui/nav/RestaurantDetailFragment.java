package com.qinggan.app.arielapp.ui.nav;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.iview.INavPoiDetailView;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.navigation.BdMapUIcontrol;
import com.qinggan.app.arielapp.minor.main.navigation.NavDataManager;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.minor.utils.Constants;
import com.qinggan.app.arielapp.phonestate.PhoneState;
import com.qinggan.app.arielapp.phonestate.PhoneStateManager;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.nav.QueryNavDetailSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.utils.CoordinateUtil;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.RestaurantBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;
import com.qinggan.app.widget.custom.StarBar;
import com.qinggan.app.widget.custom.SwipeBackLayout;

import static com.qinggan.app.voiceapi.control.ConstantNavUc.NAV_IN_ROUTE_SELECT;

public class RestaurantDetailFragment extends UIControlBaseFragment implements INavPoiDetailView,
        PhoneStateManager.PhoneStateChangeListener, WakeupControlMgr.WakeupControlListener{

    IFragmentStatusListener mFragmentStatusListener;

    private TextView mRestaurantName;
    private TextView mRestaurantAddress;
    private TextView mRestaurantPrice;
    private TextView mRestaurantDistance;
    private ImageView mRestaurantPic;
    private StarBar mRestaurantStar;
    private ImageView mNaviImage;
    private ImageView mFavImage;
    private RelativeLayout mPhoneLayout;

    private RestaurantBean mRestaurantBean;
    private int mBeanType;

    private SwipeBackLayout mSwipeBackLayout;
    private IntegrationCore integrationCore;
    private BdMapUIcontrol mBdMapUIcontrol;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryNavDetailSession) session).registerOnShowListener(this);
    }
    boolean isCollect = false;
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.voice_restaurant_detail, container, false);

        mRestaurantName = root.findViewById(R.id.restaurant_detail_name);
        mRestaurantAddress = root.findViewById(R.id.restaurant_detail_address);
        mRestaurantPrice = root.findViewById(R.id.restaurant_detail_price);
        mRestaurantDistance = root.findViewById(R.id.restaurant_detail_distance);
        mRestaurantPic = root.findViewById(R.id.restaurant_detail_pic);
        mRestaurantStar = root.findViewById(R.id.restaurant_detail_star);
        mPhoneLayout = root.findViewById(R.id.restaurant_detail_call);
        mNaviImage = root.findViewById(R.id.restaurant_detail_navi);
        mFavImage = root.findViewById(R.id.restaurant_detail_fav);

        mNaviImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhoneStateManager.getInstance(getActivity()).getPhoneState()
                        == PhoneState.OUT_CAR_MODE) {
                    setPreDestination();
                } else {
                    naviToAdrees();
                }
            }
        });

        mFavImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollect) {
                    deleteFav();
                    mFavImage.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_normal));
                    isCollect = false;
                } else {
                    addToFav();
                    mFavImage.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_pressed));
                    isCollect = true;
                }
            }
        });

        mPhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

        if (mFragmentStatusListener != null) {
            mFragmentStatusListener.onLoaded();
        }

        mSwipeBackLayout = new SwipeBackLayout(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
        mSwipeBackLayout.attachToFragment(this, root,
                getResources().getDimensionPixelSize(R.dimen.nav_detail_layout_top));
        mSwipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);

        integrationCore = IntegrationCore.getIntergrationCore(getActivity());


        return mSwipeBackLayout;
    }

    @Override
    public void onShowPoiDetail(DcsBean dcsBean, int beanType) {
        mRestaurantBean = (RestaurantBean)dcsBean;
        mRestaurantName.setText(mRestaurantBean.getName());
        mRestaurantAddress.setText(mRestaurantBean.getAddress());
        mRestaurantPrice.setText(getString(R.string.restaurant_price_start) + mRestaurantBean.getPrice() + getString(R.string.restaurant_price_end));
        mRestaurantDistance.setText(getString(R.string.restaurant_distance) + mRestaurantBean.getDistance());
        Glide.with(getActivity().getApplicationContext()).load(mRestaurantBean.getImageUrl()).
                placeholder(R.drawable.default_loading).into(mRestaurantPic);
        mRestaurantStar.setStarMark((float) mRestaurantBean.getScore());
        if (PhoneStateManager.getInstance(ArielApplication.getApp()).getPhoneState()
                == PhoneState.OUT_CAR_MODE) {
            mNaviImage.setImageResource(R.drawable.detail_preinstall_selector);
        } else {
            mNaviImage.setImageResource(R.drawable.detail_navi_selector);
        }
        addContentItemList();
    }

    private void addContentItemList() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem startNav = new UIControlElementItem();
        startNav.addWord(getString(R.string.start_nav));
        startNav.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_NAV);
        mUIControlElements.add(startNav);

        UIControlElementItem startCall = new UIControlElementItem();
        startCall.addWord(getString(R.string.start_phone_call));
        startCall.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_CALL);
        mUIControlElements.add(startCall);

        UIControlElementItem addFav = new UIControlElementItem();
        addFav.addWord(getString(R.string.start_fav));
        addFav.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_COLLECTION);
        mUIControlElements.add(addFav);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }


    private void addBdCmdUIControl() {
        mUiControlItems.clear();
        mUIControlElements.clear();
        mBdMapUIcontrol = new BdMapUIcontrol(getActivity());
        mUIControlElements = mBdMapUIcontrol.getBdCmdUIControl(mFragmentHashCode);
        addElementAndListContent();
    }


    private void addWakeupElements() {
        WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.RESTAURANT_NAME_SPACE,
                0, 0, this);
    }

    @Override
    public void onItemSelected(String type, String key) {
        Log.d(TAG,"RestaurantDetailFragment addWakeupElements type ： " + type + "--key : " + key);

        if (!WakeupControlMgr.RESTAURANT_NAME_SPACE.equals(type)) {
            return;
        }

        if (TextUtils.isEmpty(key)) return;
        if (WakeupControlMgr.RESTAURANT_START_NAV.equals(key)) {
            if (PhoneStateManager.getInstance(getActivity()).getPhoneState()
                    == PhoneState.OUT_CAR_MODE) {
                setPreDestination();
            } else {
                naviToAdrees();
            }
        } else if (WakeupControlMgr.RESTAURANT_START_CALL.equals(key)) {
            makeCall();
        } else if (WakeupControlMgr.RESTAURANT_START_FAV.equals(key)) {
            addToFav();
        } else if (WakeupControlMgr.RESTAURANT_BACK_TO.equals(key)) {
            getActivity().onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        addWakeupElements();
    }

    @Override
    public void onPause() {
        super.onPause();
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.RESTAURANT_NAME_SPACE);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden && mSwipeBackLayout != null) {
            mSwipeBackLayout.hiddenPreFragment();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG,"onSelectOtherOC action ： " + action);
        if (TextUtils.isEmpty(action)) return;
        if (ConstantNavUc.NAV_START_NAV.equals(action)) {
            if (PhoneStateManager.getInstance(getActivity()).getPhoneState()
                    == PhoneState.OUT_CAR_MODE) {
                setPreDestination();
            } else {
                naviToAdrees();
            }
        } else if (ConstantNavUc.NAV_START_CALL.equals(action)) {
            makeCall();
        } else if (ConstantNavUc.NAV_START_COLLECTION.equals(action)) {
            if (isCollect){
                ToastUtil.show(getResources().getString(R.string.start_fav_already), getActivity());
            }else{
                addToFav();
                mFavImage.setImageDrawable(getResources().getDrawable(R.drawable.navi_buttun_sc_pressed));
                isCollect = true;

            }
        } else if (mBdMapUIcontrol != null) {
            mBdMapUIcontrol.handleBdVoiceAction(action);
        }
    }

    private void naviToAdrees() {
        Log.d(TAG,"--naviToAdrees--");
        double longitude = mRestaurantBean.getLng();
        double latitude = mRestaurantBean.getLat();
        if (mRestaurantBean.getGeoSystem().equals("BD09MC")) {
            double[] covertGeo = CoordinateUtil.bdmc2gll(longitude, latitude);
            longitude = covertGeo[0];
            latitude = covertGeo[1];
        }
        LatLng dest = new LatLng(latitude, longitude);
        IntegrationCore.getIntergrationCore(getContext()).planRoute(getContext(), mRestaurantBean.getName(), dest);
        addBdCmdUIControl();

//        addNaviCommandList();
    }

    /**
     * 离车模式下不开启导航，设置预设目的地
     */
    private void setPreDestination() {
        NavDataManager manager = NavDataManager.getInstance();
        manager.init(getActivity());
        manager.savePresetDestInfo("预设", Constants.PRSET_NORMAL_TYPE, mRestaurantBean.getLat() + "",
                mRestaurantBean.getLng() + "", mRestaurantBean.getName(), mRestaurantBean.getId());
        ToastUtil.show("预设目的地完成", getActivity());
    }
    // 取消收藏
    private void deleteFav(){
        String id = mRestaurantBean.getId();
        NavDataManager.getInstance().deleteCloundNaviInfo(id);
        ToastUtil.show(getResources().getString(R.string.cancelcollected), getActivity());
    }
    private void addToFav() {
        Log.d(TAG,"--addToFav--");
        double longitude = mRestaurantBean.getLng();
        double latitude = mRestaurantBean.getLat();
        if (mRestaurantBean.getGeoSystem().equals("BD09MC")) {
            double[] covertGeo = CoordinateUtil.bdmc2gll(longitude, latitude);
            longitude = covertGeo[0];
            latitude = covertGeo[1];
        }

        String id = mRestaurantBean.getId();
        String name = mRestaurantBean.getName();
        String address = mRestaurantBean.getAddress();

        NavDataManager.getInstance().savePersonCenterCollectionDest(String.valueOf(latitude), String.valueOf(longitude),
                name, address, id);

        ToastUtil.show(getResources().getString(R.string.start_fav), getActivity());
    }

    private void addNaviCommandList(){
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

        UIControlElementItem startNav = new UIControlElementItem();
        startNav.addWord(getString(R.string.nav_start));
        startNav.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_NAV);
        mUIControlElements.add(startNav);

        UIControlElementItem startCall = new UIControlElementItem();
        startCall.addWord(getString(R.string.start_phone_call));
        startCall.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_CALL);
        mUIControlElements.add(startCall);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void makeCall() {
        Log.d(TAG,"--makeCall--");
        String tel = mRestaurantBean.getTel();
        if (!TextUtils.isEmpty(tel)) {
            CallUtils.startCallByPhoneNumber(getContext(), tel);
        }
    }

    @Override
    public void onPhoneStateChange(PhoneState phoneState) {
        if(phoneState== PhoneState.OUT_CAR_MODE){
            mNaviImage.setImageResource(R.drawable.detail_preinstall_selector);
        } else {
            mNaviImage.setImageResource(R.drawable.detail_navi_selector);
        }
    }
}
