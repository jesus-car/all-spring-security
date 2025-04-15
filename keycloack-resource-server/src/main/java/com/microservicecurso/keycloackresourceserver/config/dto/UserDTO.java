package com.microservicecurso.keycloackresourceserver.config.dto;

import lombok.Builder;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Set;

@Builder
public record UserDTO(
        String username,
        String email,
        String password,
        String firstName,
        String lastName,
        Set<String> roles) {

    public static UserRepresentation toUserRepresentation(UserDTO userDTO) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setFirstName(userDTO.firstName());
        userRepresentation.setLastName(userDTO.lastName());
        userRepresentation.setEmail(userDTO.email());
        userRepresentation.setUsername(userDTO.username());
        userRepresentation.setEnabled(true);
        // Lo ideal es mandar un correo de confirmacion al usuario
        // Si lo verifica con el link que enviamos, se activa el usuario
        userRepresentation.setEmailVerified(true);

        return userRepresentation;
    }
}
