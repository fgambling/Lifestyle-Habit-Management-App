package com.comp90018.a2;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.comp90018.a2.calendar.CalendarFragment;
import com.comp90018.a2.diary.DiaryEntry;
import com.comp90018.a2.diary.DiaryListFragment;
import com.comp90018.a2.habits.HabitFragment;
import com.comp90018.a2.settings.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<DiaryEntry> diaryListAdapter;

    public static JobScheduler jobScheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_bar);


        // Set a listener for item click events in the BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item click here
                int itemId = item.getItemId();
                if (itemId == R.id.diary) {// Handle the "diary" menu item click
                    replaceFragment(new DiaryListFragment());
                    return true;
                } else if (itemId == R.id.calendar) {// Handle the "calendar" menu item click
                    replaceFragment(new CalendarFragment());
                    return true;
                } else if (itemId == R.id.habits) {// Handle the "habits" menu item click
                    replaceFragment(new HabitFragment());
                    return true;
                } else if (itemId == R.id.settings) {// Handle the "settings" menu item click
                    replaceFragment(new Settings());
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new DiaryListFragment())
                    .commit();
        }
        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        if (!avoidRepeatService())
            scheduleJob();


    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        // Check if the current fragment is the same as the new fragment
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            // The current fragment is the same as the new fragment, do nothing
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Optional: Add the transaction to the back stack
        transaction.commit();
    }

    public boolean avoidRepeatService() {
        for (JobInfo jobInfo : jobScheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == 1) {
                return true;
            }
        }
        return false;
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, MyJobService.class);
        JobInfo info = new JobInfo.Builder(1, componentName)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        int resultCode = jobScheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Service started!!!");
        } else {
            Log.d(TAG, "Service starting failed");
        }
    }


}