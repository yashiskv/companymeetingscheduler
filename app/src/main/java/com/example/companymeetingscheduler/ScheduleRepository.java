package com.example.companymeetingscheduler;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleRepository {
    public ScheduleApi scheduleApi;

    public ScheduleRepository(ScheduleApi scheduleApi){
        this.scheduleApi = scheduleApi;
    }

    public MutableLiveData<List<ScheduleLog>> getDailyScheduleLog(String date){
        MutableLiveData<List<ScheduleLog>> dailyScheduleLog = new MutableLiveData<>();
        scheduleApi.getDailyScheduleLog(date).enqueue(new Callback<List<ScheduleLog>>() {
            @Override
            public void onResponse(Call<List<ScheduleLog>> call, Response<List<ScheduleLog>> response) {
                if (response.isSuccessful()) {
                    dailyScheduleLog.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<ScheduleLog>> call, Throwable t) {
                t.printStackTrace();
            }
        });
        return dailyScheduleLog;
    }
}
