package com.tuna_salmon.home_inventory_app.inventory.item;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.DataModel;

import java.util.ArrayList;

public interface ItemListener {
    public void OnDataItemCallback(LiveData<String> liveData);
    public void OnShowItemDetailed(ArrayList<DataModel.DatabaseModel.Item> ViewedDataInstance, int position);
}
