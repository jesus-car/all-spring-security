package com.oauth2demo.basicsecurity.model.dto;

import com.oauth2demo.basicsecurity.model.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterInput {
    private String username;
    private String password;
    private String email;

    public  UserEntity toEntity() {
        return UserEntity.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();
    }
}
