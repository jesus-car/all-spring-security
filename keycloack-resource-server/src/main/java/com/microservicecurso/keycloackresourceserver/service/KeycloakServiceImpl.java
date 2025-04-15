package com.microservicecurso.keycloackresourceserver.service;

import com.microservicecurso.keycloackresourceserver.config.dto.UserDTO;
import com.microservicecurso.keycloackresourceserver.util.KeycloakProvider;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {

    /**
     * Metodo para listar todos los usuarios de Keycloak
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> findAllUsers(){
        return KeycloakProvider.getRealmResource()
                .users()
                .list();
    }


    /**
     * Metodo para buscar un usuario por su username
     * @return List<UserRepresentation>
     */
    @Override
    public List<UserRepresentation> findUserByUsername(String username) {
        return KeycloakProvider.getRealmResource()
                .users()
                // El booleano true indica que el username es case sensitive, osea es el nombre exacto
                .searchByUsername(username, true);
    }


    /**
     * Metodo para crear un usuario en keycloak
     * @return String
     */
    @Override
    public String createUser(@NonNull UserDTO userDTO) {

        int status = 0;
        UsersResource usersResource = KeycloakProvider.getUserResource();
        // Se esta guardando el usuario sin password
        Response response = usersResource.create(UserDTO.toUserRepresentation(userDTO));

        status = response.getStatus();

        // Si el usuario se creo con exito se le asigna el password
        // de lo contrario se retorna un error
        switch (status) {
            case 201 -> {
                // El userId viene en el path del response, por lo que hay que extraerlo
                String path = response.getLocation().getPath();
                String userId = path.substring(path.lastIndexOf("/") + 1);

                // Construimos el password
                CredentialRepresentation credentialRepresentation = createPassword(userDTO);
                // Reseteamos la password del usuario con la nueva
                usersResource.get(userId).resetPassword(credentialRepresentation);
                assignRoles(userDTO, userId);

                return "User created successfully!!";

            }
            case 409 -> {
                log.error("User exist already!");
                return "User exist already!";
            }
            default -> {
                log.error("Error creating user, please contact with the administrator.");
                String responseString = response.readEntity(String.class);

                log.info("Status: " + status);
                log.info("Response: " + responseString);
                return "Error creating user, please contact with the administrator.";
            }
        }
    }

    private static void assignRoles(UserDTO userDTO, String userId) {
        RealmResource realmResource = KeycloakProvider.getRealmResource();
        List<RoleRepresentation> rolesRepresentation = null;

        // Si no se especifica roles se le asigna el rol de user por defecto
        if (userDTO.roles() == null || userDTO.roles().isEmpty()) {
            rolesRepresentation = List.of(realmResource.roles().get("user").toRepresentation());
            // Si se especifican roles se les asigna el rol correspondiente
        } else {
            // Obtenemos todos los roles de keycloak
            // y luego filtramos los que coincidan con los roles del usuario que se quiere crear
            rolesRepresentation = realmResource.roles()
                    .list()
                    .stream()
                    .filter(role -> userDTO.roles()
                            .stream()
                            .anyMatch(roleName -> roleName.equalsIgnoreCase(role.getName())))
                    .toList();
        }

        realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentation);
    }

    /**
     * Metodo para borrar un usuario en keycloak
     * @return void
     */
    @Override
    public void deleteUser(String userId){
        KeycloakProvider.getUserResource()
                .get(userId)
                .remove();
    }


    /**
     * Metodo para actualizar un usuario en keycloak
     * @return void
     */
    @Override
    public void updateUser(String userId, @NonNull UserDTO userDTO){

        CredentialRepresentation credentialRepresentation = createPassword(userDTO);

        // Armamos el user y le asignamos el password
        UserRepresentation user = UserDTO.toUserRepresentation(userDTO);
        user.setCredentials(Collections.singletonList(credentialRepresentation));

        // Actualizamos el usuario en keycloak
        UserResource usersResource = KeycloakProvider.getUserResource().get(userId);
        usersResource.update(user);
    }

    /**
     * Construye un objeto de tipo CredentialRepresentation a partir de un objeto
     * UserDTO, el cual se utiliza para establecer la contrase a de un usuario en
     * Keycloak.
     *
     * @param userDTO El objeto UserDTO que contiene la informaci n del usuario,
     *                incluyendo la contrase a.
     * @return Un objeto CredentialRepresentation que se puede utilizar para
     *         establecer la contrase a de un usuario en Keycloak.
     */
    private static CredentialRepresentation createPassword(UserDTO userDTO) {
        // Construimos el password
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userDTO.password());

        return credentialRepresentation;
    }
}
