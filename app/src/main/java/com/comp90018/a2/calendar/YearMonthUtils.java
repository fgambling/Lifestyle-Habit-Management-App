package com.comp90018.a2.calendar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class YearMonthUtils {
    public static int getDaysInMonth(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString, formatter);
        return date.lengthOfMonth();
    }

    public static String getCurrentYearMonth(){
        LocalDate date = LocalDate.now();
        // ensure month is two digits
        String month = date.getMonthValue() < 10 ? "0" + date.getMonthValue() : "" + date.getMonthValue();
        return date.getYear() + "-" + month;
    }

    /**
     * Returns a pretty string representation of year month, e.g. October 2023
     */
    public static String getYearMonthPretty(String yearMonth){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate date = LocalDate.parse(yearMonth + "-01");
        return date.getMonth().toString() + " " + date.getYear();
    }

    public static String getNextYearMonth(String yearMonth){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate date = LocalDate.parse(yearMonth + "-01");
        date = date.plusMonths(1);
        return date.format(formatter);
    }

    public static String getPreviousYearMonth(String yearMonth){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        LocalDate date = LocalDate.parse(yearMonth + "-01");
        date = date.minusMonths(1);
        return date.format(formatter);
    }

    public static int getDay(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString);
        return date.getDayOfMonth();
    }

    public static String getYearMonth(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(dateString);
        // ensure month is two digits
        String month = date.getMonthValue() < 10 ? "0" + date.getMonthValue() : "" + date.getMonthValue();
        return date.getYear() + "-" + month;
    }
}
