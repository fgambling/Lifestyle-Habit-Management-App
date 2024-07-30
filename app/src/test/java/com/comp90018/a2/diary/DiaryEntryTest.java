package com.comp90018.a2.diary;

import android.util.Log;

import androidx.annotation.NonNull;

import com.comp90018.a2.location.LatLong;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

public class DiaryEntryTest extends TestCase {

    public void testGetAddress() {
        //DiaryEntry diaryEntry = new DiaryEntry("2021-09-25", "Entry 1", "This is entry 1", 1, new LatLong(-37.7964, 144.9612), "Melbourne");
        //System.out.println(diaryEntry.getAddress());
    }


    public void diaryServiceReadAllTest() {
        DiaryCRUDService diaryService = new DiaryCRUDService();

        diaryService.readAllDiaryTitlesAndDates().addOnSuccessListener(resultList -> {
            for (Map<String, Object> map : resultList) {
                String title = (String) map.get("Title");
                String date = (String) map.get("Date");
                Log.d("DiaryReadTest", "Title: " + title + ", Date: " + date);
            }
        }).addOnFailureListener(e -> {
            Log.e("DiaryReadTest", "Error reading diary titles and dates", e);
        });
    }

    public void readSpecificDiaryContentTest() {
        DiaryCRUDService diaryService = new DiaryCRUDService();
        String docIDtoRead = "raokOFWoS58wCskt9esj";
        diaryService.readSpecificDiaryContent(docIDtoRead).addOnCompleteListener(new OnCompleteListener<Map<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, Object>> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Map<String, Object> data = task.getResult();
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        Log.d("DiaryContentLog", entry.getKey() + ": " + entry.getValue());
                    }
                } else {
                    Log.d("DiaryContentLog", "Failed to fetch diary content or no content available.");
                }
            }
        });
    }

    public void deleteDiaryTest() {
        DiaryCRUDService diaryService = new DiaryCRUDService();
        String docIDtoDelete = "raokOFWoS58wCskt9esj";
        diaryService.deleteDiary(docIDtoDelete).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Diary entry delete successfully
                // TODO: Handle success. E.g., show a success message or navigate to another activity.
                Log.d("DiaryService", "delete diary success");

            } else {
                // Failed to delete diary entry
                // TODO: Handle failure. E.g., show an error message.
                Log.d("DiaryService", "delete diary failed");
            }
        });
    }

    public void searchDiariesByTitleTest() {
        DiaryCRUDService diaryService = new DiaryCRUDService();
        String titleToSearch = "123";
        diaryService.searchDiariesByTitle(titleToSearch).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<String> diaryIds = task.getResult();
                if (diaryIds != null && !diaryIds.isEmpty()) {
                    for (String diaryId : diaryIds) {
                        Log.d("DiaryService", "Found matching diary ID: " + diaryId);
                    }
                } else {
                    Log.d("DiaryService", "No diaries found with title: " + titleToSearch);
                }
            } else {
                // Handle failure
                Log.d("DiaryService", "Failed to search diaries", task.getException());
            }
        });
    }
}