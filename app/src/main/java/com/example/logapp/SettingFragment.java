package com.example.logapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.example.logapp.service.AppInfoGetService;
import com.example.logapp.service.AppRunSeqGetService;

import java.util.Calendar;

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

        Long s = Calendar.getInstance().getTimeInMillis();
        Log.e("time", s + "");

        return view;
    }

}