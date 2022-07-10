package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.notes_app.R;
import com.example.notes_app.adapters.TrashsAdapter;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.database.TrashsDatabase;
import com.example.notes_app.entities.DataSettingManager;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Trash;
import com.example.notes_app.listeners.TrashsListener;
import com.example.notes_app.services.Trashservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityTrash extends AppCompatActivity implements TrashsListener {
    private ArrayList<Trash> trashList;
    RecyclerView recyclerView;
    TrashsAdapter trashsAdapter;
    int index=-1;
    ImageView imgDelete;
    Trash tmpTrashForContextMenu;
    AlertDialog dialogDeleteTrashs;
    Toolbar toolbar;
    private static final int JOB_ID = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        toolbar= findViewById(R.id.toolbar);
        if(!DataSettingManager.getColor().equals("")){
            toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
        }
        trashList = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Thùng rác");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        recyclerView = findViewById(R.id.trashRecyclerview);
        trashsAdapter = new TrashsAdapter(trashList,this);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(trashsAdapter);
        getAllTrash();

    }

    public void insertTrash(Trash trash){
        Trash trash1 = new Trash();
        trash1.setTitle(trash.getTitle());
        trash1.setSubtitle(trash.getSubtitle());
        trash1.setNoteText(trash.getNoteText());
        trash1.setDateTime(trash.getDateTime());
        trash1.setPrioritize(trash.getPrioritize());
        trash1.setColor(trash.getColor());
        trash1.setImagePath(trash.getImagePath());
        trash1.setTag(trash.getTag());
        trash1.setId(trash.getId());
        trash1.setDateDelete(trash.getDateDelete());
        String current = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a",
                Locale.UK.getDefault())
                .format(new Date());
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a",Locale.UK.getDefault());
        try {
            Date dateDelete = simpleDateFormat.parse(trash.getDateDelete());
            Date dateCurrent = simpleDateFormat.parse(current);
            String s = DateUtils.getRelativeTimeSpanString(dateDelete.getTime(), dateCurrent.getTime(), DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            trash1.setDayDelete(s);

        } catch (ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }
        @SuppressLint("staticFieldLeak")
        class SaveTrashTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                TrashsDatabase.getDatabase(getApplicationContext()).trashDao().insertNote(trash1);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

            }

        }
        new SaveTrashTask().execute();
    }
    private void getAllTrash(){

        @SuppressLint("StaticFieldLeak")
        class getTrashTask extends AsyncTask<Void, Void, List<Trash>> {
            @Override
            protected List<Trash> doInBackground(Void... voids) {
                return TrashsDatabase
                        .getDatabase(getApplicationContext())
                        .trashDao().getAllTrash();
            }
            @Override
            protected void onPostExecute(List<Trash> trashes) {
                super.onPostExecute(trashes);
                trashList.clear();
                trashList.addAll(trashes);
                for (Trash trash: trashList){
                    insertTrash(trash);
                }
                trashsAdapter.notifyDataSetChanged();
            }
        }
        new getTrashTask().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_trash,menu);
        SearchManager searchManager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.mnu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                trashsAdapter.searchNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                trashsAdapter.searchNotes(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.delete:
                showDeleteTrashDialog();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onNoteClicked(Trash trash, int position) {

    }

    @Override
    public void onNoteLongClicked(Trash trash, int posittion) {
        tmpTrashForContextMenu = trash;
        registerForContextMenu(recyclerView);
        index=posittion;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_trash,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){

            case R.id.delete:
                deleteTrash();
                break;
            case R.id.restore:
                restoreTrash();
                break;

        }
        return super.onContextItemSelected(item);
    }

    private void deleteTrash() {
        @SuppressLint("StaticFieldLeak")
        class DeleteTrashTask extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                TrashsDatabase
                        .getDatabase(getApplicationContext())
                        .trashDao().deleteNote(trashList.get(index));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                trashList.remove(index);
                trashsAdapter.notifyItemRemoved(index);
                Toast.makeText(getApplicationContext(),"Nhật ký này đã được xóa thành công!",Toast.LENGTH_SHORT).show();

            }
        }
        new DeleteTrashTask().execute();
    }

    private void showDeleteTrashDialog(){
        if(dialogDeleteTrashs==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_notification, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(view);
            dialogDeleteTrashs = builder.create();
            if(dialogDeleteTrashs.getWindow()!=null){
                dialogDeleteTrashs.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteAllTrashTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            TrashsDatabase
                                    .getDatabase(getApplicationContext())
                                    .trashDao().deleteAll();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            trashList.clear();
                            trashsAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(),"Tất cả nhật ký rác đã được xóa thành công!",Toast.LENGTH_SHORT).show();

                        }
                    }
                    new DeleteAllTrashTask().execute();
                    dialogDeleteTrashs.dismiss();

                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDeleteTrashs.dismiss();
                }
            });


        }
        dialogDeleteTrashs.show();
    }
    private void restoreTrash(){
        Note note = new Note();
        note.setTitle(trashList.get(index).getTitle());
        note.setSubtitle(trashList.get(index).getSubtitle());
        note.setNoteText(trashList.get(index).getNoteText());
        note.setDateTime(trashList.get(index).getDateTime());
        note.setPrioritize(trashList.get(index).getPrioritize());
        note.setColor(trashList.get(index).getColor());
        note.setImagePath(trashList.get(index).getImagePath());
        note.setTag(trashList.get(index).getTag());
        @SuppressLint("staticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                TrashsDatabase
                        .getDatabase(getApplicationContext())
                        .trashDao().deleteNote(trashList.get(index));
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                setResult(RESULT_OK);
                finish();
            }

        }
        new SaveNoteTask().execute();
    }
    @Override
    public void onStop () {
        onClickStartScheduleJob();
        super.onStop();
    }
    private void onClickStartScheduleJob(){
        ComponentName componentName = new ComponentName(this, Trashservice.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID,componentName)
                .setPersisted(true)
                .setPeriodic(15*60*1000)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }


}