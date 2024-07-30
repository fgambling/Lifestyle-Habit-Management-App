package com.comp90018.a2.habits;

import android.util.Log;

import com.comp90018.a2.diary.DiaryEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.UUID;


public class HabitEntriesService {
    // Singleton pattern to ensure a single instance of the service
    private static HabitEntriesService instance;
    private final List<HabitEntry> habitEntries = new ArrayList<>();
    HabitCRUDService habitCRUDService = new HabitCRUDService();
    private final List<OnHabitsChangedListener> listeners = new ArrayList<>();

    public HabitEntriesService() {
        loadEntries();
    }

    public interface OnHabitsChangedListener {
        void onHabitsChanged(List<HabitEntry> habits);
    }

    public void addOnHabitsChangedListener(OnHabitsChangedListener listener) {
        listeners.add(listener);
    }

    private void notifyHabitsChanged() {
        for (OnHabitsChangedListener listener : listeners) {
            listener.onHabitsChanged(habitEntries);
        }
    }

    public HabitEntry getEntryById(String id) {
        for (HabitEntry entry : habitEntries) {
            if (entry.getId().equals(id)) {
                return entry;
            }
        }
        return null;
    }

    public void loadEntries(){
        habitCRUDService.index().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                habitEntries.clear(); // Clearing previous entries
                List<HabitEntry> results = task.getResult(); // Could be null if no documents are found
                if (results != null) {
                    habitEntries.addAll(results); // Add results (if not null)
                    sortList();
                } else {
                    Log.d("HabitService", "No habit found."); // Handle the case where no documents are found
                }
                // Notify the adapter of the data change
                notifyHabitsChanged();
                //Log.d("HabitService", habitEntries.toString()); // Logging the entries
            } else {
                // Handle the case where the task is not successful
                Exception e = task.getException();
                Log.d("HabitService", "get habits failed", e); // Log the exception
            }
        });
    }

    public static synchronized HabitEntriesService getInstance() {
        if (instance == null) {
            instance = new HabitEntriesService();
        }
        return instance;
    }

    public void addEntry(HabitEntry entry) {
        List<Boolean> daysChecked = new ArrayList<>(Collections.nCopies(7, false));
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // 24-hour format
        int minute = calendar.get(Calendar.MINUTE);
        Map<String, Object> data = new HashMap<>();
        data.put("StartDate", entry.getStartDate());
        data.put("Title", entry.getTitle());
        data.put("EndDate", entry.getEndDate());
        data.put("AlarmTime", hour + ":" + minute);
        data.put("AlarmDay", daysChecked);
        data.put("Alarm", false);

        habitCRUDService.createHabit(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // habit entry created successfully
                String docID = task.getResult();
                Log.d("HabitService", "create habit success" + docID);

                Map<String, Object> updateData = new HashMap<>();
                updateData.put("habitId", docID);

                habitCRUDService.updateHabit(docID, updateData).addOnCompleteListener(updateTask -> {
                    if(updateTask.isSuccessful()) {
                        // Document updated successfully
                        Log.d("HabitService", "Habit ID set successfully");
                        entry.setHabitId(docID);
                        entry.setAlarmOn(false);
                        entry.setAlarmDay(daysChecked);
                        entry.setAlarmTime(hour + ":" + minute);
                        habitEntries.add(entry);
                        notifyHabitsChanged();
                    } else {
                        // Handle the case where the update fails
                        Exception updateException = updateTask.getException();
                        Log.d("HabitService", "Failed to set habit ID", updateException);
                    }
                });

            } else {
                // Failed to create habit entry
                Log.d("HabitService", "create habit failed");
            }
        });
    }

    public void deleteEntry(String habitId) {
        habitCRUDService.deleteHabit(habitId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifyHabitsChanged();
                Log.d("HabitService", "delete habit success" + habitId);
            } else {
                // Failed to delete habit entry
                Log.d("HabitService", "delete habit failed");

            }
        });
    }

    public void updateEntry(String habitId, Map<String, Object> data) {
        habitCRUDService.updateHabit(habitId, data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifyHabitsChanged();
                Log.d("HabitService", "update habit success" + habitId);
            } else {
                // Failed to delete habit entry
                Log.d("HabitService", "update habit failed");

            }
        });
    }



    public List<HabitEntry> getAllEntries() {
        return habitEntries;
    }

    public void sortList(){
        habitEntries.sort(new Comparator<HabitEntry>() {
            @Override
            public int compare(HabitEntry t1, HabitEntry t2) {
                return t1.getEndDate().compareTo(t2.getEndDate());
            }
        });
    }
}

