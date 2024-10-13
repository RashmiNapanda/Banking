package com.assignment.constants;

/**
 * Defines constants related to transactions.
 */
public final class TransactionConstants {

    // Prevent instantiation
    private TransactionConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String CORRELATION_ID = "X-Correlation-Id";
    public static final String ACCOUNTS = "accounts";
    public static final String CUSTOMERS = "customers";
    public static final String TRANSACTIONS = "transactions";
}
