package com.qinggan.app.cast.window.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.qinggan.app.cast.window.CastWindowManager;
import com.qinggan.app.cast.window.CastWindowViewPosition;
import com.qinggan.app.cast.window.WindowViewType;

/**
 * <投屏WindowManager弹出窗View构造对象>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-12]
 * @see [相关类/方法]
 * @since [V1]
 */
public class CastWindowView {
    protected String LOG_TAG = getClass().getSimpleName();
    private int width;

    private int height;

    private CastWindowViewPosition position = CastWindowViewPosition.TOP;

    private int layoutId;

    private long timeout;

    private boolean cancelable;

    protected View rootView;

    private WindowViewType tag;

    public CastWindowView() {
    }

    public CastWindowView(WindowViewType tag) {
        this.tag = tag;
    }

    public CastWindowView setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public CastWindowView setViewPosition(CastWindowViewPosition position) {
        this.position = position;
        return this;
    }

    public CastWindowView setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public CastWindowView setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public CastWindowView setContentView(int layoutId) {
        this.layoutId = layoutId;
        this.rootView = LayoutInflater.from(CastWindowManager.getInstance().getContext()).inflate(layoutId, null);
        onViewInit();
        return this;
    }

    public CastWindowView setContentView(View view) {
        this.rootView = view;
        onViewInit();
        return this;
    }


    public void onViewInit() {
        Log.d(LOG_TAG, "---onViewInit---");
    }

    public void onViewRemove() {
        Log.d(LOG_TAG, "---onViewRemove---");
    }

    /**
     * 是否在当前view的点击区域
     */
    public boolean touchInWindowArea(float x, float y) {
        Log.d(LOG_TAG, "---touchInWindowArea---");
        return false;
    }
    //=====getter======


    public WindowViewType getTag() {
        return tag;
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }


    public CastWindowViewPosition getPosition() {
        return position;
    }


    public int getLayoutId() {
        return layoutId;
    }


    public long getTimeout() {
        return timeout;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public View getRootView() {
        return rootView;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPosition(CastWindowViewPosition position) {
        this.position = position;
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    public void setTag(WindowViewType tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "CastWindowView (tag:" + tag + " width:" + width + " height:" + height + " position:" + position + " timeout:" + timeout + " cancelable:" + cancelable + ")";
    }
}
