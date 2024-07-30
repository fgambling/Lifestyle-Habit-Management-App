package com.comp90018.a2.habits;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HabitCRUDService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private String uid = user.getUid();

    private void handleError(Exception e) {
        // Log the error
        Log.w("HabitService", "Error: ", e);

    }

    public Task<String> createHabit(Map<String, Object> data) {
        Log.d("HabitService", "uid: "+uid);
        CollectionReference habitRef = db.collection("habit").document(uid).collection("habits");
        DocumentReference newHabitDoc = habitRef.document();
        return newHabitDoc.set(data)
                .continueWith(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        handleError(task.getException());
                        return null;
                    }
                    return newHabitDoc.getId();
                });
    }

    public Task<List<HabitEntry>> index() {
        CollectionReference habitsRef = db.collection("habit").document(uid).collection("habits");
        return habitsRef.get()
                .continueWith(task -> {
                    List<HabitEntry> habitEntries = new ArrayList<>();
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return null;
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        HabitEntry habitEntry = new HabitEntry(
                                data.get("StartDate").toString(),
                                data.get("EndDate").toString(),
                                data.get("Title").toString(),
                                data.get("habitId").toString(),
                                data.get("AlarmTime").toString(),
                                (Boolean)data.get("Alarm"),
                                (List<Boolean>) data.get("AlarmDay")
                        );
                        habitEntries.add(habitEntry);
                    }
                    return habitEntries;
                })
                .addOnFailureListener(this::handleError);
    }

    public Task<List<Map<String, Object>>> readAllHabitTitlesAndDates() {
        CollectionReference habitsRef = db.collection("habit").document(uid).collection("habits");
        return habitsRef.get()
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

    public Task<Map<String, Object>> readSpecificHabitContent(String habitId) {
        return db.collection("habit").document(uid).collection("habits").document(habitId).get()
                .continueWith(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return new HashMap<String, Object>();
                    }
                    DocumentSnapshot document = task.getResult();
                    return document.getData();
                })
                .addOnFailureListener(this::handleError);
    }

    public Task<Void> updateHabit(String habitId, Map<String, Object> data) {
        DocumentReference habitDoc = db.collection("habit").document(uid).collection("habits").document(habitId);
        return habitDoc.update(data).addOnFailureListener(this::handleError);
    }

    public Task<Void> deleteHabit(String habitId) {
        DocumentReference habitDoc = db.collection("habit").document(uid).collection("habits").document(habitId);
        Log.d("deleteHabit", "UID: " + uid + ", Habit ID: " + habitId);
        return habitDoc.delete()
                .addOnSuccessListener(aVoid -> Log.d("deleteHabit", "DocumentSnapshot successfully deleted!"))
                .addOnFailureListener(e -> Log.e("deleteHabit", "Error deleting document", e));

    }

    public Task<List<String>> searchHabitsByTitle(String titleToSearch) {
        CollectionReference habitsRef = db.collection("habit").document(uid).collection("habits");

        // Create a query against the collection.
        Query query = habitsRef.whereEqualTo("Title", titleToSearch);

        return query.get()
                .continueWith(task -> {
                    List<String> matchingHabitIds = new ArrayList<>();
                    if (!task.isSuccessful() || task.getResult() == null) {
                        handleError(task.getException());
                        return matchingHabitIds;
                    }
                    for (DocumentSnapshot document : task.getResult()) {
                        matchingHabitIds.add(document.getId());
                    }
                    return matchingHabitIds;
                })
                .addOnFailureListener(this::handleError);
    }


}
