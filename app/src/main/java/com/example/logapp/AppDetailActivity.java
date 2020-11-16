package com.example.logapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.entity.App;
import com.example.logapp.entity.AppInfo;
import com.example.logapp.util.AppAdapter;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppDetailActivity extends AppCompatActivity {
    private List<App> appList = new ArrayList<>();
    public static final String EXTRA_MESSAGE = "appName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        //显示列表数据
        appList = getList("");
        AppAdapter adapter = new AppAdapter(AppDetailActivity.this, R.menu.app_info_view, appList);
        ListView listView = (ListView) findViewById(R.id.app_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Intent intent = new Intent(getApplicationContext(), LineActivity.class);
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                App app = appList.get(i);
                Log.e("msee",app.getAppName());
                intent.putExtra(EXTRA_MESSAGE,app.getAppName());
                startActivity(intent);
            }
        });

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
                    appList = getList(s);
                    AppAdapter adapter = new AppAdapter(AppDetailActivity.this, R.menu.app_info_view, appList);
                    ListView listView = (ListView) findViewById(R.id.app_list_view);
                    listView.setAdapter(adapter);
                }
            }
        });

        //刷新按钮点击事件
        Button button8 = (Button) findViewById(R.id.button8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //收集获取APP信息
                List<ApplicationInfo> applicationInfoList = queryFilterAppInfo();
                //所有数据封装成appInfo类(这里获取的是所有能打开app的应用程序)
                ArrayList<AppInfo> appInfoList = new ArrayList<>();
                for(ApplicationInfo applicationInfo : applicationInfoList) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppName(applicationInfo.loadLabel(getPackageManager()).toString());
                    appInfo.setPackageName(applicationInfo.packageName);
                    appInfoList.add(appInfo);
                }
                //收集获取App运行数据
                List<UsageStats> usageStatsList = getAppRunInfo();
                System.out.println("长度：" + usageStatsList.size());
                for(UsageStats usageStats : usageStatsList) {
                    Log.e(usageStats.getPackageName(),usageStats.getFirstTimeStamp() + "," + usageStats.getLastTimeStamp());
                }
                for(AppInfo appInfo : appInfoList) {
                    for(UsageStats usageStats : usageStatsList) {
                        if(appInfo.packageName.equals(usageStats.getPackageName())) {
                            try {
                                appInfo.setFirstTimeStamp(usageStats.getFirstTimeStamp());
                                appInfo.setLastTimeUsed(usageStats.getLastTimeStamp());
                                appInfo.setTotalTimeInForeground(usageStats.getTotalTimeInForeground());
                                Field field4 = usageStats.getClass().getDeclaredField("mLaunchCount");
                                appInfo.setAppLaunchCount(field4.getInt(usageStats));
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                for(AppInfo appInfo : appInfoList) {
                    Log.e("tag",appInfo.toString());
                }

                //封装数据存入SQLite
                SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(AppDetailActivity.this, "appInfo_db",null,1);
                SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();
                //将之前数据删除（可注释掉）
                db.delete("app_info", null, null);
                //存入新的数据
                for(AppInfo appInfo : appInfoList){
                    ContentValues values = new ContentValues();
                    values.put("app_name", appInfo.getAppName());
                    values.put("package_name", appInfo.getPackageName());
                    values.put("first_running_time", appInfo.getFirstTimeStamp());
                    values.put("last_running_time", appInfo.getLastTimeUsed());
                    values.put("lunch_count", appInfo.getAppLaunchCount());
                    values.put("total_use_time", appInfo.getTotalTimeInForeground());
                    //数据库执行插入命令
                    db.insert("app_info", null, values);
                }
                //关闭数据库
                db.close();

                //更新表
                appList = getList("");
                AppAdapter adapter = new AppAdapter(AppDetailActivity.this, R.menu.app_info_view, appList);
                ListView listView = (ListView) findViewById(R.id.app_list_view);
                listView.setAdapter(adapter);
            }
        });

    }

    //获取手机app运行数据 (默认为一月)
    public List<UsageStats> getAppRunInfo() {
        /*Calendar calendar=Calendar.getInstance();
        calendar.setTime(new Date());
        long endt = calendar.getTimeInMillis();//结束时间
        calendar.add(Calendar.DAY_OF_MONTH, -1);//时间间隔为一个月
        long statt = calendar.getTimeInMillis();//开始时间*/

        UsageStatsManager usageStatsManager=(UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
        Long endt = System.currentTimeMillis();
        Long statt = endt - 7*24*60*60*1000;
        //获取一个月内的信息
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,statt,endt);
        Log.e("UsageStats",queryUsageStats.size() + "");
        return queryUsageStats;
    }

    //获取手机app信息列表
    private List<ApplicationInfo> queryFilterAppInfo() {
        PackageManager pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<ApplicationInfo> applicationInfos = new ArrayList<>();

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // 通过getPackageManager()的queryIntentActivities方法遍历,得到所有能打开的app的packageName
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        Set<String> allowPackages = new HashSet();
        for (ResolveInfo resolveInfo : resolveinfoList) {
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }

        for (ApplicationInfo app : appInfos) {
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(app);
            }
        }
        return applicationInfos;
    }

    //根据时间，app名称获取数据
    public List<App> getList(String appName) {
        List<App> list = new ArrayList<>();

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
                App info = new App();
                info.setAppName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setPackageName(cursor1.getString(cursor1.getColumnIndex("package_name")));
                list.add(info);
            }
        }
        db.close();
        Log.e("查询结果",list.size() + "条数据");
        return list;
    }

    //将时间戳转换为时间
    public static String times(Long time) {
        if(time == 0L) {
            return "-";
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String times = sdr.format(new Date(time));
        return times;
    }

}