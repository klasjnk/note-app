package com.example.notes_app.entities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

public class Setting {
    private static final String MY_SETTING = "MY_SETTING";
    private Context mContext;
    public Setting(Context context){
        this.mContext=context;
    }
    public void  putBoolean(String key , boolean b){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,b);
        editor.apply();
    }
    public boolean getBoolean(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }
    public void  putString(String key , String b){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,b);
        editor.apply();
    }
    public String getString(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,"");
    }

    public void  putInt(String key , int b){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key,b);
        editor.apply();
    }
    public int getInt(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key,0);
    }

    public void putKey(String key, boolean b){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,b);
        editor.apply();
    }
    public boolean getKey(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MY_SETTING,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }

}
