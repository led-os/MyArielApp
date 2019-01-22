package com.qinggan.app.cast.presentation.nav;

import android.content.Context;
import android.view.Display;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

/**
 * <导航>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class NavPresentation extends BasePresentation {
    public NavPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public NavPresentation(Context outerContext, Display display, int theme) {
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
        return R.layout.p_nav_view;
    }
}
