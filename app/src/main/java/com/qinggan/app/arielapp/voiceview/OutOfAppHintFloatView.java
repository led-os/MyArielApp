package com.qinggan.app.arielapp.voiceview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.qinggan.app.arielapp.R;

/**
 * Created by zhongquansun on 2018/11/29.
 */
public class OutOfAppHintFloatView extends BaseFloatView{

    public OutOfAppHintFloatView(Context context) {
        super(context);
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.BOTTOM;
        //layoutParams.verticalMargin = mContext.getResources().getDimensionPixelSize(R.dimen.float_voice_height);
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp423);
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.out_of_app_hint_float_view;
    }

    @Override
    protected void onViewCreate(View view) {

    }
}
