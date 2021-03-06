package com.example.logapp.entity;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="应用列表信息列表")
public class TableAppInfo {

    @SmartColumn(id = 1,name = "应用名")
    public String appName = "";
    @SmartColumn(id = 2,name = "包名")
    public String packageName = "";

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

}
