package com.example.companymeetingscheduler;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ScheduleViewModelFactory implements ViewModelProvider.Factory {
    private ScheduleRepository scheduleRepository;

    public ScheduleViewModelFactory(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ScheduleViewModel(this.scheduleRepository);
    }
}
