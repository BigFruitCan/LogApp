package com.example.logapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.logapp.R;
import com.example.logapp.entity.App;

import java.util.List;

public class AppAdapter extends ArrayAdapter {

    private final int resourceId;

    public AppAdapter(Context context, int textViewResourceId, List<App> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        App app = (App) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView appName = (TextView) view.findViewById(R.id.app_name);//获取该布局内的图片视图
        TextView PackageName = (TextView) view.findViewById(R.id.package_name);//获取该布局内的文本视图
        appName.setText(app.getAppName());//为图片视图设置图片资源
        PackageName.setText(app.getPackageName());//为文本视图设置文本内容
        return view;
    }
}
