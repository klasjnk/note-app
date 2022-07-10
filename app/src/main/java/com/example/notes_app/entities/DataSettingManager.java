package com.example.notes_app.entities;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class DataSettingManager {
    private static final String MINUTE = "MINUTE";
    private static final String SECURITY_FINGERPRINT = "SECURITY_FINGERPRINT";
    private static final String USER_ID = "USER_ID";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_IMG = "USER_IMG";
    private static final String TOOLBAR_COLOR = "TOOLBAR_COLOR";
    private static final String STATUS_COLOR = "STATUS_COLOR";
    private static final String NAVIGATION_COLOR = "NAVIGATION_COLOR";
    private static final String STATUS_SORT = "STATUS_SORT";
    private static DataSettingManager instance;
    private static final String IS_FIRST_INSTALL="IS_FIRST_INSTALL";
    private static final String NOTIFICAIONS="NOTIFICAIONS";
    private static final String LIGHTMODE="LIGHTMODE";
    private static final String PRIVATEACCOUNT="PRIVATEACCOUNT";
    private static final String TIME_NOTIFICATION="TIME_NOTIFICATION";
    private Setting mSetting;
    public static void init(Context context){
        instance= new DataSettingManager();
        instance.mSetting=new Setting(context);
    }
    public static DataSettingManager getInstance(){
        if(instance==null){
            instance=new DataSettingManager();
        }
        return instance;
    }
    public static void setFirstInstalled(boolean isFirst){
        DataSettingManager.getInstance().mSetting.putBoolean(IS_FIRST_INSTALL,isFirst);
    }
    public static boolean getFirstInstalled(){
        return DataSettingManager.getInstance().mSetting.getBoolean(IS_FIRST_INSTALL);
    }
    //Notificaion
    public static void setNotificaions(boolean notificaions){
        DataSettingManager.getInstance().mSetting.putBoolean(NOTIFICAIONS,notificaions);
    }
    public static boolean getNotification(){
        return DataSettingManager.getInstance().mSetting.getBoolean(NOTIFICAIONS);

    }
    //Light mode
    public static void setLightmode(boolean lightmode){
        DataSettingManager.getInstance().mSetting.putBoolean(LIGHTMODE,lightmode);
    }
    public static boolean getLightmode(){
        return DataSettingManager.getInstance().mSetting.getBoolean(LIGHTMODE);
    }
    //private account
    public static void setPrivateaccount(boolean privateaccount){
        DataSettingManager.getInstance().mSetting.putBoolean(PRIVATEACCOUNT,privateaccount);
    }
    public static boolean getPrivateAccount(){
        return DataSettingManager.getInstance().mSetting.getBoolean(PRIVATEACCOUNT);
    }
    public static void setTimeNotification(String timeNotification){
        DataSettingManager.getInstance().mSetting.putString(TIME_NOTIFICATION,timeNotification);
    }
    public static String getTimeNotification(){
        return DataSettingManager.getInstance().mSetting.getString(TIME_NOTIFICATION);
    }
    public static void setTime(int minute){
        DataSettingManager.getInstance().mSetting.putInt(TIME_NOTIFICATION,minute);
    }
    public static int getTime(){
        return DataSettingManager.getInstance().mSetting.getInt(MINUTE);
    }

    public static void setProtected_fingerPrint(boolean fingerPrint){
        DataSettingManager.getInstance().mSetting.putBoolean(SECURITY_FINGERPRINT,fingerPrint);
    }
    public static boolean getProtected_fingerPrint(){
        return DataSettingManager.getInstance().mSetting.getBoolean(SECURITY_FINGERPRINT);
    }
    public static void setKey(boolean isFirst){
        DataSettingManager.getInstance().mSetting.putBoolean(SECURITY_FINGERPRINT,isFirst);
    }
    public static boolean getKey(){
        return DataSettingManager.getInstance().mSetting.getBoolean(SECURITY_FINGERPRINT);
    }
    public static void setColor(String color){
        DataSettingManager.getInstance().mSetting.putString(TOOLBAR_COLOR,color);
    }
    public static String getColor(){
        return DataSettingManager.getInstance().mSetting.getString(TOOLBAR_COLOR);
    }
    public static void setColorStatus(String color){
        DataSettingManager.getInstance().mSetting.putString(STATUS_COLOR,color);
    }
    public static String getColorStatus(){
        return DataSettingManager.getInstance().mSetting.getString(STATUS_COLOR);
    }
    public static void setColorNavigation(String color){
        DataSettingManager.getInstance().mSetting.putString(NAVIGATION_COLOR,color);
    }
    public static String getColorNavigation(){
        return DataSettingManager.getInstance().mSetting.getString(NAVIGATION_COLOR);
    }
    public static void setSort(String color){
        DataSettingManager.getInstance().mSetting.putString(STATUS_SORT,color);
    }
    public static String getSort(){
        return DataSettingManager.getInstance().mSetting.getString(STATUS_SORT);
    }

//    public static void setId(String id){
//        DataSettingManager.getInstance().mSetting.putString(USER_ID,id);
//    }
//
//    public static String getId(){
//        return DataSettingManager.getInstance().mSetting.getString(USER_ID);
//    }
//
//    public static void setImg(String img){
//        DataSettingManager.getInstance().mSetting.putString(USER_IMG,img);
//    }
//    public static String getImg(){
//        return DataSettingManager.getInstance().mSetting.getString(USER_IMG);
//    }
//
//    public static void setUserName(String name){
//        DataSettingManager.getInstance().mSetting.putString(USER_NAME,name);
//    }
//    public static String getUserName(){
//        return DataSettingManager.getInstance().mSetting.getString(USER_NAME);
//    }
//    public static void setEmail(String email){
//        DataSettingManager.getInstance().mSetting.putString(USER_EMAIL,email);
//    }
//    public static String getEmail(){
//        return DataSettingManager.getInstance().mSetting.getString(USER_EMAIL);
//    }



}
