package com.assignment.services;

import com.assignment.constants.TransferType;
import com.assignment.entities.Account;
import com.assignment.entities.Transaction;
import com.assignment.exceptions.AccountNotFoundException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static com.assignment.constants.TransactionConstants.ACCOUNTS;
import static com.assignment.constants.TransactionConstants.TRANSACTIONS;

@Service
public class ServiceImpl implements Services {

    private final HazelcastInstance hazelcastInstance;

    public ServiceImpl(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public ResponseEntity<String> insertTransaction(Account account, BigDecimal transferAmount) {
        IMap<Long, Account> accountIMap = hazelcastInstance.getMap(ACCOUNTS);
        IMap<Long, Transaction> transactionIMap = hazelcastInstance.getMap(TRANSACTIONS);

        // Check if account exists
        Account existingAccount = Optional.ofNullable(accountIMap.get(account.getAccountId()))
                .orElseThrow(() -> new AccountNotFoundException(String.format("Account with id %s not found", account.getAccountId())));

        // Create new transaction
        Transaction newTransaction = createTransaction(transferAmount);

        // Process transaction with Hazelcast lock
        try {
            accountIMap.lock(account.getAccountId());
            processTransaction(accountIMap, transactionIMap, account, newTransaction);
        } finally {
            accountIMap.unlock(account.getAccountId());
        }

        String body = String.format("Transaction entry created %s for the account number %s", newTransaction.getTransactionId(), account.getAccountId());
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    /**
     * Creates a new transaction with the specified transfer amount.
     *
     * @param transferAmount the amount to transfer
     * @return the new transaction object
     */
    private Transaction createTransaction(BigDecimal transferAmount) {
        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        Transaction newTransaction = new Transaction();
        newTransaction.setTransactionId(Long.valueOf(randomGenerator.nextInt(Integer.MAX_VALUE)));
        newTransaction.setTime(LocalDateTime.now());
        newTransaction.setTransferAmount(transferAmount);

        // Set the transaction type based on transfer amount using a switch statement
        TransferType type = switch (transferAmount.signum()) {
            case 1 -> TransferType.CREDIT;
            case -1 -> TransferType.DEBIT;
            default -> throw new IllegalArgumentException("Invalid transfer amount: " + transferAmount);
        };
        newTransaction.setType(type);

        return newTransaction;
    }

    /**
     * Processes the transaction by updating Hazelcast maps.
     *
     * @param accountIMap      the account map
     * @param transactionIMap  the transaction map
     * @param account          the account being updated
     * @param newTransaction   the new transaction
     */
    private void processTransaction(IMap<Long, Account> accountIMap, IMap<Long, Transaction> transactionIMap, Account account, Transaction newTransaction) {
        transactionIMap.put(newTransaction.getTransactionId(), newTransaction);
        account.getTransactions().add(newTransaction);
        accountIMap.put(account.getAccountId(), account);
    }
}
