package com.qinggan.app.arielapp.ui.stock;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.qinggan.app.arielapp.BR;
import com.qinggan.app.arielapp.MainActivity;
import com.qinggan.app.arielapp.R;
import com.qinggan.app.arielapp.databinding.StockFragBinding;
import com.qinggan.app.arielapp.iview.IStockView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.arielapp.session.stock.QueryStockSession;
import com.qinggan.app.arielapp.ui.IFragmentStatusListener;
import com.qinggan.app.arielapp.ui.UIControlBaseFragment;
import com.qinggan.app.voiceapi.bean.common.StockBean;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shuohuang on 18-5-2.
 */

public class StockFragment extends UIControlBaseFragment implements IStockView {
    private static final String TAG = "StockFragment";

    private IFragmentStatusListener mFragmentStatusListener;

    private LinearLayout mStockLayout;

    StockFragBinding binding;

    @Override
    public void setLoadedListener(IFragmentStatusListener fragmentStatus) {
        mFragmentStatusListener = fragmentStatus;
    }

    @Override
    public void init(IASRSession session) {
        ((QueryStockSession) session).registerOnShowListener(this);
    }

    @Nullable
    @Override
    public View inflaterView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        binding = DataBindingUtil.inflate(inflater, R.layout.stock_frag, container, false);
        binding.setVariable(BR.event, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.iv_back:
                        getActivity().onBackPressed();
                        break;
                }
            }
        });


        if (mFragmentStatusListener != null)
            mFragmentStatusListener.onLoaded();
        return binding.getRoot();
    }


    @Override
    public void onShowStock(StockBean bean) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd  HH:mm");
        Date now = new Date();
        bean.setCurrentTime(format.format(now));
        bean.setName(bean.getName() + "(" + bean.getCode() + ")");
        BigDecimal b = new BigDecimal(bean.getChangeInPercentage() * 100);
        bean.setCurrentChange(bean.getChangeInPrice() + "\n" + b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "%");

        binding.setBean(bean);
    }

}