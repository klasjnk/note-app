package com.example.notes_app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.notes_app.R;
import com.example.notes_app.applications.NotifyApplication;

import java.util.Date;

public class NotificationService extends JobService {
    public static final String TAG=NotificationService.class.getName();
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d("TAG","Job started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters jobParameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Notification notification = new NotificationCompat.Builder(getApplicationContext(), NotifyApplication.CHANNEL_ID)
                        .setContentTitle("Your diary ")
                        .setContentText("Hello ! Don't forget to take your dairy today  ")
                        .setSmallIcon(R.drawable.ic_notification)
                        .build();
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if(notificationManager!=null){
                    notificationManager.notify((int) new Date().getTime(),notification);
                }

                jobFinished(jobParameters,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d("TAG","Job stopped");
        jobCancelled =true;
        return true;
    }
}
