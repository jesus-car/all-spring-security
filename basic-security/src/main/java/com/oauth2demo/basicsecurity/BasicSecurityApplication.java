package com.oauth2demo.basicsecurity;

import com.oauth2demo.basicsecurity.model.entity.RoleEntity;
import com.oauth2demo.basicsecurity.model.entity.RoleEnum;
import com.oauth2demo.basicsecurity.model.entity.UserEntity;
import com.oauth2demo.basicsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
public class BasicSecurityApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(BasicSecurityApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        // Create roles
        RoleEntity roleAdmin = RoleEntity.builder()
                .role(RoleEnum.ADMIN)
                .build();

        RoleEntity roleUser = RoleEntity.builder()
                .role(RoleEnum.USER)
                .build();

        UserEntity user = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .email("admin@admin.com")
                .roles(Set.of(roleAdmin, roleUser))
                .build();

        userRepository.save(user);
    }
}
