package com.assignment.services;

import com.assignment.entities.Account;
import com.assignment.entities.Transaction;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceImplTest {

    @InjectMocks
    private ServiceImpl service;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Test
    void insertTransaction() {
        // Arrange
        Account account = new Account();
        account.setAccountType("Current Savings");
        account.setAccountId(1L);
        BigDecimal transferAmount = new BigDecimal(10);
        account.setBalance(transferAmount);

        // Mocking Hazelcast Maps
        IMap<Long, Account> accountMapMock = mock(IMap.class);
        IMap<Long, Transaction> transactionMapMock = mock(IMap.class);

        when(hazelcastInstance.<Long, Account>getMap("accounts")).thenReturn(accountMapMock);  // Cast to the expected types
        when(hazelcastInstance.<Long, Transaction>getMap("transactions")).thenReturn(transactionMapMock);

        when(accountMapMock.get(anyLong())).thenReturn(account);

        // Act
        ResponseEntity<String> response = service.insertTransaction(account, transferAmount);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
    }
}
