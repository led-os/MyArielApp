package com.qinggan.app.arielapp.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.util.Log;

import com.qinggan.app.arielapp.user.Bean.UserInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserInfoDBHelper extends SQLiteOpenHelper
{
	private static final String TAG = "UserInfoDBHelper";
    private static final String DB_NAME="account.db";
    public static final String TABLE_USER="users";
    public SQLiteDatabase db;
    private Context mContext;
    private static UserInfoDBHelper instance = null;
    public final void openDataBase()
    {
      try {
        this.db = getWritableDatabase();
        Log.d(TAG, "+++openDataBase() open database!!!");
      } catch (IllegalStateException e) {
        this.db = null;
      } catch (SQLiteException e) {
        this.db = null;
      }
    }

    public static UserInfoDBHelper getInstance(Context context) {
      if (instance != null){
        return instance;
      }
      synchronized (UserInfoDBHelper.class) {
        if (instance == null){
          instance = new UserInfoDBHelper(context.getApplicationContext());
        }
        if(instance.db == null){
      	  instance = null;
        }
      }
      return instance;
    }
    
    public static byte[] bmpToByteArray(Bitmap bmp) {
        // Default size is 32 bytes  
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {  
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.close();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
        return bos.toByteArray();  
    }  

    public UserInfoDBHelper(Context c){
    	super(c, DB_NAME, null, 1);
    	Log.e(TAG,"UserInfoDBHelper() +++");
		this.mContext = c;
		openDataBase();

    };
    


    public void onCreate(SQLiteDatabase db)
    {
        this.db=db;
        Log.e(TAG,"onCreate() +++");
        db.execSQL(UserInfo.getCreateTableSql());

    }
    

    public long insert(String table, ContentValues values)
    {
        return db.insert(table, null, values);
    }

    public Cursor query(String table)
    {
        Cursor c=db.query(table, null, null, null, null, null, null);
        return c;
    }
    
    
    public Cursor sqlQuery(String sql)
    {
        Cursor c=db.rawQuery(sql, null);
        return c;
    }
    
    public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sort)
    {
	      SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	      if ((this.db == null) || (!this.db.isOpen())){
	        return null;
	      }
	      qb.setTables(table);
	      if (selectionArgs != null) {
	        for (String str : selectionArgs){
	          Log.e(TAG, "query() selectionArgs=" + str);
	        }
	      }
	      Cursor cursor = qb.query(this.db, projection, selection, selectionArgs, null, null, sort, null);
	      return cursor;
    }
    
    public int delete(String table, String where, String[] whereArgs) {
	        int count = 0;
	        if ((this.db == null) || (!this.db.isOpen())) {
	          return count;
	        }
	        count = this.db.delete(table, where, whereArgs);
	        return count;
    }
    
    public int update(String table, ContentValues newValues, String userWhere, String[] whereArgs)
    {
      int count = 0;
      if ((this.db == null) || (!this.db.isOpen())) {
        return count;
      }
        if (newValues.size() > 0){
	          try {
	            count = this.db.update(table, newValues, userWhere, whereArgs);
	          } catch (Exception e) {
	            e.printStackTrace();
	          }
        }
        else {
          count = 0;
        }

      return count;
    }
    
    public void colse()
    {
        if(db!=null)
        {
            db.close();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
         
    }
    


	

}