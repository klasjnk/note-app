package com.example.notes_app.listeners;

import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Trash;

public interface TrashsListener {
    void onNoteClicked(Trash trash, int position);

    void onNoteLongClicked(Trash trash, int posittion);
//    void onPririoritizeCliked(Note note,int posittion);


}
