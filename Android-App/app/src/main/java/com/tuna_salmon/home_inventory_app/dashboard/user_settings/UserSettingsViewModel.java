package com.tuna_salmon.home_inventory_app.dashboard.user_settings;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.dashboard.DashboardRepository;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;
import com.tuna_salmon.home_inventory_app.services.DataService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserSettingsViewModel extends ViewModel {

    private DashboardRepository mDashboardRepository;
    public UserSettingsListener userSettingsListener;

    @Inject
    public UserSettingsViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public void Change_ID() {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.CHANGE_ID;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        userSettingsListener.OnChangeIDCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }

    public void SetLockState(boolean State) {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.SET_LOCK_STATE;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        accountRequest.LockState = State;

        userSettingsListener.OnChangeLockStateCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }

    public void GetLockState() {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.GET_LOCK_STATE;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        userSettingsListener.OnChangeLockStateCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }

    public void SetVisibilityState(int State) {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.SET_VISIBILITY_STATE;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        accountRequest.Visibility = State;

        userSettingsListener.OnChangeVisibilityStateCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }

    public void GetVisibilityState() {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.GET_VISIBILITY_STATE;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        userSettingsListener.OnChangeVisibilityStateCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }

    public void Delete_Account() {
        DataModel.Send.Account accountRequest = new DataModel().new Send().new Account();
        accountRequest.Action = Const.Account.DELETE_ACCOUNT;
        accountRequest.Token = Const.App.TOKEN;
        accountRequest.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        accountRequest.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        accountRequest.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        accountRequest.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        userSettingsListener.OnDeleteAccountCallback(mDashboardRepository.user_settings(new Gson().toJson(accountRequest)));
    }
}