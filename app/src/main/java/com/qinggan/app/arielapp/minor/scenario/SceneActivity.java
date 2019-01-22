package com.qinggan.app.arielapp.minor.scenario;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;

public class SceneActivity extends BaseActivity {

    FragmentManager mFragmentManager;
    private ProfilesFragment mCurrentFragment;
    @Override
    protected void initView() {
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    protected void initData() {
        mFragmentManager = getSupportFragmentManager();
        updateDcsViews();

    }


    private void updateDcsViews() {
        mCurrentFragment = new ProfilesFragment();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.activity_scene_content, mCurrentFragment, "scene_fragment").addToBackStack(null).commitAllowingStateLoss();
    }
    @Override
    protected void initListener() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_scene;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
