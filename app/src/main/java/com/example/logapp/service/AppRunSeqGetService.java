package com.example.logapp.service;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.entity.RunInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AppRunSeqGetService extends Service {
    private static final String TAG = "AppRunSeqGetService";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "OnStartCommand__Running");

        //查询从某一时间段到另一时间段的总运行时间
        Long currentTime = Calendar.getInstance().getTimeInMillis();
        Long startTime = currentTime - 7*24*60*60*1000;
        HashMap<String, String> packagesMap = getAllPackages();
        for(String s : packagesMap.keySet()) {
            Log.e(s,packagesMap.get(s));
        }
        HashMap<Integer, RunInfo> infoMap = getTimeSpent(this.getBaseContext(),packagesMap.keySet(),startTime,currentTime);
        for(Integer i : infoMap.keySet()) {
            infoMap.get(i).setAppName(packagesMap.get(infoMap.get(i).getPackageName()));
            Log.e(i+ "",infoMap.get(i).toString());
        }
        Log.e(TAG,"from 1604810582000 to " + currentTime);

        //获取当前数据库中最新的结果（只保存最新结果之后的数据，防止数据重复）
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(this.getBaseContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("select * from app_run_info order by aid desc limit 0,1", null);
        int lastStartTimeStamp = 0;
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                lastStartTimeStamp = cursor1.getInt(cursor1.getColumnIndex("start_time"));
            }
        }

        //存入新的数据
        for(Integer i : infoMap.keySet()){
            ContentValues values = new ContentValues();
            RunInfo runInfo = infoMap.get(i);
            if(lastStartTimeStamp > runInfo.getStartStamp()) {
                continue;   //时间小于最后一条记录的时间，则不保存
            }
            values.put("app_name", runInfo.getAppName());
            values.put("package_name", runInfo.getPackageName());
            values.put("start_time", runInfo.getStartStamp());
            values.put("end_time", runInfo.getEndStamp());
            values.put("use_time", runInfo.getUseStamp());
            //数据库执行插入命令
            db.insert("app_run_info", null, values);
        }
        //关闭数据库
        db.close();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "OnBind__Running");
        return null;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "OnDestroy__Running");
        super.onDestroy();
    }

    //获取所有包名与对应的app名称 hash<package名，app名>
    HashMap<String, String> getAllPackages() {
        HashMap<String, String> appInfoHash = new HashMap<>();
        //封装数据存入SQLite
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(this.getBaseContext(), "appInfo_db",null,1);
        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select app_name,package_name from app_info", null);
        //遍历保存数据
        if(cursor != null && cursor.getCount() > 0) {   //判断结果集是否有效
            while (cursor.moveToNext()) {   //游标是否继续向下移动
                appInfoHash.put(cursor.getString(cursor.getColumnIndex("package_name")), cursor.getString(cursor.getColumnIndex("app_name")));
            }
        }
        //关闭数据库
        db.close();
        return appInfoHash;
    }

    //计算从beginTime到endTime名称在packageNames中的App的运行时间
    HashMap<Integer, RunInfo> getTimeSpent(Context context, Set<String> packageNames, long beginTime, long endTime) {
        UsageEvents.Event currentEvent;         //当前event
        List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<Integer, RunInfo> appUsageMap = new HashMap<>();

        UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);

        //获取从start到end的所有app使用event
        while (usageEvents.hasNextEvent()) {
            currentEvent = new UsageEvents.Event();
            usageEvents.getNextEvent(currentEvent);
            if(packageNames.contains(currentEvent.getPackageName()) || packageNames == null) {  //当前event是否是要寻找的包
                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED       //应用程序进入前台时的时间戳
                        || currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {  //进入后台时的时间戳
                    allEvents.add(currentEvent);
                }
            }
        }

        /*//打印采集数据日志
        for(UsageEvents.Event event : allEvents) {
            Log.e("asd","out");
            Log.e(event.getPackageName(),event.getTimeStamp() + ", " + event.getEventType());
        }*/

        //数据处理
        for (int i = 0; i < allEvents.size() - 1; i++) {
            UsageEvents.Event E0 = allEvents.get(i);
            UsageEvents.Event E1 = allEvents.get(i + 1);

            if (E0.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                    && E1.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED
                    && E0.getClassName().equals(E1.getClassName())) {       //当前前一个是启动，后一个是关闭时
                int diff = (int)(E1.getTimeStamp() - E0.getTimeStamp());    //计算在前台显示时间
                RunInfo runInfo = new RunInfo();        //封装运行信息对象
                runInfo.setStartStamp(E0.getTimeStamp());
                runInfo.setEndStamp(E1.getTimeStamp());
                runInfo.setUseStamp(diff);
                runInfo.setPackageName(E0.getPackageName());

                appUsageMap.put(i, runInfo);
            }
        }

        /*UsageEvents.Event lastEvent = allEvents.get(allEvents.size() - 1);
        if(lastEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
            int diff = (int)System.currentTimeMillis() - (int)lastEvent.getTimeStamp();
            diff /= 1000;
            Integer prev = appUsageMap.get(lastEvent.getPackageName());
            if(prev == null) prev = 0;
            appUsageMap.put(lastEvent.getPackageName(), prev + diff);
        }*/

        return appUsageMap;
    }

}
