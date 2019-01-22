package com.qinggan.app.arielapp.minor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.qinggan.app.arielapp.ArielApplication;

import java.util.ArrayList;

public class ShardPreUtils {
    Context mContext;
    SharedPreferences sharedPreferences;
    private static volatile ShardPreUtils mShardPreUtils;
    private static final Object mLock = new Object();
    SharedPreferences.Editor editor;
    ArrayList<String> mList = new ArrayList<String>();

    private ShardPreUtils(String fileName){
        mContext = ArielApplication.getApp().getApplicationContext();
        sharedPreferences = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static ShardPreUtils getInstance(String fileName) {
        synchronized (mLock) {
            if (mShardPreUtils == null) {
                mShardPreUtils = new ShardPreUtils(fileName);
            }
        }
        return mShardPreUtils;
    }

    public void putStringValue(String key,String value){
        editor = sharedPreferences.edit();
        editor.putString(key, value).commit();
    }

    public void putIntValue(String key,int value){
        editor = sharedPreferences.edit();
        editor.putInt(key, value).commit();
    }

    public void putLongValue(String key,Long value){
        editor = sharedPreferences.edit();
        editor.putLong(key, value).commit();
    }

    public void putStringValues(ArrayList<String> keys,ArrayList values){
        //TODO
    }

    public void putOldList(String key,ArrayList<String> list){
        editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for(String temp:list){
            sb.append(temp);
            sb.append(",");
        }
        editor.putString(key, sb.toString()).commit();
    }

    public ArrayList<String> getOldList(String key){
        if(mList != null) mList.clear();
        String array = sharedPreferences.getString(key, null);
        if(array != null && !array.equals("")){
            String[] sArray = array.split(",");
            for(String temp:sArray){
                if(temp != null && !temp.equals("")){
                    mList.add(temp);
                }
            }
            return mList;
        }
        return null;
    }

    public String getStringValue(String key){
        return sharedPreferences.getString(key, null);
    }

    public int getIntValue(String key){
        return sharedPreferences.getInt(key, 0);
    }

    public long getLongValue(String key){
        return sharedPreferences.getLong(key, 0);
    }

    public ArrayList<String> getStringValues(ArrayList<String> keys){
        return null;
    }

    public ArrayList getIntValues(ArrayList<String> keys){
        return null;
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
