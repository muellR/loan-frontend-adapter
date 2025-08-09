package com.appletree.lfa.data.financingobject;

import lombok.Getter;

@Getter
public enum FinancingObjectStatus {
    ACTIVE("active"),
    INACTIVE("inactive");

    // Optional getter if you want to retrieve the string
    private final String value;

    FinancingObjectStatus(String value) {
        this.value = value;
    }
}
