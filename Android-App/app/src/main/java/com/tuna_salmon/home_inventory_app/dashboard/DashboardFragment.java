package com.tuna_salmon.home_inventory_app.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends Fragment {

    private NavController navController;
    private DashboardViewModel mViewModel;

    private ConstraintLayout NoAccountLayout;
    private CardView UserIDContainer;
    private TextView NameView;
    private TextView CodeView;
    private EditText CustomID;
    private Button UseUserID;
    private Button UseCustomID;
    private ImageView ViewUserSettings;
    private Button InventoryDataAccess;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigate(R.id.action_dashboardFragment_to_mainFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        navController = Navigation.findNavController(view);

        NoAccountLayout = view.findViewById(R.id.dashboardNoAccountOnDeviceOverlay);
        UserIDContainer = view.findViewById(R.id.dashboardInventoryUserIDContainer);
        NameView = view.findViewById(R.id.dashboardNameView);
        CodeView = view.findViewById(R.id.dashboardCodeView);
        CustomID = view.findViewById(R.id.dashboardEnterCustomID);
        UseUserID = view.findViewById(R.id.dashboardUseUserID);
        UseCustomID = view.findViewById(R.id.dashboardUseCustomID);
        ViewUserSettings = view.findViewById(R.id.dashboardUserSettingsButton);
        InventoryDataAccess = view.findViewById(R.id.dashboardInventoryAccessButton);

        UpdateUI();

        ViewUserSettings.setOnClickListener(v -> {
            navController.navigate(R.id.action_dashboardFragment_to_userSettingsFragment);
        });

        UseUserID.setOnClickListener(v -> {
            // Set as ID Used
            UserAppHandler.Data.AppData().Set_ID_Instance(
                    UserAppHandler.Data.AppData().Get_App_Data().get(Const.ID_Type.USER_ID),
                    Const.ID_Type.USER_ID);
            // Update UI
            UpdateUI();
            // Show Display
            UserAppHandler.UI.AppUI().SimpleDialog("ID Used", "User ID is Used");
        });

        UseCustomID.setOnClickListener(v -> {
            UserAppHandler.UI.AppUI().StartLoadingAnimation();
            mViewModel.UseCustomID(CustomID.getText().toString()).observe(getViewLifecycleOwner(), s -> {
                UserAppHandler.UI.AppUI().EndLoadingAnimation();
                if(s != null) {
                    DataModel.Recv.Account response = new Gson().fromJson(s, DataModel.Recv.Account.class);
                    switch (response.ID_Status) {
                        case 0:
                            switch (response.AuthResponse) {
                                case 0: // 0 -> REJECTED
                                    // Show Display
                                    UserAppHandler.UI.AppUI().SimpleDialog("ID Not Used", response.Message);
                                    break;
                                case 1: // 1 -> ACCEPTED
                                    // Set as ID Used
                                    UserAppHandler.Data.AppData().Set_ID_Instance(CustomID.getText().toString(), Const.ID_Type.CUSTOM_ID);

                                    // Update UI
                                    UpdateUI();

                                    // Show Display
                                    UserAppHandler.UI.AppUI().SimpleDialog("ID Used", "Custom ID is Used");
                                    break;
                            }
                            break;
                        case 1:
                            // Show Display
                            UserAppHandler.UI.AppUI().SimpleDialog("Server Error", response.Message);
                            break;
                    }
                } else {
                    // Show Display
                    UserAppHandler.UI.AppUI().SimpleDialog("Server Error", "ERROR");
                }
            });
        });

        InventoryDataAccess.setOnClickListener(v -> {
            navController.navigate(R.id.action_dashboardFragment_to_inventoryDataAccessFragment);
        });

        view.findViewById(R.id.dashboardRegisterButton).setOnClickListener(v -> {
            navController.navigate(R.id.action_dashboardFragment_to_accountFragment);
        });
    }

    private void UpdateUI()
    {
        Map<String, String> instanceDataMap = UserAppHandler.Data.AppData().Get_App_Data();
        Map<String, String> localDataMap = UserAppHandler.Data.AppData().Get_App_Data();

        // Check Account
        if(UserAppHandler.Data.AppData().isAccountRegistered()) {
            // Hide No Account Layout
            NoAccountLayout.setVisibility(View.GONE);

            // Set UI
            NameView.setText(instanceDataMap.get(Const.Device.NAME));
            CodeView.setText(localDataMap.get(Const.ID_Type.USER_ID));
        }

        // User ID Container
        if(localDataMap.get(Const.ID_Type.USER_ID) != null) {
            if(!localDataMap.get(Const.ID_Type.USER_ID).equals("")) {
                UserIDContainer.setVisibility(View.VISIBLE);
            }
        }

        // Check Custom ID Block
        if(localDataMap.get(Const.ID_Type.CUSTOM_ID) != null) {
            if(!localDataMap.get(Const.ID_Type.CUSTOM_ID).equals("")) {
                // Set UI
                CustomID.setText(localDataMap.get(Const.ID_Type.CUSTOM_ID));
            }
        }
    }
}