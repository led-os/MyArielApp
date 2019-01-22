package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.res.TypedArray;

import com.qinggan.app.arielapp.R;

public class ImitateIphoneSwitch extends View {

    private int mWidth, mHeight;
    private Path mPath;
    private RectF mBgRect;
    private int strokenWidth;
    private boolean isWrapperLine;
    private float lastX, lastY, curX, curY;
    private int mCurrValue = 100, mLastValue = 0;
    private int curPoint;
    private int mBgColor;
    private Paint paint;
    private ValueChangeCallback mCallback;


    public ImitateIphoneSwitch(Context context) {
        this(context, null);
    }

    public ImitateIphoneSwitch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImitateIphoneSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        strokenWidth = strokenWidth != -1 ? strokenWidth : mWidth / 5;
    }

    private void init(Context context, AttributeSet attrs) {
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ImitateIphoneSwitchView);
        int color = (int) array.getInt(R.styleable.ImitateIphoneSwitchView_bgColor, -1);
        mBgColor = color != -1 ? color : getResources().getColor(R.color.white_alpha_10);
        strokenWidth = (int) array.getInt(R.styleable.ImitateIphoneSwitchView_radius, -1);
        isWrapperLine = array.getBoolean(R.styleable.ImitateIphoneSwitchView_isWrappeLine, true);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mPath) {
            mPath = new Path();
            if (null == mBgRect) {
                mBgRect = new RectF(0, 0, mWidth, mHeight);
            }
            mPath.addRoundRect(mBgRect, 80, 80, Path.Direction.CCW);
        }


        int layerId = canvas.saveLayer(0, 0, mWidth, mHeight, null, Canvas.ALL_SAVE_FLAG);
        canvas.clipPath(mPath);
        paint.setColor(mBgColor);
        canvas.drawRect(mBgRect, paint);

        RectF rectF = new RectF(0, mHeight - mCurrValue, mWidth, mHeight);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
        canvas.restoreToCount(layerId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                resetValues(lastY - curY);
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void resetValues(float v) {
        float cacheValue = mCurrValue + v;
        if (cacheValue <= mHeight && cacheValue >= 0) {
            mCurrValue = (int)
                    cacheValue;
            if (mCallback != null) {
                mCallback.onValueChanged(cacheValue / mHeight);
            }
        }
        postInvalidate();
    }

    public void setValues(float v, int height) {
        setInitialValue(v, height);
    }

    public ImitateIphoneSwitch setColor(int color) {
        mBgColor = color;
        postInvalidate();
        return this;
    }

    public ImitateIphoneSwitch setMoveValue(int value) {
        mCurrValue = mCurrValue - value;
        if (mCurrValue >= mHeight){
            mCurrValue = mHeight;
        } else if (mCurrValue <= 0){
            mCurrValue = 0;
        }
        postInvalidate();
        return this;
    }


    public int getCurrentValue(){
        return mCurrValue;
    }

    public void setCurrentValue(int value){
        mCurrValue = value;
        postInvalidate();
    }

    public ImitateIphoneSwitch setInitialValue(double value, int height) {
        if (value > 1 || value < 0) {
            mCurrValue = 0;
        } else {
            mCurrValue = (int) (height * value);
        }
        if (mCallback != null) {
            mCallback.onValueChanged(mCurrValue / height);
        }
        mLastValue = mCurrValue;
        postInvalidate();
        return this;
    }

    public void registerCallback(ValueChangeCallback callback) {
        mCallback = callback;
    }

    interface ValueChangeCallback {
        void onValueChanged(double value);
    }

}

