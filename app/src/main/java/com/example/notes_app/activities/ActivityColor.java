package com.example.notes_app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.notes_app.R;
import com.example.notes_app.entities.DataSettingManager;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ActivityColor extends AppCompatActivity {
    RelativeLayout toolbar_color, statusbar_color, navigation_color, reset;
    Toolbar toolbar;
    int customColor, customColor1, customColor2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);
        toolbar_color = findViewById(R.id.toolbar_color);
        statusbar_color = findViewById(R.id.statusBar_color);
        navigation_color = findViewById(R.id.navigation_color);
        reset = findViewById(R.id.reset);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Đổi màu");
        toolbar.setNavigationIcon(R.drawable.ic_back);
        if(!DataSettingManager.getColor().equals("")){
            toolbar.setBackgroundColor(Integer.parseInt(DataSettingManager.getColor()));
        }
        if(!DataSettingManager.getColorStatus().equals("")){
            getWindow().setStatusBarColor(Integer.parseInt(DataSettingManager.getColorStatus()));
        }
        if(!DataSettingManager.getColorNavigation().equals("")){
            getWindow().setNavigationBarColor(Integer.parseInt(DataSettingManager.getColorNavigation()));
        }
        toolbar_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();
            }
        });
        statusbar_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPickerStatus();
            }
        });
        navigation_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPickerNavigation();
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Đã reset giao diện thành công! Vui lòng khởi động lại ứng dụng",Toast.LENGTH_SHORT).show();
                DataSettingManager.setColor("");
                DataSettingManager.setColorStatus("");
                DataSettingManager.setColorNavigation("");

            }
        });
        setResult(RESULT_OK);
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
    public void openColorPicker(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, customColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if(color!=-1){
                    toolbar.setBackgroundColor(color);
                    DataSettingManager.setColor(String.valueOf(color));
                }
            }

        });
        ambilWarnaDialog.show();
    }
    public void openColorPickerStatus(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, customColor1, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if(color!=-1){

                    getWindow().setStatusBarColor(color);
                    DataSettingManager.setColorStatus(String.valueOf(color));
                }
            }

        });
        ambilWarnaDialog.show();
    }
    public void openColorPickerNavigation(){
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, customColor2, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                if(color!=-1){

                    getWindow().setNavigationBarColor(color);
                    DataSettingManager.setColorNavigation(String.valueOf(color));
                }
            }

        });
        ambilWarnaDialog.show();
    }
}