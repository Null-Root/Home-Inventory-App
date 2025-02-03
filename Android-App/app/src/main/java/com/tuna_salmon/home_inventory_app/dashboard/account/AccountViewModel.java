package com.tuna_salmon.home_inventory_app.dashboard.account;

import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.dashboard.DashboardRepository;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;

import java.util.Random;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AccountViewModel extends ViewModel {

    public AccountListener accountListener;
    private DashboardRepository mDashboardRepository;

    @Inject
    public AccountViewModel(DashboardRepository dashboardRepository) {
        this.mDashboardRepository = dashboardRepository;
    }

    public boolean login_check_input(String Email, String Password) {
        boolean isEmailValid = false, isPasswordValid = false;

        if(!Email.equals("")) {
            if(Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(Email).find()) {
                isEmailValid = true;
            }
        }

        if(!Password.equals("")) {
            if(Pattern.compile("^[A-Za-z0-9 _\\-.,]{5,30}$").matcher(Password).find()) {
                isPasswordValid = true;
            }
        }

        if(!isEmailValid || !isPasswordValid)
            accountListener.OnAccountErrorCallback(true, isEmailValid, isPasswordValid);

        return isEmailValid && isPasswordValid;
    }

    public boolean register_check_input(String Name, String Email, String Password) {
        boolean isNameValid = false, isEmailValid = false, isPasswordValid = false;

        if(!Name.equals("")) {
            if(Pattern.compile("^[a-zA-Z0-9_]{1,12}$").matcher(Name).find()) {
                CustomFunctions.Logln("VALID NAME!");
                isNameValid = true;
            }
        }

        if(!Email.equals("")) {
            if(Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$").matcher(Email).find()) {
                isEmailValid = true;
            }
        }

        if(!Password.equals("")) {
            if(Pattern.compile("^[A-Za-z0-9 _\\-.,]{1,30}$").matcher(Password).find()) {
                isPasswordValid = true;
            }
        }

        if(!isNameValid || !isEmailValid || !isPasswordValid)
            accountListener.OnAccountErrorCallback(isNameValid, isEmailValid, isPasswordValid);

        return isEmailValid && isPasswordValid;
    }

    public void register_or_login(String AccAction, String Name, String Email, String Password, String Code) {
        boolean ProceedFlag = true;

        switch (AccAction) {
            case Const.Account.SIGN_UP:
                ProceedFlag = register_check_input(Name, Email, Password);
                break;
            case Const.Account.LOG_IN:
                ProceedFlag = login_check_input(Email, Password);
                break;
        }

        if(ProceedFlag) {
            DataModel.Send.Account auth_account = new DataModel().new Send().new Account();
            auth_account.Token = Const.App.TOKEN;
            auth_account.Action = AccAction;
            auth_account.Name = Name;
            auth_account.Email = Email;
            auth_account.Password = Password;
            auth_account.Code = Code;
            accountListener.OnAuthDataCallback(mDashboardRepository.check_auth(new Gson().toJson(auth_account)), Name, Email, Password, Code);
        }
    }

    public String GenerateVerificationCode(int Length) {
        String code = "";
        Random random = new Random();
        for (int i = 0; i < Length; i++) {
            code += random.nextInt(10); // Put together the random numbers every iteration
        }
        return code;
    }
}