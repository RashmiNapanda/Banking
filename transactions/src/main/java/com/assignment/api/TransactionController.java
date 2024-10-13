package com.assignment.api;

import com.assignment.entities.Account;
import com.assignment.services.ServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

import static com.assignment.constants.TransactionConstants.CORRELATION_ID;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final ServiceImpl serviceImpl;

    public TransactionController(ServiceImpl serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    /**
     * API method to create a new transaction.
     *
     * @param account the account for the transaction
     * @param amount  the amount for the transaction
     * @return ResponseEntity with transaction status
     */
    @PostMapping("/create")
    public ResponseEntity<String> saveTransaction(
            @RequestBody @Valid Account account,
            @RequestParam @Valid BigDecimal amount) {

        String traceId = Optional.ofNullable(MDC.get(CORRELATION_ID))
                .orElse("N/A");

        log.info("Request received to log a transaction entry for trace: {}", traceId);

        return serviceImpl.insertTransaction(account, amount);
    }
}
