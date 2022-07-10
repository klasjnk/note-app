package com.example.notes_app.listeners;

import com.example.notes_app.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);

    void onNoteLongClicked(Note note, int posittion);
//    void onPririoritizeCliked(Note note,int posittion);

    void onNoteClickedStar(Note note, int posittion);
}
