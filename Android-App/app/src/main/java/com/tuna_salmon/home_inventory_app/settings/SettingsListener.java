package com.tuna_salmon.home_inventory_app.settings;

import androidx.lifecycle.LiveData;

public interface SettingsListener {
    void OnAppDataCallback(LiveData<String> liveData);
}
