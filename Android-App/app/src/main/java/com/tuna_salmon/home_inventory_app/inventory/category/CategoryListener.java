package com.tuna_salmon.home_inventory_app.inventory.category;

import androidx.lifecycle.LiveData;

public interface CategoryListener {
    public void OnDataCategoryCallback(LiveData<String> liveData);
}
