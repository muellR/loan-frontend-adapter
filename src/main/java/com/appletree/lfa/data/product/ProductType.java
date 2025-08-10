package com.appletree.lfa.data.product;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProductType {
    FESTHYPOTHEK, @JsonProperty("SARON Hypothek") SARON_HYPOTHEK, @JsonProperty("Variable Hypothek") VARIABLE_HYPOTHEK
}
