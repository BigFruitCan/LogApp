package com.example.logapp.entity;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="应用列表信息列表")
public class TableAppInfo {

    @SmartColumn(id = 1,name = "应用名")
    public String appName = "";
    @SmartColumn(id = 2,name = "包名")
    public String packageName = "";
    @SmartColumn(id = 3,name ="第一次启动时间")
    public long firstTimeStamp = 0;
    @SmartColumn(id = 4,name = "最后一次使用时间")
    public long lastTimeUsed = 0;
    @SmartColumn(id = 5,name ="app启动次数")
    public int appLaunchCount = 0;
    @SmartColumn(id = 6,name ="前台运行总时间")
    public long totalTimeInForeground = 0;

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
}
