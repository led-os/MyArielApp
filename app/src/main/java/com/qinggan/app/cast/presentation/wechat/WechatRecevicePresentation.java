package com.qinggan.app.cast.presentation.wechat;

import android.content.Context;
import android.view.Display;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

import org.greenrobot.eventbus.EventBus;

/**
 * <微信>
 * 微信接收消息界面
 */

public class WechatRecevicePresentation extends BasePresentation {
    public WechatRecevicePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public WechatRecevicePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onViewInit() {

    }

    @Override
    protected void onDataInit() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_wechat_receive_main_view;
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
