package com.oauth2demo.basicsecurity.controller;

import com.oauth2demo.basicsecurity.model.dto.UserLoginInput;
import com.oauth2demo.basicsecurity.model.dto.UserLoginResponse;
import com.oauth2demo.basicsecurity.model.dto.UserRegisterInput;
import com.oauth2demo.basicsecurity.model.entity.UserEntity;
import com.oauth2demo.basicsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public UserEntity register(@RequestBody UserRegisterInput user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody UserLoginInput user) {
        return userService.login(user);
    }

}
