package com.tuna_salmon.home_inventory_app;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tuna_salmon.home_inventory_app.local_database.LocalDatabase;
import com.tuna_salmon.home_inventory_app.services.UserAppHandler;
import com.tuna_salmon.home_inventory_app.services.DataService;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // Initialize "Services"
        UserAppHandler.Data.AppData().Initialize(this);
        UserAppHandler.UI.AppUI().Initialize(this, getLayoutInflater());
        DataService.getService().Initialize(this);
        LocalDatabase.Initialize(this);
    }
}