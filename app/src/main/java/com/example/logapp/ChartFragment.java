package com.example.logapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ChartFragment extends Fragment {

    private View view;
    private WebView webView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null){
            view = inflater.inflate(R.layout.fragment_chart, container, false);
        }

        //初始化一个手机APP使用时间的饼图
        webView = (WebView) view.findViewById(R.id.chart_web);
        //进行webwiev的一堆设置
        //开启本地文件读取（默认为true，不设置也可以）
        webView.getSettings().setAllowFileAccess(true);
        //开启脚本支持
        webView.getSettings().setJavaScriptEnabled(true);
        //设置编码
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        // 清除浏览器缓存
        webView.clearCache(true);
        //  放在 assets目录
        //获取Assets目录下的文件
        /*webView.loadUrl("http://www.baidu.com");*/
        webView.loadUrl("file:///android_asset/echart/pie-legend.html");
        //在当前页面打开链接了
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //js加上这个就好啦！
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
    }

}