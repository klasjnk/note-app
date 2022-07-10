package com.example.notes_app.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.example.notes_app.database.TrashsDatabase;

public class Trashservice extends JobService {
    public static final String TAG= NotificationService.class.getName();
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
                jobFinished(jobParameters,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        TrashsDatabase
                .getDatabase(getApplicationContext())
                .trashDao().deleteTrash();
        Log.d("TAG","Job stopped");
        jobCancelled =true;
        return true;
    }
}


