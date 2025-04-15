package com.oauth2demo.basicsecurity.service;

import com.oauth2demo.basicsecurity.model.dto.UserLoginInput;
import com.oauth2demo.basicsecurity.model.dto.UserLoginResponse;
import com.oauth2demo.basicsecurity.model.dto.UserRegisterInput;
import com.oauth2demo.basicsecurity.model.entity.RoleEntity;
import com.oauth2demo.basicsecurity.model.entity.RoleEnum;
import com.oauth2demo.basicsecurity.model.entity.UserEntity;
import com.oauth2demo.basicsecurity.repository.UserRepository;
import com.oauth2demo.basicsecurity.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserLoginResponse login(UserLoginInput user) {

        // Se encarga de autenticar al usuario
        // Si el usuario no existe o la contraseña es incorrecta, lanzará una excepción
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // Get the authenticated user
        UserEntity authenticatedUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtils.generateAccessToken(authenticatedUser);

        return UserLoginResponse.builder()
                .token(accessToken)
                .username(authenticatedUser.getUsername())
                .build();
    }

    @Transactional
    public UserEntity register(UserRegisterInput user) {

        // Check if the user already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // Check if the email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // assign roles to the user
        UserEntity userEntity = user.toEntity();
        RoleEntity role = RoleEntity.builder()
                .role(RoleEnum.USER)
                .build();
        userEntity.setRoles(Set.of(role));
        // Encode the password
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(userEntity);
    }
}
