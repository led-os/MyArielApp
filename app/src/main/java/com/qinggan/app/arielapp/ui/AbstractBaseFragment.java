package com.qinggan.app.arielapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qinggan.app.arielapp.session.IASRSession;
import com.umeng.analytics.MobclickAgent;

public abstract class AbstractBaseFragment extends Fragment implements View.OnClickListener{

    protected View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflaterView(inflater,container,savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public abstract void setLoadedListener(IFragmentStatusListener fragmentStatus);

    public abstract void init(IASRSession session);

    protected void initWidget(View parentView) {
    }

    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        initWidget(v);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }
}