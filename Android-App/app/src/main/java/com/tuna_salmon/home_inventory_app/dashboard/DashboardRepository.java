package com.tuna_salmon.home_inventory_app.dashboard;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.Const.*;
import com.tuna_salmon.home_inventory_app.services.DataService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class DashboardRepository {

    private static DashboardRepository s_instance = null;
    private DashboardRepository() {}

    @Provides
    @ViewModelScoped
    public static DashboardRepository getInstance() {
        if(s_instance == null)
            s_instance = new DashboardRepository();
        return s_instance;
    }

    public LiveData<String> check_auth(String AuthData) {
        return DataService.getService().DataRequest(API.ACCOUNT, AuthData, true);
    }

    public LiveData<String> confirm_account(String AccountData) {
        return DataService.getService().DataRequest(API.ACCOUNT, AccountData, true);
    }

    public LiveData<String> use_custom_id(String ID_Data) {
        return DataService.getService().DataRequest(API.ACCOUNT, ID_Data, true);
    }

    public LiveData<String> user_settings(String SettingsData) {
        return DataService.getService().DataRequest(API.ACCOUNT, SettingsData, true);
    }

    public LiveData<String> share_data(String SharedData) {
        return DataService.getService().DataRequest(API.SHARE_DATA, SharedData, true);
    }
}