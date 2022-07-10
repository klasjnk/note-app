package com.example.notes_app.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes_app.R;
import com.example.notes_app.entities.Note;
import com.example.notes_app.listeners.NotesListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder>{
    private List<Note> notes;
    private NotesListener notesListener;
    private List<Note> notesSource;

    Timer timer;
    public NotesAdapter(List<Note> notes,NotesListener notesListener) {
        this.notesListener = notesListener;
        this.notes = notes;
        notesSource = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note,
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesListener.onNoteClicked(notes.get(position),position);
            }
        });
        holder.layoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                notesListener.onNoteLongClicked(notes.get(position),position);
                return false;
            }
        });
        holder.prioritize_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(notes.get(position).getPrioritize()){
                    holder.prioritize_start.setImageResource(R.drawable.ic_star_off);
                    notes.get(position).setPrioritize(false);
                }else{
                    holder.prioritize_start.setImageResource(R.drawable.ic_star_off);
                    notes.get(position).setPrioritize(true);
                }
                notesListener.onNoteClickedStar(notes.get(position),position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubtitle, textDateTime, textTag;
        ImageView prioritize_start;
        LinearLayout layoutNote;

        RoundedImageView imageNote;
        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textSubtitle= itemView.findViewById(R.id.textSubtitle);
            textDateTime = itemView.findViewById(R.id.textDateTime);
            layoutNote = itemView.findViewById(R.id.layoutNote);
            imageNote = itemView.findViewById(R.id.imageNote);
            prioritize_start = itemView.findViewById(R.id.prioritize_start);
            textTag = itemView.findViewById(R.id.tag_note);

        }
        void setNote(Note note){
            if(note.getPrioritize()==true){
                prioritize_start.setImageResource(R.drawable.ic_star_on);
            }else{
                prioritize_start.setImageResource(R.drawable.ic_star_off);
            }
            textTitle.setText(note.getTitle());
            if(note.getTitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(note.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if(note.getImagePath()!=null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }else{
                imageNote.setVisibility(View.GONE);
            }
            textTag.setText(note.getTag());
        }
    }
    public void searchNotes(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = notesSource;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }

        }, 0);
    }
    public void cancelTimer(){
        if(timer!=null){
            timer.cancel();
        }
    }


}
