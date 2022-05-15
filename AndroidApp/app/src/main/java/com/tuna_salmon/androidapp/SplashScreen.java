package com.tuna_salmon.androidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {
    private final Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_splashscreen);

        Objects.requireNonNull(getSupportActionBar()).hide();

        handler.postDelayed(runnable, 400);
    }

    private Runnable runnable = () -> {
        if(!isFinishing()) {
            OtherTasks();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    };

    private void OtherTasks() {
        //createNotificationChannel();
        //Update.CheckForUpdates();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);

            NotificationChannel channel = new NotificationChannel("App Channel", name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
