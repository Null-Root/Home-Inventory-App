package com.tuna_salmon.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.tuna_salmon.androidapp.services.ServiceCollection;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServiceCollection.getServiceCollection().Initialize(
                this.getApplicationContext(),
                this.getLayoutInflater()
        );

        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();
    }
}