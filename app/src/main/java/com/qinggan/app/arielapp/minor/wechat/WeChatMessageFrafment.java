package com.qinggan.app.arielapp.minor.wechat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.TestAarActivity;
import com.qinggan.app.arielapp.minor.wechat.utils.WeChatUtils;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.ui.AbstractBaseFragment;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.utils.AccessibilityUtil;
import com.qinggan.app.virtualclick.Bean.ActionBean;
import com.qinggan.app.virtualclick.sdk.PateoVirtualSDK;

/**
 * Created by yang
 * Time on 2018/11/8.
 * Function  微信消息
 */
public class WeChatMessageFrafment extends AbstractBaseFragment {

    View mView;
    private Context mContext = ArielApplication.getApp();

    private IFragmentStatusListener mFragmentStatusListener;

    private Button mReplyBtn, mIgnoreBtn;
    private NotificationBean mBean;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wechat_message, container, false);
        return mView;
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
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        WeChatUtils mWeChatUtils = new WeChatUtils();
//        mBean = mWeChatUtils.handleNotificationStrToBean(
//                ((MainActivity) getActivity()).getNotification());

        mReplyBtn = mView.findViewById(R.id.ask_button);
        mReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doAction();
            }
        });

        mIgnoreBtn = mView.findViewById(R.id.ignore_button);
        mIgnoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getActivity(), TestAarActivity.class);
                startActivity(mIntent);
            }
        });
    }

    void nextFragment() {
        //开启事务跳转
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
//        String textItem =  ((TextView) mRootView).getText().toString();
        VoiceFragment profileFragment = new VoiceFragment();
        Bundle bundle = new Bundle();
        bundle.putString("productTitle", "fragment参数");
        profileFragment.setArguments(bundle);

//        transaction
//                .addToBackStack(null)  //将当前fragment加入到返回栈中
//                .replace(R.id.fragment_container,profileFragment)
//                .show(profileFragment)
//                .commit();
    }


    private void doAction() {
        if (!AccessibilityUtil.isAccessibilitySettingsOn(getActivity())) {
            this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }


        ActionBean actionBean = new ActionBean();
        actionBean.setActionCode(1001);
        actionBean.setAppName("微信");
        actionBean.setAddressee(mBean.getSender() != null ? mBean.getSender() : "老兄");
        actionBean.setAction("测试语句");

//        PateoVirtualSDK.doAction(actionBean, (MainActivity) getActivity());
    }
}



