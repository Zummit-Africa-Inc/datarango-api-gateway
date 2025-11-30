package com.datarango.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
            org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/health", "/health/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/prometheus").permitAll()
                        .requestMatchers("/actuator/**").authenticated()
                        .requestMatchers("/api/users/auth/signup",
                                "/api/users/auth/signin",
                                "/api/users/auth/signout",
                                "/api/users/auth/google",
                                "/api/users/auth/github",
                                "/api/users/auth/forgot-password",
                                "/api/users/auth/reset-password",
                                "/api/users/auth/refresh")
                        .permitAll()
                        .requestMatchers("/api/users/login/**", "/api/users/oauth2/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }
}