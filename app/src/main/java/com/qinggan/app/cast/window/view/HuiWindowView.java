package com.qinggan.app.cast.window.view;

import android.util.Log;
import android.view.View;

import com.qinggan.app.cast.window.WindowViewType;

/**
 * <hui弹窗消息>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-21]
 * @see [相关类/方法]
 * @since [V1]
 */
public class HuiWindowView extends CastWindowView {

    public HuiWindowView() {
        super(WindowViewType.HUI);
    }

    private int[] viewPosition = new int[]{0, 0, 0, 0};

    @Override
    public void onViewInit() {
        super.onViewInit();
        rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Log.d(LOG_TAG, "onLayoutChange");
                int[] location = new int[2];
                rootView.getLocationOnScreen(location);
                int viewLeft = location[0];
                int viewTop = location[1];
                int viewRight = viewLeft + rootView.getMeasuredWidth();
                int viewBottom = viewTop + rootView.getMeasuredHeight();
                viewPosition = new int[]{viewLeft, viewTop, viewRight, viewBottom};
            }
        });
    }

    @Override
    public boolean touchInWindowArea(float x, float y) {
        if (x >= viewPosition[0] && x <= viewPosition[2] && y >= viewPosition[1] && y <= viewPosition[3]) {
            Log.d(LOG_TAG, "in hui area");
            return true;
        }
        return super.touchInWindowArea(x, y);
    }

    @Override
    public void onViewRemove() {
        super.onViewRemove();
    }
}
