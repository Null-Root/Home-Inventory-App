package com.tuna_salmon.androidapp.dashboard.user_settings;

import androidx.lifecycle.ViewModel;

import com.tuna_salmon.androidapp.repositories.DashboardRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.scopes.ViewModelScoped;

@HiltViewModel
public class UserSettingsViewModel extends ViewModel {
    private DashboardRepository mDashboardRepository;
    public UserSettingsListener userSettingsListener;

    @Inject
    public UserSettingsViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public void Change_ID() {
        //
    }

    public void SetLockState(boolean State) {
        //
    }

    public void GetLockState() {
        //
    }

    public void SetVisibilityState(int State) {
        //
    }

    public void GetVisibilityState() {
        //
    }

    public void Delete_Account() {
        //
    }
}