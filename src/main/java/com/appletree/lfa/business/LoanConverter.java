package com.appletree.lfa.business;

import com.appletree.lfa.data.financingobject.FinancingObject;
import com.appletree.lfa.data.limit.Limit;
import com.appletree.lfa.data.product.Product;
import com.appletree.lfa.model.Loan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class LoanConverter {

    public List<Loan> convertLoans(List<FinancingObject> financingObjects, Map<Long, Limit> limits, Map<Long, Product> products) {
        log.info("converting loans");
        return financingObjects.stream()
                // so far we carried limits and products across various financing objects, now we extract the ones of our current financing object
                .map(fo -> convertLoans(fo, limits.get(fo.getLimitId()), fo.getProductIds().stream().map(products::get).toList()))
                .flatMap(List::stream)
                .toList();
    }

    private List<Loan> convertLoans(FinancingObject financingObject, Limit limit, List<Product> products) {
        Loan parentLoan = convertParentLoan(financingObject, limit, products);
        List<Loan> childLoans = convertChildLoans(financingObject, limit, products);
        return Stream.concat(Stream.of(parentLoan), childLoans.stream()).toList();
    }

    private Loan convertParentLoan(FinancingObject financingObject, Limit limit, List<Product> products) {
        return new Loan(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private List<Loan> convertChildLoans(FinancingObject financingObject, Limit limit, List<Product> products) {
        return products.stream()
                .map(p -> convertChildLoan(financingObject, limit, p))
                .toList();
    }

    private Loan convertChildLoan(FinancingObject financingObject, Limit limit, Product product) {
        return new Loan(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}
