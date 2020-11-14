package com.example.logapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.bin.david.form.core.SmartTable;
import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.entity.TableInfo;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
        }

        //第一次进入页面显示数据
        //显示表格前10条数据
        List<TableInfo> list = getList(null,0L,0L,30);
        SmartTable table = (SmartTable<TableInfo>) view.findViewById(R.id.table2);
        table.setData(list);

        final EditText editText5 = (EditText) view.findViewById(R.id.editText5);
        editText5.setOnTouchListener(new View.OnTouchListener() {
            int count=0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (count== 0){
                    editText5.setText("");
                }
                count++;
                return false;
            }
        });

        Button button1 = (Button) view.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText5 = (EditText) view.findViewById(R.id.editText5);
                String s = editText5.getText().toString();
                Spinner spinner1 = (Spinner) view.findViewById(R.id.spinner1);
                if(s != null && s != ""){
                    Log.e("spinner",spinner1.getSelectedItem() + "");
                    List<TableInfo> tableInfoList = getList(s,0L,0L,30);
                    SmartTable table = (SmartTable<TableInfo>) view.findViewById(R.id.table2);
                    table.setData(tableInfoList);
                }
            }
        });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    //根据时间，app名称获取数据
    public List<TableInfo> getList(String appName, Long startTime, Long endTime, int limit) {
        List<TableInfo> list = new ArrayList<>();

        //数据库中查询数据
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(view.getContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();

        String queryStr = "select * from app_run_info";
        if(appName == null || appName.equals("")) {
            if(startTime != 0L && endTime == 0L) {
                queryStr += " where start_time > " + startTime;
            }else if(startTime == 0L && endTime != 0L) {
                queryStr += " where start_time < " + endTime;
            }else if(startTime != 0L && endTime != 0L) {
                queryStr += " where start_time between " + startTime + " and " + endTime;
            }
        }else {
            queryStr += " where app_name like '%" + appName + "%'";
            if(startTime != 0L && endTime == 0L) {
                queryStr += " and start_time > " + startTime;
            }else if(startTime == 0L && endTime != 0L) {
                queryStr += " and start_time < " + endTime;
            }else if(startTime != 0L && endTime != 0L) {
                queryStr += " and start_time between " + startTime + " and " + endTime;
            }
        }

        queryStr += " order by aid desc limit 0," + limit;
        Log.e("查询语句",queryStr);
        Cursor cursor1 = db.rawQuery(queryStr, null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            Log.e("查询数据长度",cursor1.getCount() + "");
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                TableInfo info = new TableInfo();
                info.setName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setStart_time(cursor1.getLong(cursor1.getColumnIndex("start_time")));
                info.setEnd_time(cursor1.getLong(cursor1.getColumnIndex("end_time")));
                info.setUse_time(cursor1.getLong(cursor1.getColumnIndex("use_time")));
                list.add(info);
            }
        }
        db.close();
        Log.e("查询结果",list.size() + "条数据");
        return list;
    }

}