package com.qinggan.app.arielapp.minor.database.dao;

import com.qinggan.app.arielapp.minor.database.bean.Account;
import com.qinggan.app.arielapp.minor.database.bean.MyBean;

import java.util.ArrayList;

/**
 * Created by pateo on 18-11-3.
 */

public interface MyDataDao {


    void insert(ArrayList<MyBean> beanArrayList);

    void insert(MyBean myBean);

    void update(String name, String price);

    void update2(String columnName, String columnValue);

    void update3(String queryColumnName, String queryColumnValue, String setColumnName, String setColumnValue);


    void delete(String name);

    int deleteAll();


    ArrayList<String> queryPrice(String name);

    String queryAuthor(String name, String price);

    long queryCount();

    ArrayList<MyBean> queryId(int id);

    ArrayList<MyBean> queryAll();

}
