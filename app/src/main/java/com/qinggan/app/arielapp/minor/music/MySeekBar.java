package com.qinggan.app.arielapp.minor.music;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Created by yang
 * Time on 2018/11/29.
 * Function
 */
public class MySeekBar  extends android.support.v7.widget.AppCompatSeekBar {

    public MySeekBar(Context context) {
        super(context);
    }

    public MySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
//        return super.onTouchEvent(event);
        // 原来是要将TouchEvent传递下去的,我们不让它传递下去就行了
        return false;
    }

}