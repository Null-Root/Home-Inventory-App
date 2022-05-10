package com.tuna_salmon.androidapp.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tuna_salmon.androidapp.repositories.DashboardRepository;

public class DashboardViewModel extends ViewModel {
    private DashboardRepository mDashboardRepository;

    public DashboardViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public LiveData<String> UseCustomID(String ID) {
        return null;
    }
}