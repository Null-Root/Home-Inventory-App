package com.tuna_salmon.home_inventory_app.dashboard.account.confirm;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.dashboard.DashboardRepository;
import com.tuna_salmon.home_inventory_app.data.DataModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ConfirmViewModel extends ViewModel {

    public ConfirmListener confirmListener;
    private DashboardRepository mDashboardRepository;

    @Inject
    public ConfirmViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public void confirm(String Code, String Name, String Email, String Password, String GeneratedCode) {
        if(Code.equals(GeneratedCode)) {
            DataModel.Send.Account accountData = new DataModel().new Send().new Account();
            accountData.Name = Name;
            accountData.Email = Email;
            accountData.Password = Password;
            accountData.Token = Const.App.TOKEN;
            accountData.Action = Const.Account.ADD_ACCOUNT;
            confirmListener.OnConfirmDataCallback(mDashboardRepository.confirm_account(new Gson().toJson(accountData)));
        }
        else {
            confirmListener.OnErrorConfirmDataCallback("Code Mismatch");
        }
    }
}