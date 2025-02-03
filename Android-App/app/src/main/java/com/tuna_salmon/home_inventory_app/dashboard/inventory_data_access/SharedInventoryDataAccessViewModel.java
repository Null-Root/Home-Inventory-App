package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.dashboard.DashboardRepository;
import com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access.InventoryDataAccessListener;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SharedInventoryDataAccessViewModel extends ViewModel {

    private DashboardRepository mDashboardRepository;
    public InventoryDataAccessListener inventoryDataAccessListener;

    @Inject
    public SharedInventoryDataAccessViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public void MyInventoryLoadProfileData() {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.MY_INVENTORY_LOAD;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }

    public void MyInventoryUpdateProfileData(String ID, int Permission) {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.MY_INVENTORY_EDIT;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        shareData.UniqueID = ID;
        shareData.Permission = Permission;

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }

    public void MyInventoryDeleteProfileData(String Name, String ID) {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.MY_INVENTORY_DELETE;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        shareData.Name = Name;
        shareData.ID = ID;

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }

    public void OtherInventoryAddProfileData(String OtherID) {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.OTHER_INVENTORY_ADD;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        shareData.Name = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);
        shareData.ID = OtherID;

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }

    public void OtherInventoryLoadProfileData() {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.OTHER_INVENTORY_LOAD;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }

    public void OtherInventoryDeleteProfileData() {
        DataModel.Send.ShareData shareData = new DataModel().new Send().new ShareData();
        shareData.Action = Const.ActionTypes.SharedData.OTHER_INVENTORY_DELETE;
        shareData.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        shareData.Token = Const.App.TOKEN;
        shareData.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        shareData.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);

        inventoryDataAccessListener.OnDataCallback(mDashboardRepository.share_data(new Gson().toJson(shareData)));
    }
}