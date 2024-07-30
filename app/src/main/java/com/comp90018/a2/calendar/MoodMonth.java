package com.comp90018.a2.calendar;

import androidx.annotation.NonNull;

import com.comp90018.a2.diary.DiaryEntry;

import java.util.HashMap;
import java.util.Map;

public class MoodMonth {
    private final int numbersOfDays;
    private final String yearMonth;
    private final Map<Integer, MoodAggregate> dayToMood;

    public MoodMonth(String yearMonth) {
        numbersOfDays = YearMonthUtils.getDaysInMonth(yearMonth+"-01");
        this.yearMonth = yearMonth;
        dayToMood = new HashMap<>();
    }

    /**
     * Add a diary entry to the month and average mood score
     *
     * @param diaryEntry
     */
    public void addEntry(DiaryEntry diaryEntry) {
        int day = YearMonthUtils.getDay(diaryEntry.getDate());
        int mood = diaryEntry.getMood();
        // check if the day is already in the map
        if (dayToMood.containsKey(day)) {
            // if the day is already in the map, update the average mood score
            dayToMood.get(day).addMood(mood);
        } else {
            // if the day is not in the map, add the day and mood score to the map
            dayToMood.put(day, new MoodAggregate(mood));
        }
    }

    public int getMoodOnDay(int day) {
        if (dayToMood.containsKey(day)) {
            return dayToMood.get(day).getAverageMood();
        } else {
            // negative mood score means no entry on that day
            return -1;
        }
    }

    public int getNumbersOfDays() {
        return numbersOfDays;
    }

    @NonNull
    @Override
    public String toString() {
        // return the string representation of dayToMood map
        StringBuilder stringBuilder = new StringBuilder();
        for (int day : dayToMood.keySet()) {
            stringBuilder.append(day).append(": ");
            stringBuilder.append(dayToMood.get(day).getAverageMood());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public class MoodAggregate {
        private int totalMood;
        private int numberOfEntries;

        public MoodAggregate(int initialMood) {
            this.totalMood = initialMood;
            this.numberOfEntries = 1; // start with 1 entry
        }

        public int getTotalMood() {
            return totalMood;
        }

        public int getNumberOfEntries() {
            return numberOfEntries;
        }

        public void addMood(int mood) {
            totalMood += mood;
            numberOfEntries++;
        }

        public int getAverageMood() {
            double average = (double) totalMood / numberOfEntries;
            return (int) Math.round(average);
        }
    }
}
