package com.qinggan.app.cast.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.qinggan.app.cast.window.view.CastWindowView;
import com.qinggan.app.cast.window.view.DefaultWindowView;

import java.util.HashMap;
import java.util.Iterator;

/**
 * <投屏WindowManager类,统一管理弹窗>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-12]
 * @see [相关类/方法]
 * @since [V1]
 */
public class CastWindowManager {

    private static final String TAG = CastWindowManager.class.getSimpleName();

    private Context mContext;
    private WindowManager mWindowManager;
    private Handler mainHandler;
    private HashMap<WindowViewType, CastWindowView> windowViews;

    private static volatile CastWindowManager instance;

    public static CastWindowManager getInstance() {
        if (null == instance) {
            synchronized (CastWindowManager.class) {
                if (null == instance)
                    instance = new CastWindowManager();
            }
        }
        return instance;
    }

    public void onCreate(Context context, WindowManager windowManager) {
        Log.d(TAG, "--onCreate--");
        mContext = context;
        mWindowManager = windowManager;
        windowViews = new HashMap<>();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 释放
     */
    public void onDestory() {
        Log.d(TAG, "--onDestory--");
        mContext = null;
        mWindowManager = null;
        windowViews.clear();
        windowViews = null;
        mainHandler = null;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 显示CastWindowView
     *
     * @param castWindowView
     */
    public void showCastWindowView(final CastWindowView castWindowView) {
        Log.d(TAG, "showCastWindowView:" + castWindowView);
        if (null == mWindowManager) {
            Log.d(TAG, "showCastWindowView:null == mWindowManager");
            return;
        }
        if (containWindowView(castWindowView.getTag())) {
            Log.d(TAG, "has containWindowView");
            castWindowView.onViewRemove();
            return;
        }
        if (castWindowView.getTag() == WindowViewType.HUI) {
            Log.d(TAG, "show hui window need hide defaultview");
            DefaultWindowView defaultWindowView = (DefaultWindowView) getCastWindowView(WindowViewType.DEFAULT);
            if (null != defaultWindowView) {
                defaultWindowView.hideControlCenter();
            }
        }
        //add windowView
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(castWindowView.getWidth() == -1 ? WindowManager.LayoutParams.MATCH_PARENT : castWindowView.getWidth(),
                castWindowView.getHeight(),
                WindowManager.LayoutParams.TYPE_TOAST,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT);
        switch (castWindowView.getPosition()) {
            case TOP:
                params.gravity = Gravity.TOP;
                break;
            case LEFT:
                params.gravity = Gravity.LEFT;
                break;
            case BOTTOM:
                params.gravity = Gravity.BOTTOM;
                break;
            case BOTTOM_LEFT:
                params.gravity = Gravity.BOTTOM | Gravity.LEFT;
                break;
        }
        params.type = Build.VERSION.SDK_INT >= 26 ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mWindowManager.addView(castWindowView.getRootView(), params);

        windowViews.put(castWindowView.getTag(), castWindowView);
        if (castWindowView.getTimeout() > 0) {
            //移除定时view
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "showCastWindowView timeout need remove view");
                    mWindowManager.removeViewImmediate(castWindowView.getRootView());
                    castWindowView.onViewRemove();
                    windowViews.remove(castWindowView.getTag());
                }
            }, castWindowView.getTimeout());
        }
    }

    /**
     * 知否已经包含指定类型的view
     *
     * @param type
     * @return
     */
    public boolean containWindowView(WindowViewType type) {
        return windowViews.containsKey(type) && null != windowViews.get(type);
    }

    /**
     * HUI消息是否显示了
     *
     * @return
     */
    public boolean isHUIShow() {
        return containWindowView(WindowViewType.HUI);
    }


    public CastWindowView getCastWindowView(WindowViewType type) {
        return windowViews.get(type);
    }

    /**
     * 删除弹出的view
     *
     * @param windowViewType
     */
    public void dismissWindowView(WindowViewType windowViewType) {
        Log.d(TAG, "dismissWindowView windowViewType:" + windowViewType);
        if (null == mWindowManager) {
            Log.d(TAG, "dismissWindowView:null == mWindowManager");
            return;
        }
        if (containWindowView(windowViewType)) {
            Log.d(TAG, "dismissWindowView:containWindowView");
            CastWindowView castWindowView = windowViews.get(windowViewType);
            mWindowManager.removeViewImmediate(castWindowView.getRootView());
            castWindowView.onViewRemove();
            windowViews.remove(windowViewType);
        }
    }

    /**
     * 触摸了CastWindowView 之外的区域,需要把能取消的,都取消了
     */
    public void dismissByTouchWindow() {
        Log.d(TAG, "dismissByTouchWindow");
        if (null == mWindowManager) {
            Log.d(TAG, "dismissByTouchWindow:null == mWindowManager");
            return;
        }
        try {
            Iterator<WindowViewType> iterator = windowViews.keySet().iterator();
            while (iterator.hasNext()) {
                WindowViewType windowViewType = iterator.next();
                CastWindowView castWindowView = windowViews.get(windowViewType);
                if (null != castWindowView && castWindowView.isCancelable()) {
                    Log.d(TAG, "dismissByTouchWindow find fit castWindowView");
                    mWindowManager.removeViewImmediate(castWindowView.getRootView());
                    castWindowView.onViewRemove();
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "dismissByTouchWindow error:" + e.getMessage());
        }
    }

    /**
     * 根据CastWindowView 的tag 更新view布局
     *
     * @param windowViewType
     * @param width
     */
    public void updateCastWindowViewLayout(WindowViewType windowViewType, int width) {
        Log.d(TAG, "updateCastWindowViewLayout windowViewType:" + windowViewType);

        if (containWindowView(windowViewType)) {
            Log.d(TAG, "dismissWindowView:containWindowView");
            CastWindowView castWindowView = windowViews.get(windowViewType);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) castWindowView.getRootView().getLayoutParams();
            params.width = width;
            castWindowView.setWidth(width);
            mWindowManager.updateViewLayout(castWindowView.getRootView(), params);
        }
    }

}
