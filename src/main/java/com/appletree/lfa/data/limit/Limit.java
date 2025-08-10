package com.appletree.lfa.data.limit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Limit {
    private Long id;
    private String name;
    private LimitType type;
    private Double limitAmount;
    private Double amortisationAmountAnnual;
    private Integer agreedAmortisationFrequency;
    private String contractNumber;
    private List<LimitRealSecurity> realSecurities;
}
