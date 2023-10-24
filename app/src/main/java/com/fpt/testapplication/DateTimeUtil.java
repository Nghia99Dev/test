package com.fpt.testapplication;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtil {
    public static LocalDate parseLocalDate(long timestamp) {
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
        return localDate.toLocalDate();
    }

    public static LocalTime parseLocalTime(long timestamp) {
        LocalDateTime localDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
        return localDate.toLocalTime();
    }

    public static LocalDateTime parseLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId());
    }

    public static boolean isLocalTime(String localTime) {
        try {
            return LocalTime.parse(localTime, DateTimeFormatter.ISO_LOCAL_TIME) != null;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static LocalDateTime parseLocalDateTime(long date, long time) {
        LocalDate localDate = parseLocalDate(date);
        LocalTime localTime = parseLocalTime(time);
        return localDate.atTime(localTime);
    }

    public static String localDateTimeToString(LocalDateTime localDateTime) {
        return String.format(Locale.getDefault(), "%02d-%02d-%02d %02d:%02d:%02d", localDateTime.getDayOfMonth(), localDateTime.getMonthValue(), localDateTime.getYear(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
    }
}
