package com.qinggan.app.arielapp.session.nav;

import com.qinggan.app.arielapp.iview.INavPoiDetailView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;

/*******************************************************************************
 *
 * @author : Pateo harrishuang@pateo.com.cn
 *
 * Copyright (c) 2017-2020 Pateo. All Rights Reserved.
 *
 * Copying of this document or code and giving it to others and the 
 * use or communication of the contents thereof, are forbidden without
 * expressed authority. Offenders are liable to the payment of damages.
 * All rights reserved in the event of the grant of a invention patent or the 
 * registration of a utility model, design or code.
 *
 * Issued by Pateo.
 * Date: 2018-03-29
 *******************************************************************************/

public class QueryNavDetailSession implements IASRSession {

    private INavPoiDetailView mNavPoiDetailViewListener;

    @Override
    public void handleASRFeedback(DcsDataWrapper wrapper) {
        DcsBean bean = wrapper.getDcsBean();
        int beanType = wrapper.getType();
        if (bean != null && mNavPoiDetailViewListener != null) {
            mNavPoiDetailViewListener.onShowPoiDetail(bean, beanType);
        }
    }

    public void registerOnShowListener(INavPoiDetailView iNavPoiDetailView) {
        mNavPoiDetailViewListener = iNavPoiDetailView;
    }
}
