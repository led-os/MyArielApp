package com.qinggan.app.arielapp.session.weather;

import com.qinggan.app.arielapp.iview.IWeatherView;
import com.qinggan.app.arielapp.session.IASRSession;
import com.qinggan.app.voiceapi.bean.DcsBean;
import com.qinggan.app.voiceapi.bean.DcsDataWrapper;
import com.qinggan.app.voiceapi.bean.common.WeatherBriefBean;
import com.qinggan.app.voiceapi.bean.common.WeatherForecastBean;

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
 * Date: 2018-03-27
 *******************************************************************************/

public class QueryWeatherSession implements IASRSession {

    private IWeatherView mIWeatherView;

    @Override
    public void handleASRFeedback(DcsDataWrapper wrapper) {
        if (null == wrapper.getDcsBean() || wrapper.getDcsBeanArray() == null || wrapper.getDcsBeanArray().size() < 1) {
            return;
        }

        WeatherBriefBean weatherBriefBean = null;
        WeatherForecastBean weatherForecastBean = null;
        if (wrapper.getDcsBean() != null) {
            weatherBriefBean = (WeatherBriefBean) wrapper.getDcsBean();
            weatherForecastBean = (WeatherForecastBean) wrapper.getDcsBeanArray().get(0);
            mIWeatherView.onShowTodayWeather(weatherBriefBean, weatherForecastBean);
        }

        mIWeatherView.onShowDaysWeather(wrapper.getDcsBeanArray());
    }

    public void registerOnShowListener(IWeatherView iWeatherView) {
        mIWeatherView = iWeatherView;
    }

}
