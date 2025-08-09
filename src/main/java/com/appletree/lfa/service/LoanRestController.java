package com.appletree.lfa.service;

import com.appletree.lfa.data.financingobject.FinancingObjectRepository;
import com.appletree.lfa.model.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoanRestController implements com.appletree.lfa.api.ServiceApiDelegate {

    private final FinancingObjectRepository financingObjectRepository;

    public ResponseEntity<List<Loan>> serviceV1LoansByUserUserIdGet(String userId) {
        try {
            List<Loan> loans = new ArrayList<>();
            System.out.println(financingObjectRepository.findFinancingObjectByUserId(Long.parseLong(userId)));
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
