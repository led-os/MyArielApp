package com.qinggan.app.arielapp.voiceview;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.qinggan.app.arielapp.R;

/**
 * Created by zhongquansun on 2018/11/29.
 */
public class NavHintFloatView extends BaseFloatView {

    private static NavHintFloatView sNavHintFloatView;
    private TextView hintTv;
    public static NavHintFloatView getInstance(Context context){
        if(null == sNavHintFloatView){
            sNavHintFloatView = new NavHintFloatView(context);
        }
        return sNavHintFloatView;
    }

    private NavHintFloatView(Context context) {
        super(context);
    }

    @Override
    protected void onWindowParamsCreate(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.dp170);
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.nav_hint_float_view;
    }

    @Override
    protected void onViewCreate(View view) {
        hintTv = view.findViewById(R.id.hint);
    }

    public void setHintTextView(String text) {
        if(hintTv != null){
            hintTv.setText(text);
        }
    }
}
