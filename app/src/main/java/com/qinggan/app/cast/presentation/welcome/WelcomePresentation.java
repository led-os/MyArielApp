package com.qinggan.app.cast.presentation.welcome;

import android.content.Context;
import android.os.Handler;
import android.view.Display;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.PresentationManager;
import com.qinggan.app.cast.presentation.BasePresentation;
import com.qinggan.app.cast.presentation.launcher.LauncherPresentation;

/**
 * <欢迎页>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-17]
 * @see [相关类/方法]
 * @since [V1]
 */
public class WelcomePresentation extends BasePresentation {

    public WelcomePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onViewInit() {

    }

    @Override
    protected void onDataInit() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LauncherPresentation launcherPresentation = new LauncherPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
                PresentationManager.getInstance().showPresentation(launcherPresentation);
                //1s之后退出欢迎页,防止黑屏,所以加延时
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }, 1000);
            }
        }, 5 * 1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_welcome_view;
    }
}
