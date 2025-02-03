package com.tuna_salmon.home_inventory_app.dashboard.account.confirm;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.DataModel;

public interface ConfirmListener {
    public void OnConfirmDataCallback(LiveData<String> auth_response);
    public void OnErrorConfirmDataCallback(String Message);
}
