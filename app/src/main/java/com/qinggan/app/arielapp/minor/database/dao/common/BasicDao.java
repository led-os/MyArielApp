package com.qinggan.app.arielapp.minor.database.dao.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 18-11-3.
 */

public interface BasicDao<Model, PK> {
    void insertList(List<Model> beanList);

    void insert(Model bean);

    long queryCount();

    Model queryById(PK id);

    List<Model> queryAll();

    void update(Model bean);

    void delete(Model bean);

    List<Model> queryByModel(Model bean);

    List<Model> queryBySeachKey(String columnName, String seachKey, String orderKey, boolean isAsce);

    List<Model> queryByQueryBuildInfo(QueryBuildInfo buildInfo);

    void deleteAll();
}
