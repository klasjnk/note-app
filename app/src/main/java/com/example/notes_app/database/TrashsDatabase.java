package com.example.notes_app.database;

import android.content.Context;

import androidx.databinding.adapters.Converters;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.notes_app.dao.NoteDao;
import com.example.notes_app.dao.TrashDao;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Trash;

@Database(entities = {Trash.class}, version = 1, exportSchema = false)
public abstract class TrashsDatabase extends RoomDatabase {
    private static TrashsDatabase trashsDatabase;

    public static synchronized  TrashsDatabase getDatabase(Context context){
        if(trashsDatabase == null){
            trashsDatabase = Room.databaseBuilder(
                    context,
                    TrashsDatabase.class,
                    "trashsssss_db"
            ).build();

        }
        return trashsDatabase;
    }
    public abstract TrashDao trashDao();

}

