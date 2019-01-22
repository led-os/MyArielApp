package com.qinggan.app.arielapp.session.nav;

import com.qinggan.app.arielapp.iview.INavPoiView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;

import java.util.List;

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

public class QueryNavSession implements IASRSession {

    private INavPoiView mNavPoiViewListener;

    @Override
    public void handleASRFeedback(DcsDataWrapper wrapper) {

        if (wrapper != null) {
            List<DcsBean> beans = wrapper.getDcsBeanArray();
            int beanType = wrapper.getType();

            if (wrapper.getDcsBean() != null) {
                if (mNavPoiViewListener != null) {
                    mNavPoiViewListener.onShowDetail(wrapper.getDcsBean(), beanType);
                }
            } else {
                if (wrapper.getDcsBeanArray() != null && wrapper.getDcsBeanArray().size() > 0) {
                    if (mNavPoiViewListener != null) {
                        mNavPoiViewListener.onShowPoi(beans, beanType);
                    }
                }
            }
        }
    }

    public void registerOnShowListener(INavPoiView iNavPoiView) {

        mNavPoiViewListener = iNavPoiView;
    }
}
