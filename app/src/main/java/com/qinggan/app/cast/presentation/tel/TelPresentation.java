package com.qinggan.app.cast.presentation.tel;

import android.content.Context;
import android.view.Display;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

/**
 * <电话>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-14]
 * @see [相关类/方法]
 * @since [V1]
 */
public class TelPresentation extends BasePresentation {
    public TelPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public TelPresentation(Context outerContext, Display display, int theme) {
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
        return R.layout.p_tel_view;
    }
}
