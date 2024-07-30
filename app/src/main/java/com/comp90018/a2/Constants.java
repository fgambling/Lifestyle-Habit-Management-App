package com.comp90018.a2;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final Map<Integer, String> INT_TO_MOOD = new HashMap<>();

    static {
        INT_TO_MOOD.put(0, "Very Negative");
        INT_TO_MOOD.put(1, "Negative");
        INT_TO_MOOD.put(2, "Neutral");
        INT_TO_MOOD.put(3, "Positive");
        INT_TO_MOOD.put(4, "Very Positive");
    }

    public static String moodToString(int feeling) {
        return INT_TO_MOOD.get(feeling);
    }
}
