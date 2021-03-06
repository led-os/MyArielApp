package com.qinggan.app.arielapp.iview;

import com.qinggan.app.voiceapi.bean.DcsBean;
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

public interface IWeatherView {

    void onShowDaysWeather(List<DcsBean> dcsBeans);

    void onShowTodayWeather(WeatherBriefBean brieBean, WeatherForecastBean forecastBean);
}
