package com.appletree.lfa.api;

import com.appletree.lfa.business.UserService;
import com.appletree.lfa.model.Loan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoanRestController implements com.appletree.lfa.api.ServiceApiDelegate {

    private final UserService userService;

    @Override
    public ResponseEntity<List<Loan>> serviceV1LoansByUserUserIdGet(String userId) {
        try {
            log.debug("parsing userId={}", userId);
            long parsedUserId = parseLong(userId);
            log.info("getting userLoans for userId={}", userId);
            return ResponseEntity.ok(userService.getLoans(parsedUserId));
        } catch (Exception e) {
            log.error("could not get loans for userId={}", userId, e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/service/v1/userIds")
    public ResponseEntity<List<String>> serviceV1UserIdsGet() {
        return ResponseEntity.ok(userService.getUserIds());
    }
}
