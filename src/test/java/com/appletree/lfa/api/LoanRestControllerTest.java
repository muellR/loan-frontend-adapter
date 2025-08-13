package com.appletree.lfa.api;

import com.appletree.lfa.data.access.ResourceDataLoader;
import com.appletree.lfa.data.access.repo.FinancingObjectRepository;
import com.appletree.lfa.data.access.repo.LimitRepository;
import com.appletree.lfa.data.access.repo.ProductRepository;
import com.appletree.lfa.data.model.financingobject.FinancingObject;
import com.appletree.lfa.data.model.limit.Limit;
import com.appletree.lfa.data.model.product.Product;
import com.appletree.lfa.model.Loan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.OffsetDateTime;
import java.util.List;

import static com.appletree.lfa.model.Loan.LoanTypeEnum.CHILD_LOAN;
import static com.appletree.lfa.model.Loan.LoanTypeEnum.PARENT_LOAN;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanRestControllerTest {

    @MockitoSpyBean
    private FinancingObjectRepository financingObjectRepository;

    @MockitoSpyBean
    private LimitRepository limitRepository;

    @MockitoSpyBean
    private ProductRepository productRepository;

    @Autowired
    private ResourceDataLoader resourceDataLoader;

    @Autowired
    private LoanRestController loanRestController;

    @BeforeAll
    void setupOnce() {
        doReturn(resourceDataLoader.readDataFromResources("/data/testFinancingObjects.json", FinancingObject.class))
                .when(financingObjectRepository).getFinancingObjects();
        doReturn(resourceDataLoader.readDataFromResources("/data/testLimits.json", Limit.class))
                .when(limitRepository).getLimits();
        doReturn(resourceDataLoader.readDataFromResources("/data/testProducts.json", Product.class))
                .when(productRepository).getProducts();
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenParentOutstandingAmountIsSumOfChildren() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("1").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getOutstandingAmount)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, "205000.00"),
                        tuple(CHILD_LOAN, "120000.00"),
                        tuple(CHILD_LOAN, "85000.00")
                );
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenParentShowsFullDateRangeOfChildren() {
        OffsetDateTime expectedDate20201101 = OffsetDateTime.of(2020, 10, 31, 23, 0, 0, 0, UTC);
        OffsetDateTime expectedDate20201215 = OffsetDateTime.of(2020, 12, 14, 23, 0, 0, 0, UTC);
        OffsetDateTime expectedDate20251101 = OffsetDateTime.of(2025, 10, 31, 23, 0, 0, 0, UTC);
        OffsetDateTime expectedDate20301215 = OffsetDateTime.of(2030, 12, 14, 23, 0, 0, 0, UTC);

        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("2").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getStartDate, Loan::getEndDate)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, expectedDate20201101, expectedDate20301215),
                        tuple(CHILD_LOAN, expectedDate20201215, expectedDate20301215),
                        tuple(CHILD_LOAN, expectedDate20201101, expectedDate20251101)
                );
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenParentIsOverdueIfAnyOfChildren() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("3").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getIsOverdue)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, true),
                        tuple(CHILD_LOAN, true),
                        tuple(CHILD_LOAN, false)
                );
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenParentIsNotOverdueIfNoneOfChildren() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("4").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getIsOverdue)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, false),
                        tuple(CHILD_LOAN, false),
                        tuple(CHILD_LOAN, false)
                );
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenOnlyParentHasAmortisationPaymentAmountAndPaymentFrequency() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("5").getBody();

        assertThat(loans).extracting(Loan::getLoanType, l -> l.getCollateral().getFirst().getAmortisationPaymentAmount(), Loan::getPaymentFrequency)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, "1250.00", "Quarterly"),
                        tuple(CHILD_LOAN, null, null),
                        tuple(CHILD_LOAN, null, null)
                );
    }

    @Test
    void givenMultipleProducts_whenLoansReturned_thenOnlyChildrenHaveInterestRateAndInterestPaymentFrequency() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("6").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getInterestRate, Loan::getInterestPaymentFrequency)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, null, null),
                        tuple(CHILD_LOAN, "2.50000", "Semiannual"),
                        tuple(CHILD_LOAN, "1.20000", "Bimonthly")
                );
    }

}