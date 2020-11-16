package com.example.logapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.entity.LaunchSum;
import com.example.logapp.entity.RunInfo;
import com.example.logapp.entity.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class LineActivity extends AppCompatActivity {

    List<RunInfo> runInfos = new ArrayList<>();
    List<LaunchSum> launchSumList = new ArrayList<>();  //统计最近七天

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        Intent intent = getIntent();
        String data = intent.getStringExtra("appName");
        Log.e("intent", data);
        getRunList(data);



    }

    public void getRunList(String appName) {
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(getApplicationContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "'", null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            Log.e("查询数据长度",cursor1.getCount() + "");
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                RunInfo info = new RunInfo();
                info.setAppName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setPackageName(cursor1.getString(cursor1.getColumnIndex("package_name")));
                info.setStartStamp(cursor1.getLong(cursor1.getColumnIndex("start_time")));
                info.setEndStamp(cursor1.getLong(cursor1.getColumnIndex("end_time")));
                info.setUseStamp(cursor1.getLong(cursor1.getColumnIndex("use_time")));
                runInfos.add(info);
            }
        }
        LaunchSum launchSum1 = new LaunchSum();
        





        db.close();
    }

}