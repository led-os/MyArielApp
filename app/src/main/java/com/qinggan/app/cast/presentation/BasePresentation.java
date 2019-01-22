package com.qinggan.app.cast.presentation;

import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.qinggan.app.cast.PresentationManager;

/**
 * <基础Presentation>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public abstract class BasePresentation extends Presentation {

    protected String TAG = getClass().getSimpleName();

    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public BasePresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    protected View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getWindowType() != -1)
            getWindow().setType(getWindowType());
        rootView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null, false);
        setContentView(rootView);
        onViewInit();
        onDataInit();
    }

    protected abstract void onViewInit();

    protected abstract void onDataInit();

    protected abstract int getLayoutId();

    protected int getWindowType() {
        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            return -1;
        } else
            return WindowManager.LayoutParams.TYPE_PHONE;
    }

    /**
     * 此处onResume的意义与Activity的意义不同
     * 当从堆栈中返回到这个Presentation才会调用
     */
    public void onResume() {
        Log.d(TAG, "--onResume--");
    }

    /**
     * 此处onPause的意义与Activity的意义不同
     * 当打开一个新的Presentation,原来堆栈中顶部的Presentation会调用
     */
    public void onPause() {
        Log.d(TAG, "--onPause--");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        PresentationManager.getInstance().addPresentation(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        PresentationManager.getInstance().removePresentation(this);
    }

    public View getRootView() {
        return rootView;
    }
}
