package com.appletree.lfa.util;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;

public class DateUtil {

    public static OffsetDateTime convert(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.of("Europe/Zurich");
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        return zonedDateTime.withZoneSameInstant(UTC).toOffsetDateTime();
    }
}
