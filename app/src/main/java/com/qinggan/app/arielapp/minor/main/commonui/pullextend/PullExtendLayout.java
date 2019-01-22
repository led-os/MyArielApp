package com.qinggan.app.arielapp.minor.main.commonui.pullextend;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.ArielApplication;
import com.qinggan.app.arielapp.minor.main.entity.EventBusBean;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 这个实现了下拉刷新和上拉加载更多的功能
 *
 * @author Li Hong
 * @since 2013-7-29
 */
public class PullExtendLayout extends LinearLayout implements IPullToExtend {

    /**
     * 回滚的时间
     */
    private static final int SCROLL_DURATION = 200;
    /**
     * 继续下滑关闭的高度
     */
    private static final int SCROLL_DOWN_HEIGHT = 180;
    /**
     * 阻尼系数
     */
    private float offsetRadio = 1.5f;

    /**
     * 上一次移动的点
     */
    private float mLastMotionY = -1;

    /**
     * 下拉刷新的布局
     */
    private ExtendLayout mHeaderLayout;

    /**
     * 上拉加载更多的布局
     */
    private ExtendLayout mFooterLayout;
    /**
     * 列表开始显示的高度
     */
    private int mHeaderHeight;
    /**
     * 列表的高度
     */
    private int headerListHeight;
    /**
     * FooterView的高度
     */
    private int mFooterHeight;
    /**
     * 列表的高度
     */
    private int footerListHeight;

    /**
     * 下拉刷新是否可用
     */
    private boolean mPullRefreshEnabled = true;
    /**
     * 上拉加载是否可用
     */
    private boolean mPullLoadEnabled = true;

    /**
     * 是否截断touch事件
     */
    private boolean mInterceptEventEnable = true;

    /**
     * 表示是否消费了touch事件，如果是，则不调用父类的onTouchEvent方法
     */
    private boolean mIsHandledTouchEvent = false;
    /**
     * 移动点的保护范围值
     */
    private int mTouchSlop;
    /**
     * 主View
     */
    View mRefreshableView;
    /**
     * 平滑滚动的Runnable
     */
    private SmoothScrollRunnable mSmoothScrollRunnable;
    private Timer timer;
    private MyTask myTask;
    private int i = 0;

    public PullExtendLayout(Context context) {
        this(context, null);
    }


    public PullExtendLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public PullExtendLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount == 2) {
            if (getChildAt(0) instanceof ExtendLayout) {
                mHeaderLayout = (ExtendLayout) getChildAt(0);
                mRefreshableView = getChildAt(1);
            } else {
                mRefreshableView = getChildAt(0);
                mFooterLayout = (ExtendLayout) getChildAt(1);
            }
        } else if (childCount == 3) {
            if (getChildAt(0) instanceof ExtendLayout) {
                mHeaderLayout = (ExtendLayout) getChildAt(0);
            }
            mRefreshableView = getChildAt(1);
            mFooterLayout = (ExtendLayout) getChildAt(2);
        } else {
            throw new IllegalStateException("布局异常，最多三个，最少一个");
        }
        if (mRefreshableView == null) {
            throw new IllegalStateException("布局异常，一定要有内容布局");
        }
        // mRefreshableView.setClickable(true);需要自己设置
        init(getContext());
    }

    public void setOffsetRadio(float offsetRadio) {
        this.offsetRadio = offsetRadio;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    private void init(Context context) {
        mTouchSlop = (int) (ViewConfiguration.get(context).getScaledTouchSlop() * 1.5);
        ViewGroup.LayoutParams layoutParams = mRefreshableView.getLayoutParams();
        layoutParams.height = 10;
        mRefreshableView.setLayoutParams(layoutParams);
        // 得到Header的高度，这个高度需要用这种方式得到，在onLayout方法里面得到的高度始终是0
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                refreshLoadingViewsSize();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**
     * 初始化padding，我们根据header和footer的高度来设置top padding和bottom padding
     */
    private void refreshLoadingViewsSize() {
        // 得到header和footer的内容高度，它将会作为拖动刷新的一个临界值，如果拖动距离大于这个高度
        // 然后再松开手，就会触发刷新操作
        int headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getContentSize() : 0;
        headerListHeight = (null != mHeaderLayout) ? mHeaderLayout.getListSize() : 0;
        int footerHeight = (null != mFooterLayout) ? mFooterLayout.getContentSize() : 0;
        footerListHeight = (null != mFooterLayout) ? mFooterLayout.getListSize() : 0;

        if (headerHeight < 0) {
            headerHeight = 0;
        }

        if (footerHeight < 0) {
            footerHeight = 0;
        }

        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;

        // 这里得到Header和Footer的高度，设置的padding的top和bottom就应该是header和footer的高度
        // 因为header和footer是完全看不见的
        headerHeight = (null != mHeaderLayout) ? mHeaderLayout.getMeasuredHeight() : 0;
        footerHeight = (null != mFooterLayout) ? mFooterLayout.getMeasuredHeight() : 0;

        int pLeft = getPaddingLeft();
        int pTop = -headerHeight;
        int pRight = getPaddingRight();
        int pBottom = -footerHeight;
        setPadding(pLeft, pTop, pRight, pBottom);
    }

    @Override
    protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshLoadingViewsSize();
        // 设置刷新View的大小
        refreshRefreshableViewSize(w, h);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * 计算刷新View的大小
     *
     * @param width  当前容器的宽度
     * @param height 当前容器的宽度
     */
    protected void refreshRefreshableViewSize(int width, int height) {
        LayoutParams lp = (LayoutParams) mRefreshableView.getLayoutParams();
        if (lp.height != height) {
            lp.height = height;
            mRefreshableView.requestLayout();
        }
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {

        if (!isInterceptTouchEventEnabled()) {
            return false;
        }
        if (!isPullLoadEnabled() && !isPullRefreshEnabled()) {
            return false;
        }
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mIsHandledTouchEvent = false;
            return false;
        }
        if (action != MotionEvent.ACTION_DOWN && mIsHandledTouchEvent) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = event.getY();
                mIsHandledTouchEvent = false;
                break;

            case MotionEvent.ACTION_MOVE:

                final float deltaY = event.getY() - mLastMotionY;
                final float absDiff = Math.abs(deltaY);
//                Log.i("Alan","getHeaderExtendLayout().getState()=-="+getHeaderExtendLayout().getState());
                if (getHeaderExtendLayout().getState() == IExtendLayout.State.RESET||getHeaderExtendLayout().getState() == IExtendLayout.State.NONE) {//如果当前是头部关闭状态,
                    if (deltaY < 0) {//上滑
                        return false;//将事件给子控件
                    } else if (deltaY > 0) {//下滑需要判断当前是否在recyclerview的顶部
                        RecyclerView recyclerView = ArielApplication.getApp().getDrRecyclerview();
                        boolean isTop = recyclerView.canScrollVertically(-1);
                        //false 代表不能下滑即已经到顶部了
                        if (isTop) {
                            return false;//将事件给子控件
                        }
                    }
                }
                // 位移差大于mTouchSlop，这是为了防止快速拖动引发刷新
                if ((absDiff > mTouchSlop)) {
                    mLastMotionY = event.getY();


                    // 第一个显示出来，Header已经显示或拉下
                    if (isPullRefreshEnabled() || isPullLoadEnabled()) {
                        // 1，Math.abs(getScrollY()) > 0：表示当前滑动的偏移量的绝对值大于0，表示当前HeaderView滑出来了或完全
                        // 不可见，存在这样一种case，当正在刷新时并且RefreshableView已经滑到顶部，向上滑动，那么我们期望的结果是
                        // 依然能向上滑动，直到HeaderView完全不可见
                        // 2，deltaY > 0.5f：表示下拉的值大于0.5f
                        // 3，deltaY <- 0.5f：表示上滑的值大于0.5f

                        mIsHandledTouchEvent = (Math.abs(getScrollYValue()) > 0 || deltaY > 0.5f || deltaY < -0.5f);
                        // 如果截断事件，我们则仍然把这个事件交给刷新View去处理，典型的情况是让ListView/GridView将按下
                        // Child的Selector隐藏
                        if (mIsHandledTouchEvent) {
                            mRefreshableView.onTouchEvent(event);
                        }
                    }
                }
                break;

            default:
                break;
        }
        return mIsHandledTouchEvent;
    }


    @Override
    public final boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mIsHandledTouchEvent = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final int scrollY_m = Math.abs(getScrollYValue());
                int ca = scrollY_m - headerListHeight;
                // 创建一个数值格式化对象
                NumberFormat numberFormat = NumberFormat.getInstance();
                // 设置精确到小数点后2位
                numberFormat.setMaximumFractionDigits(0);
                if (0 < ca && ca <= SCROLL_DOWN_HEIGHT) {
                    String result = numberFormat.format((float) ca / (float) SCROLL_DOWN_HEIGHT * 100);

                    updateProgressBar(Integer.valueOf(result));
                }
                final float deltaY = ev.getY() - mLastMotionY;
                mLastMotionY = ev.getY();
                if (isPullRefreshEnabled() && isReadyForPullDown(deltaY)) {
                    pullHeaderLayout(deltaY / offsetRadio);
                    handled = true;
                    if (null != mFooterLayout && 0 != mFooterHeight) {
                        mFooterLayout.setState(IExtendLayout.State.RESET);
                    }
                } else if (isPullLoadEnabled() && isReadyForPullUp(deltaY)) {
                    pullFooterLayout(deltaY / offsetRadio);
                    handled = true;
                    if (null != mHeaderLayout && 0 != mHeaderHeight) {
                        mHeaderLayout.setState(IExtendLayout.State.RESET);

                    }
                } else {
                    mIsHandledTouchEvent = false;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if(mHeaderLayout.getState()==IExtendLayout.State.RESET||isPullRefreshEnabled()){
                    i=0;
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    if (myTask != null) {
                        myTask.cancel();
                        myTask = null;
                    }
                    timer = new Timer();
                    myTask = new MyTask();
                    timer.schedule(myTask, 0, 1000);
                }
                updateProgressBar(0);
                if (mIsHandledTouchEvent) {
                    mIsHandledTouchEvent = false;
                    // 当第一个显示出来时
                    if (isReadyForPullDown(0)) {
                        // 未处于刷新状态，更新箭头
                        int scrollY = Math.abs(getScrollYValue());

                        if (scrollY - headerListHeight > SCROLL_DOWN_HEIGHT) {//继续下拉关闭页面
                            EventBus.getDefault().post(new EventBusBean("simDropDown"));
                        } else {
                            resetHeaderLayout();
                        }
                    } else if (isReadyForPullUp(0)) {
                        resetFooterLayout();
                    }
                }
                break;

            default:
                break;
        }
        return handled;
    }
    //更新进度条
    private void updateProgressBar(final int pro) {
//        final ProgressBar progressBar1 = ArielApplication.getApp().getDownProgressBar();
//        progressBar1.post(new Runnable() {
//            @Override
//            public void run() {
//                progressBar1.setProgress(pro);
//            }
//        });

        EventBus.getDefault().post(new EventBusBean("proNum",pro));
    }

    @Override
    public void setPullRefreshEnabled(boolean pullRefreshEnabled) {
        mPullRefreshEnabled = pullRefreshEnabled;
    }

    @Override
    public void setPullLoadEnabled(boolean pullLoadEnabled) {
        mPullLoadEnabled = pullLoadEnabled;
    }


    @Override
    public boolean isPullRefreshEnabled() {
        return mPullRefreshEnabled && (null != mHeaderLayout);
    }

    @Override
    public boolean isPullLoadEnabled() {
        return mPullLoadEnabled && (null != mFooterLayout);
    }


    @Override
    public ExtendLayout getHeaderExtendLayout() {
        return mHeaderLayout;
    }

    @Override
    public ExtendLayout getFooterExtendLayout() {
        return mFooterLayout;
    }


    /**
     * 开始刷新，通常用于调用者主动刷新，典型的情况是进入界面，开始主动刷新，这个刷新并不是由用户拉动引起的
     *
     * @param smoothScroll 表示是否有平滑滚动，true表示平滑滚动，false表示无平滑滚动
     * @param delayMillis  延迟时间
     */
    public void doPullRefreshing(final boolean smoothScroll, final long delayMillis) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                int newScrollValue = -mHeaderHeight;
                int duration = smoothScroll ? SCROLL_DURATION : 0;

                smoothScrollTo(newScrollValue, duration, 0);
            }
        }, delayMillis);
    }

    public boolean canScrollVertically(int direction) {
        final int offset = computeVerticalScrollOffset();
        final int range = computeVerticalScrollRange() - computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    /**
     * 判断刷新的View是否滑动到顶部
     *
     * @return true表示已经滑动到顶部，否则false
     */
    protected boolean isReadyForPullDown(float deltaY) {
        return getScrollYValue() < 0 || (getScrollYValue() == 0 && deltaY > 0);
    }

    /**
     * 判断刷新的View是否滑动到底
     *
     * @return true表示已经滑动到底部，否则false
     */
    protected boolean isReadyForPullUp(float deltaY) {
        return getScrollYValue() > 0 || (getScrollYValue() == 0 && deltaY < 0);
    }


    /**
     * 得到平滑滚动的时间，派生类可以重写这个方法来控件滚动时间
     *
     * @return 返回值时间为毫秒
     */
    protected long getSmoothScrollDuration() {
        return SCROLL_DURATION;
    }

    /**
     * 拉动Header Layout时调用
     *
     * @param delta 移动的距离
     */
    protected void pullHeaderLayout(float delta) {
        // 向上滑动，并且当前scrollY为0时，不滑动
        int oldScrollY = getScrollYValue();
        if (delta < 0 && (oldScrollY - delta) >= 0) {
            setScrollTo(0, 0);
            if (null != mHeaderLayout && 0 != mHeaderHeight) {
                mHeaderLayout.setState(IExtendLayout.State.RESET);
                mHeaderLayout.onPull(0);

            }
            return;
        }
        // 向下滑动布局
        setScrollBy(0, -(int) delta);
        // 未处于刷新状态，更新箭头
        int scrollY = Math.abs(getScrollYValue());
        if (null != mHeaderLayout && 0 != mHeaderHeight) {
            if (scrollY >= headerListHeight) {

                mHeaderLayout.setState(IExtendLayout.State.arrivedListHeight);

                setOffsetRadio(3.5f);

            } else {
                setOffsetRadio(1.1f);
            }
            mHeaderLayout.onPull(scrollY);
        }
    }
    //头部下拉定时器 Alan

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                i++;
                if (i % 5==0) {
                    i=0;
                    closeExtendHeadAndFooter();
                } else {

                }
            }
            super.handleMessage(msg);
        }
    };



    class MyTask extends TimerTask {
        @Override
        public void run(){
            // TODO Auto-generated method stub
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            mHandler.sendMessage(message);
        }
    };
    /**
     * 拉Footer时调用
     *
     * @param delta 移动的距离
     */
    protected void pullFooterLayout(float delta) {
        int oldScrollY = getScrollYValue();
        if (delta > 0 && (oldScrollY - delta) <= 0) {
            setScrollTo(0, 0);
            if (null != mFooterLayout && 0 != mFooterHeight) {
                mFooterLayout.setState(IExtendLayout.State.RESET);
                mFooterLayout.onPull(0);
            }
            return;
        }
        setScrollBy(0, -(int) delta);
        int scrollY = Math.abs(getScrollYValue());
        if (null != mFooterLayout && 0 != mFooterHeight) {
            if (scrollY >= footerListHeight) {
                mFooterLayout.setState(IExtendLayout.State.arrivedListHeight);
                setOffsetRadio(4.0f);
            } else {
                setOffsetRadio(1.0f);
            }
            mFooterLayout.onPull(Math.abs(getScrollYValue()));
        }
    }

    /**
     * 重置header
     */
    public void resetHeaderLayout() {
        final int scrollY = Math.abs(getScrollYValue());
        if (scrollY < mHeaderHeight) {
            smoothScrollTo(0);
        } else if (scrollY >= mHeaderHeight) {
            smoothScrollTo(-headerListHeight);
        }
    }


    /**
     * 重置footer
     */
    protected void resetFooterLayout() {
        int scrollY = Math.abs(getScrollYValue());
        if (scrollY < mFooterHeight) {
            smoothScrollTo(0);
        } else if (scrollY >= mFooterHeight) {
            smoothScrollTo(footerListHeight);
        }
    }

    /**
     * 隐藏header和footer
     */
    public void closeExtendHeadAndFooter() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
        smoothScrollTo(0);
    }

    /**
     * 设置滚动位置
     *
     * @param x 滚动到的x位置
     * @param y 滚动到的y位置
     */
    private void setScrollTo(int x, int y) {
        scrollTo(x, y);
    }

    /**
     * 设置滚动的偏移
     *
     * @param x 滚动x位置
     * @param y 滚动y位置
     */
    private void setScrollBy(int x, int y) {
        scrollBy(x, y);
    }

    /**
     * 得到当前Y的滚动值
     *
     * @return 滚动值
     */
    private int getScrollYValue() {
        return getScrollY();
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     */
    private void smoothScrollTo(int newScrollValue) {
        smoothScrollTo(newScrollValue, getSmoothScrollDuration(), 0);
    }

    /**
     * 平滑滚动
     *
     * @param newScrollValue 滚动的值
     * @param duration       滚动时候
     * @param delayMillis    延迟时间，0代表不延迟
     */
    private void smoothScrollTo(int newScrollValue, long duration, long delayMillis) {
        if (null != mSmoothScrollRunnable) {
            mSmoothScrollRunnable.stop();
        }

        int oldScrollValue = this.getScrollYValue();
        boolean post = (oldScrollValue != newScrollValue);
        if (post) {
            mSmoothScrollRunnable = new SmoothScrollRunnable(oldScrollValue, newScrollValue, duration);
        }

        if (post) {
            if (delayMillis > 0) {
                postDelayed(mSmoothScrollRunnable, delayMillis);
            } else {
                post(mSmoothScrollRunnable);
            }
        }
    }

    /**
     * 设置是否截断touch事件
     *
     * @param enabled true截断，false不截断
     */
    public void setInterceptTouchEventEnabled(boolean enabled) {
        mInterceptEventEnable = enabled;
    }

    /**
     * 标志是否截断touch事件
     *
     * @return true截断，false不截断
     */
    private boolean isInterceptTouchEventEnabled() {
        return mInterceptEventEnable;
    }

    /**
     * 实现了平滑滚动的Runnable
     *
     * @author Li Hong
     * @since 2013-8-22
     */
    final class SmoothScrollRunnable implements Runnable {
        /**
         * 动画效果
         */
        private final Interpolator mInterpolator;
        /**
         * 结束Y
         */
        private final int mScrollToY;
        /**
         * 开始Y
         */
        private final int mScrollFromY;
        /**
         * 滑动时间
         */
        private final long mDuration;
        /**
         * 是否继续运行
         */
        private boolean mContinueRunning = true;
        /**
         * 开始时刻
         */
        private long mStartTime = -1;
        /**
         * 当前Y
         */
        private int mCurrentY = -1;

        /**
         * 构造方法
         *
         * @param fromY    开始Y
         * @param toY      结束Y
         * @param duration 动画时间
         */
        public SmoothScrollRunnable(int fromY, int toY, long duration) {
            mScrollFromY = fromY;
            mScrollToY = toY;
            mDuration = duration;
            mInterpolator = new DecelerateInterpolator();
        }

        @Override
        public void run() {
            /**
             * If the duration is 0, we scroll the view to target y directly.
             *
             */
            if (mDuration <= 0) {
                setScrollTo(0, mScrollToY);
                return;
            }

            /**
             * Only set mStartTime if this is the first time we're starting,
             * else actually calculate the Y delta
             */
            if (mStartTime == -1) {
                mStartTime = System.currentTimeMillis();
            } else {

                final long oneSecond = 1000;    // SUPPRESS CHECKSTYLE
                long normalizedTime = (oneSecond * (System.currentTimeMillis() - mStartTime)) / mDuration;
                normalizedTime = Math.max(Math.min(normalizedTime, oneSecond), 0);

                final int deltaY = Math.round((mScrollFromY - mScrollToY)
                        * mInterpolator.getInterpolation(normalizedTime / (float) oneSecond));
                mCurrentY = mScrollFromY - deltaY;
                setScrollTo(0, mCurrentY);

                if (null != mHeaderLayout && 0 != mHeaderHeight) {
                    mHeaderLayout.onPull(Math.abs(mCurrentY));
                    if (mCurrentY == 0) {
                        mHeaderLayout.setState(IExtendLayout.State.RESET);
                    }
                    if (Math.abs(mCurrentY) == headerListHeight) {
                        mHeaderLayout.setState(IExtendLayout.State.arrivedListHeight);
                    }
                }
                if (null != mFooterLayout && 0 != mFooterHeight) {
                    mFooterLayout.onPull(Math.abs(mCurrentY));
                    if (mCurrentY == 0) {
                        mFooterLayout.setState(IExtendLayout.State.RESET);
                    }
                    if (Math.abs(mCurrentY) == footerListHeight) {
                        mFooterLayout.setState(IExtendLayout.State.arrivedListHeight);
                    }
                }
            }

            // If we're not at the target Y, keep going...
            if (mContinueRunning && mScrollToY != mCurrentY) {
                PullExtendLayout.this.postDelayed(this, 16);// SUPPRESS CHECKSTYLE
            }
        }

        /**
         * 停止滑动
         */
        public void stop() {
            mContinueRunning = false;
            removeCallbacks(this);
        }
    }

}
