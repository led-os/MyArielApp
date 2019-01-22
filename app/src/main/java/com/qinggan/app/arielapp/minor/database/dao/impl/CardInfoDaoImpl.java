package com.qinggan.app.arielapp.minor.database.dao.impl;


import android.content.Context;

import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.dao.CardInfoDao;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDaoImpl;
import com.qinggan.app.arielapp.minor.database.helper.ArielDatabaseHelper;

import java.sql.SQLException;

/**
 * Created by pateo on 18-11-6.
 */
public class CardInfoDaoImpl extends BasicDaoImpl<CardInfo, Integer> implements CardInfoDao {

    private static CardInfoDaoImpl instance;
    private final Context mContext;
    private ArielDatabaseHelper mHelper;


    protected CardInfoDaoImpl(Context context) {
        this.mContext = context;
        try {
            mHelper = ArielDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(CardInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static CardInfoDaoImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (CardInfo.class) {
                if (instance == null) {
                    instance = new CardInfoDaoImpl(context);
                }
            }

        }
        return instance;
    }
}
