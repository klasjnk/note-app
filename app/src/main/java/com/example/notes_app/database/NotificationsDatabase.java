package com.example.notes_app.database;

import android.content.Context;

import androidx.databinding.adapters.Converters;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.notes_app.dao.NoteDao;
import com.example.notes_app.dao.NotificationDao;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Notification;

@Database(entities = {Notification.class}, version = 1, exportSchema = false)
public abstract class NotificationsDatabase extends RoomDatabase {
    private static NotificationsDatabase notificationsDatabase;

    public static synchronized  NotificationsDatabase getDatabase(Context context){
        if(notificationsDatabase == null){
            notificationsDatabase = Room.databaseBuilder(
                    context,
                    NotificationsDatabase.class,
                    "notifications_db"
            ).build();

        }
        return notificationsDatabase;
    }
    public abstract NotificationDao NotificationDao();

}

