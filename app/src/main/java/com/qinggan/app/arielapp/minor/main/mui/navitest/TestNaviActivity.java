package com.qinggan.app.arielapp.minor.main.mui.navitest;

import android.support.v4.app.FragmentManager;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;

public class TestNaviActivity extends BaseActivity {
    FragmentManager mFragmentManager;
    LinearLayout main_content_view;

    @Override
    protected void initView() {
        initMainViewItems();
    }

    private void initMainViewItems() {
        mFragmentManager = getSupportFragmentManager();
        main_content_view = (LinearLayout) findViewById(R.id.main_content_view);
    }

    @Override
    protected void initData() {
        AbstractBaseFragment fragment = new TestNaviMainFragment();
        mFragmentManager.beginTransaction().replace(R.id.main_content_view, fragment, "main").addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test_navi;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
