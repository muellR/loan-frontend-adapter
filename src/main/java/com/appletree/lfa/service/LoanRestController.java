package com.appletree.lfa.service;

import com.appletree.lfa.business.LoanProvider;
import com.appletree.lfa.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoanRestController implements com.appletree.lfa.api.ServiceApiDelegate {

    private final LoanProvider loanProvider;

    public ResponseEntity<List<Loan>> serviceV1LoansByUserUserIdGet(String userId) {
        try {
            log.info("getting loans for userId={}", userId);
            long parsedUserId = Long.parseLong(userId);
            List<Loan> loans = loanProvider.provideLoans(parsedUserId);
            return ResponseEntity.ok(loans);
        } catch (NumberFormatException e) {
            log.warn("could not parse userId={}", userId);
            return ResponseEntity.status(400).body(null);
        } catch (Exception e) {
            log.error("could not get loans for userId={}", userId, e);
            return ResponseEntity.status(500).body(null);
        }
    }
}
