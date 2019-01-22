package com.qinggan.app.arielapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.SessionFactory;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.LocalFragmentManager;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;

public class VoiceDcsActivity extends BaseActivity {

    private final static String TAG = "VoiceDirectiveActivity";

    FragmentManager mFragmentManager;
    public AbstractBaseFragment mCurrentFragment;
    LinearLayout main_content_view;
    private View rootView;
    private Context mContext = ArielApplication.getApp();
    private DcsDataWrapper mDcsWrapper;

    @Override
    protected void initView() {
        getSwipeBackLayout().setEnableGesture(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mDcsWrapper = intent.getParcelableExtra("dcsWrapper");
        updateDcsViews();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        mDcsWrapper = getIntent().getParcelableExtra("dcsWrapper");
        mFragmentManager = getSupportFragmentManager();
        updateDcsViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_voice_dcs;
    }

    private void updateDcsViews() {
        Log.i(TAG, "updateDcsViews");
        if (mDcsWrapper == null) {
            return;
        }

        final IASRSession session = SessionFactory.getInstance().obtain(mDcsWrapper.getType());
        AbstractBaseFragment mfragment = LocalFragmentManager.getInstance().createDcsFragment(mFragmentManager, mDcsWrapper, session);
        if (mfragment == null) {
            Log.i(TAG, "null fragment");
            return;
        }
        mCurrentFragment = mfragment;
        mCurrentFragment.setLoadedListener(new IFragmentStatusListener() {
            @Override
            public void onLoaded() {
                Log.i(TAG, "handle session");
                session.handleASRFeedback(mDcsWrapper);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (mFragmentManager.getFragments().size() == 1) {
            this.finish();
        } else {
            mFragmentManager.popBackStack();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
