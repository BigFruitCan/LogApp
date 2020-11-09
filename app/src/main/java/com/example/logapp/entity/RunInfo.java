package com.example.logapp.entity;

public class RunInfo {

    public String appName = "";
    public String packageName = "";

    public long startStamp = 0;     //app启动时间戳
    public long endStamp = 0;       //app关闭时间

    public long useStamp = 0;       //app使用时间,start - end

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

    public long getStartStamp() {
        return startStamp;
    }

    public void setStartStamp(long startStamp) {
        this.startStamp = startStamp;
    }

    public long getEndStamp() {
        return endStamp;
    }

    public void setEndStamp(long endStamp) {
        this.endStamp = endStamp;
    }

    public long getUseStamp() {
        return useStamp;
    }

    public void setUseStamp(long useStamp) {
        this.useStamp = useStamp;
    }

    @Override
    public String toString() {
        return "RunInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", startStamp=" + startStamp +
                ", endStamp=" + endStamp +
                ", useStamp=" + useStamp +
                '}';
    }
}
