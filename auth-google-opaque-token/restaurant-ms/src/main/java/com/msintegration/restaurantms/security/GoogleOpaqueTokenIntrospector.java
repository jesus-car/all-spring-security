package com.msintegration.restaurantms.security;

import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GoogleOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        try {
            String uri = UriComponentsBuilder
                    .fromUriString("https://oauth2.googleapis.com/tokeninfo")
                    .queryParam("access_token", token)
                    .toUriString();

            Map<String, Object> attributes = restTemplate.getForObject(uri, Map.class);

            // Convertir los timestamps de String a Instant
            convertTimestampAttributes(attributes);

            return new DefaultOAuth2AuthenticatedPrincipal(attributes, null);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "Token introspection failed", ex);
        }
    }

    private void convertTimestampAttributes(Map<String, Object> attributes) {
        // Lista de atributos que deben ser convertidos a Instant
        List<String> timestampAttributes = Arrays.asList("exp", "iat", "nbf");

        for (String attribute : timestampAttributes) {
            if (attributes.containsKey(attribute)) {
                Object value = attributes.get(attribute);
                if (value instanceof String) {
                    try {
                        // Convertir de String a Long y luego a Instant
                        long timestamp = Long.parseLong((String) value);
                        attributes.put(attribute, Instant.ofEpochSecond(timestamp));
                    } catch (NumberFormatException e) {
                        // Si no se puede convertir, eliminar el atributo para evitar errores
                        attributes.remove(attribute);
                    }
                } else if (value instanceof Number) {
                    // Si ya es un número, convertir directamente a Instant
                    long timestamp = ((Number) value).longValue();
                    attributes.put(attribute, Instant.ofEpochSecond(timestamp));
                }
            }
        }
    }
}