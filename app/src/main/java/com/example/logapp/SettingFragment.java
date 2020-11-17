package com.example.logapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.entity.RunInfo;
import com.example.logapp.service.GuardianService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
            Intent intent = new Intent(getActivity(), GuardianService.class);
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


        /**
         * 数据发送格式
         * {"dataList":
         *    [
         *       {"appName":"qq","packageName":"com.example.aa","startStamp":123456789,"endStamp":288927800,"useStamp":1288931400},
         *       {"appName":"微信","packageName":"com.example.bb","startStamp":456789789,"endStamp":1288933200,"useStamp":1288936800}
         *   ]
         * }
         */
        //上传数据按钮
        Button button7 = (Button) view.findViewById(R.id.button7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendJsonPost(getJson(),1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                openDialog("数据上传成功","提示");
            }
        });

        return view;
    }

    private void openDialog(String strMsg, String strTitle){
        new AlertDialog.Builder(getContext())
                .setTitle(strTitle)
                .setMessage(strMsg)
                .setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        })
                .show();
    }

    //获取json数据
    public String getJson() throws JSONException {
        List<RunInfo> list = new ArrayList<>();
        //数据库中查询数据
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(view.getContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();
        Cursor cursor1 = db.rawQuery("select * from app_run_info order by aid desc limit 0,10", null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            Log.e("查询数据长度",cursor1.getCount() + "");
            while (cursor1.moveToNext()) {   //游标是否继续向下移动
                RunInfo info = new RunInfo();
                info.setAppName(cursor1.getString(cursor1.getColumnIndex("app_name")));
                info.setPackageName(cursor1.getString(cursor1.getColumnIndex("package_name")));
                info.setStartStamp(cursor1.getLong(cursor1.getColumnIndex("start_time")));
                info.setEndStamp(cursor1.getLong(cursor1.getColumnIndex("end_time")));
                info.setUseStamp(cursor1.getLong(cursor1.getColumnIndex("use_time")));
                list.add(info);
            }
        }
        db.close();

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for(RunInfo info : list) {
            JSONObject tag = new JSONObject();
            tag.put("appName",info.getAppName());
            tag.put("packageName",info.getPackageName());
            tag.put("startStamp",info.getStartStamp());
            tag.put("endStamp",info.getEndStamp());
            tag.put("useStamp",info.getUseStamp());
            jsonArray.put(tag);
        }
        jsonObject.put("dataList",jsonArray);

        return jsonObject.toString();
    }

    //发送json数据
    public static String sendJsonPost(String Json, int uid) {
        String apiUrl = "https://www.fastmock.site/mock/720a015f6935e07bda47458d513750d6/posttest/post";
        Log.e("json",Json);
        String result = "";
        BufferedReader reader = null;
        try {
            String urlPath = apiUrl;
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            conn.setRequestProperty("accept", "application/json");
            // 往服务器里面发送数据
            if (Json != null && !TextUtils.isEmpty(Json)) {
                byte[] writebytes = Json.getBytes();
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(Json.getBytes());
                outwritestream.flush();
                outwritestream.close();
                Log.e("hlhupload", "doJsonPost: conn " + conn.getResponseCode());
            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                result = reader.readLine();
                Log.e("result",result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    //将时间戳转换为时间
    public static String times(Long time) {
        if(time == 0L) {
            return "-";
        }
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        String times = sdr.format(new Date(time));
        return times;
    }

}