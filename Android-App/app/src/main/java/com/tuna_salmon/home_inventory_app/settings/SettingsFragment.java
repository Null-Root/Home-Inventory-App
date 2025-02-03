package com.tuna_salmon.home_inventory_app.settings;

import androidx.activity.OnBackPressedCallback;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tuna_salmon.home_inventory_app.R;
import com.tuna_salmon.home_inventory_app.data.Const;
import com.tuna_salmon.home_inventory_app.data.DataModel;
import com.tuna_salmon.home_inventory_app.extra_stuffs.CustomFunctions;
import com.tuna_salmon.home_inventory_app.local_database.api.LocalAPI;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;

import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsFragment extends Fragment implements SettingsListener {

    private SettingsViewModel mViewModel;

    private Switch InventoryDataSource;
    private Button MergeOfflineToOnline;
    private Button MergeOnlineToOffline;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        mViewModel.settingsListener = this;

        //region Variable
        InventoryDataSource = view.findViewById(R.id.settingsInventoryDataSource);
        MergeOfflineToOnline = view.findViewById(R.id.settingsMergeOfflineToOnlineInventoryButton);
        MergeOnlineToOffline = view.findViewById(R.id.settingsMergeOnlineToOfflineInventoryButton);
        //endregion

        //region Determine Data Source

        String DataSource = UserAppHandler.Data.AppData().Get_App_Data().get(Const.Device.DATA_SOURCE);
        if(DataSource != null) {
            switch (DataSource) {
                case Const.DataSource.ONLINE:
                    UserAppHandler.Data.AppData().<String>Set_Local_Item(Const.Device.DATA_SOURCE, DataSource);
                    InventoryDataSource.setChecked(true);
                    break;
                case Const.DataSource.OFFLINE:
                    InventoryDataSource.setChecked(false);
                    break;
            }
        }

        InventoryDataSource.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                UserAppHandler.Data.AppData().<String>Set_Local_Item(Const.Device.DATA_SOURCE, Const.DataSource.ONLINE);
            else
                UserAppHandler.Data.AppData().<String>Set_Local_Item(Const.Device.DATA_SOURCE, Const.DataSource.OFFLINE);
        });

        //endregion

        //region Set Listener For Merging

        MergeOfflineToOnline.setOnClickListener(v -> {
            AlertDialog builder = new AlertDialog.Builder(getContext())
                    .setTitle("Overwrite Inventory")
                    .setMessage("Do You Want To Permanently Overwrite your Online Inventory From Offline Inventory?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.OverwriteInventoryOfflineToOnline();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });

        MergeOnlineToOffline.setOnClickListener(v -> {
            AlertDialog builder = new AlertDialog.Builder(getContext())
                    .setTitle("Overwrite Inventory")
                    .setMessage("Do You Want To Permanently Overwrite your Offline Inventory From Online Inventory?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mViewModel.OverwriteInventoryOnlineToOffline();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .create();
            builder.show();
        });

        //endregion

    }

    @Override
    public void OnAppDataCallback(LiveData<String> liveData) {
        liveData.observe(getViewLifecycleOwner(), s -> {
            DataModel.Recv.App response = new Gson().fromJson(s, DataModel.Recv.App.class);
            if(response.ChangeDatabaseData) {
                LocalAPI.getAPI().setDatabaseDataFromWeb(response.New_DB_Data);
            }

            UserAppHandler.UI.AppUI().SimpleDialog("Action Success", "Database has been overwritten");
        });
    }
}