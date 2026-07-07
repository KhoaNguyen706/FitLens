package com.example.fitlens.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public record DayRange(Instant start, Instant end) {
    }

    public static DayRange dayRangeUtc(LocalDate day) {
        Instant start = day.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant end = day.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return new DayRange(start, end);
    }
}
