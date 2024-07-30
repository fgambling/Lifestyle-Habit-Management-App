package com.comp90018.a2.calendar;

import com.comp90018.a2.diary.DiaryEntry;

import junit.framework.TestCase;

import kotlin.random.Random;

public class MoodCalendarTest extends TestCase {

    public void testAddEntry() {
        MoodCalendar moodCalendar = MoodCalendar.getInstance();
        // add one entry for each day in October
        for (int x = 0; x < 2; x++) {
            for (int i = 1; i <= 31; i++) {
                // add leading zero for single digit number
                String day;
                if (i < 10) {
                    day = "0" + i;
                } else {
                    day = String.valueOf(i);
                }
                String date = "2020-10-" + day;
                int mood = Random.Default.nextInt(1, 5);
                DiaryEntry diaryEntry = new DiaryEntry(date, "title", "entryText", mood);
                moodCalendar.addEntry(diaryEntry);
            }
        }
        // print the mood calendar
        System.out.println(moodCalendar.toString());
    }
}