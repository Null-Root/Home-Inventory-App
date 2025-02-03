package com.tuna_salmon.home_inventory_app.inventory.item;

import android.widget.EditText;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.inventory.InventoryRepository;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ItemViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private InventoryRepository inventoryRepository;
    public ItemListener itemListener;

    @Inject
    public ItemViewModel(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public String GetCurrentDateTime() {
        SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy hh:mm aa", Locale.getDefault());
        return df.format(Calendar.getInstance().getTime());
    }

    public boolean isItemNameValid(EditText Name, EditText Count, EditText CriticalCount, EditText Price) {
        boolean IsValidName = false, IsValidCount = false, IsValidCriticalCount = false, IsValidPrice = false;

        String NameStr = Name.getText().toString();
        if(CustomFunctions.isVarSet(NameStr)) {
            if(CustomFunctions.isValidStr(NameStr, 1, 12)) {
                IsValidName = true;
            }
        }

        String CountStr = Count.getText().toString();
        if(CustomFunctions.isVarSet(CountStr)) {
            if(CustomFunctions.isValidNumber(CountStr)) {
                IsValidCount = true;
            }
        }

        String CriticalCountStr = CriticalCount.getText().toString();
        if(CustomFunctions.isVarSet(CriticalCountStr)) {
            if(CustomFunctions.isValidNumber(CriticalCountStr)) {
                IsValidCriticalCount = true;
            }
        }

        String PriceStr = Price.getText().toString();
        if(CustomFunctions.isVarSet(PriceStr)) {
            if(CustomFunctions.isValidNumber(PriceStr)) {
                IsValidPrice = true;
            }
        }


        if(!IsValidName) {
            Name.setError("Invalid Name");
        }
        if(!IsValidCount) {
            Count.setError("Invalid Count");
        }
        if(!IsValidCriticalCount) {
            CriticalCount.setError("Invalid Critical Count");
        }
        if(!IsValidPrice) {
            Price.setError("Invalid Price");
        }

        if(IsValidName && IsValidCount && IsValidCriticalCount && IsValidPrice)
            return true;
        return false;
    }

    public boolean CheckItemCallback(String returnString) {
        if(returnString != null) {
            if(!returnString.contains("<html>")) {
                return true;
            }
        }
        return false;
    }

    public void AddItem(String Name, float Count, float CriticalCount, float Price, String Unit) {
        DataModel.Send.Item item = new DataModel().new Send().new Item();
        item.Action = Const.ActionTypes.ADD;
        item.Token = Const.App.TOKEN;
        item.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        item.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        item.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        item.Item_Category = UserAppHandler.Data.AppData().CurrentCategory;
        item.Item_Name = Name;
        item.Item_Count = Count;
        item.Item_Unit = Unit;
        item.Item_CriticalCount = CriticalCount;
        item.Item_Price = Price;
        item.Item_LastEdited = GetCurrentDateTime();
        item.Item_PersonLastEdit = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);

        itemListener.OnDataItemCallback(inventoryRepository.MakeItemRequest(new Gson().toJson(item)));
    }

    public void LoadItem() {
        DataModel.Send.Item item = new DataModel().new Send().new Item();
        item.Action = Const.ActionTypes.LOAD;
        item.Token = Const.App.TOKEN;
        item.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        item.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        item.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        item.Item_Category = UserAppHandler.Data.AppData().CurrentCategory;

        itemListener.OnDataItemCallback(inventoryRepository.MakeItemRequest(new Gson().toJson(item)));
    }

    public void DeleteItem(String Name) {
        DataModel.Send.Item item = new DataModel().new Send().new Item();
        item.Action = Const.ActionTypes.DELETE;
        item.Token = Const.App.TOKEN;
        item.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        item.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        item.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        item.Item_Category = UserAppHandler.Data.AppData().CurrentCategory;
        item.Item_Name = Name;

        itemListener.OnDataItemCallback(inventoryRepository.MakeItemRequest(new Gson().toJson(item)));
    }

    public void EditItem(String Ref_Name, String Name, float Count, float CriticalCount, float Price, String Unit) {
        DataModel.Send.Item item = new DataModel().new Send().new Item();
        item.Action = Const.ActionTypes.EDIT;
        item.Token = Const.App.TOKEN;
        item.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        item.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        item.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);

        item.Ref_Item_Name = Ref_Name;
        item.Item_Category = UserAppHandler.Data.AppData().CurrentCategory;
        item.Item_Name = Name;
        item.Item_Count = Count;
        item.Item_Unit = Unit;
        item.Item_CriticalCount = CriticalCount;
        item.Item_Price = Price;
        item.Item_LastEdited = GetCurrentDateTime();
        item.Item_PersonLastEdit = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.NAME);

        itemListener.OnDataItemCallback(inventoryRepository.MakeItemRequest(new Gson().toJson(item)));
    }

    public void UseItem(ArrayList<DataModel.DatabaseModel.Item> ViewedDataInstance, int position) {
        itemListener.OnShowItemDetailed(ViewedDataInstance, position);
    }
}