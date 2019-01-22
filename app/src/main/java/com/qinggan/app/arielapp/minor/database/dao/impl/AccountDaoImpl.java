package com.qinggan.app.arielapp.minor.database.dao.impl;


import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.qinggan.app.arielapp.minor.database.bean.Account;
import com.qinggan.app.arielapp.minor.database.dao.AccountDao;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDaoImpl;
import com.qinggan.app.arielapp.minor.database.helper.ArielDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by pateo on 18-11-6.
 */

public class AccountDaoImpl extends BasicDaoImpl<Account, Integer> implements AccountDao {

    private static AccountDaoImpl instance;
    private final Context mContext;
    private ArielDatabaseHelper mHelper;


    protected AccountDaoImpl(Context context) {
        this.mContext = context;
        try {
            mHelper = ArielDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(Account.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static AccountDaoImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (AccountDao.class) {
                if (instance == null) {
                    instance = new AccountDaoImpl(context);
                }
            }

        }
        return instance;
    }
}
