package com.comp90018.a2.calendar;

public class MoodDay {
    private String day;
    private int mood;

    public MoodDay (String day, int mood) {
        this.day = day;
        this.mood = mood;
    }

    public String getDay() {
        return day;
    }

    public int getMood() {
        return mood;
    }
}
