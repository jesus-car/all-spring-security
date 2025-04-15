package com.oauth2demo.securityserver.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @GetMapping("/user")
    public String readUser(Authentication authentication) {
        return "Hello " + authentication.getName() + ", you have access to this resource!" + "\n" +
                "Roles: " + authentication.getAuthorities() + "\n" +
                "Client ID: " + authentication.getCredentials();
    }

    @PostMapping("/user")
    public String writeUser(Authentication authentication) {
        return "Hello " + authentication.getName() + ", you have access to this resource!" + "\n" +
                "Roles: " + authentication.getAuthorities() + "\n" +
                "Client ID: " + authentication.getCredentials();
    }
}
