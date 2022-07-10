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
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes_app.R;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Trash;
import com.example.notes_app.listeners.NotesListener;
import com.example.notes_app.listeners.TrashsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TrashsAdapter extends RecyclerView.Adapter<TrashsAdapter.NoteViewHolder>{
    private List<Trash> trashes;
    private TrashsListener trashsListener;
    private List<Trash> trashesSource;

    Timer timer;
    public TrashsAdapter(List<Trash> trashes,TrashsListener trashsListener) {
        this.trashsListener = trashsListener;
        this.trashes = trashes;
        trashesSource = trashes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_trash,
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setNote(trashes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trashsListener.onNoteClicked(trashes.get(position),position);
            }
        });
        holder.layoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                trashsListener.onNoteLongClicked(trashes.get(position),position);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return trashes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textSubtitle, textDateTime,textTag,textdateDelete;
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
            textdateDelete = itemView.findViewById(R.id.dateDelete);
        }
        void setNote(Trash trash){
            if(trash.getPrioritize()==true){
                prioritize_start.setImageResource(R.drawable.ic_star_on);
            }else{
                prioritize_start.setImageResource(R.drawable.ic_star_off);
            }
            textTitle.setText(trash.getTitle());
            if(trash.getTitle().trim().isEmpty()){
                textSubtitle.setVisibility(View.GONE);
            }else{
                textSubtitle.setText(trash.getSubtitle());
            }
            textDateTime.setText(trash.getDateTime());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(trash.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(trash.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if(trash.getImagePath()!=null){
                imageNote.setImageBitmap(BitmapFactory.decodeFile(trash.getImagePath()));
                imageNote.setVisibility(View.VISIBLE);
            }else{
                imageNote.setVisibility(View.GONE);
            }
            textTag.setText(trash.getTag());
            textdateDelete.setText(trash.getDayDelete());


        }
    }

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    trashes = trashesSource;
                } else {
                    ArrayList<Trash> temp = new ArrayList<>();
                    for (Trash trash : trashesSource) {
                        if (trash.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) || trash.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || trash.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(trash);
                        }
                    }
                    trashes = temp;
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
