package com.assignment.entities;


import com.assignment.constants.TransferType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a transaction entity with transaction details.
 */
@Data
@AllArgsConstructor
public class Transaction {
    private Long transactionId;
    private BigDecimal transferAmount;
    private LocalDateTime time;
    private TransferType type;
}
