package com.qinggan.app.arielapp.minor.radio;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.BaseActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.WheelControl.Listener.RadioControlListener;
import com.qinggan.app.arielapp.WheelControl.WheelControl;

public class FMActivity extends BaseActivity {

    private final static String TAG = "FMActivity";

    FragmentManager mFragmentManager;
    public FMFragment mCurrentFragment;
    private RadioControlListener radioControlListener = null;
    private WheelControl wheelControl = null;

    @Override
    protected void initView() {
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateDcsViews();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mFragmentManager = getSupportFragmentManager();
        updateDcsViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fm;
    }

    private void updateDcsViews() {
        mCurrentFragment = new FMFragment();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.activity_fm_content, mCurrentFragment, "fm_fragment").addToBackStack(null).commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        initRadioControlListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
        radioControlListener = null;
        if (wheelControl != null) {
            wheelControl.setRadioListener(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initRadioControlListener() {
        if (wheelControl == null) {
            wheelControl = ArielApplication.getWheelControlManager();
        }
        if (radioControlListener != null) radioControlListener = null;
        radioControlListener = new RadioControlListener() {
            @Override
            public void previous() {
                mCurrentFragment.onPrevious();
            }

            @Override
            public void next() {
                mCurrentFragment.onNext();
            }
        };
        wheelControl.setRadioListener(radioControlListener);
    }

}
