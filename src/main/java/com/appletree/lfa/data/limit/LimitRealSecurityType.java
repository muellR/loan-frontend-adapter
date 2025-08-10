package com.appletree.lfa.data.limit;


import com.appletree.lfa.model.LoanCollateralInner;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LimitRealSecurityType {
    EINFAMILIENHAUS("Einfamilienhaus"),

    REIHENHAUS("Reihenhaus"),

    STOCKWERKEIGENTUM("Stockwerkeigentum"),

    MEHRFAMILIENHAUS("Mehrfamilienhaus"),

    GARTENHAUS("Gartenhaus"),

    BAULAND("Bauland");

    private final String value;

    LimitRealSecurityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
