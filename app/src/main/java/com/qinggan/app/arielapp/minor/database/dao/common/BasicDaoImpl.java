package com.qinggan.app.arielapp.minor.database.dao.common;

import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.qinggan.app.arielapp.minor.database.dao.common.BasicDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by pateo on 18-11-10.
 */

public class BasicDaoImpl<Model, PK> implements BasicDao<Model, PK> {

    private static final String TAG = "BasicDaoImpl";
    public Dao<Model, PK> dao;

    @Override
    public void insertList(List<Model> beanList) {
        try {
            dao.create(beanList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insert(Model bean) {
        try {
            dao.create(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long queryCount() {
        long number = 0;
        try {
            number = dao.queryBuilder().countOf();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

    @Override
    public Model queryById(PK id) {
        Model model = null;
        try {
            model = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    @Override
    public List<Model> queryAll() {
        List<Model> list = null;
        try {
            list = (List<Model>) dao.queryForAll();

            if (list != null) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void update(Model bean) {
        try {
            dao.update(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void delete(Model bean) {
        try {
            dao.delete(bean);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Model> queryByModel(Model bean) {
        List<Model> list = null;
        try {
            list = (List<Model>) dao.queryForMatching(bean);

            if (list != null) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Model> queryBySeachKey(String columnName, String seachKey, String orderKey, boolean isAsce) {
        List<Model> list = null;
        try {
            if (orderKey != null && !orderKey.equals("")) {
                list = (List<Model>) dao.queryBuilder().orderBy(orderKey, isAsce).where().
                        like(columnName, "%" + seachKey + "%").query();
            } else {
                list = (List<Model>) dao.queryBuilder().where().like(columnName, "%" + seachKey + "%").query();
            }

            if (list != null) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Model> queryByQueryBuildInfo(QueryBuildInfo buildInfo) {
        List<Model> list = null;
        QueryBuilder queryBuilder = dao.queryBuilder();

        try {
            if (buildInfo.isDistinct()) {
                queryBuilder.distinct();
            }
            if (buildInfo.getLimit() > 0) {
                queryBuilder.limit(buildInfo.getLimit());
            }
            if (buildInfo.getOffset() > 0) {
                queryBuilder.offset(buildInfo.getOffset());
            }

            if (buildInfo.getGroupBy() != null && !buildInfo.getGroupBy().equals("")) {
                queryBuilder.groupBy(buildInfo.getGroupBy());
            }

            if (buildInfo.getOrderBy() != null && !buildInfo.getOrderBy().equals("")) {
                queryBuilder.orderBy(buildInfo.getOrderBy(), buildInfo.isAsec());
            }

            List<QueryItem> queryItemList = buildInfo.getQueryItemList();
            Where where = queryBuilder.where();
            if (queryItemList != null && queryItemList.size() > 0) {

                for (int i = 0; i < queryItemList.size(); i++) {

                    QueryItem item = queryItemList.get(i);
                    if (!item.isAvailable()) {
                        continue;
                    }

                    String columnName = item.getColumnName();
                    String value = item.getSeachKey();
                    boolean isAnd = item.isAnd();
                    String conditionSymbol = item.getConditionSymbol();

                    if (i > 0) {
                        if (isAnd) {
                            where.and();
                        } else {
                            where.or();
                        }
                    }
                    where = packageWhere(where, columnName, value, conditionSymbol);

                }
            } else {
                where.gt("id", 0);
            }
            Log.d(TAG, "query where:" + where.getStatement());
            Log.d(TAG, "queryBuilder where:" + queryBuilder.toString());

            /*if ((buildInfo.getColumnName() != null && !buildInfo.getColumnName().equals(""))
                    && buildInfo.getSeachKey() != null && !buildInfo.getSeachKey().equals("")) {
                queryBuilder.where().like(buildInfo.getColumnName(), "%" + buildInfo.getSeachKey() + "%");
            }*/

            list = (List<Model>) where.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private Where packageWhere(Where where, String columnName, String value, String conditionSymbol) throws SQLException {
        //only support,eq/ne/gt/lt/ge/le/like
        if (conditionSymbol != null && !"".equals(conditionSymbol)) {
            if ("like".equals(conditionSymbol.toLowerCase())) {
                value = "%" + value + "%";
                where.like(columnName, value);
            } else if (conditionSymbol.equals("eq")) {
                where.eq(columnName, value);
            } else if (conditionSymbol.equals("ne")) {
                where.ne(columnName, value);
            } else if (conditionSymbol.equals("gt")) {
                where.gt(columnName, value);
            } else if (conditionSymbol.equals("lt")) {
                where.lt(columnName, value);
            } else if (conditionSymbol.equals("ge")) {
                where.ge(columnName, value);
            } else if (conditionSymbol.equals("le")) {
                where.le(columnName, value);
            }
        }
        return where;
    }

    @Override
    public void deleteAll() {
        DeleteBuilder deleteBuilder = dao.deleteBuilder();
        try {
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
