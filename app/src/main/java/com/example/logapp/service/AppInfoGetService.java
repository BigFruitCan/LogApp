package com.example.logapp.service;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppInfoGetService extends Service {
    private static final String TAG = "AppInfoGetService";
    private boolean flag = true;

    @Override
    public void onCreate() {
        Log.e(TAG, "OnCreate__Running");
        super.onCreate();

        //创建一个线程执行任务（每隔1分钟执行一次）
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag){
                    try {
                        Log.e(TAG,"执行一次");
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG,"执行结束");
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "OnStartCommand__Running");
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
        flag = false;
        super.onDestroy();
    }


    //获取手机app运行数据 (默认为一天)
    public List<UsageStats> getAppRunInfo() {
        UsageStatsManager m = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis() - 24*60*60*1000;
        List<UsageStats> list = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time, System.currentTimeMillis());
        return list;
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
//            if((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)//通过flag排除系统应用，会将电话、短信也排除掉
//            {
//                applicationInfos.add(app);
//            }
//            if(app.uid > 10000){//通过uid排除系统应用，在一些手机上效果不好
//                applicationInfos.add(app);
//            }
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(app);
            }
        }
        return applicationInfos;
    }
}
