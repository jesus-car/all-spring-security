package com.msintegration.restaurantms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant")
public class TestController {

    @RequestMapping
    public String hello() {
        return "hello";
    }
}
