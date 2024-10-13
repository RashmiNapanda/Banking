package com.assignment.dtos;

import com.assignment.constants.TransferType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A DTO (Data Transfer Object) representing transaction details.
 *
 * @param amount          the transaction amount
 * @param time            the time of the transaction
 * @param transactionType the type of transaction (credit or debit)
 */
public record TransactionDTO(BigDecimal amount, LocalDateTime time, TransferType transactionType) {
}
