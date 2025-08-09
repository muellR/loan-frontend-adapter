package com.appletree.lfa.data.financingobject;

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
    private Long limit;
    private List<Long> products;
    private FinancingObjectStatus status;
}
