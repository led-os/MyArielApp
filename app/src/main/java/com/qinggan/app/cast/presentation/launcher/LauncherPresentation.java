package com.qinggan.app.cast.presentation.launcher;

import android.content.Context;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.PresentationManager;
import com.qinggan.app.cast.presentation.BasePresentation;
import com.qinggan.app.cast.presentation.allapp.AllappPresentation;
import com.qinggan.app.cast.presentation.lock.LockPresentation;
import com.qinggan.app.cast.window.CastWindowManager;
import com.qinggan.app.cast.window.view.CastWindowView;
import com.qinggan.app.cast.window.view.DefaultWindowView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <首页>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public class LauncherPresentation extends BasePresentation implements View.OnClickListener {

    public LauncherPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public LauncherPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    //allApp
    private Button allAppBtn;

    @Override
    protected void onViewInit() {
        CastWindowManager.getInstance().onCreate(getContext(), getWindow().getWindowManager());
        allAppBtn = rootView.findViewById(R.id.allapp);
        allAppBtn.setOnClickListener(this);
    }

    @Override
    protected void onDataInit() {
        EventBus.getDefault().register(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //语音
                CastWindowView voiceWindowView = new DefaultWindowView();
                CastWindowManager.getInstance().showCastWindowView(voiceWindowView);
            }
        }, 500);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_launcher_view;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        CastWindowManager.getInstance().onDestory();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAllWakeUpEvent(String event) {
        if (event.equals("allApp")) {
            allAppBtn.performClick();
        } else if (event.equals("pop")) {
            PresentationManager.getInstance().removeTopPresentation();
        } else if (event.equals("lock")) {
            LockPresentation lockPresentation = new LockPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
            PresentationManager.getInstance().showPresentation(lockPresentation);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == allAppBtn) {
            //显示allapp
            AllappPresentation allappPresentation = new AllappPresentation(ArielApplication.getApp(), PresentationManager.getInstance().getDisplay());
            PresentationManager.getInstance().showPresentation(allappPresentation);
        }
    }
}
