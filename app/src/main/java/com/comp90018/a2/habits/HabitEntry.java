package com.comp90018.a2.habits;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HabitEntry {

    private String habitId = "";
    private String startDate = "";
    private String endDate = "";
    private String title = "";

    private boolean alarmOn = false;
    private String alarmTime = "";
    private List<Boolean> alarmDay = new ArrayList<>();

    public HabitEntry(String sDate, String eDate, String title) {
        //this.habitId = id; // create a unique ID
        this.startDate = sDate;
        this.endDate = eDate;
        this.title = title;
    }

    public HabitEntry(String sDate, String eDate, String title, String id, String alarmTime, boolean alarmOn, List<Boolean> alarmDay) {
        this.habitId = id; // create a unique ID
        this.startDate = sDate;
        this.endDate = eDate;
        this.title = title;
        this.alarmTime = alarmTime;
        this.alarmOn = alarmOn;
        this.alarmDay = alarmDay;
    }

    @NonNull
    @Override
    public String toString() {
        return title; // Return the title for display in the list
    }
    public String getId() {
        return habitId;
    }

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAlarmOn() {
        return alarmOn;
    }

    public void setAlarmOn(boolean alarmOn) {
        this.alarmOn = alarmOn;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public List<Boolean> getAlarmDay() {
        return alarmDay;
    }

    public void setAlarmDay(List<Boolean> alarmDay) {
        this.alarmDay = alarmDay;
    }
}
