package com.qinggan.app.arielapp.voiceview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.qinggan.app.arielapp.R;

/**
 * Created by zhongquansun on 2018/11/29.
 */
public class SelectHintFloatView extends BaseFloatView {

    private static SelectHintFloatView sSelectHintFloatView;
    public static SelectHintFloatView getInstance(Context context){
        if(null == sSelectHintFloatView){
            sSelectHintFloatView = new SelectHintFloatView(context);
        }
        return sSelectHintFloatView;
    }

    private SelectHintFloatView(Context context) {
        super(context);
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp170);
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.select_hint_float_view;
    }

    @Override
    protected void onViewCreate(View view) {

    }
}
