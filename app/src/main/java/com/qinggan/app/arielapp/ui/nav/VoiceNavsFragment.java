package com.qinggan.app.arielapp.ui.nav;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.iview.INavPoiView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.nav.QueryNavSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.arielapp.ui.adpater.VoiceRestaurantAdapter;
import com.qinggan.app.arielapp.ui.adpater.VoiceScenicAdapter;
import com.qinggan.app.arielapp.utils.WakeupControlMgr;
import com.qinggan.app.voiceapi.DataTypeConstant;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.nav.NavBean;
import com.qinggan.app.voiceapi.control.ConstantNavUc;
import com.qinggan.app.voiceapi.control.UIControlElementItem;
import com.qinggan.app.voiceapi.control.UIControlItem;

import java.util.List;


public class VoiceNavsFragment extends UIControlBaseFragment implements INavPoiView, WakeupControlMgr.WakeupControlListener{

    IFragmentStatusListener mFragmentStatusListener;

    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView mTitleView;
    private VoiceRestaurantAdapter mRestaurantAdapter = null;
    private VoiceScenicAdapter mScenicAdapter = null;

    private DcsBean mDcsBean;
    private List<DcsBean> mDcsBeanList;
    private int mBeanType;
    private String mUIControlItem;
    private int firstPositon;
    private int endPositon;
    private int listSize;
    private boolean mIsUp = false;
    private boolean isVoiceScroll = false;

    private View mBackView;

    private boolean isOnHidden = false;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryNavSession) session).registerOnShowListener(this);
    }

    @Nullable
    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "inflaterView");

        final View root = inflater.inflate(R.layout.voice_navs_frag, container, false);

        mTitleView = root.findViewById(R.id.voice_navs_title);
        mRecyclerView = root.findViewById(R.id.voice_navs_recyclerview);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mBackView = root.findViewById(R.id.voice_navs_close);
        mBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ViewTreeObserver observer = root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mFragmentStatusListener != null)
                    mFragmentStatusListener.onLoaded();
            }
        });

        return root;
    }

    @Override
    public void onShowPoi(final List<DcsBean> dcsBeanList, int beanType) {

        if (mRecyclerView == null) {
            return;
        }
        mDcsBeanList = dcsBeanList;
        mBeanType = beanType;
        switch (beanType) {
            case DataTypeConstant.SCENIC_TYPE:
                mUIControlItem = ConstantNavUc.TOUR_UI_CONTROL_ITEM;
                mTitleView.setText(R.string.nav_scenic_title);
                mRestaurantAdapter = null;
                mScenicAdapter = new VoiceScenicAdapter(dcsBeanList);
                mRecyclerView.setAdapter(mScenicAdapter);
                mScenicAdapter.setOnItemClickListener(new VoiceScenicAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        showNavItemDetail(position);
                    }
                });
                break;
            case DataTypeConstant.RESTAURANT_TYPE:
                mUIControlItem = ConstantNavUc.RESTAURANT_UI_CONTROL_ITEM;
                mTitleView.setText(R.string.nav_restaurant_title);
                mScenicAdapter= null;
                mRestaurantAdapter = new VoiceRestaurantAdapter(dcsBeanList);
                mRecyclerView.setAdapter(mRestaurantAdapter);
                mRestaurantAdapter.setOnItemClickListener(new VoiceRestaurantAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        showNavItemDetail(position);
                    }
                });
                break;
        }

        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                addContentItemList();
                addWakeupElementList();
            }
        });
    }

    private void addContentItemList() {
        Log.d(TAG,"addContentItemList");
        if (null == mDcsBeanList || mDcsBeanList.size() < 1) {
            Log.d(TAG,"addContentItemList return");
            return;
        }

        firstPositon = mLayoutManager.findFirstVisibleItemPosition();
        endPositon = mLayoutManager.findLastVisibleItemPosition();
        listSize = mDcsBeanList.size();
        Log.d(TAG,"addContentItemList firstPositon : " + firstPositon + "endPositon : " + endPositon + "listSize : " + listSize);

        if (firstPositon >= 0 && endPositon >= 0 && listSize>0 && firstPositon<listSize && endPositon<listSize ) {
//            mUIControlList = mDcsBeanList.subList(firstPositon, listSize+1);
        } else {
            Log.d(TAG,"addContentItemList size return");
            return;
        }

        mUiControlItems.clear();
        mUIControlElements.clear();


        for(int i = firstPositon; i < endPositon + 1; i++) {
            NavBean navBean = (NavBean) mDcsBeanList.get(i);
            UIControlItem uiItem = new UIControlItem();
            uiItem.setLabel(navBean.getName());
            uiItem.setIndex(i);
            String url = mFragmentHashCode + "-" + mUIControlItem + ":" + i;
            uiItem.setUrl(url);
            mUiControlItems.add(uiItem);
        }

        UIControlElementItem prePage = new UIControlElementItem();
        prePage.addWord(getString(R.string.last_page));
        prePage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(prePage);

        UIControlElementItem uiNextPage = new UIControlElementItem();
        uiNextPage.addWord(getString(R.string.next_page));
        uiNextPage.setIdentify(mFragmentHashCode + "-" + ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM);
        mUIControlElements.add(uiNextPage);

        mUIControlElements.addAll(defaultElementItems);

        addElementAndListContent();
    }

    private void addWakeupElementList() {
        WakeupControlMgr.getInstance().setElementUCWords(WakeupControlMgr.NAV_LIST_SPACE,
                firstPositon, endPositon, this);
    }


    @Override
    public void onItemSelected(String type, String key) {
        Log.e(TAG,"onItemSelected type : " + type + "--key : " + key);
        if (!WakeupControlMgr.NAV_LIST_SPACE.equals(type)) {
            return;
        }

        if (isOnHidden) {
            return;
        }

        if (WakeupControlMgr.NAVLIST_NEXT_PAGE.equals(key)) {
            if (endPositon < listSize -1) {
                isVoiceScroll = true;
                mIsUp = false;
                mRecyclerView.smoothScrollToPosition(endPositon);
            }
        } else if (WakeupControlMgr.NAVLIST_LAST_PAGE.equals(key)) {
            if (firstPositon > 0) {
                isVoiceScroll = true;
                mIsUp = true;
                mRecyclerView.smoothScrollToPosition(firstPositon);
            }
        } else if (WakeupControlMgr.NAVLIST_BACK_TO.equals(key)) {
            getActivity().onBackPressed();
        } else {
            if ("0".equals(key)) {
                showNavItemDetail(0);
            } else {
                int index = Integer.valueOf(key);
                showNavItemDetail(index - 1);
            }
        }


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG,"VoiceNavsFragment onHiddenChanged hidden : " + hidden);
        isOnHidden = hidden;
        super.onHiddenChanged(hidden);
        if (hidden) {
            WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.NAV_LIST_SPACE);
        } else {
            addWakeupElementList();
        }
    }

    private void showNavItemDetail(int posionId) {

        mDcsBean = mDcsBeanList.get(posionId);
        switch (mBeanType) {
            case DataTypeConstant.SCENIC_TYPE:
                final ScenicDetailFragment scenicDetailFragment = (ScenicDetailFragment)LocalFragmentManager.
                        getInstance().crateDcsSubFragment(getFragmentManager(), this,
                        LocalFragmentManager.FragType.SCENIC_DETAIL, null);
                scenicDetailFragment.setLoadedListener(new IFragmentStatusListener() {
                    @Override
                    public void onLoaded() {
                        scenicDetailFragment.onShowPoiDetail(mDcsBean, DataTypeConstant.SCENIC_TYPE);
                    }
                });
                break;
            case DataTypeConstant.RESTAURANT_TYPE:
                final RestaurantDetailFragment restaurantFragment = (RestaurantDetailFragment)LocalFragmentManager.
                        getInstance().crateDcsSubFragment(getFragmentManager(), this,
                        LocalFragmentManager.FragType.RESTAURANT_DETAIL, null);
                restaurantFragment.setLoadedListener(new IFragmentStatusListener() {
                    @Override
                    public void onLoaded() {
                        restaurantFragment.onShowPoiDetail(mDcsBean, DataTypeConstant.RESTAURANT_TYPE);
                    }
                });
                break;
        }
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                isVoiceScroll = false;
                addContentItemList();
                addWakeupElementList();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (!isVoiceScroll) {
                return;
            }

            int distance = 0;
            if (mIsUp) {
                int n = mLayoutManager.findLastVisibleItemPosition() - firstPositon;
                if ( 0 <= n && n < mRecyclerView.getChildCount()){
                    distance = -mRecyclerView.getChildAt(0).getBottom();
                }
            } else {
                int n = endPositon - mLayoutManager.findFirstVisibleItemPosition();
                if ( 0 <= n && n < mRecyclerView.getChildCount()){
                    distance = mRecyclerView.getChildAt(n).getTop();
                }
            }

            mRecyclerView.scrollBy(0, distance);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        isOnHidden = false;
//        addWakeupElementList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WakeupControlMgr.getInstance().clearElementUCWords(WakeupControlMgr.NAV_LIST_SPACE);
    }

    @Override
    public void onShowDetail(DcsBean dcsBean, int beanType) {

    }

    @Override
    public void onSelectItemPosition(int position) {
        showNavItemDetail(position);
    }

    @Override
    public void onSelectOtherOC(String action) {
        Log.d(TAG,"onSelectOtherOC");
        if (ConstantNavUc.NEXT_PAGE_UI_CONTROL_ITEM.equals(action)) {
            if (endPositon < listSize -1) {
                isVoiceScroll = true;
                mIsUp = false;
                mRecyclerView.smoothScrollToPosition(endPositon);
            }
        } else if (ConstantNavUc.LAST_PAGE_UI_CONTROL_ITEM.equals(action)) {
            if (firstPositon > 0) {
                isVoiceScroll = true;
                mIsUp = true;
                mRecyclerView.smoothScrollToPosition(firstPositon);
            }
        }
    }
}
