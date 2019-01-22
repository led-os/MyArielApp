package com.qinggan.app.arielapp.minor.integration;

import android.content.Context;

import com.qinggan.app.arielapp.minor.core.DatabaseInterface;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDao;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.QueryBuildInfo;
import com.qinggan.app.arielapp.minor.database.utils.ArielDaoFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brian on 18-10-30.
 */

public class PateoDatabaseCMD implements DatabaseInterface{

    private static PateoDatabaseCMD instance;

    public void insertList(List<BasicInfo> beanList, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        dao.insertList(beanList);
    }

    public void insert(BasicInfo bean, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        dao.insert(bean);
    }


    public BasicInfo queryById(Integer pk, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        return (BasicInfo) dao.queryById(pk);
    }

    public List<BasicInfo> queryAll(Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        return (List<BasicInfo>) dao.queryAll();
    }


    public void update(BasicInfo bean, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        dao.update(bean);
    }

    public void delete(BasicInfo bean, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        dao.delete(bean);
    }

    @Override
    public List<BasicInfo> queryBySeachKey(String columnName, String seachKey, String orderKey,
                                           boolean isAsce, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        return dao.queryBySeachKey(columnName, seachKey, orderKey, isAsce);
    }

    @Override
    public List<BasicInfo> queryBySeachKey(String columnName, String seachKey, String orderKey,
                                           boolean isAsce, Context context, String daoType, int limit) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        List<BasicInfo> list = dao.queryBySeachKey(columnName, seachKey, orderKey, isAsce);

        List<BasicInfo> returnList = new ArrayList<BasicInfo>();
        if (limit < list.size()) {
            for (int i = 0; i <= limit; i++) {
                returnList.add(list.get(i));
            }
        }

        return list;
    }

    @Override
    public List<BasicInfo> queryByQueryBuildInfo(QueryBuildInfo queryBuildInfo, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        List<BasicInfo> list = dao.queryByQueryBuildInfo(queryBuildInfo);
        return list;
    }

    @Override
    public void deleteAll(Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        dao.deleteAll();
    }


    @Override
    public List<BasicInfo> queryByModel(BasicInfo bean, Context context, String daoType) {
        BasicDao dao = ArielDaoFactory.getArielDaoImpl(context, daoType);
        return dao.queryByModel(bean);
    }

    public static PateoDatabaseCMD getInstance() {

        if (instance == null) {
            instance = new PateoDatabaseCMD();
        }
        return instance;
    }
}
