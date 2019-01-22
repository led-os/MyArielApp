package com.qinggan.app.arielapp.minor.main.commonui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qinggan.app.arielapp.minor.main.utils.LocalStorageTools;

public class myRecyclerview extends RecyclerView {
    private Context context;
    private LocalStorageTools localStorageTools;

    public myRecyclerview(Context context) {
        super(context);
        this.context=context;
    }

    public myRecyclerview(Context context, Context context1) {
        super(context);
        this.context = context1;
    }

    public myRecyclerview(Context context, @Nullable AttributeSet attrs, Context context1) {
        super(context, attrs);
        this.context = context1;
    }

    public myRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle, Context context1) {
        super(context, attrs, defStyle);
        this.context = context1;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        localStorageTools = new LocalStorageTools(context);//初始化本地储存
        String isTop= localStorageTools.getString("isTop");
        switch (ev.getAction()){
            case MotionEvent.ACTION_POINTER_DOWN:
                //父容器禁止拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isTop.equals("yes")){//事件交给父容器的的条件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:

                break;
        }

        return super.dispatchTouchEvent(ev);
    }
}
