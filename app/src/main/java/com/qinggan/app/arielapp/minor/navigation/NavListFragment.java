package com.qinggan.app.arielapp.minor.navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;

/**
 * <导航地址列表>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 18-11-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class NavListFragment extends UIControlBaseFragment {

    public static NavListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        NavListFragment fragment = new NavListFragment();
        fragment.setArguments(args);
        return fragment;
    }
    

    @Override
    public void onSelectItemPosition(int position) {

    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {

    }

    @Override
    public void init(IASRSession session) {

    }
}
