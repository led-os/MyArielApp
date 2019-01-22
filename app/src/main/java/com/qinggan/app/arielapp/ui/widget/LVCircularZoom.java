package com.qinggan.app.arielapp.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.qinggan.app.arielapp.ui.widget.base.LVBase;


public class LVCircularZoom extends LVBase {

    private Paint mPaint;
    private Paint mPaint1;

    private float mWidth = 0f;
    private float mHigh = 0f;
    private float mMaxRadius = 8;
    private int circularCount = 2;
    private float mAnimatedValue = 1.0f;
    private int mJumpValue = 0;
    private int mCount;
    private int mMaxCount=20;
    private boolean isAdd =true;

    public LVCircularZoom(Context context) {
        super(context);
    }

    public LVCircularZoom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LVCircularZoom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHigh = getMeasuredHeight();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCount!=0 && mCount%mMaxCount==0 && mCount>0){
            isAdd=false;
        }else if (mCount!=0 && mCount%mMaxCount==0 && mCount<0){
            isAdd=true;
        }
        if (isAdd){
            mCount++;
        }else{
            mCount--;
        }
        canvas.drawCircle(mWidth/2+mCount*2,
                mHigh / 2,
                mMaxRadius , mPaint);

        canvas.drawCircle(mWidth / 2-mCount*2,
                mHigh / 2,
                mMaxRadius, mPaint1);



    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#CE6EFF"));

        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setColor(Color.parseColor("#0A94FF"));


    }


    public void setViewColor(int color)
    {
        mPaint.setColor(color);
        postInvalidate();
    }


    @Override
    protected void OnAnimationRepeat(Animator animation) {
        mJumpValue++;
    }

    @Override
    protected void OnAnimationUpdate(ValueAnimator valueAnimator) {
        mAnimatedValue = (float) valueAnimator.getAnimatedValue();

        if (mAnimatedValue < 0.2) {
            mAnimatedValue = 0.2f;
        }


        invalidate();
    }

    @Override
    protected int OnStopAnim() {
        mAnimatedValue = 0f;
        mJumpValue = 0;
        return 0;
    }
    @Override
    protected int SetAnimRepeatMode() {
        return ValueAnimator.RESTART;
    }
    @Override
    protected void InitPaint() {
        initPaint();
    }
    @Override
    protected void AinmIsRunning() {

    }
    @Override
    protected int SetAnimRepeatCount() {
        return ValueAnimator.INFINITE;
    }
}
