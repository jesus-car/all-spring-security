package com.oauth2demo.securityclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/authorized").permitAll()
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/custom-oidc-client"))
                .oauth2Client(Customizer.withDefaults())
                .build();
    }
}
