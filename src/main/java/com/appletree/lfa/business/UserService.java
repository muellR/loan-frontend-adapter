package com.appletree.lfa.business;

import com.appletree.lfa.business.convert.LoanConverter;
import com.appletree.lfa.data.model.financingobject.FinancingObject;
import com.appletree.lfa.data.access.repo.FinancingObjectRepository;
import com.appletree.lfa.data.model.limit.Limit;
import com.appletree.lfa.data.access.repo.LimitRepository;
import com.appletree.lfa.data.model.product.Product;
import com.appletree.lfa.data.access.repo.ProductRepository;
import com.appletree.lfa.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final FinancingObjectRepository financingObjectRepository;
    private final LimitRepository limitRepository;
    private final ProductRepository productRepository;
    private final LoanConverter loanConverter;

    public List<Loan> getLoans(Long userId) {
        log.debug("getting financing objects of userId={}", userId);
        List<FinancingObject> userFinancingObjects = financingObjectRepository.findByUserId(userId);

        log.debug("getting limits of userId={}", userId);
        Map<Long, Limit> userLimits = getUserLimits(userFinancingObjects);

        log.debug("getting products of userId={}", userId);
        Map<Long, Product> userProducts = getUserProducts(userFinancingObjects);

        return loanConverter.convertLoans(userFinancingObjects, userLimits, userProducts);
    }

    public List<String> getUserIds() {
        return financingObjectRepository.findUserIds();
    }

    private Map<Long, Limit> getUserLimits(List<FinancingObject> userFinancingObjects) {
        List<Long> userLimitIds = userFinancingObjects.stream().map(FinancingObject::getLimitId).toList();
        return limitRepository.findByIds(userLimitIds);
    }

    private Map<Long, Product> getUserProducts(List<FinancingObject> userFinancingObjects) {
        List<Long> userProductIds = userFinancingObjects.stream().map(FinancingObject::getProductIds).flatMap(List::stream).toList();
        return productRepository.findByIds(userProductIds);
    }
}
