package com.oauth2demo.securityclient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public String home() {
        return "Welcome to the OAuth2 Client!";
    }

    @GetMapping("/helloSecured")
    public String helloSecured() {
        return "Hello, this is a secured endpoint!";
    }

    @GetMapping("/authorized")
    public Map<String, String> authorized(@RequestParam String code) {
        return Map.of(
                "message", "Authorization code received",
                "authorizationCode", code
        );
    }
}
