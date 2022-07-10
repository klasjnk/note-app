package com.example.notes_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.notes_app.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.makeramen.roundedimageview.RoundedImageView;

public class ProfileActivity extends AppCompatActivity {
    RoundedImageView img;
    TextView name,gmail;
    GoogleSignInAccount account;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        if(account!=null){
            name.setText(account.getDisplayName());
            gmail.setText(account.getEmail());
            Glide.with(getApplicationContext())
                    .load(account.getPhotoUrl())
                    .into(img);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void init(){
        img = findViewById(R.id.img);
        account = GoogleSignIn.getLastSignedInAccount(this);
        name = findViewById(R.id.name);
        gmail = findViewById(R.id.gmail);
        back = findViewById(R.id.back);

    }
}