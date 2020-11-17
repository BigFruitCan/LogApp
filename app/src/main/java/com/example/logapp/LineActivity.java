package com.example.logapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.logapp.dao.RunInfoDBHelper;
import com.example.logapp.dao.SqliteDBHelper;
import com.example.logapp.entity.AppInfo;
import com.example.logapp.entity.LaunchSum;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LineActivity extends AppCompatActivity {

    AppInfo runInfos = new AppInfo();
    List<LaunchSum> launchSumList = new ArrayList<>();  //统计最近七天

    private LineChart lineChart;
    private XAxis xAxis;                //X轴
    private YAxis leftYAxis;            //左侧Y轴
    private YAxis rightYaxis;           //右侧Y轴
    private Legend legend;              //图例

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line);

        //加载数据
        Intent intent = getIntent();
        String name = intent.getStringExtra("appName");
        Log.e("intent", name);
        getRunList(name);
        getAppInfo(name);

        //记录基本信息
        TextView appName = findViewById(R.id.tv_table_name);
        appName.setText(runInfos.getAppName());
        TextView packageName = findViewById(R.id.tv_table_sex);
        packageName.setText(runInfos.getPackageName());
        TextView firstTime = findViewById(R.id.first_time);
        firstTime.setText(runInfos.getFirstTimeStamp()+"");
        TextView lastTime = findViewById(R.id.last_time);
        lastTime.setText(runInfos.getLastTimeUsed()+"");
        TextView launchCount = findViewById(R.id.launch_count);
        launchCount.setText(runInfos.getAppLaunchCount()+"");
        TextView useTime = findViewById(R.id.use_time);
        useTime.setText(runInfos.getTotalTimeInForeground()+"");

        //加载图表
        lineChart = (LineChart) findViewById(R.id.chart);
        initChart(lineChart);
        showLineChart("启动次数", Color.BLUE);
        Drawable drawable = getResources().getDrawable(R.drawable.fade_blue);
        setChartFillDrawable(drawable);
    }

    /**
     * 初始化图表
     */
    private void initChart(LineChart lineChart) {
        /***图表设置***/
        //是否展示网格线
        lineChart.setDrawGridBackground(false);
        //是否显示边界
        lineChart.setDrawBorders(true);
        //是否可以拖动
        lineChart.setDragEnabled(false);
        //是否有触摸事件
        lineChart.setTouchEnabled(true);
        //设置XY轴动画效果
        lineChart.animateY(2500);
        lineChart.animateX(1500);

        lineChart.setBackgroundColor(Color.WHITE);
        //是否显示边界
        lineChart.setDrawBorders(false);

        /***XY轴的设置***/
        xAxis = lineChart.getXAxis();
        leftYAxis = lineChart.getAxisLeft();
        rightYaxis = lineChart.getAxisRight();
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        //保证Y轴从0开始，不然会上移一点
        leftYAxis.setAxisMinimum(0f);
        rightYaxis.setAxisMinimum(0f);

        /***折线图例 标签 设置***/
        legend = lineChart.getLegend();
        //设置显示类型，LINE CIRCLE SQUARE EMPTY 等等 多种方式，查看LegendForm 即可
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(12f);
        //显示位置 左下方
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //是否绘制在图表里面
        legend.setDrawInside(false);

        xAxis.setDrawGridLines(false);
        rightYaxis.setDrawGridLines(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.enableGridDashedLine(10f, 10f, 0f);
        rightYaxis.setEnabled(false);

        Description description = new Description();
        description.setText("");
        lineChart.setDescription(description);

        //设置x轴
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return launchSumList.get((int) value % launchSumList.size()).getDate();
            }
        });
    }

    /**
     * 曲线初始化设置 一个LineDataSet 代表一条曲线
     *
     * @param lineDataSet 线条
     * @param color       线条颜色
     * @param mode
     */
    private void initLineDataSet(LineDataSet lineDataSet, int color, LineDataSet.Mode mode) {
        lineDataSet.setColor(color);
        lineDataSet.setCircleColor(color);
        lineDataSet.setLineWidth(1f);
        lineDataSet.setCircleRadius(3f);
        //设置曲线值的圆点是实心还是空心
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(10f);
        //设置折线图填充
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        if (mode == null) {
            //设置曲线展示为圆滑曲线（如果不设置则默认折线）
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        } else {
            lineDataSet.setMode(mode);
        }
        lineDataSet.setDrawCircles(false);
    }

    /**
     * 展示曲线
     *
     * @param name     曲线名称
     * @param color    曲线颜色
     */
    public void showLineChart(String name, int color) {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < launchSumList.size(); i++) {
            Entry entry = new Entry(i, (float) launchSumList.get(i).getCount());
            entries.add(entry);
        }
        // 每一个LineDataSet代表一条线
        LineDataSet lineDataSet = new LineDataSet(entries, name);
        initLineDataSet(lineDataSet, color, LineDataSet.Mode.LINEAR);
        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }


    /**
     * 设置线条填充背景颜色
     *
     * @param drawable
     */
    public void setChartFillDrawable(Drawable drawable) {
        if (lineChart.getData() != null && lineChart.getData().getDataSetCount() > 0) {
            LineDataSet lineDataSet = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
            //避免在 initLineDataSet()方法中 设置了 lineDataSet.setDrawFilled(false); 而无法实现效果
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(drawable);
            lineChart.invalidate();
        }
    }

    //获取app基本信息
    public void getAppInfo(String appName) {
        SqliteDBHelper sqliteDBHelper = new SqliteDBHelper(this.getBaseContext(), "appInfo_db",null,1);
        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();

        Cursor cursor1 = db.rawQuery("select * from app_info where app_name='" + appName + "'", null);
        if(cursor1 != null && cursor1.getCount() > 0) {   //判断结果集是否有效
            cursor1.moveToNext();
            runInfos.setAppName(cursor1.getString(cursor1.getColumnIndex("app_name")));
            runInfos.setPackageName(cursor1.getString(cursor1.getColumnIndex("package_name")));
            runInfos.setFirstTimeStamp(cursor1.getLong(cursor1.getColumnIndex("first_running_time")));
            runInfos.setLastTimeUsed(cursor1.getLong(cursor1.getColumnIndex("last_running_time")));
            runInfos.setTotalTimeInForeground(cursor1.getLong(cursor1.getColumnIndex("total_use_time")));
            runInfos.setAppLaunchCount(cursor1.getInt(cursor1.getColumnIndex("lunch_count")));
        }
        db.close();
    }

    //获取一周内每天启动次数
    public void getRunList(String appName) {
        Long startTime;
        Long endTime;
        RunInfoDBHelper runInfoDBHelper = new RunInfoDBHelper(getApplicationContext(), "appRunSeq_db",null,1);
        SQLiteDatabase db = runInfoDBHelper.getWritableDatabase();
        Cursor cursor1;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        int pre = 24*60*60*1000;
        Date date;

        //当天
        startTime = getTodayStartTime();
        endTime = System.currentTimeMillis();
        date = new Date(startTime);
        LaunchSum launchSum1 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum1.setDate(simpleDateFormat.format(date));
        launchSum1.setCount(cursor1.getCount());

        //前一天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum2 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum2.setDate(simpleDateFormat.format(date));
        launchSum2.setCount(cursor1.getCount());

        //前二天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum3 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum3.setDate(simpleDateFormat.format(date));
        launchSum3.setCount(cursor1.getCount());

        //前三天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum4 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum4.setDate(simpleDateFormat.format(date));
        launchSum4.setCount(cursor1.getCount());

        //前四天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum5 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum5.setDate(simpleDateFormat.format(date));
        launchSum5.setCount(cursor1.getCount());

        //前五天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum6 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum6.setDate(simpleDateFormat.format(date));
        launchSum6.setCount(cursor1.getCount());

        //前六天
        startTime -= pre;
        endTime -= pre;
        date = new Date(startTime);
        LaunchSum launchSum7 = new LaunchSum();
        cursor1 = db.rawQuery("select * from app_run_info where app_name='" + appName + "' and start_time between " + startTime + " and " + endTime, null);
        launchSum7.setDate(simpleDateFormat.format(date));
        launchSum7.setCount(cursor1.getCount());

        db.close();
        launchSumList.add(launchSum7);
        launchSumList.add(launchSum6);
        launchSumList.add(launchSum5);
        launchSumList.add(launchSum4);
        launchSumList.add(launchSum3);
        launchSumList.add(launchSum2);
        launchSumList.add(launchSum1);

        for(LaunchSum launchSum : launchSumList) {
           Log.e("launch",launchSum.getDate() + "," + launchSum.getCount());
        }
    }

    public long getTodayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }

}