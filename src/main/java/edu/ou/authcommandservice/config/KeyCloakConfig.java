package edu.ou.authcommandservice.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakConfig {
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.config.username}")
    private String username;
    @Value("${keycloak.config.password}")
    private String password;
    @Value("${keycloak.config.clientId}")
    private String clientId;
    @Value("${keycloak.config.clientSecret}")
    private String clientSecret;

    @Bean("keyCloakInstance")
    public Keycloak getKeyCloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realm)
                .username(username)
                .password(password)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
