package com.assignment.api;

import com.assignment.dtos.CustomerDTO;
import com.assignment.services.ServiceImpl;
import jakarta.validation.constraints.Digits;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@Validated
public class AccountController {

    private final ServiceImpl serviceImpl;

    public AccountController(ServiceImpl serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    /**
     * API method to open a new account
     *
     * @param customerId    The customer ID
     * @param initialCredit The initial credit for the account, which must have 4 digits
     *                      and 2 decimal places
     * @return ResponseEntity with status and message
     */
    @PostMapping("/open/{customerId}")
    public ResponseEntity<String> openAccount(
            @PathVariable Long customerId,
            @Digits(integer = 4, fraction = 2, message = "Initial credit can have 4 digits and 2 decimal places")
            @RequestParam BigDecimal initialCredit) {

        String accountId = UUID.randomUUID().toString();
        return serviceImpl.createAccount(customerId, initialCredit, accountId);
    }

    /**
     * API method to get customer account details and transactions
     *
     * @param customerId The customer ID
     * @return ResponseEntity containing CustomerDTO with account and transaction
     *         details
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerDetails(@PathVariable Long customerId) {
        return serviceImpl.getUserAccounts(customerId);
    }
}
