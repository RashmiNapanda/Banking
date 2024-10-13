package com.assignment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String EUREKA_URI = "/eureka/**"; // Use constant for better readability

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection as it's not required here
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EUREKA_URI).hasRole("SUPERUSER") // Superuser role for Eureka endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(Customizer.withDefaults()) // Default form login configuration
                .httpBasic(Customizer.withDefaults()); // Default HTTP basic authentication configuration
        return http.build();
    }
}
