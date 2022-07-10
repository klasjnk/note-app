package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notes_app.R;
import com.example.notes_app.adapters.NotificationsAdapter;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.database.NotificationsDatabase;
import com.example.notes_app.entities.DataSettingManager;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Notification;
import com.example.notes_app.listeners.NotificationsListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements NotificationsListener {
    private List<Notification> notificationsList;
    RecyclerView recyclerView;
    NotificationsAdapter notificationsAdapter;
    int index=-1;
    AlertDialog dialogDeleteNotifications;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationsList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Hoạt động");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        recyclerView = findViewById(R.id.notificationRecyclerview);
        if(!DataSettingManager.getColor().equals("")){
            toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
        }
        notificationsAdapter = new NotificationsAdapter(notificationsList,this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(notificationsAdapter);
        getNotifications();

    }
    private void deleteNotification(){
        @SuppressLint("StaticFieldLeak")
        class DeleteNotificationTask extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotificationsDatabase.getDatabase(getApplicationContext()).NotificationDao().deleteNotification(notificationsList.get(index));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                notificationsList.remove(index);
                notificationsAdapter.notifyItemRemoved(index);

            }
        }
        new DeleteNotificationTask().execute();


    }
    private void getNotifications(){

        @SuppressLint("StaticFieldLeak")
        class getNotificationsTask extends AsyncTask<Void, Void, List<Notification>> {
            @Override
            protected List<Notification> doInBackground(Void... voids) {
                return NotificationsDatabase
                        .getDatabase(getApplicationContext())
                        .NotificationDao().getAllNotification();
            }
            @Override
            protected void onPostExecute(List<Notification> notifications) {
                super.onPostExecute(notifications);
                notificationsList.addAll(notifications);
                notificationsAdapter.notifyDataSetChanged();
            }
        }
        new getNotificationsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_notification,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.delete:
                showDeleteNoteDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onNotificationDeleteClicked(Notification notification, int position) {
        index=position;
        deleteNotification();
    }

    private void showDeleteNoteDialog(){
        if(dialogDeleteNotifications==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_notification, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(view);
            dialogDeleteNotifications = builder.create();
            if(dialogDeleteNotifications.getWindow()!=null){
                dialogDeleteNotifications.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteAllNotificationTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotificationsDatabase
                                    .getDatabase(getApplicationContext())
                                    .NotificationDao().deleteAll();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            notificationsList.clear();
                            notificationsAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"Tất cả thông báo đã được xóa thành công!",Toast.LENGTH_SHORT).show();

                        }
                    }
                    new DeleteAllNotificationTask().execute();
                    dialogDeleteNotifications.dismiss();

                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogDeleteNotifications.dismiss();
                }
            });


        }
        dialogDeleteNotifications.show();
    }

}