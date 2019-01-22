package com.qinggan.app.arielapp.ui.nav;

import android.graphics.Color;
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
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.iview.INavPoiDetailView;
import com.qinggan.app.arielapp.minor.core.IntegrationCore;
import com.qinggan.app.arielapp.minor.main.navigation.NavDataManager;
import com.qinggan.app.arielapp.minor.scenario.ToastUtil;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.nav.QueryNavDetailSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.utils.CoordinateUtil;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.ScenicBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.widget.custom.StarBar;
import com.qinggan.app.widget.custom.SwipeBackLayout;

public class ScenicDetailFragment extends UIControlBaseFragment implements INavPoiDetailView, WakeupControlMgr.WakeupControlListener{

    IFragmentStatusListener mFragmentStatusListener;

    private TextView mScenicName;
    private TextView mScenicAddress;
    private TextView mScenicDescribeText;
    private RelativeLayout mScenicDescribeLayout;
    private TextView mScenicDistance;
    private ImageView mScenicPic;
    private StarBar mScenicStar;
    private ImageView mNavi;
    private ImageView mFav;

    private ScenicBean mScenicBean;
    private SwipeBackLayout mSwipeBackLayout;
    private IntegrationCore integrationCore;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryNavDetailSession) session).registerOnShowListener(this);
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.voice_scenic_detail, container, false);

        mScenicName = root.findViewById(R.id.scenic_detail_name);
        mScenicAddress = root.findViewById(R.id.scenic_detail_address);
        mScenicDescribeText = root.findViewById(R.id.scenic_detail_description_text);
        mScenicDescribeLayout = root.findViewById(R.id.scenic_detail_description_layout);
        mScenicDistance = root.findViewById(R.id.scenic_detail_distance);
        mScenicPic = root.findViewById(R.id.scenic_detail_pic);
        mScenicStar = root.findViewById(R.id.scenic_detail_star);
        mNavi = root.findViewById(R.id.scenic_detail_navi);
        mFav = root.findViewById(R.id.scenic_detail_fav);

        mNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naviToAdrees();
            }
        });
        mFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFav();
            }
        });

        if (mFragmentStatusListener != null) {
            mFragmentStatusListener.onLoaded();
        }

        mSwipeBackLayout = new SwipeBackLayout(getActivity());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mSwipeBackLayout.setLayoutParams(params);
        mSwipeBackLayout.setBackgroundColor(Color.TRANSPARENT);
        mSwipeBackLayout.attachToFragment(this, root,
                getResources().getDimensionPixelSize(R.dimen.nav_detail_layout_top));
        mSwipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MED);

        integrationCore = IntegrationCore.getIntergrationCore(getActivity());

        return mSwipeBackLayout;
    }

    @Override
    public void onShowPoiDetail(DcsBean dcsBean, int beanType) {
        mScenicBean = (ScenicBean)dcsBean;
        mScenicName.setText(mScenicBean.getName());
        mScenicAddress.setText(mScenicBean.getAddress());
        if (TextUtils.isEmpty(mScenicBean.getDescription())) {
            mScenicDescribeLayout.setVisibility(View.GONE);
        } else {
            mScenicDescribeLayout.setVisibility(View.VISIBLE);
            mScenicDescribeText.setText(mScenicBean.getDescription());
        }
        mScenicDistance.setText(getString(R.string.scenic_distance) + mScenicBean.getDistance());
        Glide.with(getActivity().getApplicationContext()).load(mScenicBean.getImageUrl()).
                placeholder(R.drawable.default_loading).into(mScenicPic);
        mScenicStar.setStarMark(Float.parseFloat(mScenicBean.getRatingValue()));

        addContentItemList();
    }

    private void addContentItemList() {
        mUiControlItems.clear();
        mUIControlElements.clear();

        UIControlElementItem startNav = new UIControlElementItem();
        startNav.addWord(getString(R.string.start_nav));
        startNav.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_NAV);
        mUIControlElements.add(startNav);

        UIControlElementItem addFav = new UIControlElementItem();
        addFav.addWord(getString(R.string.start_fav));
        addFav.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NAV_START_COLLECTION);
        mUIControlElements.add(addFav);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void addWakeupElements() {
        WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.SCENIC_NAME_SPACE,
                0, 0, this);
    }

    @Override
    public void onItemSelected(String type, String key) {
        Log.d(TAG,"ScenicDetailFragment onItemSelected type ： " + type + "--key : " + key);

        if (!WakeupControlMgr.SCENIC_NAME_SPACE.equals(type)) {
            return;
        }

        if (WakeupControlMgr.SCENIC_START_NAV.equals(key)) {
            naviToAdrees();
        } else if (WakeupControlMgr.SCENIC_START_FAV.equals(key)) {
            addToFav();
        } else if (WakeupControlMgr.SCENIC_BACK_TO.equals(key)) {
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
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.SCENIC_NAME_SPACE);
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
        if (ConstantNavUc.NAV_START_NAV.equals(action)) {
            naviToAdrees();
        } else if (ConstantNavUc.NAV_START_COLLECTION.equals(action)) {
            addToFav();
        }
    }

    private void addToFav() {
        Log.d(TAG,"--ScenicDetailFragment-addToFav--");
        double longitude = mScenicBean.getLng();
        double latitude = mScenicBean.getLat();
        if (mScenicBean.getGeoSystem().equals("BD09MC")) {
            double[] covertGeo = CoordinateUtil.bdmc2gll(longitude, latitude);
            longitude = covertGeo[0];
            latitude = covertGeo[1];
        }

        String id = mScenicBean.getId();
        String name = mScenicBean.getName();
        String address = mScenicBean.getAddress();
        String url = mScenicBean.getUrl();
        if (id == null && url != null) {
            id = url.substring(url.indexOf("uid=") + 4, url.indexOf("/%"));
        }

        NavDataManager.getInstance().savePersonCenterCollectionDest(String.valueOf(latitude), String.valueOf(longitude),
                name, address, id);

        ToastUtil.show("加入收藏", getActivity());
    }

    private void naviToAdrees() {
        Log.d(TAG,"--ScenicDetailFragment-naviToAdrees--");
        double longitude = mScenicBean.getLng();
        double latitude = mScenicBean.getLat();
        if (mScenicBean.getGeoSystem().equals("BD09MC")) {
            double[] covertGeo = CoordinateUtil.bdmc2gll(longitude, latitude);
            longitude = covertGeo[0];
            latitude = covertGeo[1];
        }
        LatLng dest = new LatLng(latitude, longitude);
        IntegrationCore.getIntergrationCore(getContext()).planRoute(getContext(), mScenicBean.getName(), dest);

    }

}
