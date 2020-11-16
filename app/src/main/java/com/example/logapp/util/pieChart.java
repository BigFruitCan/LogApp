package com.example.logapp.util;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.code.LegendType;
import com.github.abel533.echarts.code.Orient;
import com.github.abel533.echarts.code.SeriesType;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.data.Data;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Pie;

import java.util.List;

public class pieChart {

    Context mContext;
    // 用户数据的数据结构，可后面自己定义
    List<AcessData> pieDatas;   //默认不显示使用时间为0的应用

    /**
     * 构造函数
     * @param context
     * @param datas:利用构造函数传入应用的数据
     */
    public pieChart(Context context, List<AcessData> datas) {
        this.mContext = context;
        // 获取数据
        this.pieDatas = datas;
    }

    // 将该方法暴露给JavaScript脚本调用
    @JavascriptInterface
    public String getLineChartOptions() {
        GsonOption option = (GsonOption) creatLineChartOptions();
        Log.e("",option.toString());
        return option.toString();
    }

    // 此函数主要是绘 pie 图
    @JavascriptInterface
    public Option creatLineChartOptions() {

        // 创建Option对象
        GsonOption option = new GsonOption();
        // 设置图标标题，并且居中显示
        option.title().text("App使用时间统计").left("center");
        option.tooltip().trigger(Trigger.item).formatter("{a} <br/>{b} : {c} ({d}%)");
        // 设置图例，居中显示
        option.legend().type(LegendType.scroll).
                right(10).top(20).bottom(20).orient(Orient.vertical);
        int count = 0;
        for(AcessData data : pieDatas) {
            option.legend().data(data.getName());
            if(count < 6) {
                option.legend().selected(data.getName(),true);
            }else {
                option.legend().selected(data.getName(),false);
            }
            count++;
        }
        Pie pie = new Pie("使用时间");
        pie.type(SeriesType.pie).radius("55%").center("40%","50%");
        for(AcessData data : pieDatas) {
            pie.data(new Data(data.getName(),data.getUseTime()));
        }
        option.series(pie);
        return option;
    }

}
