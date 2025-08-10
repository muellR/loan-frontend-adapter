package com.appletree.lfa.business;

import com.appletree.lfa.data.financingobject.FinancingObject;
import com.appletree.lfa.data.limit.Limit;
import com.appletree.lfa.data.product.Product;
import com.appletree.lfa.model.Loan;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LoanConverter {

    public List<Loan> convertLoans(List<FinancingObject> financingObjects, Map<Long, Limit> limits, Map<Long, Product> products) {
        return List.of();
    }
}
