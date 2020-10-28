package com.example.logapp.entity;

import android.graphics.drawable.Drawable;

public class AppInfo {

    //通过PackageManager获取所有app信息
    public String appName = "";
    public String packageName = "";

    //从UsageStatsManager中获取,根据相同的packageName保存到对应数据行中
    public long firstTimeStamp = 0;     //app第一次启动时间戳
    public long lastTimeUsed = 0;       //app最后一次使用时间
    public int appLaunchCount = 0;      //app启动次数
    public long totalTimeInForeground = 0;//app在前台运行总时间

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public long getFirstTimeStamp() {
        return firstTimeStamp;
    }

    public void setFirstTimeStamp(long firstTimeStamp) {
        this.firstTimeStamp = firstTimeStamp;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public int getAppLaunchCount() {
        return appLaunchCount;
    }

    public void setAppLaunchCount(int appLaunchCount) {
        this.appLaunchCount = appLaunchCount;
    }

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", firstTimeStamp=" + firstTimeStamp +
                ", lastTimeUsed=" + lastTimeUsed +
                ", appLaunchCount=" + appLaunchCount +
                ", totalTimeInForeground=" + totalTimeInForeground +
                '}';
    }
}
