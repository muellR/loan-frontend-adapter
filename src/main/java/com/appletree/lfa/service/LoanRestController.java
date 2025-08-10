package com.appletree.lfa.service;

import com.appletree.lfa.business.UserLoanProvider;
import com.appletree.lfa.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoanRestController implements com.appletree.lfa.api.ServiceApiDelegate {

    private final UserLoanProvider userLoanProvider;

    public ResponseEntity<List<Loan>> serviceV1LoansByUserUserIdGet(String userId) {
        try {
            log.debug("parsing userId={}", userId);
            long parsedUserId = Long.parseLong(userId);
            log.info("getting userLoans for userId={}", userId);
            return ResponseEntity.ok(userLoanProvider.provideLoans(parsedUserId));
        } catch (NumberFormatException e) {
            log.warn("could not parse userId={}", userId);
            return new ResponseEntity<>(BAD_REQUEST);
        } catch (Exception e) {
            log.error("could not get loans for userId={}", userId, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }
}
