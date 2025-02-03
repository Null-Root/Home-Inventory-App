package com.tuna_salmon.home_inventory_app.dashboard.account;

import androidx.lifecycle.LiveData;

import com.tuna_salmon.home_inventory_app.data.DataModel;

public interface AccountListener {
    public void OnAuthDataCallback(LiveData<String> auth_response, String Name, String email, String password, String code);
    public void OnAccountErrorCallback(boolean isNameValid, boolean isEmailValid, boolean isPasswordValid);
}
