package com.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * The main entry point for the Accounts Spring Boot application.
 */
@SpringBootApplication
@Slf4j
public class AccountsApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(AccountsApplication.class)
                .run(args);

        // Add a shutdown hook to perform any cleanup if necessary
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down AccountsApplication...");
            context.close();
        }));

        log.info("AccountsApplication started successfully.");
    }
}
