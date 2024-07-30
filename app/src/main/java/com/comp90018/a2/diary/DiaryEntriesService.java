package com.comp90018.a2.diary;

import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class DiaryEntriesService {
    // Singleton pattern to ensure a single instance of the service
    private static DiaryEntriesService instance;
    private final List<DiaryEntry> diaryEntries = new ArrayList<>();
    DiaryCRUDService diaryCRUDService = new DiaryCRUDService();
    private Random random = new Random();
    private ArrayAdapter<DiaryEntry> diaryListAdapter;

    private DiaryEntriesService() {
        // dummy diary entries
//        for (int i = 1; i < 5; i++) {
//            String title = "Entry " + i;
//            String entryText = "This is entry " + i;
//            this.addEntry(new DiaryEntry("2023-09-25", title, entryText, random.nextInt(5)));
//        }
        loadEntries();
    }

    public static DiaryEntriesService getInstance() {
        if (instance == null) {
            instance = new DiaryEntriesService();
        }
        return instance;
    }

    public void loadEntries() {
        diaryCRUDService.index().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // clear the diaryEntries list to maintain reference
                diaryEntries.clear();
                diaryEntries.addAll(task.getResult());

                // Sort the diaryEntries list by date
                Collections.sort(diaryEntries);

                // notify changes to the adapter
                try {
                    diaryListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("DiaryService", "adapter not set");
                }
                Log.d("DiaryService", diaryEntries.toString());
            } else {
                Log.d("DiaryService", "get diary failed");
            }
        });
    }

    public DiaryEntry getEntryById(UUID id) {
        for (DiaryEntry entry : diaryEntries) {
            if (entry.getId().equals(id)) {
                return entry;
            }
        }
        return null;
    }

    public ArrayAdapter<DiaryEntry> getDiaryListAdapter() {
        return diaryListAdapter;
    }

    public void setDiaryListAdapter(ArrayAdapter<DiaryEntry> diaryListAdapter) {
        this.diaryListAdapter = diaryListAdapter;
    }

    public void addEntry(DiaryEntry entry) {
        Map<String, Object> data = new HashMap<>();
        data.put("Date", entry.getDate());
        data.put("Title", entry.getTitle());
        data.put("Entry", entry.getTitle());
        data.put("Mood", entry.getMood());

        if (entry.getImageBytes() != null) {
            // Convert Byte objects to primitive int values
            List<Long> imageBytesList = new ArrayList<>();
            for (Byte b : entry.getImageBytes()) {
                imageBytesList.add((long) b);
            }

            // Store the List in Firestore
            data.put("ImageBytes", imageBytesList);
        }

        if (entry.getLatLong() != null) {
            data.put("Coordinates", new GeoPoint(entry.getLatLong().latitude, entry.getLatLong().longitude));
        }

        if (entry.getAddress() != null) {
            data.put("Address", entry.getAddress());
        }

        diaryCRUDService.createDiary(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Add entry to local list
                String docID = task.getResult();
                Log.d("DiaryService", "create diary success: " + docID);
                entry.setDocumentId(docID);
                diaryEntries.add(entry);

                // Sort the diaryEntries list by date
                Collections.sort(diaryEntries);

                // notify changes to the adapter
                try {
                    diaryListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("DiaryService", "adapter not set");
                }
            } else {
                // Failed to create diary entry
                // TODO: Handle failure. E.g., show an error message.
                Log.d("DiaryService", "create diary failed");
            }
            return;
        });
    }

    public void deleteEntry(UUID id){
        Log.d("DiaryEntryActivity", "delete local diary");
        diaryEntries.remove(getEntryById(id));
        try {
            diaryListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("DiaryService", "adapter not set");
        }
    }

    public void updateEntry(DiaryEntry entry) {
        Map<String, Object> data = new HashMap<>();
        data.put("Date", entry.getDate());
        data.put("Title", entry.getTitle());
        data.put("Entry", entry.getTitle());
        data.put("Mood", entry.getMood());

        if (entry.getImageBytes() != null) {
            // Convert Byte objects to primitive int values
            List<Long> imageBytesList = new ArrayList<>();
            for (Byte b : entry.getImageBytes()) {
                imageBytesList.add((long) b);
            }

            // Store the List in Firestore
            data.put("ImageBytes", imageBytesList);
        }

        if (entry.getLatLong() != null) {
            data.put("Coordinates", new GeoPoint(entry.getLatLong().latitude, entry.getLatLong().longitude));
        }

        if (entry.getAddress() != null) {
            data.put("Address", entry.getAddress());
        }

        diaryCRUDService.updateDiary(entry.getDocumentId(), data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Add entry to local list
                diaryEntries.remove(getEntryById(entry.getId()));
                diaryEntries.add(entry);

                // Sort the diaryEntries list by date
                Collections.sort(diaryEntries);

                // notify changes to the adapter
                try {
                    diaryListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.d("DiaryService", "adapter not set");
                }
            } else {
                // Failed to create diary entry
                Log.d("DiaryService", "update diary failed");
            }
            return;
        });
    }

    public List<DiaryEntry> getAllEntries() {
        return diaryEntries;
    }
}
