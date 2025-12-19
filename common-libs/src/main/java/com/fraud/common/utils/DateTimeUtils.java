package com.fraud.common.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * DateTime Utility class for date/time operations.
 * 
 * Provides standardized date/time formatting and manipulation
 * methods used throughout the fraud detection system.
 */
public final class DateTimeUtils {

    public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String READABLE_FORMAT = "MMM dd, yyyy HH:mm:ss";

    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern(ISO_FORMAT)
            .withZone(ZoneOffset.UTC);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final DateTimeFormatter READABLE_FORMATTER = DateTimeFormatter.ofPattern(READABLE_FORMAT);

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Get current UTC instant.
     */
    public static Instant now() {
        return Instant.now();
    }

    /**
     * Get current UTC timestamp in milliseconds.
     */
    public static long nowMillis() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Format Instant to ISO string.
     *
     * @param instant The instant to format
     * @return ISO formatted string
     */
    public static String formatIso(Instant instant) {
        if (instant == null) {
            return null;
        }
        return ISO_FORMATTER.format(instant);
    }

    /**
     * Parse ISO string to Instant.
     *
     * @param isoString The ISO formatted string
     * @return Instant, or null if parsing fails
     */
    public static Instant parseIso(String isoString) {
        if (isoString == null || isoString.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(isoString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Format Instant to readable string.
     *
     * @param instant The instant to format
     * @return Human-readable formatted string
     */
    public static String formatReadable(Instant instant) {
        if (instant == null) {
            return null;
        }
        return READABLE_FORMATTER.format(instant.atZone(ZoneOffset.UTC));
    }

    /**
     * Get the hour of day (0-23) from an Instant.
     *
     * @param instant The instant
     * @return Hour of day in UTC
     */
    public static int getHourOfDay(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).getHour();
    }

    /**
     * Get the day of week (1=Monday, 7=Sunday) from an Instant.
     *
     * @param instant The instant
     * @return Day of week
     */
    public static int getDayOfWeek(Instant instant) {
        return instant.atZone(ZoneOffset.UTC).getDayOfWeek().getValue();
    }

    /**
     * Check if the instant is during night time (1 AM - 5 AM).
     *
     * @param instant The instant to check
     * @return true if during night time
     */
    public static boolean isNightTime(Instant instant) {
        int hour = getHourOfDay(instant);
        return hour >= 1 && hour <= 5;
    }

    /**
     * Check if the instant is during weekend.
     *
     * @param instant The instant to check
     * @return true if Saturday or Sunday
     */
    public static boolean isWeekend(Instant instant) {
        DayOfWeek dayOfWeek = instant.atZone(ZoneOffset.UTC).getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Calculate duration between two instants in milliseconds.
     *
     * @param start Start instant
     * @param end   End instant
     * @return Duration in milliseconds
     */
    public static long durationMillis(Instant start, Instant end) {
        return ChronoUnit.MILLIS.between(start, end);
    }

    /**
     * Calculate duration between two instants in seconds.
     *
     * @param start Start instant
     * @param end   End instant
     * @return Duration in seconds
     */
    public static long durationSeconds(Instant start, Instant end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * Get instant from N hours ago.
     *
     * @param hours Number of hours
     * @return Instant from N hours ago
     */
    public static Instant hoursAgo(int hours) {
        return Instant.now().minus(hours, ChronoUnit.HOURS);
    }

    /**
     * Get instant from N minutes ago.
     *
     * @param minutes Number of minutes
     * @return Instant from N minutes ago
     */
    public static Instant minutesAgo(int minutes) {
        return Instant.now().minus(minutes, ChronoUnit.MINUTES);
    }

    /**
     * Check if instant is within the last N minutes.
     *
     * @param instant The instant to check
     * @param minutes Number of minutes
     * @return true if within the specified time window
     */
    public static boolean isWithinLastMinutes(Instant instant, int minutes) {
        return instant.isAfter(minutesAgo(minutes));
    }

    /**
     * Check if instant is within the last N hours.
     *
     * @param instant The instant to check
     * @param hours   Number of hours
     * @return true if within the specified time window
     */
    public static boolean isWithinLastHours(Instant instant, int hours) {
        return instant.isAfter(hoursAgo(hours));
    }
}
