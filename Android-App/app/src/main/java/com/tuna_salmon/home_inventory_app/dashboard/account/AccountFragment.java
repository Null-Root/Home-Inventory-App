package com.tuna_salmon.home_inventory_app.dashboard.account;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AccountFragment extends Fragment implements AccountListener, TextWatcher {

    private NavController navController;
    private AccountViewModel mViewModel;

    private EditText Name;
    private EditText Email;
    private EditText Password;
    private RadioGroup AccountAction;
    private String AccountActionSelected = null;
    private Button CommitAuth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_accountFragment_to_dashboardFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sub_fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout NameContainer = view.findViewById(R.id.accountNameContainer);

        mViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        navController = Navigation.findNavController(view);

        Name = view.findViewById(R.id.accountName);
        Email = view.findViewById(R.id.accountEmail);
        Password = view.findViewById(R.id.accountPassword);
        AccountAction = view.findViewById(R.id.accountActionsHolder);
        CommitAuth = view.findViewById(R.id.accountCommitAction);

        // Setup Listeners
        Name.addTextChangedListener(this);
        Email.addTextChangedListener(this);
        Password.addTextChangedListener(this);
        mViewModel.accountListener = this;

        final TextView accountAuthLabel = view.findViewById(R.id.accountAuthLabel);

        CommitAuth.setOnClickListener(v -> {
            if(AccountActionSelected != null) {
                UserAppHandler.UI.AppUI().StartLoadingAnimation();
                String GeneratedCode = mViewModel.GenerateVerificationCode(6);
                mViewModel.register_or_login(AccountActionSelected, Name.getText().toString(), Email.getText().toString(), Password.getText().toString(), GeneratedCode);
            }
            else {
                UserAppHandler.UI.AppUI().SimpleDialog("Cannot Continue", "Choose if Sign Up or Log In");
            }
        });

        AccountAction.setOnCheckedChangeListener((group, checkedId) -> {
            switch (group.getCheckedRadioButtonId())
            {
                case R.id.accountSignUpButton:
                    AccountActionSelected = Const.Account.SIGN_UP;
                    NameContainer.setVisibility(View.VISIBLE);
                    accountAuthLabel.setText(Const.Account.SIGN_UP);
                    break;
                case R.id.accountLogInButton:
                    AccountActionSelected = Const.Account.LOG_IN;
                    NameContainer.setVisibility(View.GONE);
                    accountAuthLabel.setText(Const.Account.LOG_IN);
                    break;
            }
            CommitAuth.setText(AccountActionSelected);
        });
    }


    @Override
    public void OnAuthDataCallback(LiveData<String> live_auth_response, String name, String email, String password, String code) {
        live_auth_response.observe(this, auth_res -> {
            UserAppHandler.UI.AppUI().EndLoadingAnimation();

            if(auth_res != null)
            {
                // JSON to Class
                DataModel.Recv.Account response = new Gson().fromJson(auth_res, DataModel.Recv.Account.class);

                switch (response.Status)
                {
                    case 0: // Success
                        switch (response.AuthResponse)
                        {
                            case 0: // 0 -> AUTH/CONFIRM PROMPT REVOKED
                                // Show Display
                                UserAppHandler.UI.AppUI().SimpleDialog("Authentication Error", response.Message);
                                break;
                            case 1: // 1 -> GO TO CONFIRM (SIGN UP)
                                // Data Saved on Temp Data Holder
                                UserAppHandler.Data.Temp.Auth.SetAccount(name, email, password, code);

                                // Go to Confirm View
                                navController.navigate(R.id.action_accountFragment_to_accountConfirm);
                                break;
                            case 2: // 2 -> GO BACK TO DASHBOARD (LOG IN)
                                // Show Display
                                UserAppHandler.UI.AppUI().SimpleDialog("User Authenticated", "User has claimed this account");

                                // Set Data
                                UserAppHandler.Data.AppData().Set_ID_Instance(response.UniqueID, Const.ID_Type.USER_ID);
                                UserAppHandler.Data.AppData().Set_User_Credentials(response.Name, email, password);

                                // Go to Dashboard View
                                navController.navigate(R.id.action_accountFragment_to_dashboardFragment);
                                break;
                        }
                        break;
                    case 1: // Server Error
                        UserAppHandler.UI.AppUI().SimpleToast("Server Error", Toast.LENGTH_LONG);
                        break;
                }
            }
        });
    }

    @Override
    public void OnAccountErrorCallback(boolean isNameValid, boolean isEmailValid, boolean isPasswordValid) {
        UserAppHandler.UI.AppUI().EndLoadingAnimation();

        if(!isNameValid) {
            Name.setError("Invalid Name, 1-12 Characters Only");
        }

        if(!isEmailValid) {
            Email.setError("Invalid Email");
        }

        if(!isPasswordValid) {
            Password.setError("Invalid Password, 1-30 Characters Only");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Check If An Auth Action is Active
        if(CustomFunctions.isVarSet(AccountActionSelected)) {
            switch (AccountActionSelected) {
                case Const.Account.SIGN_UP:
                    mViewModel.register_check_input(Name.getText().toString(), Email.getText().toString(), Password.getText().toString());
                    break;
                case Const.Account.LOG_IN:
                    mViewModel.login_check_input(Email.getText().toString(), Password.getText().toString());
                    break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {}
}