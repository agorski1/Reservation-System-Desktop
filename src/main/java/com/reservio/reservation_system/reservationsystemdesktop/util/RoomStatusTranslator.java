package com.reservio.reservation_system.reservationsystemdesktop.util;

import java.util.List;
import java.util.Map;

public final class RoomStatusTranslator {

    private RoomStatusTranslator() {}

    private static final Map<String, String> TO_ENGLISH = Map.of(
        "Dostępny",     "ACTIVE",
        "Niedostępny",  "DELETED",
        "Remont",       "UNDER_MAINTENANCE"
    );

    private static final Map<String, String> TO_POLISH = Map.of(
        "ACTIVE",     "Dostępny",
        "DELETED",   "Niedostępny",
        "UNDER_MAINTENANCE",   "Remont"
    );

    public static final List<String> POLISH_STATUSES = List.of(
        "Dostępny",
        "Niedostępny",
        "Remont"
    );

    public static String toEnglish(String polish) {
        return TO_ENGLISH.getOrDefault(polish, "AVAILABLE");
    }

    public static String toPolish(String english) {
        return TO_POLISH.getOrDefault(english, "Dostępny");
    }
}