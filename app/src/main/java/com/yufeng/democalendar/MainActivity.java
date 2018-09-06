package com.yufeng.democalendar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements WriteCalendar.OnDaySelectedListener, WriteCalendar.OnDateChangeListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WriteCalendar writeCalendar = findViewById(R.id.write_calendar);
        writeCalendar.setOnDateChangeListener(this);
        writeCalendar.setOnDaySelectedListener(this);
    }

    @Override
    public void onDateChange(int year, int month) {
        Log.e("onDateChange", "year=" + year + ",month=" + month);
    }

    @Override
    public void onDaySelected(int year, int month, int day) {
        Log.e("onDaySelected", "year=" + year + ",month=" + month + ",day=" + day);
    }
}
