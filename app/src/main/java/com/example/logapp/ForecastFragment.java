package com.example.logapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.example.logapp.entity.TableInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ForecastFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_forecast, container, false);
        }

        //查询按钮
        Button button6 = (Button) view.findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //todo
                        String result = getAppName();
                        //解析结果
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JSONArray array = jsonObject.getJSONArray("dataList");
                            JSONObject array1 = array.getJSONObject(0);
                            JSONObject array2 = array.getJSONObject(1);
                            JSONObject array3 = array.getJSONObject(2);

                            String s1 = array1.getString("appName") + "(" + array1.getString("percent") + ")";
                            String s2 = array2.getString("appName") + "(" + array2.getString("percent") + ")";
                            String s3 = array3.getString("appName") + "(" + array3.getString("percent") + ")";

                            TextView textView2 = (TextView) view.findViewById(R.id.textView2);
                            Long currenTime = System.currentTimeMillis();
                            String s = "当前时间为：" + times(currenTime) + ",下一个将要启动的应用为：" + s1 + "," + s2 + "," + s3;
                            textView2.setText(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

    /**
     * 向服务器获取下个应用
     * {"dataList":
     *    [
     *       {"appName":"qq","percent":"70%"},
     *       {"appName":"微信","percent":"20%"},
     *       {"appName":"支付宝","percent":"10%"}
     *   ]
     * }
     */
    public String getAppName() {
        String result = "";
        try {
            String urlPath = "https://www.fastmock.site/mock/720a015f6935e07bda47458d513750d6/posttest/getApp";
            URL url=new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                //得到响应流
                InputStream inputStream = connection.getInputStream();
                //将响应流转换成字符串
                result = is2String(inputStream);//将流转换为字符串。
                Log.e("result=",result);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String is2String(InputStream is){
        //连接后，创建一个输入流来读取response
        BufferedReader bufferedReader;
        String line;
        String response = "";
        try {
            bufferedReader = new BufferedReader(new
                    InputStreamReader(is,"utf-8"));
            StringBuilder stringBuilder = new StringBuilder();
            //每次读取一行，若非空则添加至 stringBuilder
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            //读取所有的数据后，赋值给 response
            response = stringBuilder.toString().trim();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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