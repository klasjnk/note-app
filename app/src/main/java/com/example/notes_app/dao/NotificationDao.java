package com.example.notes_app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.notes_app.entities.Notification;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications ORDER BY id DESC")
    List<Notification> getAllNotification();

    @Query("DELETE FROM notifications")
    Void deleteAll();

    @Delete
    void deleteNotification(Notification notification);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    onConflic Replace this means if id or new note is already available in the database then it will be replaced with new note and our note get updated.
    void insertNotification(Notification notification);




}

