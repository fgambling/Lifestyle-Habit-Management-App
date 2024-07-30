package com.comp90018.a2.calendar;

import androidx.annotation.NonNull;

import com.comp90018.a2.diary.DiaryEntriesService;
import com.comp90018.a2.diary.DiaryEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MoodCalendar {
    private static MoodCalendar instance;
    private Map<String, MoodMonth> yearMonthToMoodMonth;
    private String yearMonth;

    public String getYearMonthPretty() {
        String prettyString = YearMonthUtils.getYearMonthPretty(yearMonth);
        return prettyString.substring(0, 1).toUpperCase(Locale.ROOT) + prettyString.substring(1).toLowerCase(Locale.ROOT);
    }

    private MoodCalendar() {
        yearMonthToMoodMonth = new HashMap<>();
        yearMonth = YearMonthUtils.getCurrentYearMonth();
    }

    public void nextMonth(){
        yearMonth = YearMonthUtils.getNextYearMonth(yearMonth);
    }

    public void previousMonth(){
        yearMonth = YearMonthUtils.getPreviousYearMonth(yearMonth);
    }

    /**
     * Singleton pattern
     *
     * @return the instance of MoodCalendar
     */
    public static MoodCalendar getInstance() {
        if (instance == null) {
            instance = new MoodCalendar();
        }
        return instance;
    }

    /**
     * Get a list of MoodDay for the current month
     *
     * @return
     */
    public List<MoodDay> getMoodDays() {
        List<MoodDay> moodDays = new ArrayList<>();
        int numberOfDays = YearMonthUtils.getDaysInMonth(yearMonth + "-01");
        if (yearMonthToMoodMonth.containsKey(yearMonth)) {
            MoodMonth moodMonth = yearMonthToMoodMonth.get(yearMonth);
            for (int i = 1; i <= numberOfDays; i++) {
                moodDays.add(new MoodDay(i + "", moodMonth.getMoodOnDay(i)));
            }
        } else {
            for (int i = 1; i <= numberOfDays; i++) {
                moodDays.add(new MoodDay(i + "", -1));
            }
        }
        return moodDays;
    }

    /**
     * Add an entry to the mood calendar
     * It will create a new MoodMonth if the yearMonth is not in the map
     */
    public void addEntry(DiaryEntry diaryEntry) {
        String yearMonth = YearMonthUtils.getYearMonth(diaryEntry.getDate());
        if (yearMonthToMoodMonth.containsKey(yearMonth)) {
            yearMonthToMoodMonth.get(yearMonth).addEntry(diaryEntry);
        } else {
            MoodMonth moodMonth = new MoodMonth(yearMonth);
            moodMonth.addEntry(diaryEntry);
            yearMonthToMoodMonth.put(yearMonth, moodMonth);
        }
    }

    /**
     * Reload mood calendar from diary entries
     */
    public void reload() {
        yearMonthToMoodMonth.clear();
        for (DiaryEntry diaryEntry : DiaryEntriesService.getInstance().getAllEntries()) {
            addEntry(diaryEntry);
        }
    }

    @NonNull
    @Override
    public String toString() {
        // return the string representation of yearMonthToMoodMonth map
        StringBuilder stringBuilder = new StringBuilder();
        for (String yearMonth : yearMonthToMoodMonth.keySet()) {
            stringBuilder.append(yearMonth).append(": ");
            stringBuilder.append(yearMonthToMoodMonth.get(yearMonth).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
