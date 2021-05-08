package com.example.companymeetingscheduler;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ScheduleApi {
    @GET("/api/schedule?")
    Call<List<ScheduleLog>> getDailyScheduleLog(@Query("date") String date);
}
