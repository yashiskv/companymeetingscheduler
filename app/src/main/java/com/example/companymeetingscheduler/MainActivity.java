package com.example.companymeetingscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ScheduleViewModel scheduleViewModel;
    ScheduleCustomAdapter scheduleCustomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar cal = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss",Locale.getDefault());
        String dateTime = df.format(cal.getTime());
        String[] arr = dateTime.split("\\s+");
        String date = arr[0];
        scheduleCustomAdapter = new ScheduleCustomAdapter(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.schedule_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(scheduleCustomAdapter);
        ScheduleApi scheduleApi = new RetrofitApi("http://fathomless-shelf-5846.herokuapp.com").getScheduleApi();
        ScheduleViewModelFactory factory = new ScheduleViewModelFactory(new ScheduleRepository(scheduleApi));
        scheduleViewModel = new ViewModelProvider(MainActivity.this, factory).get(ScheduleViewModel.class);

        TextView barDate = findViewById(R.id.bar_date);
        barDate.setText(date);
        setDate(date);


        ImageView prev = findViewById(R.id.previ);
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.DATE, -1);
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String yesterdayAsString = dateFormat.format(cal.getTime());
                barDate.setText(yesterdayAsString);
                setDate(yesterdayAsString);
            }
        });

        ImageView nex = findViewById(R.id.nex);
        nex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.DATE, 1);
                DateFormat dateFormatNext = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String tomorrowAsString = dateFormatNext.format(cal.getTime());
                barDate.setText(tomorrowAsString);
                setDate(tomorrowAsString);
            }
        });

        Button scheduleButton = (Button) findViewById(R.id.schedule_button);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchScheduleMeetingActivity();
            }
        });
    }
    public void launchScheduleMeetingActivity(){
        Intent intent = new Intent(this, ScheduleMeetingActivity.class);
        startActivity(intent);
    }

    public void setDate(String date){
        scheduleViewModel.getDailyScheduleLog(date).observe(this, scheduleLogs -> {
            Collections.sort(scheduleLogs, new SortByStartTime());
            scheduleCustomAdapter.setScheduleLogs(scheduleLogs);
        });
    }
}
class SortByStartTime implements Comparator<ScheduleLog>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(ScheduleLog a, ScheduleLog b)
    {
        String[] startTimeArrSet = a.getStart_time().split(":");
        String[] endTimeArrSet = b.getStart_time().split(":");
        int startHour = Integer.parseInt(String.valueOf(startTimeArrSet[0]));
        int endHour = Integer.parseInt(String.valueOf(endTimeArrSet[0]));
        int startMin = Integer.parseInt(String.valueOf(startTimeArrSet[1]));
        int endMin = Integer.parseInt(String.valueOf(endTimeArrSet[1]));
        int aTime = startHour*60 + startMin;
        int bTime = endHour*60 +endMin;

        return aTime-bTime;
    }
}