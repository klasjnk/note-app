package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.notes_app.R;
import com.example.notes_app.database.NotesDatabase;
import com.example.notes_app.entities.DataSettingManager;

public class Security_private_account extends AppCompatActivity {

    RelativeLayout security_fingerprint, passwordProtected,deletePassword;
    private AlertDialog dialogDeleteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_private_account);
        init();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Bảo mật và riêng tư");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        if(!DataSettingManager.getColor().equals("")){
            toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        deletePassword = findViewById(R.id.deletePassword);
        deletePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataSettingManager.getKey()){
                    Intent intent = new Intent(getApplicationContext(),ActivityFingerprint.class);
                    intent.putExtra("isDeleted",true);
                    startActivityForResult(intent,888);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Bạn chưa tạo khóa vân tay",Toast.LENGTH_SHORT).show();
                }
            }
        });
        security_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataSettingManager.getKey()){
                    Toast.makeText(getApplicationContext(),"Khóa vân tay đã tồn tại",Toast.LENGTH_SHORT).show();
                }else{
                    showConfirmtDialog();
                }

            }

        });



    }
    public void init(){

        security_fingerprint= findViewById(R.id.security_fingerprint);
        passwordProtected = findViewById(R.id.password_protected);
        deletePassword = findViewById(R.id.deletePassword);
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
    private void showConfirmtDialog(){
        if(dialogDeleteNote==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_security_fingerprint, findViewById(R.id.layoutConfirmDialog));
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow()!=null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textYes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DataSettingManager.setKey(true);
                    Toast.makeText(getApplicationContext(),"Đã tạo khóa vân tay",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==888 & resultCode==RESULT_OK){
            DataSettingManager.setKey(false);
            Toast.makeText(getApplicationContext(),"Đã xóa khóa vân tay thành công",Toast.LENGTH_SHORT).show();
        }
    }
}