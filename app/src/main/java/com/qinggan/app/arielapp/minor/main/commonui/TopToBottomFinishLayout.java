package com.qinggan.app.arielapp.minor.main.commonui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;
import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;

import org.greenrobot.eventbus.EventBus;

/***
 *
 * 从上向下滑结束Activity
 *
 * @author zsq
 *
 */
public class TopToBottomFinishLayout extends RelativeLayout {
    /** * TopToBottomFinishLayout布局的父布局 */
    private ViewGroup mParentView;
    /** * 滑动的最小距离 */
    private int mTouchSlop;
    /** * 按下点的X坐标 */
    private int downX;
    /** * 按下点的Y坐标 */
    private int downY;
    /** * 临时存储X坐标 */
    private int tempY;
    /** * 滑动类 */
    private Scroller mScroller;
    /** * TopToBottomFinishLayout的宽度 */
    private int viewHeight;
    private boolean isSilding;
    private OnFinishListener onFinishListener;
    private boolean isFinish;

    private Context context;
    private LocalStorageTools localStorageTools;

    public TopToBottomFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
        public TopToBottomFinishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
        this.context=context;
    }

    /** * 事件拦截操作 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = tempY = (int) ev.getRawY();
                return false;
            case MotionEvent.ACTION_MOVE:
                localStorageTools = new LocalStorageTools(context);//初始化本地储存
                int index= localStorageTools.getInteger("pageIndex");
                String isTop= localStorageTools.getString("isTop");
                System.out.println("isTop=-=="+isTop);
                if (isTop.equals("no")&&index==1) {//智能推荐模式并且recyclerview没滑到顶部
                    return false;
                } else {

                    int moveY = (int) ev.getRawY();
                    // 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
                    if (Math.abs(moveY - downY) > mTouchSlop && Math.abs((int) ev.getRawX() - downX) < mTouchSlop) {
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                return false;

            default:
                return super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) event.getRawY();
                // 触摸点相对于屏幕的位置
                int deltaY = tempY - moveY; tempY = moveY;
                if (Math.abs(moveY - downY) > mTouchSlop && Math.abs((int) event.getRawX() - downX) < mTouchSlop) {
                    isSilding = true;
                }
                if (moveY - downY >= 0 && isSilding) {
                    mParentView.scrollBy(0, deltaY);
                }
                break;
                case MotionEvent.ACTION_UP:
                    isSilding = false;

                if (mParentView.getScrollY() <0) {

                    localStorageTools.setString("isTop","yes");//恢复
                    isFinish = true;
                    EventBus.getDefault().post(new EventBusBean("close"));
//                    scrollBottom();
                } else {
                    localStorageTools.setString("isTop","no");//继续将焦点给子控件
                    scrollOrigin();
                    isFinish = false;
                }
                break;
        }
        return true;
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            mParentView = (ViewGroup) this.getParent();
            viewHeight = this.getHeight();
        }
    }
    /*** * 接口回调 */
    public void setOnFinishListener( OnFinishListener onSildingFinishListener) {
        this.onFinishListener = onSildingFinishListener;
    }
    /** * 滚动出界面 */
    private void scrollBottom() {
//        mParentView.get
        final int delta = (viewHeight + mParentView.getScrollY());
        mScroller.startScroll(0, mParentView.getScrollY(), 0, -delta + 1, Math.abs(delta));
        postInvalidate();
    }
    /** * 滚动到起始位置 */
    private void scrollOrigin() {
        int delta = mParentView.getScrollY();
        mScroller.startScroll(0, mParentView.getScrollY(), 0, -delta, Math.abs(delta));
        postInvalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            mParentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
            if (mScroller.isFinished() && isFinish) {
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                } else {
                    // 没有设置OnSildingFinishListener，让其滚动到其实位置
                    scrollOrigin();
                    isFinish = false;
                }
            }
        }
    }
    public interface OnFinishListener {
        public void onFinish();
    }
}

