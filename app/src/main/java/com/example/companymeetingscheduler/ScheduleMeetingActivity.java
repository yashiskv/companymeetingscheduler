package com.example.companymeetingscheduler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ScheduleMeetingActivity extends AppCompatActivity {
    final Calendar myCalendar = Calendar.getInstance();
    ScheduleViewModel scheduleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);

        ImageView imageView = findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMainActivity();
            }
        });
        updateLabel();
        EditText pickDate = findViewById(R.id.date_pick);
        DatePickerDialog.OnDateSetListener datePick = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        pickDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog da = new DatePickerDialog(ScheduleMeetingActivity.this, datePick,
                        myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                da.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                da.show();
            }
        });

        final Calendar c = Calendar.getInstance();
        int startHour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int startMinute = myCalendar.get(Calendar.MINUTE);
        EditText pickStartTime = findViewById(R.id.time_pick_start);
        // Launch Time Picker Dialog

        pickStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMeetingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                pickStartTime.setText(hourOfDay + ":" + minute);
                            }
                        }, startHour, startMinute, false);
                timePickerDialog.show();
            }
        });


        int endHour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int endMinute = myCalendar.get(Calendar.MINUTE);
        EditText pickEndTime = findViewById(R.id.time_pick_end);
        // Launch Time Picker Dialog
        pickEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog2 = new TimePickerDialog(ScheduleMeetingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String[] startTimeArrSet = pickStartTime.getText().toString().split(":");
                                int startHour = Integer.parseInt(String.valueOf(startTimeArrSet[0]));
                                int startMin = Integer.parseInt(String.valueOf(startTimeArrSet[1]));
                                int startTime = startHour*60 + startMin;
                                int endTime = hourOfDay*60 +minute;
                                if (endTime > startTime) {
                                    //it's after current
                                    pickEndTime.setText(hourOfDay + ":" + minute);
                                } else {
                                    //it's before current'
                                    Toast.makeText(getApplicationContext(), "Invalid Time", Toast.LENGTH_LONG).show();
                                }

                            }
                        }, endHour, endMinute, false);
                timePickerDialog2.show();

            }
        });
        EditText descriptionBox = findViewById(R.id.description_box);
        Button submitButton = findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitLogic(pickDate.getText().toString(),pickStartTime.getText().toString(),pickEndTime.getText().toString());
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        EditText pickDate = findViewById(R.id.date_pick);
        pickDate.setText(sdf.format(myCalendar.getTime()));
    }
    private void submitLogic(String date,String startTimeSet,String endTimeSet){
        String[] startTimeArrSet = startTimeSet.split(":");
        String[] endTimeArrSet = endTimeSet.split(":");
        int startHour = Integer.parseInt(String.valueOf(startTimeArrSet[0]));
        int endHour = Integer.parseInt(String.valueOf(endTimeArrSet[0]));
        int startMin = Integer.parseInt(String.valueOf(startTimeArrSet[1]));
        int endMin = Integer.parseInt(String.valueOf(endTimeArrSet[1]));
         int startTime = startHour*60 + startMin;
         int endTime = endHour*60 +endMin;
        ScheduleApi scheduleApi = new RetrofitApi("http://fathomless-shelf-5846.herokuapp.com").getScheduleApi();
        ScheduleViewModelFactory factory = new ScheduleViewModelFactory(new ScheduleRepository(scheduleApi));
        scheduleViewModel = new ViewModelProvider(ScheduleMeetingActivity.this, factory).get(ScheduleViewModel.class);
        scheduleViewModel.getDailyScheduleLog(date).observe(this, scheduleLogs -> {
            boolean slotAvailable = true;
            for (int i = 0; i < scheduleLogs.size(); i++) {
                String[] startTimeArr = scheduleLogs.get(i).getStart_time().split(":");
                String[] endTimeArr = scheduleLogs.get(i).getEnd_time().split(":");
                int startHr = Integer.parseInt(String.valueOf(startTimeArr[0]));
                int endHr = Integer.parseInt(String.valueOf(endTimeArr[0]));
                int startMn = Integer.parseInt(String.valueOf(startTimeArr[1]));
                int endMn = Integer.parseInt(String.valueOf(endTimeArr[1]));
                int startTm = startHr * 60 + startMn;
                int endTm = endHr * 60 + endMn;
                if (!(endTime <= startTm || startTime >= endTm)) {
                    slotAvailable = false;
                    break;
                }
            }
            if (slotAvailable) {
                Toast.makeText(getApplicationContext(), "Slot is available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Slot not available", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void launchMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}