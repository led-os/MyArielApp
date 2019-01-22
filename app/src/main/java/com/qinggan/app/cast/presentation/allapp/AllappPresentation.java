package com.qinggan.app.cast.presentation.allapp;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import com.qinggan.app.arielapp.R;
import com.qinggan.app.cast.presentation.BasePresentation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <Allapp>
 *
 * @author NAME:yanguozhu
 * @version [版本号, 19-1-16]
 * @see [相关类/方法]
 * @since [V1]
 */
public class AllappPresentation extends BasePresentation implements View.OnClickListener {
    public AllappPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    public AllappPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    private Button backBtn;
    private RecyclerView mRecyclerView;

    /**
     * 30s倒计时
     */
    private int MAX_TIME = 6;


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "runnable MAX_TIME:" + MAX_TIME);
            if (MAX_TIME < 0)
                dismiss();
            else {
                MAX_TIME--;
                handler.postDelayed(this, 5 * 1000);
            }
        }
    };

    @Override
    protected void onViewInit() {
        mRecyclerView = rootView.findViewById(R.id.all_app_icon);
        backBtn = rootView.findViewById(R.id.title_back_btn);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        mRecyclerView.setLayoutManager(layoutManager);
        AllAppAdapter allAppAdapter = new AllAppAdapter();
        mRecyclerView.setAdapter(allAppAdapter);
        backBtn.setOnClickListener(this);
    }

    @Override
    protected void onDataInit() {
        EventBus.getDefault().register(this);
        handler.postDelayed(runnable, 5 * 1000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.p_allapp_view;
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn)
            dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doAllWakeUpEvent(String event) {
        if (event.equals("allAppclick")) {
            mRecyclerView.getLayoutManager().findViewByPosition(2).performClick();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        MAX_TIME = 6;
        handler.postDelayed(runnable, 5 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        MAX_TIME = 6;
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(runnable);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
