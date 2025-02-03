package com.tuna_salmon.home_inventory_app.multi_edit;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.DataModel;

import java.util.ArrayList;

public interface MultiEditListener {
    public void OnDataMultiEditCallback(LiveData<String> liveData);
    public void OnUseMultiEditCallback(LiveData<String> liveData, String Name, ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditItemContainer> multiEditItemContainers);
    public void OnShowMultiEditCallback(ArrayList<DataModel.DatabaseModel.MultiEdit.MultiEditContainer> ViewedDataInstance, int position);
}