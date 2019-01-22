package com.qinggan.app.arielapp.minor.database.dao.impl;


import android.content.Context;

import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.NaviSearchHistoryDao;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDaoImpl;
import com.qinggan.app.arielapp.minor.database.helper.ArielDatabaseHelper;

import java.sql.SQLException;

/**
 * Created by pateo on 18-11-6.
 */

public class NaviSearchHistoryDaoImpl extends BasicDaoImpl<NaviSearchHistory, Integer> implements NaviSearchHistoryDao {

    private static NaviSearchHistoryDaoImpl instance;
    private final Context mContext;
    private ArielDatabaseHelper mHelper;


    protected NaviSearchHistoryDaoImpl(Context context) {
        this.mContext = context;
        try {
            mHelper = ArielDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(NaviSearchHistory.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static NaviSearchHistoryDaoImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (NaviSearchHistory.class) {
                instance = new NaviSearchHistoryDaoImpl(context);
            }

        }
        return instance;
    }
}
