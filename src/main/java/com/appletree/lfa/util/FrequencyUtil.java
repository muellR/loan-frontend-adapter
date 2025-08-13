package com.appletree.lfa.util;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public final class FrequencyUtil {

    public static final Map<Integer, String> FREQUENCIES = Map.of(
            1, "Annually",
            2, "Semiannual",
            3, "Triannual",
            4, "Quarterly",
            6, "Bimonthly",
            12, "Monthly");

}
