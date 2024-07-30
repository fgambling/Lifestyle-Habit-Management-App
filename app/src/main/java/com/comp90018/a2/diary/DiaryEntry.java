package com.comp90018.a2.diary;

import androidx.annotation.NonNull;

import com.comp90018.a2.location.LatLong;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DiaryEntry implements Comparable<DiaryEntry> {
    private final UUID id;
    private int mood;
    private String date = "";
    private String title = "";
    private String entryText = "";
    private LatLong latlong = null;
    private String address = null;
    private byte[] imageBytes;

    private String documentId;

    // No location constructor
    public DiaryEntry(String date, String title, String entryText, int mood) {
        this.id = UUID.randomUUID(); // create a unique ID
        this.date = date;
        this.title = title;
        this.entryText = entryText;
        this.mood = mood;
    }
    public DiaryEntry(String date, String title, String entryText, int mood, String documentId) {
        this.id = UUID.randomUUID(); // create a unique ID
        this.date = date;
        this.title = title;
        this.entryText = entryText;
        this.mood = mood;
        this.documentId=documentId;
    }

    public LatLong getLatlong() {
        return latlong;
    }

    public void setLatlong(LatLong latlong) {
        this.latlong = latlong;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    @Override
    public int compareTo(DiaryEntry other) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date thisDate = dateFormat.parse(this.date);
            Date otherDate = dateFormat.parse(other.date);

            // Compare in reverse order to sort by most recent date first
            return otherDate.compareTo(thisDate);
        } catch (ParseException e) {
            // Handle date parsing exception (e.g., invalid date format)
            e.printStackTrace();
            return 0; // Return 0 in case of an error
        }
    }

    @NonNull
    @Override
    public String toString() {
        return title; // Return the title for display in the list
    }

    public UUID getId() {
        return id;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getEntryText() {
        return entryText;
    }

    public String getDocumentId() { return documentId; }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public LatLong getLatLong() {
        return latlong;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    // Getter methods for date, title, and entryText

    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public DiaryEntry updateDiaryEntry(String date, String title, String entryText, int mood){
        this.date = date;
        this.title = title;
        this.entryText = entryText;
        this.mood = mood;
        return this;
    }

}
