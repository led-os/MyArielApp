package com.qinggan.app.cast.presentation.wechat;

import android.content.Context;
import android.view.Display;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

/**
 * <微信>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class WechatPresentation extends BasePresentation {
    public WechatPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public WechatPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onViewInit() {

    }

    @Override
    protected void onDataInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_wechat_view;
    }
}
