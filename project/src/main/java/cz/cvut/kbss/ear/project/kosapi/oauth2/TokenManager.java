package cz.cvut.kbss.ear.project.kosapi.oauth2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

// The following code for authorizing using OAuth2 is taken from following website
// https://laurspilca.com/consuming-an-endpoint-protected-by-an-oauth-2-resource-server-from-a-spring-boot-service
@Component
public class TokenManager {

    @Value("${client.registration.name}")
    private String clientRegistrationName;

    @Value("${spring.security.oauth2.client.registration.app.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.app.client-secret}")
    private String clientSecret;


    private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

    public TokenManager(
            AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager) {
        this.authorizedClientServiceAndManager = authorizedClientManager;
    }

    public String getAccessToken() {
        OAuth2AuthorizeRequest authorizeRequest =
                OAuth2AuthorizeRequest
                        .withClientRegistrationId(clientRegistrationName)
                        .principal(clientId)
                        .build();

        OAuth2AuthorizedClient authorizedClient =
                this.authorizedClientServiceAndManager
                        .authorize(authorizeRequest);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        return accessToken.getTokenValue();
    }

}
