package com.tuna_salmon.home_inventory_app.dashboard.user_settings;

import androidx.lifecycle.LiveData;

public interface UserSettingsListener {
    public void OnChangeIDCallback(LiveData<String> liveData);
    public void OnChangeLockStateCallback(LiveData<String> liveData);
    public void OnChangeVisibilityStateCallback(LiveData<String> liveData);
    public void OnDeleteAccountCallback(LiveData<String> liveData);
}
