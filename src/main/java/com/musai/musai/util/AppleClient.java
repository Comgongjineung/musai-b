package com.musai.musai.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
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

        log.info("[AppleClient] 요청 시작 - code: {}", code);
        log.debug("[AppleClient] clientId: {}", clientId);
        log.debug("[AppleClient] redirectUri: {}", redirectUri);

        try {
            AppleTokenResponse response = WebClient.create("https://appleid.apple.com")
                    .post()
                    .uri("/auth/token")
                    .body(BodyInserters.fromFormData("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("code", code)
                            .with("grant_type", "authorization_code")
                            .with("redirect_uri", redirectUri))
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            clientResponse -> {
                                log.error("[AppleClient] 애플 토큰 요청 실패 - status: {}", clientResponse.statusCode());
                                return clientResponse.bodyToMono(String.class)
                                        .doOnNext(errorBody ->
                                                log.error("[AppleClient] 에러 응답 내용: {}", errorBody))
                                        .then(Mono.error(new RuntimeException("Apple token request failed")));
                            }
                    )
                    .bodyToMono(AppleTokenResponse.class)
                    .block();

            log.info("[AppleClient] 애플 토큰 요청 성공 ✅");
            log.debug("[AppleClient] 응답 내용: {}", response);

            return response;

        } catch (WebClientResponseException e) {
            log.error("[AppleClient] WebClientResponseException 발생 - status: {}, body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            log.error("[AppleClient] 알 수 없는 예외 발생", e);
            throw e;
        }
    }
}
