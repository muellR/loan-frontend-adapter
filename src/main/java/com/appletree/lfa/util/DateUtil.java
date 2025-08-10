package com.appletree.lfa.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.ZoneOffset.UTC;

public final class DateUtil {

    private static final Map<Integer, String> FREQUENCIES = Map.of(
            1, "Annually",
            2, "Semiannual",
            3, "Triannual",
            4, "Quarterly",
            6, "Bimonthly",
            12, "Monthly");

    public static OffsetDateTime convertOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.of("Europe/Zurich");
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        return zonedDateTime.withZoneSameInstant(UTC).toOffsetDateTime();
    }

    public static String getFrequencies(Integer frequency) {
        return FREQUENCIES.get(frequency);
    }
}
