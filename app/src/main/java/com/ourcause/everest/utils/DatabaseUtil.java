package com.ourcause.everest.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by infonous on 16-9-3.
 *
 *
 */
public class DatabaseUtil {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    //打开/创建数据库
    public DatabaseUtil(Context context, String databaseName, Integer version){
        dbHelper = new DatabaseHelper(context, databaseName, version);
        db = dbHelper.getWritableDatabase();
    }

    //执行拼凑的 SQL 语句
    public void execSQL(String sql){

        db.beginTransaction();  //开始事务

        try {

            db.execSQL(sql);

            db.setTransactionSuccessful();  //设置事务成功完成

        } finally {

            db.endTransaction();    //结束事务

        }
    }

    //以参数形式执行
    public void execSQL(String sql, Object[] args){

        db.beginTransaction();  //开始事务

        try {

            db.execSQL(sql, args);

            db.setTransactionSuccessful();  //设置事务成功完成

        } finally {

            db.endTransaction();    //结束事务

        }
    }

    //查询语句
    public Cursor rawQuerySQL(String sql, String[] args ){

        return db.rawQuery(sql, args);

    }

    //删除当前数据库
    public boolean uninstallDatabase(Context context, String databaseName) {

        return context.deleteDatabase(databaseName);
    }

}
