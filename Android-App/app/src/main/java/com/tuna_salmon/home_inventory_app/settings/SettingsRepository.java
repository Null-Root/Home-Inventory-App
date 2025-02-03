package com.tuna_salmon.home_inventory_app.settings;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.inventory.InventoryRepository;
import com.tuna_salmon.home_inventory_app.services.DataService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.scopes.ViewModelScoped;

@Module
@InstallIn(ViewModelComponent.class)
public class SettingsRepository {

    private static SettingsRepository s_instance = null;
    private SettingsRepository() {}

    @Provides
    @ViewModelScoped
    public static SettingsRepository getInstance() {
        if(s_instance == null)
            s_instance = new SettingsRepository();
        return s_instance;
    }

    public LiveData<String> AppRequest(String Data) {
        return DataService.getService().DataRequest(Const.API.APP, Data, true);
    }
}
