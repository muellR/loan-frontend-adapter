package com.appletree.lfa.data.financingobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancingObject {
    private Long id;
    private List<FinancingObjectOwner> owners;
    @JsonProperty("limit")
    private Long limitId;
    @JsonProperty("products")
    private List<Long> productIds;
    private FinancingObjectStatus status;
}
