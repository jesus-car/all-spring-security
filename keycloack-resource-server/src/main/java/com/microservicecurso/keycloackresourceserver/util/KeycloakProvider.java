package com.microservicecurso.keycloackresourceserver.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeycloakProvider {

    // Todos los datos que vienen del Auth Server y nos solicita Keycloak
    // para poder conectarnos a su API
    // URL del Auth Server, keycloak en este caso
    private static final String SERVER_URL = "http://localhost:8181/";
    private static final String REALM_NAME = "spring-dev";
    private static final String REALM_MASTER = "master";
    // Es un cliente del realm 'master'
    private static final String ADMIN_CLI= "admin-cli";
    // Son los datos que creamos en el Auth Server al crear el servidor(al comienzo)
    private static final String USER_CONSOLE = "admin";
    private static final String PASSWORD_CONSOLE = "admin";
    private static final String CLIENT_SECRET = "WbZpYSJCuTNFgoEJGPTGnZNc99gS4koh";


    public static RealmResource getRealmResource() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                // Nos permite acceder a la API de Keycloak
                // podemos crear realms, usuarios, etc
                .realm(REALM_MASTER)
                // Es un cliente del realm 'master'
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        // Tamaño de la pool
                        .connectionPoolSize(10)
                        .build())
                .build();

        return keycloak.realm(REALM_NAME);
    }

    /**
     * Returns a UsersResource that allows access and management of users
     * within the specified realm.
     *
     * @return UsersResource for interacting with users in the realm
     */
    public static UsersResource getUserResource() {
        // Obtain the realm resource from Keycloak
        RealmResource realmResource = getRealmResource();
        // Access and return the users resource
        return realmResource.users();
    }

}
