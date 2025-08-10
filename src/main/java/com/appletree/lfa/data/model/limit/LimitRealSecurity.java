package com.appletree.lfa.data.model.limit;

import com.appletree.lfa.data.model.shared.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimitRealSecurity {
    private LimitRealSecurityType type;
    private String address;
    private Long collateralValue;
    private CurrencyCode currency;
    private LocalDate nextRevaluationDate;
}
