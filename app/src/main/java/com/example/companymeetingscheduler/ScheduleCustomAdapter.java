package com.example.companymeetingscheduler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCustomAdapter extends RecyclerView.Adapter<ScheduleCustomAdapter.ViewHolder> {
    private List<ScheduleLog> scheduleLogs;
    private LayoutInflater inflater;
    private Context context;

    ScheduleCustomAdapter(Context context) {
        this.scheduleLogs = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView description;
        TextView participants;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            description = itemView.findViewById(R.id.description);
            participants = itemView.findViewById(R.id.participants);
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_schedule_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final ScheduleLog scheduleLog = scheduleLogs.get(position);
        String[] startTimeArr = scheduleLog.getStart_time().split(":");
        String[] endTimeArr = scheduleLog.getEnd_time().split(":");
        int startHour = Integer.parseInt(String.valueOf(startTimeArr[0]));
        int endHour = Integer.parseInt(String.valueOf(endTimeArr[0]));
        String startMin = startTimeArr[1];
        String endMin = endTimeArr[1];
        String startTime = "";
        if(startHour>12){
            startTime = String.valueOf(Integer.valueOf(startHour-12)) + ":" + startMin + "PM";
        }
        else if(startHour == 12){
            startTime = scheduleLog.getStart_time()+"PM";
        }
        else {
            startTime = scheduleLog.getStart_time() + "AM";
        }
        String endTime = "";
        if(endHour>12){
            endTime = String.valueOf(Integer.valueOf(endHour-12)) + ":" + endMin + "PM";
        }
        else if(endHour == 12){
            endTime = scheduleLog.getEnd_time()+"PM";
        }
        else {
            endTime = scheduleLog.getEnd_time() + "AM";
        }

        StringBuilder sb = new StringBuilder();
        for(int i =0;i<scheduleLog.getParticipants().size()-1;i++){
            sb.append(scheduleLog.getParticipants().get(i)+" , ");
        }
        sb.append(scheduleLog.getParticipants().get(scheduleLog.getParticipants().size()-1));
        String participants = sb.toString();
        try {
            holder.time.setText(startTime + " - " + endTime);
            holder.description.setText(scheduleLog.getDescription());
            holder.participants.setText(participants);

        } catch (Exception exception) {
            Log.e("Exception","exception",exception);

        }
    }

    @Override
    public int getItemCount() {
        if (scheduleLogs == null) {
            return  0;
        } else {
            return scheduleLogs.size();
        }

    }

    /* Takes a list of schedules as parameter and updates the view. */
    public void setScheduleLogs(List<ScheduleLog> scheduleLog) {
        scheduleLogs.clear();
        scheduleLogs.addAll(scheduleLog);
        this.scheduleLogs = scheduleLog;
        this.notifyDataSetChanged();
    }
}

