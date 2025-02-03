package com.tuna_salmon.home_inventory_app.dashboard.inventory_data_access;

import androidx.lifecycle.LiveData;

public interface InventoryDataAccessListener {
    public void OnDataCallback(LiveData<String> liveData);
}
