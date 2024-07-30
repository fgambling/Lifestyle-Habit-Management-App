package com.comp90018.a2.calendar;

import junit.framework.TestCase;

public class YearMonthUtilsTest extends TestCase {

    public void testGetYearMonthPretty() {
        assertEquals("OCTOBER 2023", YearMonthUtils.getYearMonthPretty("2023-10"));
    }

    public void testGetCurrentYearMonth() {
        System.out.println(YearMonthUtils.getCurrentYearMonth());
    }
}