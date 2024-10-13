package com.assignment.api;

import com.assignment.entities.Account;
import com.assignment.services.ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static com.assignment.constants.TransactionConstants.CORRELATION_ID;
import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBigDecimal;
import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServiceImpl serviceImpl;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Initialize MockMvc to mock the web layer
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testSaveTransaction_Success() throws Exception {
        // Given
        Account account = new Account();
        account.setAccountId(1L);
        account.setBalance(BigDecimal.valueOf(100));

        BigDecimal amount = BigDecimal.valueOf(50);

        // Mocking the service layer
        when(serviceImpl.insertTransaction(any(Account.class), any(BigDecimal.class)))  // Replaced anyBigDecimal with any(BigDecimal.class)
                .thenReturn(ResponseEntity.ok("Transaction successful"));

        // When & Then
        mockMvc.perform(post("/api/v1/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountId\": 1, \"balance\": 100}")
                        .param("amount", "50"))
                .andExpect(status().isOk());

        // Verify that the service method was called
        verify(serviceImpl, times(1)).insertTransaction(any(Account.class), any(BigDecimal.class));
    }




    @Test
    void testSaveTransaction_EmptyAccount() throws Exception {
        // Empty or missing account details
        mockMvc.perform(post("/api/v1/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("amount", "50"))
                .andExpect(status().isBadRequest());

        // Verify that the service method was never called because of validation failure
        verify(serviceImpl, never()).insertTransaction(any(Account.class), any(BigDecimal.class));
    }
}