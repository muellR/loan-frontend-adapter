package com.appletree.lfa.util;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class DateUtil {

    public static OffsetDateTime convertOffsetDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.of("CET");
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        return zonedDateTime.withZoneSameInstant(UTC).toOffsetDateTime();
    }
}
