package com.appletree.lfa.business;

import com.appletree.lfa.data.financingobject.FinancingObject;
import com.appletree.lfa.data.financingobject.FinancingObjectOwner;
import com.appletree.lfa.data.limit.Limit;
import com.appletree.lfa.data.limit.LimitRealSecurity;
import com.appletree.lfa.data.product.Product;
import com.appletree.lfa.model.Loan;
import com.appletree.lfa.model.LoanCollateralInner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.appletree.lfa.model.Loan.LoanTypeEnum.CHILD_LOAN;
import static com.appletree.lfa.model.Loan.LoanTypeEnum.PARENT_LOAN;
import static com.appletree.lfa.model.LoanCollateralInner.TypeEnum;
import static java.time.ZoneOffset.UTC;
import static java.util.Comparator.naturalOrder;

@Slf4j
@Service
public class LoanConverter {

    private static final DecimalFormat ZERO_DECIMALS_FORMAT = new DecimalFormat("#");
    private static final DecimalFormat TWO_DECIMALS_FORMAT = new DecimalFormat("#.00");
    private static final DecimalFormat FIVE_DECIMALS_FORMAT = new DecimalFormat("#.00000");
    private static final Map<Integer, String> FREQUENCIES = Map.of(
            1, "Annually",
            2, "Semiannual",
            3, "Triannual",
            4, "Quarterly",
            6, "Bimonthly",
            12, "Monthly");

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
                Loan.LoanStatusEnum.fromValue(financingObject.getStatus().toString().toLowerCase()),
                null,
                TWO_DECIMALS_FORMAT.format(products.stream().mapToDouble(Product::getAmount).sum()),
                ZERO_DECIMALS_FORMAT.format(limit.getLimitAmount()),
                null,
                null,
                products.stream().anyMatch(Product::getIsOverdue),
                null,
                convertLocalDate(products.stream().map(Product::getStartDate).min(naturalOrder()).orElse(null)),
                convertLocalDate(products.stream().map(Product::getEndDate).max(naturalOrder()).orElse(null)),
                financingObject.getOwners().stream().map(FinancingObjectOwner::getName).toList(),
                null,
                FREQUENCIES.get(limit.getAgreedAmortisationFrequency()),
                null,
                convertCollaterals(limit)
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
                Loan.LoanStatusEnum.fromValue(financingObject.getStatus().toString().toLowerCase()),
                product.getCurrencyCode().toString(),
                TWO_DECIMALS_FORMAT.format(product.getAmount()),
                ZERO_DECIMALS_FORMAT.format(limit.getLimitAmount()),
                FIVE_DECIMALS_FORMAT.format(product.getInterestRate()),
                FIVE_DECIMALS_FORMAT.format(product.getInterestDue()),
                product.getIsOverdue(),
                financingObject.getId().toString(),
                convertLocalDate(product.getStartDate()),
                convertLocalDate(product.getEndDate()),
                financingObject.getOwners().stream().map(FinancingObjectOwner::getName).toList(),
                product.getDefaultSettlementAccountNumber(),
                FREQUENCIES.get(limit.getAgreedAmortisationFrequency()),
                FREQUENCIES.get(product.getInterestPaymentFrequency()),
                convertCollaterals(limit)
        );
    }

    private List<LoanCollateralInner> convertCollaterals(Limit limit) {
        return limit.getRealSecurities().stream()
                .map(rs -> convertCollateral(limit.getAmortisationAmountAnnual(), rs))
                .toList();
    }

    private LoanCollateralInner convertCollateral(Double amortisationAmountAnnual, LimitRealSecurity realSecurity) {
        return new LoanCollateralInner(
                TypeEnum.fromValue(realSecurity.getType().toString()),
                ZERO_DECIMALS_FORMAT.format(realSecurity.getCollateralValue()),
                realSecurity.getCurrency().toString(),
                realSecurity.getAddress(),
                realSecurity.getNextRevaluationDate(),
                TWO_DECIMALS_FORMAT.format(amortisationAmountAnnual)
        );
    }

    private OffsetDateTime convertLocalDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        ZoneId zoneId = ZoneId.of("Europe/Zurich");
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(zoneId);
        return zonedDateTime.withZoneSameInstant(UTC).toOffsetDateTime();
    }
}
