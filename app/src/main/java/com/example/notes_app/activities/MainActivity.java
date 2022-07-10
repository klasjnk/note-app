package com.example.notes_app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.notes_app.BuildConfig;
import com.example.notes_app.R;
import com.example.notes_app.adapters.NotesAdapter;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.database.NotificationsDatabase;
import com.example.notes_app.database.TrashsDatabase;
import com.example.notes_app.entities.DataSettingManager;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Notification;
import com.example.notes_app.entities.Trash;
import com.example.notes_app.listeners.NotesListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NotesListener {
    private static  final int REQUEST_CODE_ADD_NOTE = 1;
    private static  final int REQUEST_CODE_UPDATE_NOTE = 2;
    private static final int REQUEST_CODE_SHOW_NOTES =3;
    private static final int REQUEST_CODE_SELECT_IMAGE = 4;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=5;
    private static final int REQUEST_CODE_CAPTURE_IMAGE=7;
    private static final int REQUEST_CODE_CHOOSE_DIRECTORY_SAVE_FILE=8;
    private static final int REQUEST_SETTING_RESTORE=100;
    private static final int RC_SIGN_IN = 123;
    private static final int RC_FINGER = 124;
    private static  final int REQUEST_CODE_PERMISSION = 6;
    private static final String TAG="GOOGlE_SIGN_IN_TAG";
    private Chip title,older,recently;
    private Chip tag_edu, tag_now, tag_normal, tag_travel, tag_schedule, tag_todo;
    private RecyclerView notesRecyclerView;
    private ImageView imageAddNoteMain;
    private String sort="recently";
    private List<Note> noteList;
    private List<Note> noteSource;
    private FloatingActionButton floatingActionButton;
    private NotesAdapter notesAdapter;
    private int noteClickedPosition =-1;
    private AlertDialog dialogDeleteNote;
    private Boolean isDeleted = false;
    private Animation rotateOpen;
    private Animation roteClose;
    private Animation fromBottom;
    private Animation toBottom;
    private FloatingActionButton add_btn;
    private FloatingActionButton setting_btn;
    private FloatingActionButton image_btn;
    private FloatingActionButton capture;
    private FloatingActionButton image_profile;
    private boolean clicked = false;
    private Note tmpNoteForContextMenu;
    private Uri uri;
    private FirebaseUser currentUser;
    private String currentImagePath;
    public int state=1;
    int tmp;
    private Toolbar toolbar;
    private TextView empty;
    private Button addNew;
    private ImageView icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        if(!DataSettingManager.getLightmode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if(DataSettingManager.getKey()){
            startActivityForResult(new Intent(MainActivity.this,ActivityFingerprint.class),RC_FINGER);
        }
        empty = findViewById(R.id.empty);
        addNew = findViewById(R.id.add_new);
        icon = findViewById(R.id.icon);


        imageAddNoteMain = findViewById(R.id.imageAddNoteMain);

        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
            }
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);
        sort=DataSettingManager.getSort();
        title = findViewById(R.id.title);
        recently = findViewById(R.id.recently);
        older = findViewById(R.id.older);
        tag_edu = findViewById(R.id.tag_edu);
        tag_now = findViewById(R.id.tag_now);
        tag_normal = findViewById(R.id.tag_normal);
        tag_todo = findViewById(R.id.tag_todo);
        tag_schedule = findViewById(R.id.tag_schedule);
        tag_travel = findViewById(R.id.tag_travel);
        if(sort.equals("title")){
            title.setChecked(true);
        }else if(sort.equals("older")){
            older.setChecked(true);
        }else if(sort.equals("recently")){
            recently.setChecked(true);
        }else if(sort.equals("Khoảnh khắc")){
            tag_now.setChecked(true);
        }else if(sort.equals("Học tập")){
            tag_edu.setChecked(true);
        }else if(sort.equals("Todo")){
            tag_todo.setChecked(true);
        }else if(sort.equals("Hằng ngày")){
            tag_normal.setChecked(true);
        }else if(sort.equals("Lịch")){
            tag_schedule.setChecked(true);
        }else if(sort.equals("Du lịch")){
            tag_travel.setChecked(true);
        }

        getNotes(REQUEST_CODE_SHOW_NOTES, false);


        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("NHẬT KÝ");
        actionBar.setDisplayUseLogoEnabled(true);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_menu));

//      SET ANIMATION FOR FLOATING ACTION BUTTON
        noteSource = noteList;
        rotateOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open_anim);
        roteClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_bottom_anim);
        setting_btn = findViewById(R.id.imageSetting);
        add_btn = findViewById(R.id.FloatingMenu);
        image_btn = findViewById(R.id.imageAddNoteMain);
        image_profile = findViewById(R.id.imageProfile);
        capture = findViewById(R.id.capture);



        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(
                                getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            }, REQUEST_CODE_PERMISSION
                    );


                }else{
                    dispatchCaptureImageIntent();
                }
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddButtonClicked();

            }

        });
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,SettingActivity.class),REQUEST_SETTING_RESTORE);
            }

        });

//        SHOW BOTTOM SHEET DIALOG PROFILE
        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this,R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.layout_bottom_sheet,
                                (LinearLayout) findViewById(R.id.bottomSheetContainer)
                        );
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();


            }
        });



//        SET EVENT SCROLL FOR FLOATING ACTION BUTTON
        floatingActionButton = findViewById(R.id.imageAddNoteMain);
        notesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    add_btn.hide();
                    if(clicked){
                        setting_btn.hide();
                        image_btn.hide();
                        image_profile.hide();
                        capture.hide();
                    }

                }else{
                    add_btn.show();
                    if(clicked){
                        setting_btn.show();
                        image_btn.show();
                        image_profile.show();
                        capture.show();
                    }

                }
            }
        });

        findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "title";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.older).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "older";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.recently).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "recently";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Du lịch";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_edu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Học tập";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Lịch";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Todo";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Khoảnh khắc";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });
        findViewById(R.id.tag_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort = "Hằng ngày";
                DataSettingManager.setSort(sort);
                noteList.clear();
                getNotes(REQUEST_CODE_SHOW_NOTES,false);
            }
        });



    }

    private void setStartNote(int position) {
        Note note = new Note();
        note.setId(noteList.get(position).getId());
        note.setTitle(noteList.get(position).getTitle());
        note.setSubtitle(noteList.get(position).getSubtitle());
        note.setNoteText(noteList.get(position).getNoteText());
        note.setImagePath(noteList.get(position).getImagePath());
        note.setColor(noteList.get(position).getColor());
        note.setDateTime(noteList.get(position).getDateTime());
        note.setPrioritize(noteList.get(position).getPrioritize());
        note.setTag(noteList.get(position).getTag());
        @SuppressLint("staticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                saveStarNotification(noteList.get(position).getPrioritize());
                notesAdapter.notifyDataSetChanged();
            }

        }
        new SaveNoteTask().execute();
    }

    // convert image or view to bitmap
    public static Bitmap getBitmapFromView(View view){
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable drawable = view.getBackground();
        if(drawable!=null){
            drawable.draw(canvas);
        }else{
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return  bitmap;

    }

    private void dispatchCaptureImageIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            File imageFile = null;
            try {
                imageFile = createImageFile();

            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
            if(imageFile!=null){
                Uri imageUri = FileProvider.getUriForFile(this,"com.example.notes_app.fileprovider",imageFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,REQUEST_CODE_CAPTURE_IMAGE);
            }
        }
    }
    private File createImageFile() throws IOException{
        String filename = "IMAGE_"+ new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault()).format(new Date());
        File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                filename,".jpg",directory
        );
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;

    }



//    CREATE OPTION MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        SearchManager searchManager = (SearchManager) getSystemService(getApplicationContext().SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.mnu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                notesAdapter.searchNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                notesAdapter.searchNotes(newText);
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

// CREATE MENU FOR EACH ITEM OF RECYCLERVIEW
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.mnuEdit:
                onNoteClicked(tmpNoteForContextMenu, noteClickedPosition);
                break;
            case R.id.mnuDelete:
                showDeleteNoteDialog();
                break;
            case R.id.mnuShare:
                shareAnotherapp(noteList.get(noteClickedPosition));
                break;
            case R.id.mnuPdf:
                exportPdf_file_share(tmpNoteForContextMenu, noteClickedPosition);
                break;
            case R.id.mnuExport_pdf:
                saveFile(noteList.get(noteClickedPosition).getTitle());

        }
        return super.onContextItemSelected(item);
    }

    //    ANIMATION FLOATING BUTTON
    private void onAddButtonClicked() {
        setVisiblility(clicked);
        setAnimation(clicked);
        setClickablily(clicked);
        if(!clicked){
            clicked=true;
        }else{
            clicked=false;
        }
    }
    private void setAnimation(Boolean clicked) {
        if(!clicked){
            image_profile.startAnimation(fromBottom);
            setting_btn.startAnimation(fromBottom);
            image_btn.startAnimation(fromBottom);
            capture.startAnimation(fromBottom);
            add_btn.startAnimation(rotateOpen);

        }else{
            image_profile.startAnimation(toBottom);
            image_btn.startAnimation(toBottom);
            setting_btn.startAnimation(toBottom);
            capture.startAnimation(toBottom);
            add_btn.startAnimation(roteClose);
        }
    }
    private void setVisiblility(Boolean clicked) {
        if(!clicked){
            image_profile.setVisibility(View.VISIBLE);
            setting_btn.setVisibility(View.VISIBLE);
            image_btn.setVisibility(View.VISIBLE);
            capture.setVisibility(View.VISIBLE);
        }else{
            image_profile.setVisibility(View.INVISIBLE);
            setting_btn.setVisibility(View.INVISIBLE);
            image_btn.setVisibility(View.INVISIBLE);
            capture.setVisibility(View.INVISIBLE);
        }
    }
    private void setClickablily(Boolean clicked){
        if(!clicked){
            image_profile.setClickable(true);
            setting_btn.setClickable(true);
            image_btn.setClickable(true);
            capture.setClickable(true);
        }else {
            image_profile.setClickable(false);
            setting_btn.setClickable(false);
            image_btn.setClickable(false);
            capture.setClickable(false);
        }
    }

//    CREATE DIALOG DELETE
    private void showDeleteNoteDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_delete_note, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow()!=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void,Void,Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(noteList.get(noteClickedPosition));
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            saveNotification();
                            saveTrash();
                            noteList.remove(noteClickedPosition);
                            isDeleted = true;
                            notesAdapter.notifyItemRemoved(noteClickedPosition);


                        }
                    }
                    new DeleteNoteTask().execute();
                    dialogDeleteNote.dismiss();

                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogDeleteNote.dismiss();
                }
            });


        }
        dialogDeleteNote.show();
    }
    private void saveStarNotification(boolean star){
        Notification notification = new Notification();
        notification.setTitle(noteList.get(tmp).getTitle());
        if(star){
            notification.setSubtitle("Nhật ký đã được gắn dấu sao");
            notification.setImage(R.drawable.ic_star_on);
        }else{
            notification.setSubtitle("Nhật ký đã được xóa dấu sao");
            notification.setImage(R.drawable.ic_star_off);
        }
        notification.setDateTime(noteList.get(tmp).getDateTime());

        notification.setColor(noteList.get(tmp).getColor());

        @SuppressLint("StaticFieldLeak")
        class saveStarNotificationTask extends AsyncTask<Void, Void,  Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotificationsDatabase
                        .getDatabase(getApplicationContext())
                        .NotificationDao().insertNotification(notification);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

            }
        }
        new saveStarNotificationTask().execute();
    }
    private void saveNotification(){
        Notification notification = new Notification();
        notification.setTitle(noteList.get(noteClickedPosition).getTitle());
        notification.setSubtitle("Nhật ký này vừa được bạn xóa");
        notification.setDateTime(noteList.get(noteClickedPosition).getDateTime());
        notification.setImage(R.drawable.ic_delete_recently);
        notification.setColor(noteList.get(noteClickedPosition).getColor());

        @SuppressLint("StaticFieldLeak")
        class saveNotificationTask extends AsyncTask<Void, Void,  Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                NotificationsDatabase
                        .getDatabase(getApplicationContext())
                        .NotificationDao().insertNotification(notification);
                return null;
            }
            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

            }
        }
        new saveNotificationTask().execute();
    }

//    PICK IMAGE IN STORAGE
    private  void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

//    PERMISSION
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==REQUEST_CODE_PERMISSION && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                dispatchCaptureImageIntent();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,null,null,null,null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

//    EVENT LONG CLICK NOTE
    @Override
    public void onNoteLongClicked(Note note, int posittion) {
        noteClickedPosition = posittion;
        tmpNoteForContextMenu = note;
        registerForContextMenu(notesRecyclerView);

    }

    @Override
    public void onNoteClickedStar(Note note, int posittion) {
        noteClickedPosition = posittion;
        tmp=posittion;
        setStartNote(tmp);

    }


    //    SHOW NOTE FROM ROOM DATABASE
    private void getNotes(final int requestCode, final boolean isNoteDeleted  ){

        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                if(sort.equals("title")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteSortByName();
                }else if(sort.equals("older")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteOldest();
                }else if(sort.equals("Khoảnh khắc")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteByNow();
                }else if(sort.equals("Học tập")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteByEdu();
                }else if(sort.equals("Todo")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteByTodo();
                }else if(sort.equals("Hằng ngày")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteByNormal();
                }else if(sort.equals("Lịch")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteBySchedule();
                }else if(sort.equals("Du lịch")){
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNoteByTravel();
                }
                return NotesDatabase
                        .getDatabase(getApplicationContext())
                        .noteDao().getAllNotes();
            }
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(requestCode==REQUEST_CODE_SHOW_NOTES){
                    noteList.clear();
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else if(requestCode==REQUEST_CODE_ADD_NOTE){
                    noteList.clear();
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                    notesRecyclerView.smoothScrollToPosition(0);
                }else if(requestCode==REQUEST_CODE_UPDATE_NOTE){
                    noteList.remove(noteClickedPosition);

                    if (isNoteDeleted){
                        notesAdapter.notifyItemRemoved(noteClickedPosition);
                    }else{
                        noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
                if(noteList.size()==0){
                    empty.setVisibility(View.VISIBLE);
                    addNew.setVisibility(View.VISIBLE);
                    icon.setVisibility(View.VISIBLE);
                }else{
                    empty.setVisibility(View.GONE);
                    addNew.setVisibility(View.GONE);
                    icon.setVisibility(View.GONE);
                }

            }
        }
        new GetNotesTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                Intent intent = new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent);
                return true;
            case R.id.trash:
                Intent intent1 = new Intent(MainActivity.this,ActivityTrash.class);
                startActivityForResult(intent1,999);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //    RESULT OF INTENT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            notesAdapter.notifyDataSetChanged();
            getNotes(REQUEST_CODE_ADD_NOTE,false);
            notesAdapter.notifyDataSetChanged();
        }
        else if(requestCode ==REQUEST_CODE_UPDATE_NOTE && resultCode ==RESULT_OK){
            if(data !=null){
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));

                notesAdapter.notifyDataSetChanged();
            }
        }else if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK){
            if(data!=null){
                Uri selectedImageUrl = data.getData();
                if(selectedImageUrl !=null){
                    try{
                        String selectedImagePath = getPathFromUri(selectedImageUrl);
                        Intent intent =new Intent(getApplicationContext(),CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions",true);
                        intent.putExtra("quickActionType","image");
                        intent.putExtra("imagePath",selectedImagePath);
                        startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
                    }catch (Exception e){
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if(requestCode==124 & resultCode==RESULT_CANCELED){
            finish();
        }
        if(requestCode==125 & resultCode==RESULT_CANCELED){
            finish();
        }
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                if(currentUser!=null){
                    FirebaseAuth.getInstance().signOut();
                }


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
        if(requestCode==REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK){
            try {
                Intent intent = new Intent(MainActivity.this,CreateNoteActivity.class);
                intent.putExtra("imagePath",currentImagePath);
                intent.putExtra("isCapture",true);
                startActivityForResult(intent,REQUEST_CODE_ADD_NOTE);
            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==REQUEST_CODE_CHOOSE_DIRECTORY_SAVE_FILE){
            if(resultCode == RESULT_OK){
                uri = data.getData();
                try{
                    exportPdf_file(tmpNoteForContextMenu, noteClickedPosition,uri);
                    Toast.makeText(getApplicationContext(),"This file save as "+uri,Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Cannot save file because "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode==999 && resultCode==RESULT_OK){
            getNotes(REQUEST_CODE_SHOW_NOTES,false);
            notesAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),"Đã khôi phục nhật ký thành công! ",Toast.LENGTH_SHORT).show();
        }
        if(requestCode == REQUEST_SETTING_RESTORE && resultCode==RESULT_OK){
            getNotes(REQUEST_CODE_SHOW_NOTES,false);
            notesAdapter.notifyDataSetChanged();
            if(!DataSettingManager.getColor().equals("")){
                toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
            }
            if(!DataSettingManager.getColorStatus().equals("")){
                getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
            }
            if(!DataSettingManager.getColorNavigation().equals("")){
                getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
            }
        }
    }
    public void  exportPdf_file(Note note, int position,Uri uri){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},PackageManager.PERMISSION_GRANTED);
        noteClickedPosition = position;
        int pageWith= 1200;
        int pageHeight=2010;
        String title= note.getTitle();
        String subtitle=note.getSubtitle();
        String content= note.getNoteText();
//        Toast.makeText(this,note.getImagePath(),Toast.LENGTH_SHORT).show();
       if(!note.getImagePath().toString().equals("")){
           Bitmap img = BitmapFactory.decodeFile(note.getImagePath());
           Bitmap scaleimg= Bitmap.createScaledBitmap(img,1200,518,false);
           PdfDocument pdfDocument= new PdfDocument();

           Paint paint = new Paint();
           Paint titlePaint = new Paint();
           Paint contentPaint= new Paint();


           PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
           PdfDocument.Page page= pdfDocument.startPage(pageInfo);
           Canvas canvas = page.getCanvas();
           canvas.drawBitmap(Bitmap.createScaledBitmap(img,1200,518,false),0,0,paint);

           titlePaint.setTextAlign(Paint.Align.CENTER);
           titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
           titlePaint.setTextSize(70);
           canvas.drawText(note.getTitle(),pageWith/2,600,titlePaint);

           contentPaint.setTextSize(30);
           contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
           contentPaint.setTextAlign(Paint.Align.LEFT);

           canvas.drawText(note.getNoteText(),30, 650,contentPaint);
           pdfDocument.finishPage(page);
           try {
               OutputStream outputStream = getContentResolver().openOutputStream(uri);
               pdfDocument.writeTo(outputStream);

               outputStream.close();
           } catch (IOException e) {
               e.printStackTrace();

           }
           pdfDocument.close();

       }else{
           PdfDocument pdfDocument= new PdfDocument();
           Paint titlePaint = new Paint();
           Paint contentPaint= new Paint();

           PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200,2010,1).create();
           PdfDocument.Page page= pdfDocument.startPage(pageInfo);
           Canvas canvas = page.getCanvas();

           titlePaint.setTextAlign(Paint.Align.CENTER);
           titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
           titlePaint.setTextSize(70);
           canvas.drawText(note.getTitle(),pageWith/2,50,titlePaint);
           contentPaint.setTextSize(30);
           contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.NORMAL));
           contentPaint.setTextAlign(Paint.Align.LEFT);

           canvas.drawText(note.getNoteText(),30, 90,contentPaint);
           pdfDocument.finishPage(page);
           try {
               OutputStream outputStream = getContentResolver().openOutputStream(uri);
               pdfDocument.writeTo(outputStream);

               outputStream.close();
           } catch (IOException e) {
               e.printStackTrace();

           }
           pdfDocument.close();

       }


    }
    private void saveFile(String title){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, title+".pdf");
        startActivityForResult(intent,REQUEST_CODE_CHOOSE_DIRECTORY_SAVE_FILE);
    }


    private void shareAnotherapp(Note note) {
        if(!note.getImagePath().equals("")){
            Bitmap img = BitmapFactory.decodeFile(note.getImagePath());
            try{
                File file = new File(getApplicationContext().getExternalCacheDir(),File.separator+note.getTitle()+".jpg");
                FileOutputStream fOut = new FileOutputStream(file);
                img.compress(Bitmap.CompressFormat.JPEG,100,fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true,false);
                final Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,note.toString());
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID+".fileprovider",file);
                intent.putExtra(Intent.EXTRA_STREAM,photoURI);
                intent.setType("image/jpg");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent,"Share image via"));

            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, note.toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, "Share text via DiaryApp");
            startActivity(shareIntent);
        }




    }
    public void shareAnotherapp_file(File file){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        Uri fileUri = FileProvider.getUriForFile(getApplicationContext(),
                BuildConfig.APPLICATION_ID+".fileprovider", file);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        startActivity(Intent.createChooser(shareIntent, "Share it"));
    }
    public void  exportPdf_file_share(Note note, int position) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        noteClickedPosition = position;
        int pageWith = 1200;
        int pageHeight = 2010;
        String filename= UUID.randomUUID().toString();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename + ".pdf");
        String title = note.getTitle();
        String subtitle = note.getSubtitle();
        String content = note.getNoteText();

//        Toast.makeText(this,note.getImagePath(),Toast.LENGTH_SHORT).show();
        if (!note.getImagePath().toString().equals("")) {
            Bitmap img = BitmapFactory.decodeFile(note.getImagePath());
            Bitmap scaleimg = Bitmap.createScaledBitmap(img, 1200, 518, false);
            PdfDocument pdfDocument = new PdfDocument();

            Paint paint = new Paint();
            Paint titlePaint = new Paint();
            Paint contentPaint = new Paint();


            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(Bitmap.createScaledBitmap(img, 1200, 518, false), 0, 0, paint);

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText(note.getTitle(), pageWith / 2, 600, titlePaint);

            contentPaint.setTextSize(30);
            contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            contentPaint.setTextAlign(Paint.Align.LEFT);

            canvas.drawText(note.getNoteText(), 30, 650, contentPaint);
            pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();

            }
            pdfDocument.close();

        } else {
            PdfDocument pdfDocument = new PdfDocument();
            Paint titlePaint = new Paint();
            Paint contentPaint = new Paint();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            titlePaint.setTextSize(70);
            canvas.drawText(note.getTitle(), pageWith / 2, 50, titlePaint);
            contentPaint.setTextSize(30);
            contentPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            contentPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(note.getNoteText(), 30, 90, contentPaint);
            pdfDocument.finishPage(page);
            try {
                pdfDocument.writeTo(new FileOutputStream(file));
                Toast.makeText(this, file.toString(), Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();

            }
            pdfDocument.close();

        }
        shareAnotherapp_file(file);
    }
    private void saveTrash(){
        String dateDelete = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a",
                Locale.UK.getDefault())
                .format(new Date());

        Trash trash = new Trash();
        trash.setTitle(noteList.get(noteClickedPosition).getTitle());
        trash.setSubtitle(noteList.get(noteClickedPosition).getSubtitle());
        trash.setNoteText(noteList.get(noteClickedPosition).getNoteText());
        trash.setDateTime(noteList.get(noteClickedPosition).getDateTime());
        trash.setPrioritize(noteList.get(noteClickedPosition).getPrioritize());
        trash.setColor(noteList.get(noteClickedPosition).getColor());
        trash.setImagePath(noteList.get(noteClickedPosition).getImagePath());
        trash.setTag(noteList.get(noteClickedPosition).getTag());
        trash.setDateDelete(dateDelete);
        trash.setDayDelete("0 phút trước");

        @SuppressLint("staticFieldLeak")
        class saveTrashTask extends AsyncTask<Void,Void,Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                TrashsDatabase.getDatabase(getApplicationContext()).trashDao().insertNote(trash);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);

            }

        }
        new saveTrashTask().execute();


    };




}