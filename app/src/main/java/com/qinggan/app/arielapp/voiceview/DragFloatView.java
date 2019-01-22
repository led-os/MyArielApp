package com.qinggan.app.arielapp.voiceview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;

import java.lang.reflect.Method;

/**
 * Created by zhongquansun on 2018/11/26.
 */
public class DragFloatView extends BaseFloatView {
    protected static final String TAG = DragFloatView.class.getSimpleName();

    private int mInAppWidth;
    private int mOutAppWidth;
    private int mOutAppHeight;
    private ValueAnimator mToEdgeAnimator;
    private boolean mCanTouchWakeUpView = true;
    private int mAnimateStartX = 0;
    private int mAnimateEndX = 0;
    private int mAnimateStartY = 0;
    private int mAnimateEndY = 0;
    private int mMaxY = 0;
    private int mMinY = 0;
    private int mStatusBarHeight = 0;
    private int mNavigationBarHeight = 0;
    private DragFloatViewClickListener mDragFloatViewClickListener;
    private LinearLayout mOutAppView;
    private LinearLayout mInAppView;
    private FrameLayout mWakeupView;
    private ImageView mBackToAppView;
    private static DragFloatView sDragFloatView;

    public static DragFloatView getInstance(Context context){
        if(null == sDragFloatView){
            sDragFloatView = new DragFloatView(context);
        }
        return sDragFloatView;
    }

    private DragFloatView(Context context) {
        super(context);
        mStatusBarHeight = getStatusBarHeight();
        mNavigationBarHeight = getNavigationBarHeight();
        Log.d(TAG, "mStatusBarHeight = " + mStatusBarHeight);
        Log.d(TAG, "mNavigationBarHeight = " + mNavigationBarHeight);

        mMaxY = (mScreenHeight - mFloatViewLayoutParams.height)/2 - mNavigationBarHeight;
        mMinY = -(mScreenHeight - mFloatViewLayoutParams.height)/2 + mStatusBarHeight;
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        mInAppWidth = mContext.getResources().getDimensionPixelSize(R.dimen.float_wakeup_in_app_width);
        mOutAppWidth = mContext.getResources().getDimensionPixelSize(R.dimen.float_wakeup_out_app_width);
        mOutAppHeight = mContext.getResources().getDimensionPixelSize(R.dimen.float_wakeup_out_app_height);
        layoutParams.width = mInAppWidth;
        layoutParams.height = mInAppWidth;
        layoutParams.x = (mScreenWidth - layoutParams.width) / 2;
        layoutParams.y = -layoutParams.height;
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.float_wakeup_view;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        } else {
            result = (int) (20 * mContext.getResources().getDisplayMetrics().density);
        }
        return result;
    }

    private String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    public boolean hasNavBar() {
        Resources res = mContext.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(mContext).hasPermanentMenuKey();
        }
    }

    public int getNavigationBarHeight() {
        if(!hasNavBar()) return 0;
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onViewCreate(View view) {
        Log.e(TAG, "onViewCreate   view = " + view);
        mWakeupView = view.findViewById(R.id.wakeup);
        mBackToAppView = view.findViewById(R.id.back_to_app);
        mInAppView = view.findViewById(R.id.voice_in_app);
        mOutAppView = view.findViewById(R.id.voice_out_app);
        view.setOnTouchListener(new View.OnTouchListener() {
            private float lastX;
            private float lastY;
            private float nowX;
            private float nowY;
            private float tranX;
            private float tranY;
            private boolean isMoving = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (!mCanTouchWakeUpView) return false;
                boolean ret = false;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        isMoving = false;
                        ret = true;
                        if(mOutAppView.getVisibility() == View.VISIBLE){
                            if ((event.getY() < mOutAppWidth)) {
                                mWakeupView.setSelected(true);
                            }else{
                                mBackToAppView.setSelected(true);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        nowX = event.getRawX();
                        nowY = event.getRawY();
                        if(Math.abs(nowX -lastX) <= 10 && Math.abs(nowY - nowY) <= 10 ){
                            return false;
                        }
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        mFloatViewLayoutParams.x += tranX;
                        mFloatViewLayoutParams.y += tranY;
                        update();
                        lastX = nowX;
                        lastY = nowY;
                        isMoving = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            if(mOutAppView.getVisibility() == View.VISIBLE){
                                if ((event.getY() < mOutAppWidth)) {
                                    onWakeUpViewClicked();
                                }else{
                                    onBackToAppViewClicked();
                                }
                            }else{
                                onWakeUpViewClicked();
                            }
                        }else {
                            animateToEdge();
                        }
                        mWakeupView.setSelected(false);
                        mBackToAppView.setSelected(false);
                        isMoving = false;
                        break;
                }
                return ret;
            }
        });
    }

    private void onBackToAppViewClicked() {
        if (null != mDragFloatViewClickListener) {
            mDragFloatViewClickListener.onBackToAppViewClicked();
        }
    }

    private void onWakeUpViewClicked() {
        if (null != mDragFloatViewClickListener) {
            mDragFloatViewClickListener.onWakeUpViewClicked();
        }
    }

    private void animateToEdge() {
        mAnimateStartX = mFloatViewLayoutParams.x;
        if (mAnimateStartX > 0) {
            mAnimateEndX = (mScreenWidth - mFloatViewLayoutParams.width) / 2;
        } else {
            mAnimateEndX = -(mScreenWidth - mFloatViewLayoutParams.width) / 2;
        }
        mAnimateStartY = mFloatViewLayoutParams.y;

        if(mAnimateStartY > mMaxY){
            mAnimateEndY = mMaxY;
        }else if(mAnimateStartY < mMinY){
            mAnimateEndY = mMinY;
        }else {
            mAnimateEndY = mAnimateStartY;
        }
        if (null == mToEdgeAnimator) {
            mToEdgeAnimator = ValueAnimator.ofFloat(0, 1);
            mToEdgeAnimator.setDuration(300);
            mToEdgeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float progress = (float) valueAnimator.getAnimatedValue();
                    mFloatViewLayoutParams.x = (int) (mAnimateStartX + (mAnimateEndX - mAnimateStartX) * progress);
                    mFloatViewLayoutParams.y = (int) (mAnimateStartY + (mAnimateEndY - mAnimateStartY) * progress);
                    update();
                }
            });
            mToEdgeAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    mCanTouchWakeUpView = false;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mCanTouchWakeUpView = true;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        mToEdgeAnimator.start();
    }

    public void init(boolean isInApp){
        if (isInApp) {
            mOutAppView.setVisibility(View.GONE);
            mInAppView.setVisibility(View.VISIBLE);
            mFloatViewLayoutParams.width = mInAppWidth;
            mFloatViewLayoutParams.height = mInAppWidth;
        } else {
            mOutAppView.setVisibility(View.VISIBLE);
            mInAppView.setVisibility(View.GONE);
            mFloatViewLayoutParams.width = mOutAppWidth;
            mFloatViewLayoutParams.height = mOutAppHeight;
        }
        mMaxY = (mScreenHeight - mFloatViewLayoutParams.height)/2 - mNavigationBarHeight;
        mMinY = -(mScreenHeight - mFloatViewLayoutParams.height)/2 + mStatusBarHeight;
    }

    public void updateViews(boolean isInApp) {
        init(isInApp);
        update();
        animateToEdge();
    }

    public void setOnDragFloatViewClickListener(DragFloatViewClickListener listener) {
        mDragFloatViewClickListener = listener;
    }

    public void removeOnDragFloatViewClickListener() {
        mDragFloatViewClickListener = null;
    }

    public interface DragFloatViewClickListener {
        void onWakeUpViewClicked();

        void onBackToAppViewClicked();
    }
}
