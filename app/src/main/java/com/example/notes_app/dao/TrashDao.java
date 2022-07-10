package com.example.notes_app.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Trash;

import java.util.List;

@Dao
public interface TrashDao {

    @Query("SELECT * FROM trashs ORDER BY id DESC")
    List<Trash> getAllTrash();

    @Query("DELETE FROM trashs")
    Void deleteAll();

    @Query("DELETE FROM trashs WHERE day_delete>'7 ngày trước'")
    Void deleteTrash();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    onConflic Replace this means if id or new note is already available in the database then it will be replaced with new note and our note get updated.
    void insertNote(Trash trash);

    @Delete
    void deleteNote(Trash trash);


}

