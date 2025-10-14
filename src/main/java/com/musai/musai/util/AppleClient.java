package com.musai.musai.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AppleClient {

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.private-key-path}")
    private String privateKeyPath;

    @Value("${apple.redirect-uri}")
    private String redirectUri;

    private final AppleJwtUtil appleJwtUtil;

    public AppleTokenResponse getToken(String code) {
        String clientSecret = appleJwtUtil.createClientSecret(clientId, teamId, keyId, privateKeyPath);

        return WebClient.create("https://appleid.apple.com")
                .post()
                .uri("/auth/token")
                .body(BodyInserters.fromFormData("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("code", code)
                        .with("grant_type", "authorization_code")
                        .with("redirect_uri", redirectUri))
                .retrieve()
                .bodyToMono(AppleTokenResponse.class)
                .block();
    }
}
