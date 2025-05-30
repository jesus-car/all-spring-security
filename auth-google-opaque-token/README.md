# Autenticación con Google OAuth2 Opaque Token

Este proyecto demuestra cómo implementar un sistema de autenticación y autorización utilizando los tokens opacos (opaque tokens) de Google OAuth2 en una arquitectura de microservicios con API Gateway.

## Descripción General

Este es un proyecto de ejemplo que muestra cómo acceder a servicios a través de un API Gateway utilizando el servicio OAuth2 de Google y validando el token opaco que proporciona. Aunque en entornos de producción se recomienda utilizar tokens JWT por sus ventajas en términos de validación local y rendimiento, este ejercicio demuestra un enfoque funcional con tokens opacos.

## Arquitectura del Proyecto

El proyecto está compuesto por tres componentes principales:

1. **Eureka Server**: Servidor de descubrimiento de servicios
2. **API Gateway**: Punto de entrada único para todas las solicitudes
3. **Restaurant Microservice**: Servicio de ejemplo que requiere autenticación

## Flujo de Autenticación

1. El usuario se autentica a través del API Gateway utilizando Google OAuth2
2. Google proporciona un token opaco de acceso
3. El API Gateway reenvía este token a los microservicios en las solicitudes
4. El microservicio valida el token mediante introspección

## Validación del Token Opaco de Google

A diferencia de los tokens JWT, los tokens opacos no contienen información decodificable localmente y requieren validación mediante introspección en el servidor de autorización. En este proyecto, la validación se realiza de la siguiente manera:

### Implementación de la Introspección

El proyecto implementa un `GoogleOpaqueTokenIntrospector` personalizado que:

1. Recibe el token opaco de Google
2. Realiza una solicitud a la API de introspección de Google (`https://oauth2.googleapis.com/tokeninfo`)
3. Valida la respuesta y extrae la información del usuario
4. Convierte los atributos de timestamp (exp, iat, nbf) a formato Instant para su correcta interpretación

```java
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
    
    // Método para convertir atributos de timestamp
    // ...
}
```

### Configuración del Microservicio

El microservicio está configurado para utilizar el introspector personalizado:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http, OpaqueTokenIntrospector introspector) throws Exception {
    return http
            .authorizeHttpRequests(auth -> auth
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .opaqueToken(token -> token
                            .introspector(introspector)
                    )
            )
            .build();
}
```

### Configuración del API Gateway

El API Gateway está configurado para:
1. Autenticar usuarios con Google OAuth2
2. Reenviar los tokens a los microservicios (Token Relay)

## Ventajas y Desventajas

### Ventajas de este Enfoque
- Implementación sencilla
- Validación centralizada en el servidor de autorización
- Revocación inmediata de tokens

### Desventajas
- Mayor latencia debido a la necesidad de validación remota
- Mayor carga en el servidor de autorización
- Dependencia de la disponibilidad del servidor de autorización

## Configuración y Ejecución

1. Configurar las credenciales de OAuth2 en los archivos `application.properties`
2. Iniciar el Eureka Server
3. Iniciar el API Gateway
4. Iniciar el Microservicio Restaurant

## Recomendaciones para Entornos de Producción

Para entornos de producción, se recomienda:
1. Utilizar tokens JWT en lugar de tokens opacos para mejorar el rendimiento
2. Implementar caché de tokens para reducir la carga en el servidor de autorización
3. Configurar tiempos de expiración adecuados para los tokens
4. Utilizar HTTPS para todas las comunicaciones