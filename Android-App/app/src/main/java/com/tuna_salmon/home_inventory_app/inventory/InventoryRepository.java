package com.tuna_salmon.home_inventory_app.inventory;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.services.DataService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class InventoryRepository {

    private static InventoryRepository s_instance = null;
    private InventoryRepository() {}

    @Provides
    @ViewModelScoped
    public static InventoryRepository getInstance() {
        if(s_instance == null)
            s_instance = new InventoryRepository();
        return s_instance;
    }

    public LiveData<String> MakeCategoryRequest(String JsonData) {
        return DataService.getService().DataRequest(Const.API.CATEGORY, JsonData, false);
    }

    public LiveData<String> MakeItemRequest(String JsonData) {
        return DataService.getService().DataRequest(Const.API.ITEM, JsonData, false);
    }

    public LiveData<String> MakeMultiEditRequest(String JsonData) {
        return DataService.getService().DataRequest(Const.API.MULTI_EDIT, JsonData, false);
    }
}