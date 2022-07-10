package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes_app.R;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.database.NotificationsDatabase;
import com.example.notes_app.entities.DataSettingManager;
import com.example.notes_app.entities.Note;
import com.example.notes_app.entities.Notification;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import yuku.ambilwarna.AmbilWarnaDialog;

public class CreateNoteActivity extends AppCompatActivity {


    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View viewSubtitleIndicator;
    private String selectedNoteColor;
    private String selectImagePath;
    private ImageView imageNote;
    private String currentImagePath;
    private String tag="Nhật ký";
    int customColor=-1;
    boolean star;
    private ImageView imageBack, imageSave;
    private AlertDialog dialogDeleteNote;
    private Note alreadyAvailableNote;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE =2;
    private static final int REQUEST_CODE_SPEECH_INPUT = 3;
    Chip tag_edu, tag_now, tag_normal, tag_note, tag_travel, tag_schedule, tag_todo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
        }
        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImagePath="";
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
            }
        });

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a",
                        Locale.UK.getDefault())
                        .format(new Date())
        );

        imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });

        if(!DataSettingManager.getLightmode()){
            selectedNoteColor = "#3E3A50";
        }else{
            selectedNoteColor = "#262636";
        }

        selectImagePath = "";
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
        if(getIntent().getBooleanExtra("isCapture",false)){
                currentImagePath = getIntent().getStringExtra("imagePath");
                imageNote.setImageBitmap(BitmapFactory.decodeFile(currentImagePath));
                imageNote.setVisibility(View.VISIBLE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
        }


        if(getIntent().getBooleanExtra("isFromQuickActions",false)){
            String type=getIntent().getStringExtra("quickActionType");
            if(type!=null){
                if(type.equals("image")){
                    selectImagePath = getIntent().getStringExtra("imagePath");
                    imageNote.setImageBitmap(BitmapFactory.decodeFile(selectImagePath));
                    imageNote.setVisibility(View.VISIBLE);

                }
            }
        }

        initMiscellaneous();
        setSubtitleIndicatorColor();

    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId())
//        {
//            case R.id.notification:
//                showNotificationDialog();
//
//            case R.id.imageSave:
//                saveNote();
//                return true;
//            case R.id.voiceToText:
//                speak();
//            default:break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hi speak something");
         try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
         }catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
         }
    }
    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        star = alreadyAvailableNote.getPrioritize();
        tag=alreadyAvailableNote.getTag();
        if(alreadyAvailableNote.getImagePath()!=null && !alreadyAvailableNote.getImagePath().trim().isEmpty() ){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility((View.VISIBLE));
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectImagePath = alreadyAvailableNote.getImagePath();
        }
    }
    private void saveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Title can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Subtitle can't be empty",Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setPrioritize(star);
        note.setTag(tag);

        if(customColor!=-1){
            note.setColor(String.format("#%06X", (0xFFFFFF & customColor)));
        }else{
            note.setColor(selectedNoteColor);
        }


        note.setImagePath(selectImagePath);
        if(getIntent().getBooleanExtra("isCapture",false)){
            note.setImagePath(currentImagePath);
        }
        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId());
        }
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
                saveNotification();
                Intent intent = new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }

        }
        new SaveNoteTask().execute();


    };

    private void saveNotification(){
        Notification notification = new Notification();
        notification.setTitle(inputNoteTitle.getText().toString());
        notification.setSubtitle("Nhật ký này vừa được bạn cập nhật");
        notification.setDateTime(textDateTime.getText().toString());
        notification.setImage(R.drawable.ic_image);
        if(customColor!=-1){
            notification.setColor(String.format("#%06X", (0xFFFFFF & customColor)));
        }else{
            notification.setColor(selectedNoteColor);
        }
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

    private  void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        tag_note = layoutMiscellaneous.findViewById(R.id.tag_note);
        tag_edu = layoutMiscellaneous.findViewById(R.id.tag_edu);
        tag_now = layoutMiscellaneous.findViewById(R.id.tag_now);
        tag_normal = layoutMiscellaneous.findViewById(R.id.tag_normal);
        tag_todo = layoutMiscellaneous.findViewById(R.id.tag_todo);
        tag_schedule = layoutMiscellaneous.findViewById(R.id.tag_schedule);
        tag_travel = layoutMiscellaneous.findViewById(R.id.tag_travel);
        if(tag.equals("Nhật ký")){
            tag_note.setChecked(true);
        }else if(tag.equals("Khoảnh khắc")){
            tag_now.setChecked(true);
        }else if(tag.equals("Hằng ngày")){
            tag_normal.setChecked(true);
        }else if(tag.equals("Todo")){
            tag_todo.setChecked(true);
        }else if(tag.equals("Lịch")){
            tag_schedule.setChecked(true);
        }else if(tag.equals("Du lịch")){
            tag_travel.setChecked(true);
        }else{
            tag_edu.setChecked(true);
        }

        layoutMiscellaneous.findViewById(R.id.more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    findViewById(R.id.layoutNoteColor).setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                }else{
                    findViewById(R.id.layoutNoteColor).setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    findViewById(R.id.layout_tag).setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    findViewById(R.id.layout_tag).setVisibility(View.GONE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Nhật ký";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Todo";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Lịch";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Hằng ngày";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Khoảnh khắc";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_edu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Học tập";
            }
        });
        layoutMiscellaneous.findViewById(R.id.tag_travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag="Du lịch";
            }
        });
        final  ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final  ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final  ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final  ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final  ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);
        final  ImageView imageColor6 = layoutMiscellaneous.findViewById(R.id.imageColor6);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!DataSettingManager.getLightmode()){
                    selectedNoteColor = "#3E3A50";
                }else{
                    selectedNoteColor = "#262636";
                }
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#3A52FC";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                imageColor6.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                imageColor6.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#D53D7E";
                openColorPicker();
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor1();
            }
        });
        if(alreadyAvailableNote!=null && alreadyAvailableNote.getColor()!=null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52FC":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;


            }
        }
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                }else{
                    selectImage();
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(inputNoteText.getText());
                spannable.setSpan(new StyleSpan(Typeface.ITALIC),inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
                inputNoteText.setText(spannable);
            }
        });
        layoutMiscellaneous.findViewById(R.id.underline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(inputNoteText.getText());
                spannable.setSpan(new UnderlineSpan(),inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
                inputNoteText.setText(spannable);
            }
        });
        layoutMiscellaneous.findViewById(R.id.bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spannable spannable = new SpannableStringBuilder(inputNoteText.getText());
                spannable.setSpan(new StyleSpan(Typeface.BOLD),inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
                inputNoteText.setText(spannable);
            }
        });


        layoutMiscellaneous.findViewById(R.id.mic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });

    }

    public void openColorPicker(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, customColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                customColor = color;
            }

        });
        ambilWarnaDialog.show();
    }
    private void showDeleteNoteDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
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
                                NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void unused) {
                                super.onPostExecute(unused);
                                Intent intent = new Intent();
                                intent.putExtra("isNoteDeleted",true);
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }
                        new DeleteNoteTask().execute();
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
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
    private void setSubtitleIndicatorColor1(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(String.format("#%06X", (0xFFFFFF & customColor))));
//        findViewById(R.id.viewColor6).setBackground(customColor);

    }
    private  void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if(data!=null){
                Uri selectedImageUri = data.getData();
                if(selectedImageUri != null ){
                    try{
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectImagePath = getPathFromUri(selectedImageUri);
                    }catch (Exception e){
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        if(requestCode==REQUEST_CODE_SPEECH_INPUT){
            if(resultCode == RESULT_OK && data !=null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(inputNoteTitle.hasFocus()){
                    String currentTitle = inputNoteTitle.getText()+result.get(0);
                    inputNoteTitle.setText(currentTitle);
                }
                else if(inputNoteSubtitle.hasFocus()){
                    String currentSubtitle = inputNoteSubtitle.getText()+result.get(0);
                    inputNoteSubtitle.setText(currentSubtitle);
                }else if(inputNoteText.hasFocus()){
                    String currentText = inputNoteText.getText()+result.get(0);
                    inputNoteText.setText(currentText);
                }
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
}