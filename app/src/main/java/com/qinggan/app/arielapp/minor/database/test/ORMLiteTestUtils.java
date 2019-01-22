package com.qinggan.app.arielapp.minor.database.test;

import android.content.Context;

import com.qinggan.app.arielapp.minor.database.bean.Account;
import com.qinggan.app.arielapp.minor.database.bean.CardInfo;
import com.qinggan.app.arielapp.minor.database.dao.AccountDao;
import com.qinggan.app.arielapp.minor.database.dao.common.QueryBuildInfo;
import com.qinggan.app.arielapp.minor.database.dao.common.QueryItem;
import com.qinggan.app.arielapp.minor.database.dao.impl.AccountDaoImpl;
import com.qinggan.app.arielapp.minor.integration.PateoDatabaseCMD;
import com.qinggan.app.arielapp.minor.phone.utils.CallUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pateo on 18-11-7.
 */

public class ORMLiteTestUtils {

    private static final String TAG = "ORMLiteTestUtils";

    public static void testInsertAccount(Context mContext) {
        Account bean = new Account();
        bean.setCreateDate(new Date());
        bean.setLastModifiedDate(new Date());
        bean.setCarLicensePlate("bbbb");
        /*byte[] imageByte=  DatataseUtils.image2byte(Environment.getExternalStorageDirectory()+"/123.jpg");
        bean.setImage(imageByte);*/
        AccountDao dao = AccountDaoImpl.getInstance(mContext);
        dao.insert(bean);
        CallUtils.logd(TAG, "insert Account:" + bean);
    }

    public static void testQueryAccounts(Context mContext) {
        AccountDaoImpl dao = AccountDaoImpl.getInstance(mContext);
        List<Account> list = dao.queryAll();
        for (Account bean : list) {
            CallUtils.logd(TAG, "get Account:" + bean);
            /*if (bean.getImage() != null) {
                DatataseUtils.byte2image(bean.getImage(), Environment.getExternalStorageDirectory() + "/" + bean.getCarLicensePlate() + "123.jpg");
            }*/
        }
    }

    public static void testInsertCardInfo(Context mContext) {
        CardInfo bean = new CardInfo();
        bean.setContent("card1");
        bean.setMessage("test card");
        /*CardInfoDao dao = CardInfoDaoImpl.getInstance(mContext);
        dao.insert(bean);*/


        PateoDatabaseCMD.getInstance().insert(bean,mContext,CardInfo.class.getName());
        CallUtils.logd(TAG, "insert CardInfo:" + bean);
    }

    public static void testDeleteCardInfo(Context mContext,CardInfo bean) {
        /*BasicDaoImpl dao = CardInfoDaoImpl.getInstance(mContext);
        dao.delete(bean);*/
        PateoDatabaseCMD.getInstance().delete(bean,mContext,bean.getClass().getName());
        CallUtils.logd(TAG, "delete CardInfo:" + bean);
    }

    public static void testUpdateCardInfo(Context mContext,CardInfo bean) {
        //BasicDaoImpl dao = CardInfoDaoImpl.getInstance(mContext);
        bean.setContent(bean.getContent() + " update");
        bean.setMessage(bean.getMessage() + " update");
        // dao.update(bean);
        PateoDatabaseCMD.getInstance().update(bean,mContext,bean.getClass().getName());

        CallUtils.logd(TAG, "update CardInfo:" + bean);
    }

    public static List<CardInfo> testQueryCardInfos(Context mContext) {
        /*CardInfoDao dao = CardInfoDaoImpl.getInstance(mContext);
        List<CardInfo> list = dao.queryAll();*/

        QueryItem item1 = new QueryItem();
        item1.setColumnName("name");
        item1.setSeachKey("card");
        item1.setConditionSymbol("like");

        QueryItem item2 = new QueryItem();
        item2.setColumnName("message");
        item2.setSeachKey("test");
        item2.setConditionSymbol("like");

        List<QueryItem> queryItemList = new ArrayList<QueryItem>();
        queryItemList.add(item1);
        queryItemList.add(item2);

        QueryBuildInfo queryBuildInfo= new QueryBuildInfo();
        queryBuildInfo.setQueryItemList(queryItemList);

        List<CardInfo> list = (List<CardInfo>) (List) PateoDatabaseCMD.getInstance()
                .queryByQueryBuildInfo(queryBuildInfo, mContext, CardInfo.class.getName());


        /*List<CardInfo> list = (List<CardInfo>) (List) PateoDatabaseCMD.getInstance()
                .queryBySeachKey("name","","id",false, mContext, CardInfo.class.getContent());*/
        //List<CardInfo> list =  (List<CardInfo>)(List) PateoDatabaseCMD.getInstance().queryAll(mContext,CardInfo.class.getContent());

        for (CardInfo bean : list) {
            CallUtils.logd(TAG, "get CardInfo:" + bean);
        }
        return list;
    }

}
