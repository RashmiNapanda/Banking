package com.assignment.utils;

import com.assignment.entities.Account;
import com.assignment.exceptions.TransactionException;
import com.assignment.exceptions.URINotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.assignment.constants.AccountConstants.AMOUNT;
import static com.assignment.constants.AccountConstants.CORRELATION_ID;

@Slf4j
@Component
public class Utility {

    /**
     * Fetch the API details from Service Registry.
     *
     * @param discoveryClient the discovery client
     * @param appName         the application name
     * @param apiUser         the API user
     * @param apiPass         the API password
     * @param apiBasePath     the base path for the API
     * @return WebClient configured with the service instance URL
     */
    public WebClient fetchProxyDetails(DiscoveryClient discoveryClient, String appName, String apiUser,
                                       String apiPass, String apiBasePath) {
        String hostPort = discoveryClient.getInstances(appName)
                .stream()
                .map(serviceInstance -> serviceInstance.getUri().toString())
                .findAny()
                .orElseThrow(() -> new URINotFoundException("No URI available for app: " + appName));

        return buildWebClient(hostPort + apiBasePath, apiUser, apiPass);
    }

    /**
     * Builds and returns a WebClient with basic authentication.
     *
     * @param url     the base URL for the WebClient
     * @param apiUser the API user
     * @param apiPass the API password
     * @return WebClient configured with basic authentication
     */
    private WebClient buildWebClient(String url, String apiUser, String apiPass) {
        String encodedAuth = Base64.getEncoder()
                .encodeToString((apiUser + ":" + apiPass).getBytes(StandardCharsets.UTF_8));

        return WebClient.builder()
                .baseUrl(url)
                .defaultHeaders(headers -> headers.setBasicAuth(encodedAuth))
                .build();
    }

    /**
     * Sends a transaction creation request to another microservice.
     *
     * @param createdAccount     the created account
     * @param creditAmount       the credit amount
     * @param transactionClient  the WebClient for transaction service
     * @param apiEndpoint        the API endpoint for transaction creation
     * @param traceId            the correlation ID for tracing
     */
    public void createTransaction(Account createdAccount, BigDecimal creditAmount, WebClient transactionClient,
                                  String apiEndpoint, String traceId) {

        transactionClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(apiEndpoint)
                        .queryParam(AMOUNT, creditAmount)
                        .build())
                .headers(httpHeaders -> httpHeaders.set(CORRELATION_ID, traceId))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(createdAccount)
                .exchangeToMono(response -> Mono.just(response.statusCode().value()))
                .doOnError(e -> handleTransactionError(e, createdAccount.getAccountId()))
                .blockOptional()
                .ifPresent(responseCode -> handleTransactionResponse(String.valueOf(responseCode), traceId, createdAccount));
    }

    /**
     * Handles any errors during the transaction creation process.
     *
     * @param error      the exception thrown during transaction creation
     * @param accountId  the account ID associated with the transaction
     */
    private void handleTransactionError(Throwable error, Long accountId) {
        log.error("Error during transaction creation for account {}: {}", accountId, error.getMessage());
        throw new TransactionException("Technical error while creating transaction for account " + accountId);
    }

    /**
     * Handles the response from the transaction creation API.
     *
     * @param responseCode   the response status code
     * @param traceId        the trace ID
     * @param createdAccount the account for which the transaction was created
     */
    private void handleTransactionResponse(String responseCode, String traceId, Account createdAccount) {
        if ("201".equals(responseCode)) {
            log.info("Transaction successfully created for requestId: {}", traceId);
        } else {
            log.error("Error during transaction insertion for requestId: {}", traceId);
            throw new TransactionException("Error while transaction insertion for account " + createdAccount.getAccountId());
        }
    }
}
