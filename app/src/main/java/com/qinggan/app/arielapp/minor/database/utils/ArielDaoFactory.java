package com.qinggan.app.arielapp.minor.database.utils;

import android.content.Context;

import com.qinggan.app.arielapp.minor.database.bean.Account;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviInfo;
import com.qinggan.app.arielapp.minor.database.bean.NaviSearchHistory;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDao;
import com.qinggan.app.arielapp.minor.database.dao.impl.AccountDaoImpl;
import com.qinggan.app.arielapp.minor.database.dao.impl.CardInfoDaoImpl;
import com.qinggan.app.arielapp.minor.database.dao.impl.NaviInfoDaoImpl;
import com.qinggan.app.arielapp.minor.database.dao.impl.NaviSearchHistoryDaoImpl;

/**
 * Created by pateo on 18-11-13.
 */

public class ArielDaoFactory {

    public static BasicDao getArielDaoImpl(Context context, String daoType) {
        BasicDao dao = null;
        if (Account.class.getName().equals(daoType)) {
            dao = AccountDaoImpl.getInstance(context);
        } else if (CardInfo.class.getName().equals(daoType)) {
            dao = CardInfoDaoImpl.getInstance(context);
        } else if (NaviInfo.class.getName().equals(daoType)) {
            dao = NaviInfoDaoImpl.getInstance(context);
        } else if (NaviSearchHistory.class.getName().equals(daoType)) {
            dao = NaviSearchHistoryDaoImpl.getInstance(context);
        }
        return dao;
    }
}
