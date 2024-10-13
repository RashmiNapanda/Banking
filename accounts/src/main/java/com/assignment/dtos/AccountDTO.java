package com.assignment.dtos;

import java.math.BigDecimal;
import java.util.List;

/**
 * A DTO (Data Transfer Object) representing account information.
 *
 * @param accountId    the unique identifier of the account
 * @param balance      the current balance of the account
 * @param accountType  the type of account (e.g., savings, checking)
 * @param transactions a list of transactions associated with this account
 */
public record AccountDTO(Long accountId,
                         BigDecimal balance,
                         String accountType,
                         List<TransactionDTO> transactions) {
}
