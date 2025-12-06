package com.reservio.reservation_system.reservationsystemdesktop.util;

import java.util.List;
import java.util.Map;

public class ReservationStatusTranslator {

    private static final Map<String, String> TO_POLISH = Map.of(
            "Pending", "Oczekująca",
            "Confirmed", "Potwierdzona",
            "Cancelled", "Anulowana",
            "Completed", "Zakończona",
            "Partial-Paid", "Częściowo opłacona",
            "Rejected", "Odrzucona",
            "Paid", "Opłacona"
    );

    private static final Map<String, String> TO_ENGLISH = Map.ofEntries(
            Map.entry("Oczekująca", "Pending"),
            Map.entry("Potwierdzona", "Confirmed"),
            Map.entry("Anulowana", "Cancelled"),
            Map.entry("Zakończona", "Completed"),
            Map.entry("Częściowo opłacona", "Partial-Paid"),
            Map.entry("Odrzucona", "Rejected"),
            Map.entry("Opłacona", "Paid")
    );

    public static String toPolish(String english) {
        return TO_POLISH.getOrDefault(english, english);
    }

    public static String toEnglish(String polish) {
        return TO_ENGLISH.getOrDefault(polish, polish);
    }

    public static final java.util.List<String> POLISH_STATUSES = List.of(
            "Oczekująca", "Potwierdzona", "Anulowana", "Zakończona",
            "Częściowo opłacona", "Odrzucona", "Opłacona"
    );
}