package com.example.logapp;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bin.david.form.core.SmartTable;
import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.entity.TableAppInfo;

import java.util.ArrayList;
import java.util.List;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        //显示列表数据
        List<TableAppInfo> list = getList("");
        SmartTable table3 = (SmartTable<TableAppInfo>) findViewById(R.id.table3);
        if(list.size() > 0) {
            table3.setData(list);
            table3.setZoom(true);   //设置可缩放
        }

        //搜索框点击事件
        final EditText editText9 = (EditText) findViewById(R.id.editText9);
        editText9.setOnTouchListener(new View.OnTouchListener() {
            int count=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count== 0){
                    editText9.setText("");
                }
                count++;
                return false;
            }
        });

        //查询按钮事件
        Button button9 = (Button) findViewById(R.id.button9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText9 = (EditText) findViewById(R.id.editText9);
                String s = editText9.getText().toString();
                if(s != null && s != ""){
                    List<TableAppInfo> list = getList(s);
                    SmartTable table3 = (SmartTable<TableAppInfo>) findViewById(R.id.table3);
                    if(list.size() > 0) {
                        table3.setData(list);
                        table3.setZoom(true);   //设置可缩放
                    }
                }
            }
        });

    }

    //根据时间，app名称获取数据
    public List<TableAppInfo> getList(String appName) {
        List<TableAppInfo> list = new ArrayList<>();

        //数据库中查询数据
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(this.getBaseContext(), "appInfo_db",null,1);
        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();

        String queryStr = "select * from app_info";
        if(appName == null || appName.equals("")) {

        }else {
            queryStr += " where app_name like '%" + appName + "%'";
        }

        Cursor cursor1 = db.rawQuery(queryStr, null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                TableAppInfo info = new TableAppInfo();
                info.setAppName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setPackageName(cursor1.getString(cursor1.getColumnIndex("package_name")));
                info.setFirstTimeStamp(cursor1.getLong(cursor1.getColumnIndex("first_running_time")));
                info.setLastTimeUsed(cursor1.getLong(cursor1.getColumnIndex("last_running_time")));
                info.setAppLaunchCount(cursor1.getInt(cursor1.getColumnIndex("lunch_count")));
                info.setTotalTimeInForeground(cursor1.getLong(cursor1.getColumnIndex("total_use_time")));
                list.add(info);
            }
        }
        db.close();
        Log.e("查询结果",list.size() + "条数据");
        return list;
    }

}