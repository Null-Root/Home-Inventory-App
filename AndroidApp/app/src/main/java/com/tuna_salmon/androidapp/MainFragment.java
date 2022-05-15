package com.tuna_salmon.androidapp;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tuna_salmon.androidapp.services.AppService;
import com.tuna_salmon.androidapp.services.ServiceCollection;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment implements View.OnClickListener {
    private AppService.Data appDataService;
    private AppService.UI appUIService;

    private NavController navController;

    private TextView ID_Type_Holder;
    private TextView ID_Holder;

    private TextView DataSourceDisplay;

    private int isExitApp;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                isExitApp++;

                if(isExitApp == 1) {
                    // Prompt to Exit
                }

                new Handler().postDelayed(() -> {
                    if(isExitApp >= 2) {
                        // EXIT APP
                        requireActivity().finishAffinity();
                    }
                    else {
                        isExitApp = 0;
                    }
                }, 2500);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDataService = ServiceCollection.getServiceCollection().getAppService().getAppDataService();
        appUIService = ServiceCollection.getServiceCollection().getAppService().getAppUIService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        ID_Type_Holder = view.findViewById(R.id.mainMenuShowIDType);
        ID_Holder = view.findViewById(R.id.mainMenuShowID);
        DataSourceDisplay = view.findViewById(R.id.mainFragmentDataSourceDisplay);

        switch (Objects.requireNonNull(appDataService.getAppData().get(Const.Device.DATA_SOURCE))) {
            case Const.DataSource.OFFLINE:
                DataSourceDisplay.setTextColor(ColorStateList.valueOf(Color.parseColor("#f23524")));
                DataSourceDisplay.setText(Const.DataSource.OFFLINE);
                ID_Type_Holder.setText("");
                ID_Holder.setText("");
                break;
            case Const.DataSource.ONLINE:
                DataSourceDisplay.setTextColor(ColorStateList.valueOf(Color.parseColor("#4dffa2")));
                DataSourceDisplay.setText(Const.DataSource.ONLINE);
                ID_Type_Holder.setText(appDataService.getAppData().get(Const.Device.ID_TYPE));
                ID_Holder.setText(appDataService.getAppData().get(Const.Device.ID));
                break;
        }

        view.findViewById(R.id.mainInventoryView).setOnClickListener(this);
        view.findViewById(R.id.mainMultiEditView).setOnClickListener(this);
        view.findViewById(R.id.mainDashboardView).setOnClickListener(this);
        view.findViewById(R.id.mainSettingsView).setOnClickListener(this);
        view.findViewById(R.id.mainCreditsView).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.mainInventoryView:
                // Check For ID Before Entering
                if(allowNavAction()) {
                    navController.navigate(R.id.action_mainFragment_to_categoryFragment);
                }
                break;
            case R.id.mainMultiEditView:
                // Check For ID Before Entering
                if(allowNavAction()) {
                    navController.navigate(R.id.action_mainFragment_to_bulkEditFragment);
                }
                break;
            case R.id.mainDashboardView:
                navController.navigate(R.id.action_mainFragment_to_dashboardFragment);
                break;
            case R.id.mainSettingsView:
                navController.navigate(R.id.action_mainFragment_to_settingsFragment);
                break;
            case R.id.mainCreditsView:
                navController.navigate(R.id.action_mainFragment_to_creditsFragment);
        }
    }

    private boolean allowNavAction() {
        switch (Objects.requireNonNull(appDataService.getAppData().get(Const.Device.DATA_SOURCE))) {
            case Const.DataSource.ONLINE:
            {
                if(appDataService.isAnyIDUsed())
                    return true;
                else {
                    appUIService.simpleDialog("Nav Error", "No ID Used!");
                    return false;
                }
            }
            case Const.DataSource.OFFLINE:
                return true;
        }
        return false;
    }
}