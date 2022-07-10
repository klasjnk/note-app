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
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY prioritize DESC, id ASC")
    List<Note> getAllNoteOldest();

    @Query("SELECT * FROM notes ORDER BY prioritize DESC,id DESC")
    List<Note> getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    onConflic Replace this means if id or new note is already available in the database then it will be replaced with new note and our note get updated.
    void insertNote(Note note);

    @Delete
    void deleteNote(Note note);

    @Query("SELECT * FROM notes ORDER BY prioritize DESC ,title ASC")
    List<Note> getAllNoteSortByName();

    @Query("SELECT * FROM notes WHERE tag='Todo' ORDER BY prioritize DESC")
    List<Note> getAllNoteByTodo();

    @Query("SELECT * FROM notes WHERE tag='Học tập' ORDER BY prioritize DESC")
    List<Note> getAllNoteByEdu();

    @Query("SELECT * FROM notes WHERE tag='Du lịch' ORDER BY prioritize DESC")
    List<Note> getAllNoteByTravel();

    @Query("SELECT * FROM notes WHERE tag='Lịch' ORDER BY prioritize DESC")
    List<Note> getAllNoteBySchedule();

    @Query("SELECT * FROM notes WHERE tag='Khoảnh khắc' ORDER BY prioritize DESC")
    List<Note> getAllNoteByNow();

    @Query("SELECT * FROM notes WHERE tag='Hằng ngày' ORDER BY prioritize DESC")
    List<Note> getAllNoteByNormal();



}

