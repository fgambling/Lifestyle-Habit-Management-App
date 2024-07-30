package com.comp90018.a2.habits;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.a2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateHabit extends AppCompatActivity {

    private TimePicker timePicker;
    private CheckBox checkBoxMonday;
    private CheckBox checkBoxTuesday;
    private CheckBox checkBoxWednesday;
    private CheckBox checkBoxThursday;
    private CheckBox checkBoxFriday;
    private CheckBox checkBoxSaturday;
    private CheckBox checkBoxSunday;
    private List<Boolean> daysChecked = new ArrayList<>(7); // For 7 days of the week
    private Switch alarmToggle;
    private HabitEntry entry;
    private boolean alarmOn;
    private int alarmHour;
    private int alarmMinute;
    private String title;
    private String startDate;
    private String endDate;

    HabitEntriesService entriesService;
    String habitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_habit);

        // Initialize your UI elements
        timePicker = findViewById(R.id.timePicker);
        checkBoxMonday = findViewById(R.id.checkBoxMonday);
        checkBoxTuesday = findViewById(R.id.checkBoxTuesday);
        checkBoxWednesday = findViewById(R.id.checkBoxWednesday);
        checkBoxThursday = findViewById(R.id.checkBoxThursday);
        checkBoxFriday = findViewById(R.id.checkBoxFriday);
        checkBoxSaturday = findViewById(R.id.checkBoxSaturday);
        checkBoxSunday = findViewById(R.id.checkBoxSunday);

        TextView titleTextView = findViewById(R.id.textView);
        TextView startDateTextView = findViewById(R.id.startDateTextView);
        TextView endDateTextView = findViewById(R.id.endDateTextView);
        ImageView backButton = findViewById(R.id.back);
        Button deleteHabitButton = findViewById(R.id.deleteHabitButton);
        ImageView confirmTimeButton = findViewById(R.id.confirmTimeButton);
        alarmToggle = findViewById(R.id.alarmToggle);

        entriesService = HabitEntriesService.getInstance();

        habitId = getIntent().getStringExtra("habitId");

        entry = entriesService.getEntryById(habitId);
        Log.d("HabitReceive", "habit success" + habitId);
        alarmOn = entry.isAlarmOn();
        alarmToggle.setChecked(alarmOn);
        daysChecked = entry.getAlarmDay();
        //Log.d("Habit test", "habit" + entry.getAlarmTime());
        String[] parts = entry.getAlarmTime().split(":");
        alarmHour = Integer.parseInt(parts[0]);
        alarmMinute = Integer.parseInt(parts[1]);

        checkBoxSunday.setChecked(daysChecked.get(0));
        checkBoxMonday.setChecked(daysChecked.get(1));
        checkBoxTuesday.setChecked(daysChecked.get(2));
        checkBoxWednesday.setChecked(daysChecked.get(3));
        checkBoxThursday.setChecked(daysChecked.get(4));
        checkBoxFriday.setChecked(daysChecked.get(5));
        checkBoxSaturday.setChecked(daysChecked.get(6));


        title = getIntent().getStringExtra("title");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date eDate = sdf.parse(endDate);
            Date currentDate = new Date();

            if (eDate.before(currentDate)) {
                alarmToggle.setChecked(false);
                alarmToggle.setEnabled(false);
            } else {
                alarmToggle.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            alarmToggle.setEnabled(false);
        }

        // Set data to views
        titleTextView.setText(title);
        startDateTextView.setText("Start date: " + startDate);
        if (endDate.equals("9999/12/31")) {
            endDateTextView.setText("End date: No End Date");
        } else {
            endDateTextView.setText("End date: " + endDate);
        }

        timePicker.setIs24HourView(true);
        timePicker.setHour(alarmHour);
        timePicker.setMinute(alarmMinute);

        confirmTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmHour = timePicker.getHour();
                alarmMinute = timePicker.getMinute();
                alarmOn = alarmToggle.isChecked();
                Log.i("TimePicker", "Time selected: " + alarmHour + ":" + alarmMinute);
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("AlarmTime", String.format("%02d:%02d", alarmHour, alarmMinute));
                updateData.put("AlarmDay", daysChecked);
                updateData.put("Alarm", alarmOn);
                entriesService.updateEntry(habitId, updateData);
                Toast.makeText(UpdateHabit.this, "Time and Alarm status have been updated.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        deleteHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHabit();
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("AlarmToggle", "Alarm is " + (isChecked ? "ON" : "OFF"));
            }
        });

        alarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!alarmToggle.isEnabled()) {
                    Toast.makeText(UpdateHabit.this, "Habit is out-of-date.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        checkBoxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(1, isChecked); // Monday
            }
        });
        checkBoxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(2, isChecked); // Tuesday
            }
        });
        checkBoxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(3, isChecked); // Wednesday
            }
        });
        checkBoxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(4, isChecked); // Thursday
            }
        });
        checkBoxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(5, isChecked); // Friday
            }
        });
        checkBoxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(6, isChecked); // Saturday
            }
        });
        checkBoxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysChecked.set(0, isChecked); // Sunday
            }
        });



    }

    private void deleteHabit() {
        Log.d("HabitService", "delete habit success" + habitId);
        try {
            // Add entry to the service and notify the adapter that the data set has changed
            entriesService.deleteEntry(habitId);

            Toast.makeText(this, "Habit deleted successfully.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Handle exceptions from adding entry
            Log.e("DeleteHabit", "Failed to delete the habit", e);
            Toast.makeText(this, "Failed to delete the habit. Please try again.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }







}
