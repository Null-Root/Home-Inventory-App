package com.tuna_salmon.home_inventory_app.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DashboardViewModel extends ViewModel {

    private DashboardRepository mDashboardRepository;

    @Inject
    public DashboardViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public LiveData<String> UseCustomID(String ID) {
        DataModel.Send.Account account = new DataModel().new Send().new Account();
        account.Token = Const.App.TOKEN;
        account.Action = Const.ID.CHECK_ID;
        account.UniqueID = ID;
        return mDashboardRepository.use_custom_id(new Gson().toJson(account));
    }
}