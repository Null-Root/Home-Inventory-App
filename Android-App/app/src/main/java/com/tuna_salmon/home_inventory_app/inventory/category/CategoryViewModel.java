package com.tuna_salmon.home_inventory_app.inventory.category;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.inventory.InventoryRepository;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategoryViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private InventoryRepository inventoryRepository;
    public CategoryListener categoryListener;

    @Inject
    public CategoryViewModel(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public boolean isCategoryNameValid(String Name) {
        if(CustomFunctions.isVarSet(Name)) {
            if(CustomFunctions.isValidStr(Name, 0, 12)) {
                return true;
            }
        }
        return false;
    }

    public boolean CheckCategoryResultCallback(String returnString) {
        if(CustomFunctions.isVarSet(returnString)) {
            if(!returnString.contains("<html>")) {
                return true;
            }
        }
        return false;
    }

    public void AddCategory(String Name) {
        DataModel.Send.Category category = new DataModel().new Send().new Category();
        category.Action = Const.ActionTypes.ADD;
        category.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        category.Token = Const.App.TOKEN;
        category.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        category.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        category.Category_Name = Name;
        categoryListener.OnDataCategoryCallback(inventoryRepository.MakeCategoryRequest(new Gson().toJson(category)));
    }

    public void LoadCategory() {
        DataModel.Send.Category category = new DataModel().new Send().new Category();
        category.Action = Const.ActionTypes.LOAD;
        category.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        category.Token = Const.App.TOKEN;
        category.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        category.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        categoryListener.OnDataCategoryCallback(inventoryRepository.MakeCategoryRequest(new Gson().toJson(category)));
    }

    public void DeleteCategory(String Name) {
        DataModel.Send.Category category = new DataModel().new Send().new Category();
        category.Action = Const.ActionTypes.DELETE;
        category.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        category.Token = Const.App.TOKEN;
        category.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        category.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        category.Category_Name = Name;
        categoryListener.OnDataCategoryCallback(inventoryRepository.MakeCategoryRequest(new Gson().toJson(category)));
    }

    public void EditCategory(String Ref_Name, String Name) {
        DataModel.Send.Category category = new DataModel().new Send().new Category();
        category.Action = Const.ActionTypes.EDIT;
        category.UniqueID = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.ID);
        category.Token = Const.App.TOKEN;
        category.Category_Name = Name;
        category.Email = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.EMAIL);
        category.Password = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.PASSWORD);
        category.Ref_Category_Name = Ref_Name;
        categoryListener.OnDataCategoryCallback(inventoryRepository.MakeCategoryRequest(new Gson().toJson(category)));
    }
}