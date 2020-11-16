package com.example.logapp.entity;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

@SmartTable(name="应用使用信息列表")
public class TableInfo {

    @SmartColumn(id =1,name = "应用名")
    private String name;
    @SmartColumn(id=2,name="启动时间")
    private String start_time;
    @SmartColumn(id=3,name="结束时间")
    private String end_time;
    @SmartColumn(id=4,name="使用时间")
    private Long use_time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Long getUse_time() {
        return use_time;
    }

    public void setUse_time(Long use_time) {
        this.use_time = use_time;
    }
}
