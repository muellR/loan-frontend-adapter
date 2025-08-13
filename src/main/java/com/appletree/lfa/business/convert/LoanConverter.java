package com.appletree.lfa.business.convert;

import com.appletree.lfa.data.model.financingobject.FinancingObject;
import com.appletree.lfa.data.model.financingobject.FinancingObjectOwner;
import com.appletree.lfa.data.model.limit.Limit;
import com.appletree.lfa.data.model.limit.LimitRealSecurity;
import com.appletree.lfa.data.model.product.Product;
import com.appletree.lfa.model.Loan;
import com.appletree.lfa.model.LoanCollateralInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.appletree.lfa.model.Loan.LoanStatusEnum;
import static com.appletree.lfa.model.Loan.LoanTypeEnum;
import static com.appletree.lfa.model.Loan.LoanTypeEnum.CHILD_LOAN;
import static com.appletree.lfa.model.Loan.LoanTypeEnum.PARENT_LOAN;
import static com.appletree.lfa.model.LoanCollateralInner.TypeEnum;
import static com.appletree.lfa.util.DateUtil.convertOffsetDateTime;
import static com.appletree.lfa.util.FrequencyUtil.FREQUENCIES;
import static java.util.Comparator.naturalOrder;

@Slf4j
@Service
public class LoanConverter {

    private static final DecimalFormat ZERO_DECIMALS = new DecimalFormat("#");
    private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("#.00");
    private static final DecimalFormat FIVE_DECIMALS = new DecimalFormat("#.00000");


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
                financingObject.getId().toString(),
                PARENT_LOAN,
                limit.getName(),
                limit.getContractNumber(),
                LoanStatusEnum.fromValue(financingObject.getStatus().toString().toLowerCase()),
                null,
                TWO_DECIMALS.format(products.stream().mapToDouble(Product::getAmount).sum()),
                ZERO_DECIMALS.format(limit.getLimitAmount()),
                null,
                null,
                products.stream().anyMatch(Product::getIsOverdue),
                null,
                convertOffsetDateTime(products.stream().map(Product::getStartDate).filter(Objects::nonNull).min(naturalOrder()).orElse(null)),
                convertOffsetDateTime(products.stream().map(Product::getEndDate).filter(Objects::nonNull).max(naturalOrder()).orElse(null)),
                financingObject.getOwners().stream().map(FinancingObjectOwner::getName).toList(),
                null,
                FREQUENCIES.get(limit.getAgreedAmortisationFrequency()),
                null,
                convertCollaterals(limit, PARENT_LOAN)
        );
    }

    private List<Loan> convertChildLoans(FinancingObject financingObject, Limit limit, List<Product> products) {
        return products.stream()
                .map(p -> convertChildLoan(financingObject, limit, p))
                .toList();
    }

    private Loan convertChildLoan(FinancingObject financingObject, Limit limit, Product product) {
        return new Loan(
                financingObject.getId().toString() + "-" + product.getId().toString(),
                CHILD_LOAN,
                product.getName(),
                limit.getContractNumber(),
                LoanStatusEnum.fromValue(financingObject.getStatus().toString().toLowerCase()),
                product.getCurrencyCode().toString(),
                TWO_DECIMALS.format(product.getAmount()),
                ZERO_DECIMALS.format(limit.getLimitAmount()),
                FIVE_DECIMALS.format(product.getInterestRate()),
                FIVE_DECIMALS.format(product.getInterestDue()),
                product.getIsOverdue(),
                financingObject.getId().toString(),
                convertOffsetDateTime(product.getStartDate()),
                convertOffsetDateTime(product.getEndDate()),
                financingObject.getOwners().stream().map(FinancingObjectOwner::getName).toList(),
                product.getDefaultSettlementAccountNumber(),
                null,
                FREQUENCIES.get(product.getInterestPaymentFrequency()),
                convertCollaterals(limit, CHILD_LOAN)
        );
    }

    private List<LoanCollateralInner> convertCollaterals(Limit limit, LoanTypeEnum loanType) {
        return limit.getRealSecurities().stream()
                .map(rs -> convertCollateral(limit, loanType, rs))
                .toList();
    }

    private LoanCollateralInner convertCollateral(Limit limit, LoanTypeEnum loanType, LimitRealSecurity realSecurity) {
        return new LoanCollateralInner(
                TypeEnum.fromValue(realSecurity.getType().toString()),
                ZERO_DECIMALS.format(realSecurity.getCollateralValue()),
                realSecurity.getCurrency().toString(),
                realSecurity.getAddress(),
                realSecurity.getNextRevaluationDate(),
                PARENT_LOAN.equals(loanType) ? TWO_DECIMALS.format(limit.getAmortisationAmountAnnual() / limit.getAgreedAmortisationFrequency()) : null
        );
    }
}
