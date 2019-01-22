package com.qinggan.app.cast.presentation.vehicle;

import android.content.Context;
import android.view.Display;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

/**
 * <描述>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class VehiclePresentation extends BasePresentation {
    public VehiclePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onViewInit() {

    }

    @Override
    protected void onDataInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_vehicle_view;
    }
}
