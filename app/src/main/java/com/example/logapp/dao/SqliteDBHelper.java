package com.example.logapp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteDBHelper extends SQLiteOpenHelper {

    // 步骤1：设置常数参量
    private static final String DATABASE_NAME = "appInfo_db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "app_info";

    // 步骤2：重载构造方法
    public SqliteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
     * 参数介绍：context 程序上下文环境 即：XXXActivity.this
     * name 数据库名字
     * factory 接收数据，一般情况为null
     * version 数据库版本号
     */
    public SqliteDBHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }
    //数据库第一次被创建时，onCreate()会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 步骤3：数据库表的创建
        String strSQL = "create table "
                + TABLE_NAME
                + "(aid integer primary key autoincrement NOT NULL, " +
                "app_name varchar(20), " +
                "package_name varchar(30), " +
                "first_running_time INT, " +
                "last_running_time INT, " +
                "lunch_count INT, " +
                "total_use_time INT)";
        //步骤4：使用参数db,创建对象
        db.execSQL(strSQL);
    }
    //数据库版本变化时，会调用onUpgrade()
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }
}