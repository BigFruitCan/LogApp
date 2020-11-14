package com.example.logapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.bin.david.form.core.SmartTable;
import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.entity.TableInfo;
import com.example.logapp.service.AppInfoGetService;
import com.example.logapp.service.AppRunSeqGetService;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_setting, container, false);
        }

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), AppDetailActivity.class);   //指定登陆后跳转到那一页
                startActivity(intent);
            }
        });

        //选择后台实时监控后,启动服务开始监控数据
        Switch switch1 = (Switch) view.findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            Intent intent = new Intent(getActivity(), AppInfoGetService.class);
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo 开启服务
                    getActivity().startService(intent);
                }else {
                    //Todo 关闭服务
                    getActivity().stopService(intent);
                }
            }
        });

        Button button20 = (Button) view.findViewById(R.id.button20);
        button20.setOnClickListener(new View.OnClickListener() {
            Intent intent2 = new Intent(getActivity(), AppRunSeqGetService.class);
            @Override
            public void onClick(View v) {
                getActivity().startService(intent2);
            }
        });

        return view;
    }

    /*public List<TableInfo> getList() {
        List<TableInfo> list = new ArrayList<>();

        //数据库中查询数据
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(view.getContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("select * from app_run_info order by aid desc limit 0,40", null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                TableInfo info = new TableInfo();
                info.setName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setStart_time(cursor1.getLong(cursor1.getColumnIndex("start_time")));
                info.setEnd_time(cursor1.getLong(cursor1.getColumnIndex("end_time")));
                info.setUse_time(cursor1.getLong(cursor1.getColumnIndex("use_time")));
                list.add(info);
            }
        }


        return list;
    }*/

}