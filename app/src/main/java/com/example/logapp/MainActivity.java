package com.example.logapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.entity.AppInfo;
import com.example.logapp.service.AppRunSeqGetService;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.logapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //定义一个setting记录APP是几次启动！！！
        SharedPreferences setting = getSharedPreferences("com.example.logapp", 0);
        Boolean user_first = setting.getBoolean("FIRST", true);
        if (user_first) {   // 第一次则跳转到欢迎页面
            //打开访问功能,若已打开过，则注掉此段代码
            setting.edit().putBoolean("FIRST",false).commit();
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            }

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
                Log.e(usageStats.getPackageName(),usageStats.getLastTimeStamp() + "");
            }
            for(UsageStats usageStats : usageStatsList) {
                for(AppInfo appInfo : appInfoList) {
                    if(appInfo.packageName.equals(usageStats.getPackageName())) {
                        appInfo.setFirstTimeStamp(usageStats.getFirstTimeStamp());
                        appInfo.setLastTimeUsed(usageStats.getLastTimeUsed());
                        appInfo.setTotalTimeInForeground(usageStats.getTotalTimeInForeground());
                        try {
                            Field field = usageStats.getClass().getDeclaredField("mLaunchCount");
                            appInfo.setAppLaunchCount(field.getInt(usageStats));
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //封装数据存入SQLite
            SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(MainActivity.this, "appInfo_db",null,1);
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

            //调用服务更新runseq
            Intent intent2 = new Intent(this, AppRunSeqGetService.class);
            startService(intent2);
        } else {
            //如果是第二次启动则不设置其他
        }

    }

    //登录跳转button
    public void sendMessage(View view) {
        Intent intent = new Intent(this, HomeActivity.class);   //指定登陆后跳转到那一页
        EditText editText = (EditText) findViewById(R.id.usename);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE,message);
        startActivity(intent);
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

}