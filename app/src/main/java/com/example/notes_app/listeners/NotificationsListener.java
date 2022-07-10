package com.example.notes_app.listeners;


import com.example.notes_app.entities.Notification;

public interface NotificationsListener {
    void onNotificationDeleteClicked(Notification notification, int position);
}
