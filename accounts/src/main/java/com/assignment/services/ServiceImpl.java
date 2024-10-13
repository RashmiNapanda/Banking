package com.assignment.services;

import com.assignment.dtos.CustomerDTO;
import com.assignment.entities.Account;
import com.assignment.entities.Customers;
import com.assignment.exceptions.UserNotFoundException;
import com.assignment.mappers.CustomerMapper;
import com.assignment.utils.Utility;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.random.RandomGenerator;

import static com.assignment.constants.AccountConstants.*;

@Service
@Slf4j
public class ServiceImpl implements Services {

    private final HazelcastInstance hazelcastInstance;
    private final DiscoveryClient discoveryClient;
    private final Utility utility;
    private final CustomerMapper mapper = Mappers.getMapper(CustomerMapper.class);

    @Value("${transaction.api.path}")
    private String apiPath;

    @Value("${transaction.api.endpoint}")
    private String apiEndpoint;

    @Value("${transaction.api.name}")
    private String apiName;

    @Value("${api.user.name}")
    private String apiUser;

    @Value("${api.user.password}")
    private String apiPass;

    private static final String ACCOUNT_TYPE = "Current Account";

    public ServiceImpl(DiscoveryClient discoveryClient, HazelcastInstance hazelcastInstance, Utility utility) {
        this.discoveryClient = discoveryClient;
        this.hazelcastInstance = hazelcastInstance;
        this.utility = utility;
    }

    /**
     * Opens a new account for an existing customer.
     *
     * @param customerId    the ID of the customer
     * @param initialCredit the initial credit of the account
     * @param traceId       the trace ID for the transaction
     * @return a ResponseEntity with a success message
     */
    @Override
    public ResponseEntity<String> createAccount(Long customerId, BigDecimal initialCredit, String traceId) {
        IMap<Long, Customers> customersMap = hazelcastInstance.getMap(CUSTOMERS);
        IMap<Long, Account> accountsMap = hazelcastInstance.getMap(ACCOUNTS);

        Customers customer = Optional.ofNullable(customersMap.get(customerId))
                .orElseThrow(() -> new UserNotFoundException(String.format("Customer with id %s not found", customerId)));

        Account account = createNewAccount(initialCredit);

        customer.getAccounts().add(account);
        accountsMap.put(account.getAccountId(), account);

        if (initialCredit.compareTo(BigDecimal.ZERO) > 0) {
            WebClient client = utility.fetchProxyDetails(discoveryClient, apiName, apiUser, apiPass, apiPath);
            utility.createTransaction(account, initialCredit, client, apiEndpoint, traceId);
        }

        customersMap.put(customerId, customer);

        String body = String.format("New Account %s is opened for the customer %s", account.getAccountId(), customerId);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    /**
     * Fetches the user information from cache maps.
     *
     * @param customerId the ID of the customer
     * @return a ResponseEntity with the customer details
     */
    @Override
    public ResponseEntity<CustomerDTO> getUserAccounts(Long customerId) {
        IMap<Long, Customers> customersMap = hazelcastInstance.getMap(CUSTOMERS);
        IMap<Long, Account> accountsMap = hazelcastInstance.getMap(ACCOUNTS);

        Customers customer = Optional.ofNullable(customersMap.get(customerId))
                .orElseThrow(() -> new UserNotFoundException(String.format("Customer with id %s not found", customerId)));

        customer.getAccounts().forEach(account ->
                account.setTransactions(accountsMap.get(account.getAccountId()).getTransactions())
        );

        CustomerDTO customerDTO = mapper.toCustomerDTO(customer);
        return new ResponseEntity<>(customerDTO, HttpStatus.OK);
    }

    /**
     * Creates a new Account object with the provided initial credit.
     *
     * @param initialCredit the initial credit for the new account
     * @return a new Account object
     */
    private Account createNewAccount(BigDecimal initialCredit) {
        RandomGenerator randomGenerator = RandomGenerator.getDefault();
        Account account = new Account();
        account.setAccountId(Long.valueOf(randomGenerator.nextInt(Integer.MAX_VALUE)));
        account.setBalance(initialCredit);
        account.setAccountType(ACCOUNT_TYPE);
        return account;
    }
}
