package com.comp90018.a2.diary;

import android.util.Log;

import com.comp90018.a2.location.LatLong;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiaryCRUDService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private String uid = user.getUid();

    private void handleError(Exception e) {
        // Log the error
        Log.w("DiaryService", "Error: ", e);

    }

    public Task<String> createDiary(Map<String, Object> data) {
        Log.d("DiaryService", "uid: " + uid);
        CollectionReference diaryRef = db.collection("diary").document(uid).collection("diaries");

        DocumentReference newDiaryDoc = diaryRef.document();
        data.put("DocumentId", newDiaryDoc.getId());
        return newDiaryDoc.set(data)
                .continueWith(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        handleError(task.getException());
                        return null;
                    }
                    return newDiaryDoc.getId();
                });
    }

    public Task<List<DiaryEntry>> index() {
        CollectionReference diariesRef = db.collection("diary").document(uid).collection("diaries");
        return diariesRef.get()
                .continueWith(task -> {
                    List<DiaryEntry> diaryEntries = new ArrayList<>();
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return null;
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        Map<String, Object> titleAndDate = new HashMap<>();
                        DiaryEntry diaryEntry = new DiaryEntry(data.get("Date").toString(), data.get("Title").toString(), data.get("Entry").toString(), Integer.parseInt(data.get("Mood").toString()),data.get("DocumentId").toString());

                        // Read optional fields
                        if (data.get("Address") != null) {
                            diaryEntry.setAddress(data.get("Address").toString());
                        }

                        if (data.get("Coordinates") != null) {
                            GeoPoint geoPoint = (GeoPoint) data.get("Coordinates");
                            LatLong latLong = new LatLong(geoPoint.getLatitude(), geoPoint.getLongitude());
                            diaryEntry.setLatlong(latLong);
                        }

                        // get list of image bytes
                        List<Long> imageBytesList = (List<Long>) data.get("ImageBytes");
                        if (imageBytesList != null) {
                            // convert list of Integer objects to primitive int values
                            byte[] imageBytes = new byte[imageBytesList.size()];

                            // convert integer bytes to non primitive bytes
                            for (int i = 0; i < imageBytesList.size(); i++) {
                                imageBytes[i] = imageBytesList.get(i).byteValue();
                            }
                            diaryEntry.setImageBytes(imageBytes);
                        }

                        diaryEntries.add(diaryEntry);
                    }
                    return diaryEntries;
                })
                .addOnFailureListener(this::handleError);
    }

    public Task<List<Map<String, Object>>> readAllDiaryTitlesAndDates() {
        CollectionReference diariesRef = db.collection("diary").document(uid).collection("diaries");
        return diariesRef.get()
                .continueWith(task -> {
                    List<Map<String, Object>> titlesAndDates = new ArrayList<>();
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return titlesAndDates;
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        Map<String, Object> titleAndDate = new HashMap<>();
                        titleAndDate.put("Title", data.get("Title"));
                        titleAndDate.put("Date", data.get("Date"));
                        titlesAndDates.add(titleAndDate);
                    }
                    return titlesAndDates;
                })
                .addOnFailureListener(this::handleError);
    }

    public Task<Map<String, Object>> readSpecificDiaryContent(String diaryId) {
        return db.collection("diary").document(uid).collection("diaries").document(diaryId).get()
                .continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return new HashMap<String, Object>(); // 返回一个空的Map
                    }
                    DocumentSnapshot document = task.getResult();
                    return document.getData();
                })
                .addOnFailureListener(this::handleError);
    }

    public Task<Void> updateDiary(String diaryId, Map<String, Object> data) {
        DocumentReference diaryDoc = db.collection("diary").document(uid).collection("diaries").document(diaryId);
        return diaryDoc.update(data).addOnFailureListener(this::handleError);
    }

    public Task<Void> deleteDiary(String diaryId) {
        DocumentReference diaryDoc = db.collection("diary").document(uid).collection("diaries").document(diaryId);
        return diaryDoc.delete().addOnFailureListener(this::handleError);
    }

    public Task<List<String>> searchDiariesByTitle(String titleToSearch) {
        CollectionReference diariesRef = db.collection("diary").document(uid).collection("diaries");

        // Create a query against the collection.
        Query query = diariesRef.whereEqualTo("Title", titleToSearch);

        return query.get()
                .continueWith(task -> {
                    List<String> matchingDiaryIds = new ArrayList<>();
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return matchingDiaryIds;
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        matchingDiaryIds.add(document.getId());
                    }
                    return matchingDiaryIds;
                })
                .addOnFailureListener(this::handleError);
    }

}
