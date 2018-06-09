package com.samluys.linechartdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChartView line_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        line_chart = findViewById(R.id.line_chart);
        // x轴上的数据
        List<String> xAixs = new ArrayList<>();
        xAixs.add("17-10");
        xAixs.add("17-11");
        xAixs.add("17-12");
        xAixs.add("18-01");
        xAixs.add("18-02");
        xAixs.add("18-03");
        xAixs.add("18-04");
        xAixs.add("18-05");
        // y轴上的数据
        List<String> yAixs = new ArrayList<>();
        yAixs.add("12000");
        yAixs.add("12300");
        yAixs.add("13040");
        yAixs.add("14090");
        yAixs.add("14400");
        yAixs.add("14800");
        yAixs.add("14900");
        yAixs.add("13800");

        line_chart.setData(xAixs,yAixs);

    }
}
