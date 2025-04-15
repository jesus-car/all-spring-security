package com.microservicecurso.keycloackresourceserver.controller;

import com.microservicecurso.keycloackresourceserver.config.dto.UserDTO;
import com.microservicecurso.keycloackresourceserver.service.KeycloakServiceImpl;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/keycloak/user")
@PreAuthorize("hasRole('spring_admin')")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakServiceImpl keycloakService;

    @GetMapping
    public List<UserRepresentation> getAllUsers() {
        return keycloakService.findAllUsers();
    }

    @GetMapping("/search/{username}")
    public List<UserRepresentation> searchUser(@PathVariable String username) {
        return keycloakService.findUserByUsername(username);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        String response = keycloakService.createUser(userDTO);
        return ResponseEntity.created(new URI("/keycloak/user/create")).body(response);
    }

    @PutMapping("/update/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@PathVariable String userId, @RequestBody UserDTO userDTO) {
        keycloakService.updateUser(userId, userDTO);
    }

    @DeleteMapping("/delete/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String userId) {
        keycloakService.deleteUser(userId);
    }

}
