package com.qinggan.app.cast.presentation.lock;

import android.content.Context;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

/**
 * <锁屏>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-17]
 * @see [相关类/方法]
 * @since [V1]
 */
public class LockPresentation extends BasePresentation {
    public LockPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected int getWindowType() {
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            return WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
            return WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
    }

    @Override
    protected void onViewInit() {

    }

    @Override
    protected void onDataInit() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_lock_view;
    }
}
