package com.tuna_salmon.home_inventory_app.settings;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.local_database.api.LocalAPI;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SettingsViewModel extends ViewModel {

    private SettingsRepository mSettingsRepository;
    public SettingsListener settingsListener;

    @Inject
    public SettingsViewModel(SettingsRepository settingsRepository) {
        this.mSettingsRepository = settingsRepository;
    }

    public void OverwriteInventoryOfflineToOnline() {
        // Get Database Info From Local API
        DataModel.Send.App request = new DataModel().new Send().new App();
        request.Token = Const.App.TOKEN;
        request.Action = Const.ActionTypes.AppFunction.OFFLINE_TO_ONLINE;
        request.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.ID_Type.USER_ID);
        request.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        request.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        request.New_DB_Data = LocalAPI.getAPI().getDatabaseDataFromLocal();

        settingsListener.OnAppDataCallback(mSettingsRepository.AppRequest(new Gson().toJson(request)));
    }

    public void OverwriteInventoryOnlineToOffline() {
        // Get Database Info From Wen API
        DataModel.Send.App request = new DataModel().new Send().new App();
        request.Token = Const.App.TOKEN;
        request.Action = Const.ActionTypes.AppFunction.ONLINE_TO_OFFLINE;
        request.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.ID_Type.USER_ID);
        request.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        request.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        settingsListener.OnAppDataCallback(mSettingsRepository.AppRequest(new Gson().toJson(request)));
    }
}