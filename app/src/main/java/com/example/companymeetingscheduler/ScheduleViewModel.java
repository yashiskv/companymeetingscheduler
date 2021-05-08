package com.example.companymeetingscheduler;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ScheduleViewModel extends ViewModel {
    private ScheduleRepository scheduleRepository;
    private MutableLiveData<List<ScheduleLog>> scheduleLogs;

    public ScheduleViewModel(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    public LiveData<List<ScheduleLog>> getDailyScheduleLog(String date){
        scheduleLogs = scheduleRepository.getDailyScheduleLog(date);
        return scheduleLogs;
    }
}
