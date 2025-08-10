package com.appletree.lfa.data.model.product;

import com.appletree.lfa.data.model.shared.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    private Long id;
    private String name;
    private ProductType type;
    private Long amount;
    private CurrencyCode currencyCode;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String productNumber;
    private String defaultSettlementAccountNumber;
    private Double interestDue;
    private Boolean isOverdue;
    private Integer interestPaymentFrequency;
}
