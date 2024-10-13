package com.assignment.api;

import com.assignment.dtos.CustomerDTO;
import com.assignment.services.ServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpStatus.*;

class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServiceImpl serviceImpl;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testOpenAccountSuccess() throws Exception {
        Long customerId = 123L;
        BigDecimal initialCredit = new BigDecimal("50.00");
        String accountId = UUID.randomUUID().toString();
        String successMessage = "New account " + accountId + " opened for customer " + customerId;

        // Mock serviceImpl.createAccount
        when(serviceImpl.createAccount(eq(customerId), eq(initialCredit), any(String.class)))
                .thenReturn(ResponseEntity.status(CREATED).body(successMessage));

        // Perform the POST request and validate
        mockMvc.perform(post("/api/v1/accounts/open/{customerId}", customerId)
                        .param("initialCredit", "50.00")
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andExpect(content().string(successMessage));

        // Verify that serviceImpl.createAccount was called
        verify(serviceImpl).createAccount(eq(customerId), eq(initialCredit), any(String.class));
    }

   /* @Test
    void testOpenAccountValidationFailure() throws Exception {
        Long customerId = 123L;

        // Perform POST request with invalid initialCredit and expect validation failure
        mockMvc.perform(post("/api/v1/accounts/open/{customerId}", customerId)
                        .param("initialCredit", "500000.00")  // Invalid credit (more than 4 digits)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }*/

    @Test
    void testGetCustomerDetailsSuccess() throws Exception {
        Long customerId = 123L;
        CustomerDTO mockCustomerDTO = new CustomerDTO("John", "Doe", null);  // Mocked DTO object

        // Mock serviceImpl.getUserAccounts
        when(serviceImpl.getUserAccounts(eq(customerId)))
                .thenReturn(ResponseEntity.ok(mockCustomerDTO));

        // Perform GET request and validate response
        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId)
                        .contentType("application/json"))
                .andExpect(status().isOk());

        // Verify that serviceImpl.getUserAccounts was called
        verify(serviceImpl).getUserAccounts(eq(customerId));
    }

    @Test
    void testGetCustomerDetailsNotFound() throws Exception {
        Long customerId = 999L;

        // Mock serviceImpl.getUserAccounts to return 404
        when(serviceImpl.getUserAccounts(eq(customerId)))
                .thenReturn(ResponseEntity.status(NOT_FOUND).build());

        // Perform GET request and expect 404 status
        mockMvc.perform(get("/api/v1/accounts/customer/{customerId}", customerId)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());

        // Verify that serviceImpl.getUserAccounts was called
        verify(serviceImpl).getUserAccounts(eq(customerId));
    }
}
