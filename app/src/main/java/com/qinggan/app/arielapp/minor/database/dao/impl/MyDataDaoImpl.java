package com.qinggan.app.arielapp.minor.database.dao.impl;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.qinggan.app.arielapp.minor.database.bean.MyBean;
import com.qinggan.app.arielapp.minor.database.dao.MyDataDao;
import com.qinggan.app.arielapp.minor.database.helper.ArielDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pateo on 18-11-3.
 */

public class MyDataDaoImpl implements MyDataDao {
    private ArielDatabaseHelper mHelper;
    private Dao<MyBean, Integer> dao;
    private Context mContext;
    private static MyDataDaoImpl instance;

    protected MyDataDaoImpl(Context context) {
        this.mContext = context;
        try {
            mHelper = ArielDatabaseHelper.getHelper(mContext);
            dao = mHelper.getDao(MyBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static MyDataDaoImpl getInstance(Context context) {
        if (instance == null) {
            synchronized (MyDataDao.class) {
                if (instance == null) {
                    instance = new MyDataDaoImpl(context);
                }
            }

        }
        return instance;
    }


    @Override
    public void insert(MyBean myBean) {


        try {

            //事务操作
           /* TransactionManager.callInTransaction(mHelper.getConnectionSource(), new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    return null;
                }
            });*/


            dao.create(myBean);
            //dao.createOrUpdate(myBean);//和上一行的方法效果一样
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void insert(ArrayList<MyBean> beanArrayList) {
        try {
            dao.create(beanArrayList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(String name, String price) {
        ArrayList<MyBean> list = null;
        try {
            list = (ArrayList<MyBean>) dao.queryForEq("name", name);
            if (list != null) {
                for (MyBean bean : list) {
                    bean.setPrice(price);
                    dao.update(bean);
                    //dao.createOrUpdate(bean);//和上一行的方法效果一样
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update2(String columnName, String columnValue) {
        try {
            //下面这两个代码的意思一样
            dao.updateBuilder().updateColumnValue(columnName, columnValue).update();
            //dao.updateRaw("update Book set " + columnName + "=?", new String[]{columnValue});
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void update3(String queryColumnName, String queryColumnValue, String setColumnName, String setColumnValue) {
        try {
            String sql = "update Book set " + setColumnName + "= '" + setColumnValue + "' where " + queryColumnName + "= '" + queryColumnValue + "'";
            System.out.println("MyDataDao.update3 sql=" + sql);
            dao.updateRaw(sql);

            //dao.updateRaw("update Book set price= '33333元' where name= '西游记'");//等价于上面的写法
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String name) {
        ArrayList<MyBean> list = null;
        try {
            list = (ArrayList<MyBean>) dao.queryForEq("name", name);
            if (list != null) {
                for (MyBean bean : list) {
                    dao.delete(bean);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return -1:删除数据异常  0：无数据
     */
    @Override
    public int deleteAll() {
        int number = -1;
        try {
            number = dao.deleteBuilder().delete();//返回删除的数据条数  例如：删除1条数据，返回1，依次类推。

            //dao.deleteBuilder().where().eq("name", "记").reset();//????
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return number;
    }

    @Override
    public ArrayList<String> queryPrice(String name) {
        List<MyBean> list = null;
        ArrayList<String> strings = null;
        try {
            list = dao.queryForEq("name", name);
            if (list != null) {
                strings = new ArrayList<>();
                for (MyBean myBean : list) {
                    strings.add(myBean.getPrice());
                }
                /*for (int i = 0; i < list.size(); i++) {
                    strings.add(list.get(i).getPrice());
                }*/
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strings;
    }

    @Override
    public String queryAuthor(String name1, String price1) {
        List<MyBean> list = null;
        String author = "";

        try {
            list = dao.queryBuilder().where().eq("name", name1).and().eq("price", price1).query();//上述相当与：select * from Book where name = name1 and price = price1 ;
            if (list != null) {
                for (MyBean myBean : list) {
                    author = myBean.getAuthor();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return author;//说明：如果这个 author 是唯一的，可以这样的返回。如果是多个的话，要返回一个ArrayList<String> 类型
    }

    /**
     * @return 表中数据的个数
     */
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

    /**
     * @param id 这个id 就是表中，每次插入数据，自己递增的id 字段
     */
    @Override
    public ArrayList<MyBean> queryId(int id) {
        ArrayList<MyBean> list = null;

        try {
            MyBean myBean = dao.queryForId(id);
            if (myBean != null) {
                list = new ArrayList<>();
                list.add(myBean);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ArrayList<MyBean> queryAll() {
        ArrayList<MyBean> list = null;
        try {
            list = (ArrayList<MyBean>) dao.queryForAll();


            if (list != null) {
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean delteTables(Context context, String DBname) {
        //?????
        return false;
    }


    /**
     * 这个方法可以的
     */
    public boolean delteDatabases(Context context, String DBname) {
        return context.deleteDatabase(DBname);
    }


}