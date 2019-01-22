
package com.qinggan.app.arielapp.ui.bluekey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.R;


public class LoadingView extends LinearLayout {
    private Context mContext;
    private LinearLayout mView;
    private ImageView mProcess;


    public LoadingView(Context context) {
        super(context);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.loading_layout, this,true);
        mProcess = (ImageView) mView.findViewById(R.id.process);
        setOrientation(LinearLayout.VERTICAL);
    }


    public void showLoading() {
        mProcess.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate));
        mProcess.setVisibility(View.VISIBLE);
        setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mProcess.clearAnimation();
        setVisibility(View.GONE);
    }

}
