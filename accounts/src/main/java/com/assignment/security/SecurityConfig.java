package com.assignment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String EUREKA_URI = "/eureka/**";
    private static final String ACCOUNTS_URI = "/api/v1/accounts/**";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disabling CSRF, if not needed
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EUREKA_URI).hasRole("SUPERUSER")
                        .requestMatchers(ACCOUNTS_URI).hasRole("ADMIN")
                        .anyRequest().authenticated()  // All other requests require authentication
                )
                .formLogin(Customizer.withDefaults())  // Enable default form login
                .httpBasic(Customizer.withDefaults()); // Enable HTTP basic authentication

        return http.build();
    }
}
