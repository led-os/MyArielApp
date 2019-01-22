package com.qinggan.app.cast;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.cast.presentation.BasePresentation;
import com.qinggan.app.cast.presentation.allapp.AllappPresentation;
import com.qinggan.app.cast.presentation.launcher.LauncherPresentation;
import com.qinggan.app.cast.presentation.welcome.WelcomePresentation;
import com.qinggan.app.cast.window.CastWindowManager;
import com.qinggan.app.cast.window.WindowViewType;
import com.qinggan.app.cast.window.view.DefaultWindowView;
import com.qinggan.qinglink.cast.source.configuration.VideoConfiguration;

import java.util.Stack;

/**
 * <车机投屏Application>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public class PresentationManager {

    private String TAG = PresentationManager.class.getSimpleName();

    private static volatile PresentationManager instance;

    public static PresentationManager getInstance() {
        if (null == instance) {
            synchronized (PresentationManager.class) {
                if (null == instance) {
                    instance = new PresentationManager();
                }
            }
        }
        return instance;
    }

    Stack<BasePresentation> presentations;
    private DisplayManager mDisplayManager;
    private VirtualDisplay mVirtualDisplay;
    private Display mDisplay;
    //------------life cycle----------

    /**
     * 创建,初始化
     */
    public void onCreate(Context appContext, Surface surface, VideoConfiguration config) {
        Log.d(TAG, "onCreate---");
        onDestory();
        Log.d(TAG, "onCreate---11");
        presentations = new Stack();
        //createVirtualDisplay
        DisplayMetrics mMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(mMetrics);
        int width = getVideoSize(1920);
        int height = getVideoSize(720);
        int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION;
        flags |= DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY;
        mDisplayManager = (DisplayManager) appContext.getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);
//        mVirtualDisplay = mDisplayManager.createVirtualDisplay("QingCast",
//                width, height, mMetrics.densityDpi, surface, flags);
        mVirtualDisplay = mDisplayManager.createVirtualDisplay("QingCast",
                width, height, 160, surface, flags);
    }

    private DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {

        private boolean mNewDisplayAdded = false;
        private int mCurrentDisplayId = -1;

        @Override
        public void onDisplayAdded(int i) {
            Log.d(TAG, "onDisplayAdded id=" + i);
            if (!mNewDisplayAdded && mCurrentDisplayId == -1) {
                mNewDisplayAdded = true;
                mCurrentDisplayId = i;
            }
        }

        @Override
        public void onDisplayRemoved(int i) {
            Log.d(TAG, "onDisplayRemoved id=" + i);
            if (mCurrentDisplayId == i) {
                Log.d(TAG, "onDisplayRemoved current display remove");
                mNewDisplayAdded = false;
                mCurrentDisplayId = -1;
                onDestory();
            }
        }

        @Override
        public void onDisplayChanged(int i) {
            Log.d(TAG, "onDisplayChanged id=" + i + ", mCurrentDisplayId: " + mCurrentDisplayId);
            if (mCurrentDisplayId == i) {
                if (mNewDisplayAdded) {
                    // create a presentation
                    mNewDisplayAdded = false;
                    mDisplay = mDisplayManager.getDisplay(i);
                    Log.d(TAG, "onDisplayChanged mDisplay:" + mDisplay);
                    if (null == mDisplay) return;
                    WelcomePresentation welcomePresentation = new WelcomePresentation(ArielApplication.getApp(), mDisplay);
                    welcomePresentation.show();
                }
            }
        }
    };


    private static int getVideoSize(int size) {
        int multiple = (int) Math.ceil(size / 16.0);
        return multiple * 16;
    }

    /**
     * 销毁
     */
    public synchronized void onDestory() {
        Log.d(TAG, "onDestory---");
        if (null != presentations) {
            clearAllPresentation();
            presentations = null;
        }
        if (null != mVirtualDisplay) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        mDisplay = null;
    }

    public Display getDisplay() {
        return mDisplay;
    }


    //-----------stack method--------
    public void showPresentation(BasePresentation presentation) {
        Log.d(TAG, "showPresentation presentation:" + presentation);
        if (containsPresentation(presentation.getClass())) {
            Log.d(TAG, "showPresentation containsPresentation");
            backToPresentation(presentation.getClass());
        } else {
            Log.d(TAG, "showPresentation not containsPresentation");
            if (null != presentations && !presentations.isEmpty()) {
                Log.d(TAG, "pause top Presentation");
                presentations.peek().onPause();
            }
            presentation.show();
            addOrRemoveMenuView(presentation);
        }
    }


    /**
     * 加菜单
     *
     * @param presentation
     */
    private void addOrRemoveMenuView(BasePresentation presentation) {
        Log.d(TAG, "addMenuView:" + presentation);
        //应用加菜单栏
        if (needShowMenu(presentation)) {
            Log.d(TAG, "addMenuView:need show menu");
            if (CastWindowManager.getInstance().containWindowView(WindowViewType.DEFAULT)) {
                Log.d(TAG, "addMenuView:need show menu find cast view");
                DefaultWindowView defaultWindowView = (DefaultWindowView) (CastWindowManager.getInstance().getCastWindowView(WindowViewType.DEFAULT));
                defaultWindowView.showOrHideMenu(true);
            }
        } else {
            Log.d(TAG, "addMenuView:need hide menu");
            if (CastWindowManager.getInstance().containWindowView(WindowViewType.DEFAULT)) {
                Log.d(TAG, "addMenuView:need hide menu find cast view");
                DefaultWindowView defaultWindowView = (DefaultWindowView) (CastWindowManager.getInstance().getCastWindowView(WindowViewType.DEFAULT));
                defaultWindowView.showOrHideMenu(false);
            }
        }
    }

    /**
     * 是否需要显示悬浮菜单
     *
     * @param presentation
     * @return
     */
    private boolean needShowMenu(BasePresentation presentation) {
        return !(presentation instanceof WelcomePresentation || presentation instanceof LauncherPresentation || presentation instanceof AllappPresentation);
    }

    public void addPresentation(BasePresentation presentation) {
        if (null != presentation && null != presentations)
            presentations.add(presentation);
    }

    public void removePresentation(BasePresentation presentation) {
        if (null != presentation && null != presentations)
            presentations.remove(presentation);
    }

    public boolean containsPresentation(Class cls) {
        if (null == presentations) return false;
        for (BasePresentation presentation : presentations) {
            if (presentation.getClass() == cls)
                return true;
        }
        return false;
    }

    /**
     * 获取顶部presentation
     *
     * @return
     */
    public BasePresentation getTopPresentation() {
        return presentations.peek();
    }

    /**
     * 移除顶部Presentation
     */
    public void removeTopPresentation() {
        BasePresentation presentation = presentations.pop();
        presentation.dismiss();
        BasePresentation presentation1 = presentations.peek();
        if (null != presentation1) {
            presentation1.onResume();
            addOrRemoveMenuView(presentation1);
        }
    }

    /**
     * 返回指定的Presentation
     *
     * @param cls
     */
    public void backToPresentation(Class cls) {
        Log.d(TAG, "backToPresentation cls:" + cls.getName());
        if (null != presentations) {
            while (presentations.size() != 0)
                if (presentations.peek().getClass() == cls) {
                    Log.d(TAG, "backToPresentation find target cls");
                    presentations.peek().onResume();
                    addOrRemoveMenuView(presentations.peek());
                    break;
                } else {
                    BasePresentation last = presentations.pop();
                    Log.d(TAG, "backToPresentation last:" + last.getClass().getName() + "  pop");
                    last.dismiss();
                }
        }
    }

    /**
     * 清空所有
     */
    private void clearAllPresentation() {
        if (null != presentations) {
            while (presentations.size() != 0) {
                BasePresentation last = presentations.pop();
                last.dismiss();
            }
        }
    }
}
