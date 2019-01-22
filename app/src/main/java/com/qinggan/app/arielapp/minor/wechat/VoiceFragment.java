package com.qinggan.app.arielapp.minor.wechat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.scenario.ProfilesFragment;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;

/**
 * Created by yang
 * Time on 2018/11/6.
 * Function  微信语音
 */
public class VoiceFragment extends AbstractBaseFragment {

    View mRootView;
    Button botton;
    private Context mContext = ArielApplication.getApp();
    private IFragmentStatusListener mFragmentStatusListener;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_wecaht_voice, container, false);
        return mRootView;
    }

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        initView();
    }

    private void initView() {
        botton = mRootView.findViewById(R.id.ask_button);

        botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextFragment();
            }
        });
    }

    void nextFragment() {
        Toast.makeText(mContext, "切换到下一个fragment中", Toast.LENGTH_SHORT).show();
        //开启事务跳转
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
//        String textItem =  ((TextView) mRootView).getText().toString();
        ProfilesFragment profileFragment = new ProfilesFragment();
        Bundle bundle = new Bundle();
        bundle.putString("productTitle", "fragment参数");
        profileFragment.setArguments(bundle);

//        transaction
//                .addToBackStack(null)  //将当前fragment加入到返回栈中
//                .replace(R.id.fragment_container,profileFragment)
//                .show(profileFragment)
//                .commit();
    }
}

