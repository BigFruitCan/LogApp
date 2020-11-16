package com.example.logapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.util.AcessData;
import com.example.logapp.util.pieChart;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private View view;
    private WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_chart, container, false);
        }

        webView = (WebView)view.findViewById(R.id.chart_web);

        // 获取指定数据格式的数据,此处可以和外部交互
        List<AcessData> datas = getWeekData();

        // 进行WebView设置
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setDisplayZoomControls(true);
        // 给JavaScript传递生成的myLineChart的Option
        webView.addJavascriptInterface(new pieChart(getContext(),datas), "myLine");
        webView.loadUrl("file:///android_asset/js/pie-legend.html");
        webView.setWebViewClient(new WebViewClient());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    //数据库中获取数据
    public List<AcessData> getWeekData() {
        List<AcessData> data = new ArrayList<>();

        //数据库中查询数据
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(getContext(), "appInfo_db",null,1);
        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("select * from app_info order by total_use_time desc", null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                Long useTime = cursor1.getLong(cursor1.getColumnIndex("total_use_time"));
                if(useTime != 0){
                    AcessData acessData = new AcessData();
                    acessData.setName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                    acessData.setUseTime(useTime);
                    data.add(acessData);
                }
            }
        }
        db.close();
        Log.e("查询结果",data.size() + "条数据");

        return data;
    }

}