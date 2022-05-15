package com.tuna_salmon.androidapp.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tuna_salmon.androidapp.repositories.DashboardRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.scopes.ViewModelScoped;

@HiltViewModel
public class DashboardViewModel extends ViewModel {
    private DashboardRepository mDashboardRepository;

    @Inject
    public DashboardViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public LiveData<String> UseCustomID(String ID) {
        return null;
    }
}