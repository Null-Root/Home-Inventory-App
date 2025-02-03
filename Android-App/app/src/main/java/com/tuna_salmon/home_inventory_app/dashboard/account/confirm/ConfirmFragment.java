package com.tuna_salmon.home_inventory_app.dashboard.account.confirm;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConfirmFragment extends Fragment implements ConfirmListener {

    private NavController navController;
    private ConfirmViewModel mViewModel;

    private EditText ConfirmCode;
    private Button SendCode;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_accountConfirmationFragment_to_dashboardFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_confirm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConfirmViewModel.class);
        navController = Navigation.findNavController(view);

        ConfirmCode = view.findViewById(R.id.accountConfirmCode);
        SendCode = view.findViewById(R.id.accountVerifyCode);

        mViewModel.confirmListener = this;

        SendCode.setOnClickListener(v -> {
            UserAppHandler.UI.AppUI().StartLoadingAnimation();
            mViewModel.confirm(
                    ConfirmCode.getText().toString(),
                    UserAppHandler.Data.Temp.Auth.getName(),
                    UserAppHandler.Data.Temp.Auth.getEmail(),
                    UserAppHandler.Data.Temp.Auth.getPassword(),
                    UserAppHandler.Data.Temp.Auth.getCode());
        });
    }


    @Override
    public void OnConfirmDataCallback(LiveData<String> confirm_response) {
        confirm_response.observe(this, auth_recv -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();
            if(auth_recv != null) {

                // JSON to Class
                DataModel.Recv.Account response = new Gson().fromJson(auth_recv, DataModel.Recv.Account.class);
                CustomFunctions.Logln(response.Message);
                switch (response.Status)
                {
                    case 0: // Success
                        // Show Display
                        UserAppHandler.UI.AppUI().SimpleDialog("User Authenticated", "User has claimed this account");

                        // Set Data
                        String Name = UserAppHandler.Data.Temp.Auth.getName();
                        String Email = UserAppHandler.Data.Temp.Auth.getEmail();
                        String Password = UserAppHandler.Data.Temp.Auth.getPassword();
                        UserAppHandler.Data.AppData().Set_User_Credentials(Name, Email, Password);
                        UserAppHandler.Data.AppData().Set_ID_Instance(response.UniqueID, Const.ID_Type.USER_ID);

                        // Reset Temp Data Holder
                        UserAppHandler.Data.Temp.Auth.SetAccount("", "", "", "");

                        // Go to Dashboard View
                        navController.navigate(R.id.action_accountConfirmationFragment_to_dashboardFragment);
                        break;
                    case 1: // Server Error
                        UserAppHandler.UI.AppUI().SimpleToast("Server Error", Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }

    @Override
    public void OnErrorConfirmDataCallback(String Message) {
        // Show Display
        UserAppHandler.UI.AppUI().SimpleDialog("Confirm Error", Message);

        // Reset Temp Data Holder
        UserAppHandler.Data.Temp.Auth.SetAccount("", "", "", "");

        // Go to Account View
        navController.navigate(R.id.action_accountConfirmationFragment_to_accountFragment);
    }
}