package com.qinggan.app.arielapp.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qinggan.app.arielapp.R;

/**
 * Created by Yorashe on 18-12-12.
 */

public class ArielRelativeLayout extends RelativeLayout{
    public ArielRelativeLayout(Context context) {
        super(context);
        setWillNotDraw(false);

    }

    public ArielRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);


    }

    public ArielRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);


    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        int topViewHight=dp2px(getContext(),213);
        Paint mPaint =new Paint();
        int colors[] = new int[2];
        float positions[] = new float[2];

        // 第1个点
        colors[0] = getContext().getResources().getColor(R.color.background_start);
        positions[0] = 0;

        // 第2个点
        colors[1] = getContext().getResources().getColor(R.color.background_end);
        positions[1] = 1f;


        LinearGradient shader = new LinearGradient(
                0, 0,
                0, topViewHight,
                colors,
                positions,
                Shader.TileMode.MIRROR);
        mPaint.setShader(shader);
        Rect clientRectTop = new Rect(0,0,w,topViewHight);
        Rect clientRectBottom = new Rect(0,topViewHight,w,h);
        Paint mPaint1 =new Paint();
        mPaint1.setColor(getContext().getResources().getColor(R.color.background_end));
        canvas.drawRect(clientRectTop,mPaint);
        canvas.drawRect(clientRectBottom,mPaint1);

    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }
    private void init(Context context){
        setBackgroundColor(Color.parseColor("#2B2B2B"));
        View view =new View(context);
        RelativeLayout.LayoutParams layoutParams =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp2px(context,213));
        view.setLayoutParams(layoutParams);
        view.setBackground(topToBottomDrawable("#121215","#2b2b2b"));
        addView(view);

    }
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 上→下渐变色
     *
     * @param StartColor
     * @param EndColor
     * @return
     */
    public static Drawable topToBottomDrawable(@NonNull String StartColor, @NonNull String EndColor) {
        GradientDrawable mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor(StartColor),
                        Color.parseColor(EndColor)});
//        mGradientDrawable.setCornerRadius(mRadius);
        return mGradientDrawable;
    }
}
