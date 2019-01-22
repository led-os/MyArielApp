package com.qinggan.app.arielapp.session;

import android.util.Log;

import com.qinggan.app.arielapp.session.media.QueryMediaSession;
import com.qinggan.app.arielapp.session.nav.QueryNavDetailSession;
import com.qinggan.app.arielapp.session.nav.QueryNavSession;
import com.qinggan.app.arielapp.session.stock.QueryStockSession;
import com.qinggan.app.arielapp.session.weather.QueryWeatherSession;
import com.qinggan.app.voiceapi.DataTypeConstant;

import java.util.HashMap;

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

public class SessionFactory {

    private final static String TAG = "SessionFactory";
    private static SessionFactory mInstance;
    HashMap<Integer, IASRSession> mSessionObjectPool;

    public static SessionFactory getInstance() {
        if (mInstance == null) {
            synchronized (SessionFactory.class) {
                if (mInstance == null)
                    mInstance = new SessionFactory();
            }
        }
        return mInstance;
    }

    private SessionFactory() {
        mSessionObjectPool = new HashMap<>();
    }

    public IASRSession obtain(int type) {
        IASRSession session = null;
        if (mSessionObjectPool.containsKey(type)) {
            session = mSessionObjectPool.get(type);
        } else {
            switch (type) {
                case DataTypeConstant.WEATHER_TYPE:
                    session = new QueryWeatherSession();
                    mSessionObjectPool.put(type, session);
                    break;
                case DataTypeConstant.STOCK_TYPE:
                    session = new QueryStockSession();
                    mSessionObjectPool.put(type, session);
                    break;
                case DataTypeConstant.RESTAURANT_TYPE:
                case DataTypeConstant.HOTEL_TYPE:
                case DataTypeConstant.POI_TYPE:
                case DataTypeConstant.SCENIC_TYPE:
                case DataTypeConstant.PARKING_TYPE:
                case DataTypeConstant.HOTEL_DETAIL_TYPE:
                    Log.i(TAG, "type is:" + type);
                    session = new QueryNavSession();
                    mSessionObjectPool.put(type, session);
                    break;
                case DataTypeConstant.SCENIC_DETAIL_TYPE:
                    session = new QueryNavDetailSession();
                    mSessionObjectPool.put(type, session);
                    break;
                case DataTypeConstant.NEWS_PLAY_INFO_TYPE:
                    session = new QueryMediaSession();
                    mSessionObjectPool.put(type, session);
                    break;
            }

        }
        return session;
    }

    public void recycle() {
        mSessionObjectPool.clear();
    }
}
