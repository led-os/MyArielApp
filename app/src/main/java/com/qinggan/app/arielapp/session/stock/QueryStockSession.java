package com.qinggan.app.arielapp.session.stock;

import com.qinggan.app.arielapp.iview.IStockView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.common.StockBean;

/**
 * Created by shuohuang on 18-5-2.
 */

public class QueryStockSession implements IASRSession {

    private IStockView mIStockView;

    @Override
    public void handleASRFeedback(DcsDataWrapper wrapper) {
        if (wrapper.getDcsBean() != null) {
            if (mIStockView != null) {
                mIStockView.onShowStock((StockBean) wrapper.getDcsBean());
            }
        }

    }

    public void registerOnShowListener(IStockView iStockView) {
        mIStockView = iStockView;
    }

}
