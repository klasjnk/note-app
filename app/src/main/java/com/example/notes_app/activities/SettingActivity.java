package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.notes_app.R;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.entities.DataSettingManager;
import com.example.notes_app.entities.Note;
import com.example.notes_app.services.NotificationService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    RelativeLayout security_private_account,restore,back_up;
    private AlertDialog dialogDeleteNote,dialogBackup,dialogRestore,dialogBackupoverride;
    private static final int REQUEST_CODE = 69;
    ImageView avatar;
    LinearLayout profile_layout,logOut_layout,logingoogle_layout;
    TextView name,email;
    SwitchCompat notificationSwitch,nightModeSwitch;
    String time_picker,id;
    int time_notification;
    FirebaseDatabase mDatabase= FirebaseDatabase.getInstance();
    DatabaseReference mData,_myref,mData1;
    RelativeLayout toolbarColor;
    Toolbar toolbar;
    RelativeLayout logoutbtn;
    AppCompatButton profile;
    final boolean[] check = {false};
    final boolean[] checkrestore = {true};

    private static final int JOB_ID = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        checkBackup();
        init();

        if(!DataSettingManager.getColor().equals("")){
            toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
        }
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acct!=null){
            profile_layout.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            name.setText(acct.getDisplayName());
            email.setText(acct.getEmail());
            logingoogle_layout.setVisibility(View.GONE);
            logOut_layout.setVisibility(View.VISIBLE);
            id = acct.getId();
            Glide.with(getApplicationContext())
                    .load(acct.getPhotoUrl())
                    .into(avatar);

        }
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this,ProfileActivity.class));
            }
        });

        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmtDialog();
            }
        });
        if(!DataSettingManager.getFirstInstalled()){
            DataSettingManager.setFirstInstalled(true);
        }
        if(!DataSettingManager.getLightmode()){
            nightModeSwitch.setChecked(DataSettingManager.getLightmode());
            nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        DataSettingManager.setLightmode(b);
                        nightModeSwitch.setChecked(b);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        DataSettingManager.setLightmode(b);
                        nightModeSwitch.setChecked(b);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });

        }
        if(DataSettingManager.getLightmode()){
            nightModeSwitch.setChecked(DataSettingManager.getLightmode());
            nightModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        DataSettingManager.setLightmode(b);
                        nightModeSwitch.setChecked(b);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }else{
                        DataSettingManager.setLightmode(b);
                        nightModeSwitch.setChecked(b);
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                }
            });
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Cài đặt");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        notificationSwitch.setChecked(DataSettingManager.getNotification());
        time_picker=DataSettingManager.getTimeNotification();
        time_notification= (int) DataSettingManager.getTime();

        back_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id!=null){
                    showConfirmBackUptDialog();
                }else{
                    Toast.makeText(getApplicationContext(),"Bạn phải đăng nhập để thực hiện chức năng này !",Toast.LENGTH_SHORT).show();
                }

            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmRestoretDialog();
            }
        });

        logingoogle_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        security_private_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, Security_private_account.class);
                startActivity(intent);
            }
        });


        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                DataSettingManager.setNotificaions(b);
                if(b){
                    showNotificationDialog();


                }else{
                    onClickCancelScheduleJob();
                }
            }
        });
        toolbarColor = findViewById(R.id.toolbarColor);
        toolbarColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                startActivityForResult(new Intent(getApplicationContext(),ActivityColor.class),90);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_setting,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(){
        security_private_account= findViewById(R.id.security_private_account);
        notificationSwitch = findViewById(R.id.notificationSwitch);
        nightModeSwitch =  findViewById(R.id.nightModeSwitch);
        toolbar =  findViewById(R.id.toolbar);

        logingoogle_layout = findViewById(R.id.logingoogle_layout);
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profile_layout = findViewById(R.id.profile_layout);
        logOut_layout = findViewById(R.id.logOut_layout);
        logingoogle_layout = findViewById(R.id.logingoogle_layout);
        restore = findViewById(R.id.restore);
        back_up = findViewById(R.id.back_up);
        logoutbtn = findViewById(R.id.logout_btn);
        profile = findViewById(R.id.profile);
    }
    private void onClickStartScheduleJob(){
        ComponentName componentName = new ComponentName(this, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID,componentName)
                .setPersisted(true)
                .setPeriodic(30*60*1000)
                .build();
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void onClickCancelScheduleJob(){
        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(JOB_ID);
    }
    private void showNotificationDialog(){
        final Calendar calendar=Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener= new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hours=i;
                int minute=i1;
                calendar.set(Calendar.HOUR_OF_DAY,i);
                calendar.set(Calendar.MINUTE,i1);
                time_picker=hours+":"+minute;
                final Calendar c = Calendar.getInstance();
                int curr_hour = c.get(Calendar.HOUR_OF_DAY);
                int curr_minute = c.get(Calendar.MINUTE);
                int h=Math.abs(curr_hour-hours);
                int m=Math.abs(curr_minute-minute);
                if(m<15){
                    m=15;
                }
                time_notification= h*360000+m*60000;
                DataSettingManager.setTime(time_notification);
                DataSettingManager.setTimeNotification(String.valueOf(time_picker));
                onClickStartScheduleJob();
            }
        };
        new TimePickerDialog(SettingActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
            name.setText(acct.getDisplayName());
            email.setText(acct.getEmail());
            Glide.with(getApplicationContext())
                    .load(acct.getPhotoUrl())
                    .into(avatar);
            profile_layout.setVisibility(View.VISIBLE);
            name.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);

            logingoogle_layout.setVisibility(View.GONE);
            logOut_layout.setVisibility(View.VISIBLE);
            id = acct.getId();

            Toast.makeText(getApplicationContext(),"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
        }
        if(requestCode==90 && resultCode==RESULT_OK){
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
    private void showConfirmtDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_confirm_logout, findViewById(R.id.layoutConfirmlogoutDialog));
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow()!=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken("702917165235-81tte261sp3jeppp0rb4vldebqkls8ij.apps.googleusercontent.com")
                            .requestEmail()
                            .build();
                    FirebaseAuth.getInstance().signOut();
                    GoogleSignInClient googleSignInClient=GoogleSignIn.getClient(getApplicationContext(),gso);
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Đăng xuất thành công!",Toast.LENGTH_SHORT).show();
                            profile_layout.setVisibility(View.GONE);
                            logingoogle_layout.setVisibility(View.VISIBLE);
                            logOut_layout.setVisibility(View.GONE);
                        }
                    });
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
    private  void checkBackup(){
        mData=mDatabase.getReference();
        mData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap: snapshot.getChildren()) {
                    if(snap.getKey().equals(id)){
                        check[0] =true;

                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void showConfirmBackUptDialog(){
        if(check[0]){
            if(dialogBackupoverride==null){
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.layout_confirm_backup_override, findViewById(R.id.layoutConfirmbackupoverrideDialog));
                builder.setView(view);
                dialogBackupoverride = builder.create();
                if(dialogBackupoverride.getWindow()!=null){
                    dialogBackupoverride.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                view.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        @SuppressLint("StaticFieldLeak")
                        class BackupDiaryTask extends AsyncTask<Void,Void,Void>{
                            @Override
                            protected Void doInBackground(Void... voids) {
                                List<Note> notes_backup = NotesDatabase
                                        .getDatabase(getApplicationContext())
                                        .noteDao().getAllNotes();
                                _myref=mDatabase.getReference();
                                _myref.child(id).setValue(notes_backup);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void unused) {
                                super.onPostExecute(unused);
                                Toast.makeText(getApplicationContext(),"Sao lưu dữ liệu thành công !",Toast.LENGTH_SHORT).show();

                            }
                        }
                        new BackupDiaryTask().execute();
                        dialogBackupoverride.dismiss();

                    }
                });
                view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBackupoverride.dismiss();
                    }
                });


            }
            dialogBackupoverride.show();
        }else{
            if(dialogBackup==null){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(SettingActivity.this);
                View view1 = LayoutInflater.from(SettingActivity.this).inflate(R.layout.layout_confirm_backup, findViewById(R.id.layoutConfirmbackupDialog));
                builder1.setView(view1);
                dialogBackup = builder1.create();
                if(dialogBackup.getWindow()!=null){
                    dialogBackup.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                view1.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        @SuppressLint("StaticFieldLeak")
                        class BackupDiaryTask extends AsyncTask<Void,Void,Void>{
                            @Override
                            protected Void doInBackground(Void... voids) {
                                List<Note> notes_backup = NotesDatabase
                                        .getDatabase(getApplicationContext())
                                        .noteDao().getAllNotes();
                                _myref=mDatabase.getReference();
                                _myref.child(id).setValue(notes_backup);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void unused) {
                                super.onPostExecute(unused);
                                Toast.makeText(getApplicationContext(),"Sao lưu dữ liệu thành công !",Toast.LENGTH_SHORT).show();

                            }
                        }
                        new BackupDiaryTask().execute();
                        dialogBackup.dismiss();

                    }
                });
                view1.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBackup.dismiss();
                    }
                });

                dialogBackup.show();
            }
        }

    }
    private void showConfirmRestoretDialog(){
        if(dialogRestore==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_confirm_restore, findViewById(R.id.layoutConfirmrestoreDialog));
            builder.setView(view);
            dialogRestore = builder.create();
            if(dialogRestore.getWindow()!=null){
                dialogRestore.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.btnYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(id!=null){
                        mData1=mDatabase.getReference();
                        mData1.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap: snapshot.getChildren()) {
                                    getRestore(snap.getKey());
                                }
                                if(checkrestore[0]){
                                    Toast.makeText(getApplicationContext(),"Khôi phục dữ liệu thành công ",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Khôi phục dữ liệu thất bại ",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        dialogRestore.dismiss();
                    }else{
                        Toast.makeText(getApplicationContext(),"Bạn phải đăng nhập để thực hiện chức năng này !",Toast.LENGTH_SHORT).show();
                        dialogRestore.dismiss();
                    }

                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialogRestore.dismiss();
                }
            });


        }
        dialogRestore.show();
    }
    private void setRestore(Note note) {
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
            }
        }
        new SaveNoteTask().execute();
    }
    private void getRestore(String key){
        _myref=mDatabase.getReference();
        _myref.child(id).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Note n = snapshot.getValue(Note.class);
                if(n!=null){
                    setRestore(n);
                    Intent i = new Intent();
                    setResult(RESULT_OK);

                }else{

                    checkrestore[0]=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}