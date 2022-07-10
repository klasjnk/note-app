package com.example.notes_app.applications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.notes_app.R;
import com.example.notes_app.entities.DataSettingManager;

public class NotifyApplication extends Application {

    public static final String CHANNEL_ID = "CHANEL_ID";
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        DataSettingManager.init(getApplicationContext());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Diary App";
            String description = "This is my app";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
