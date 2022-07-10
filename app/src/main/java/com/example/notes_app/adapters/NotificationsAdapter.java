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
import com.example.notes_app.entities.Notification;
import com.example.notes_app.listeners.NotesListener;
import com.example.notes_app.listeners.NotificationsListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>{
    private List<Notification> notifications;
    private List<Notification> notificationsSource;
    private NotificationsListener notificationsListener;

    Timer timer;
    public NotificationsAdapter(List<Notification> notifications, NotificationsListener notificationsListener) {
        this.notifications = notifications;
        notificationsSource = notifications;
        this.notificationsListener = notificationsListener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification,
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.setNote(notifications.get(position));
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationsListener.onNotificationDeleteClicked(notifications.get(position),position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textDateTime, textSubtitle;
        ImageView imageNotification, imageDelete;
        LinearLayout layoutNotification;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.titleNotification);
            textSubtitle = itemView.findViewById(R.id.subtitleNotification);
            textDateTime = itemView.findViewById(R.id.dateNotification);
            layoutNotification = itemView.findViewById(R.id.layoutNotification);
            imageNotification = itemView.findViewById(R.id.imageNotification);
            imageDelete = itemView.findViewById(R.id.deleteNotification);


        }
        void setNote(Notification notification){
            textTitle.setText(notification.getTitle());
            textSubtitle.setText(notification.getSubtitle());
            textDateTime.setText(notification.getDateTime());
            imageNotification.setImageResource(notification.getImage());
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNotification.getBackground();
            if(notification.getColor()!=null){
                gradientDrawable.setColor(Color.parseColor(notification.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

        }
    }
    public void searchNotification(final String searchKeyword) {
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    notifications = notificationsSource;
                } else {
                    ArrayList<Notification> temp = new ArrayList<>();
                    for (Notification notification : notificationsSource) {
                        if (notification.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) || notification.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                ) {
                            temp.add(notification);
                        }
                    }
                    notifications = temp;
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
