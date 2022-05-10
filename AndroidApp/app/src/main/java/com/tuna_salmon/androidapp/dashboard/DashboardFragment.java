package com.tuna_salmon.androidapp.dashboard;

import androidx.activity.OnBackPressedCallback;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.tuna_salmon.androidapp.Const;
import com.tuna_salmon.androidapp.R;
import com.tuna_salmon.androidapp.services.AppService;
import com.tuna_salmon.androidapp.services.ServiceCollection;

import java.util.Map;

public class DashboardFragment extends Fragment {
    private AppService.Data appDataService;
    private AppService.UI appUIService;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        appDataService = ServiceCollection.getServiceCollection().getAppService().getAppDataService();
        appUIService = ServiceCollection.getServiceCollection().getAppService().getAppUIService();
        return inflater.inflate(R.layout.dashboard_fragment, container, false);
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
            appDataService.setIDInstance(
                    appDataService.getAppData().get(Const.ID_Type.USER_ID),
                    Const.ID_Type.USER_ID);
            // Update UI
            UpdateUI();
            // Show Display
            appUIService.simpleDialog("ID Used", "User ID is Used");
        });

        UseCustomID.setOnClickListener(v -> {
            appUIService.startLoadingAnimation();
            mViewModel.UseCustomID(CustomID.getText().toString()).observe(getViewLifecycleOwner(), s -> {
                appUIService.endLoadingAnimation();
                //
            });
        });

        InventoryDataAccess.setOnClickListener(v -> {
            //navController.navigate(R.id.action_dashboardFragment_to_inventoryDataAccessFragment);
        });

        view.findViewById(R.id.dashboardRegisterButton).setOnClickListener(v -> {
            //navController.navigate(R.id.action_dashboardFragment_to_accountFragment);
        });
    }

    private void UpdateUI()
    {
        Map<String, String> localDataMap = appDataService.getAppData();

        // Check Account
        if(appDataService.isAccountRegistered()) {
            // Hide No Account Layout
            NoAccountLayout.setVisibility(View.GONE);

            // Set UI
            NameView.setText(localDataMap.get(Const.Device.NAME));
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