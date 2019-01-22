package com.qinggan.app.cast.touch;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.qinggan.app.cast.PresentationManager;
import com.qinggan.app.cast.window.CastWindowManager;
import com.qinggan.app.cast.window.WindowViewType;
import com.qinggan.app.cast.window.view.CastWindowView;

/**
 * <车机点击事件分发器>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-10]
 * @see [相关类/方法]
 * @since [V1]
 */
public class CastTouchDispatch {

    /**
     * 当手势的y在该值以内,则响应下拉控制中心
     */
    public static final int CONTROL_RSP_Y = 150;

    private static String TAG = "CastTouchDispatch";
    /**
     * ACTION_DOWN的时间戳
     */
    private static long downTime;

    private static View targetView;

    public static void doEventDown(float x, float y) {
        if (targetView == null) {
            getTargetView(x, y);
            downTime = SystemClock.uptimeMillis();
            MotionEvent motionEvent = MotionEvent.obtain(downTime, downTime,
                    MotionEvent.ACTION_DOWN, x, y, 0);
            targetView.dispatchTouchEvent(motionEvent);
        } else {
            doEventMove(x, y);
        }
    }

    private static View getTargetView(float x, float y) {
        // TODO: 19-1-19 找到需要响应手势的view
        if (y < CONTROL_RSP_Y) {
            Log.d(TAG, "y < 50 targetView is defaultWindowView");
            targetView = CastWindowManager.getInstance().getCastWindowView(WindowViewType.DEFAULT).getRootView();
            if (CastWindowManager.getInstance().isHUIShow()) {
                Log.d(TAG, "y < 50 hui is show,need remove hui view");
                CastWindowManager.getInstance().dismissWindowView(WindowViewType.HUI);
            }
        } else if (CastWindowManager.getInstance().isHUIShow()) {
            //hui显示
            Log.d(TAG, "hui show");
            CastWindowView castWindowView = CastWindowManager.getInstance().getCastWindowView(WindowViewType.HUI);
            if (castWindowView.touchInWindowArea(x, y)) {
                Log.d(TAG, "x < castWindowView.getWidth() targetView is hui view");
                targetView = castWindowView.getRootView();
            } else {
                Log.d(TAG, "x >= castWindowView.getWidth() remove hui view");
                CastWindowManager.getInstance().dismissWindowView(WindowViewType.HUI);
                Log.d(TAG, "x >= castWindowView.getWidth() targetView is top Presentation rootview");
                targetView = PresentationManager.getInstance().getTopPresentation().getRootView();
            }
        } else {
            Log.d(TAG, "check defaultview or presentation");
            CastWindowView defaultCastView = CastWindowManager.getInstance().getCastWindowView(WindowViewType.DEFAULT);
            if (defaultCastView.touchInWindowArea(x, y)) {
                Log.d(TAG, "in defaultview");
                targetView = defaultCastView.getRootView();
            } else {
                Log.d(TAG, "in presentation");
                targetView = PresentationManager.getInstance().getTopPresentation().getRootView();
            }
        }
        Log.d(TAG, "did not find targetView");
        return null;
    }

    public static void doEventMove(float x, float y) {
        MotionEvent moveEvent = MotionEvent.obtain(downTime, System.currentTimeMillis(),
                MotionEvent.ACTION_MOVE, x, y, 0);
        targetView.dispatchTouchEvent(moveEvent);
    }

    public static void doEventUp(float x, float y) {
        MotionEvent motionEvent = MotionEvent.obtain(downTime, System.currentTimeMillis(),
                MotionEvent.ACTION_UP, x, y, 0);
        targetView.dispatchTouchEvent(motionEvent);
        targetView = null;
    }

    public static void doOtherEvent(MotionEvent motionEvent) {
        targetView.dispatchTouchEvent(motionEvent);
        targetView = null;
    }
}
