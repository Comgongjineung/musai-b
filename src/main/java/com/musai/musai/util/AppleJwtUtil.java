package com.musai.musai.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class AppleJwtUtil {

    public String createClientSecret(String clientId, String teamId, String keyId, String privateKeyPath) {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);

            Instant now = Instant.now();
            return Jwts.builder()
                    .setHeaderParam("kid", keyId)
                    .setIssuer(teamId)
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusSeconds(60 * 60 * 24))) // 1일
                    .setAudience("https://appleid.apple.com")
                    .setSubject(clientId)
                    .signWith(privateKey, SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Apple client secret", e);
        }
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path.replace("classpath:", ""));
        String key = new String(is.readAllBytes())
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("EC").generatePrivate(spec);
    }

    public AppleUserInfo verifyIdentityToken(String idToken) {
        try {
            String[] parts = idToken.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> payload = new ObjectMapper().readValue(payloadJson, Map.class);

            String email = (String) payload.get("email");
            String sub = (String) payload.get("sub");
            String name = (String) payload.getOrDefault("name", "AppleUser");

            return new AppleUserInfo(email, sub, name);

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify identity token", e);
        }
    }
}
