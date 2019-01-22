package com.qinggan.app.arielapp.minor.database.dao.impl;


import android.content.Context;

import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.dao.NaviInfoDao;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDaoImpl;
import com.qinggan.app.arielapp.minor.database.helper.ArielDatabaseHelper;

import java.sql.SQLException;

/**
 * Created by pateo on 18-11-6.
 */

public class NaviInfoDaoImpl extends BasicDaoImpl<NaviInfo, Integer> implements NaviInfoDao {

    private static NaviInfoDaoImpl instance;
    private final Context mContext;
    private ArielDatabaseHelper mHelper;


    protected NaviInfoDaoImpl(Context context) {
        this.mContext = context;
        try {
            mHelper = ArielDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(NaviInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static NaviInfoDaoImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (NaviInfo.class) {
                instance = new NaviInfoDaoImpl(context);
            }

        }
        return instance;
    }
}
