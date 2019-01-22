package com.qinggan.app.arielapp.minor.core;

import android.content.Context;

import com.qinggan.app.arielapp.minor.database.dao.common.BasicInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.QueryBuildInfo;

import java.util.List;

/**
 * Created by brian on 18-10-30.
 */

public interface DatabaseInterface {
    void insertList(List<BasicInfo> beanList, Context context, String daoType);

    void insert(BasicInfo bean, Context context, String daoType);

    BasicInfo queryById(Integer pk, Context context, String daoType);

    List<BasicInfo> queryAll(Context context, String daoType);

    void update(BasicInfo bean, Context context, String daoType);

    void delete(BasicInfo bean, Context context, String daoType);

    List<BasicInfo> queryBySeachKey(String columnName, String seachKey, String orderKey, boolean isAsce, Context context, String daoType);

    List<BasicInfo> queryBySeachKey(String columnName, String seachKey, String orderKey, boolean isAsce, Context context, String daoType,int limit);

    List<BasicInfo> queryByModel(BasicInfo bean, Context context, String daoType);

    List<BasicInfo> queryByQueryBuildInfo(QueryBuildInfo queryBuildInfo, Context context, String daoType);

    void deleteAll(Context context, String daoType);
}
