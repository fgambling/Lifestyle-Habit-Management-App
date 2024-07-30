package com.comp90018.a2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.comp90018.a2.habits.HabitCRUDService;
import com.comp90018.a2.habits.HabitEntriesService;
import com.comp90018.a2.habits.HabitEntry;
import com.comp90018.a2.login.LoginActivity;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";

    private static HabitEntriesService habitService;

    private boolean terminateJob = false;

    private List<HabitEntry> habits;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Start the service");
        habitService = new HabitEntriesService();
        habitService.loadEntries();
        myService(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "Terminate the service");
        terminateJob = true;
        return true;
    }

    private void myService(final JobParameters parameters) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!terminateJob) {
                    habitService.loadEntries();

                    Log.d("HabitAlarm", "start");
                    LocalDate currentDate = LocalDate.now();
                    LocalTime currentTime = LocalTime.now();
                    DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();
                    int dayIndex = currentDayOfWeek.getValue() % 7;

                    List<HabitEntry> habitEntries = habitService.getAllEntries();
                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
                    //Log.d("HabitService", habitEntries.toString());

                    for (HabitEntry entry : habitEntries) {
                        List<Boolean> daysOfWeek = entry.getAlarmDay();
                        String entryTimeString = ensureTwoDigitMinutes(entry.getAlarmTime());

                        LocalTime entryTime = LocalTime.parse(entryTimeString, timeFormatter);
                        //Log.d("HabitAlarm", entryTimeString);

                        // Check if the current day is set to true in the entry's day list
                        if (entry.isAlarmOn() &&
                                daysOfWeek.get(dayIndex) &&
                                !currentTime.isBefore(entryTime) &&
                                currentTime.isBefore(entryTime.plusSeconds(30))) { // 1 minute range to show notification

                            showNotification("It's time", entry.getTitle());
                        }
                    }

                    // Sleep before checking again
                    try {
                        Thread.sleep(25 * 1000); // Sleep for 50 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                jobFinished(parameters, false);
            }
        }).start();
    }

    public void showNotification(String title, String message) {
        Intent intent
                = new Intent(this, MainActivity.class);
        String channel_id = "notification_channel";

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Create a Builder object using NotificationCompat
        // class. This will allow control over all the flags
        NotificationCompat.Builder builder
                = new NotificationCompat
                .Builder(getApplicationContext(),
                channel_id)
                .setSmallIcon(R.drawable.baseline_hearing_24)
                .setAutoCancel(true)
                .setVibrate(new long[] { 1000, 1000, 1000,
                        1000, 1000 })
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.JELLY_BEAN) {
            builder = builder.setContentText(title).setContentTitle(message);
        }
        else {
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.baseline_hearing_24);
        }

        NotificationManager notificationManager
                = (NotificationManager)getSystemService(
                Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = new NotificationChannel(
                    channel_id, "web_app",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(
                    notificationChannel);
        }

        notificationManager.notify(0, builder.build());
    }

    private static String ensureTwoDigitMinutes(String time) {
        if (time == null) {
            throw new IllegalArgumentException("Time string cannot be null");
        }
        String[] parts = time.split(":");
        // Check if the minute part is a single digit
        if (parts.length == 2 && parts[1].length() == 1) {
            // Reformat the minute part to be two digits
            return parts[0] + ":0" + parts[1];
        }

        return time;
    }

}
