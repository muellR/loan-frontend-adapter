package com.appletree.lfa.service;

import com.appletree.lfa.data.financingobject.FinancingObject;
import com.appletree.lfa.data.financingobject.FinancingObjectRepository;
import com.appletree.lfa.data.limit.Limit;
import com.appletree.lfa.data.limit.LimitRepository;
import com.appletree.lfa.data.product.Product;
import com.appletree.lfa.data.product.ProductRepository;
import com.appletree.lfa.data.shared.ResourceDataLoader;
import com.appletree.lfa.model.Loan;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;

import static com.appletree.lfa.model.Loan.LoanTypeEnum.CHILD_LOAN;
import static com.appletree.lfa.model.Loan.LoanTypeEnum.PARENT_LOAN;
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
    public void givenMultipleProducts_whenLoansReturned_thenParentOutstandingAmountIsSumOfChildren() {
        List<Loan> loans = loanRestController.serviceV1LoansByUserUserIdGet("1").getBody();

        assertThat(loans).extracting(Loan::getLoanType, Loan::getOutstandingAmount)
                .containsExactlyInAnyOrder(
                        tuple(PARENT_LOAN, "205000.00"),
                        tuple(CHILD_LOAN, "120000.00"),
                        tuple(CHILD_LOAN, "85000.00")
                );
    }
}