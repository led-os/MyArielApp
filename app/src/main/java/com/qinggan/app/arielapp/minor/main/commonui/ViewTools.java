package com.qinggan.app.arielapp.minor.main.commonui;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/***
 * 解决华为、魅族等手机虚拟案件适配的问题
 * zsq
 *
 * ****/

public class ViewTools {
    /* 关联要监听的视图
     * @param viewObserving
     **/
    public static void assistActivity(View viewObserving) {
        new ViewTools(viewObserving);
    }

    private View mViewObserved;//被监听的视图
    private int usableHeightPrevious;//视图变化前的可用高度
    private ViewGroup.LayoutParams frameLayoutParams;

    private ViewTools(View viewObserving) {
        mViewObserved = viewObserving; //给View添加全局的布局监听器
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                resetLayoutByUsableHeight(computeUsableHeight());
            }
        });
        frameLayoutParams = mViewObserved.getLayoutParams();
    }

    private void resetLayoutByUsableHeight(int usableHeightNow) { //比较布局变化前后的View的可用高度
        if (usableHeightNow != usableHeightPrevious) { //如果两次高度不一致 //将当前的View的可用高度设置成View的实际高度
            frameLayoutParams.height = usableHeightNow;
            mViewObserved.requestLayout();
            //请求重新布局
            usableHeightPrevious = usableHeightNow;
        }
    }


    private int computeUsableHeight() {
        Rect r = new Rect();
        mViewObserved.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}

