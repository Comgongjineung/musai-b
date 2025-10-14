package com.musai.musai.controller.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.musai.musai.entity.user.User;
import com.musai.musai.jwt.JwtTokenProvider;
import com.musai.musai.service.user.UserService;
import com.musai.musai.util.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "회원가입", description = "회원가입 관련 API")
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    private final AppleClient appleClient;
    private final AppleJwtUtil appleJwtUtil;

    @Value("${google.client-id.android}")
    private String androidClientId;

    @Value("${google.client-id.ios}")
    private String iosClientId;

    @Value("${google.client-id.web}")
    private String webClientId;

    @Operation(summary = "구글 회원가입", description = "구글 계정으로 로그인 및 회원가입을 진행합니다.")
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");
        log.info("[GoogleLogin] 요청 수신 - idToken: {}", idTokenString != null ? "받음" : "없음");

        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance())
                    .setAudience(List.of(androidClientId, iosClientId, webClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                log.warn("[GoogleLogin] 유효하지 않은 구글 토큰");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            log.debug("[GoogleLogin] 구글 로그인 성공 - email: {}", payload.getEmail());

            User user = userService.findOrCreateUserFromGoogle(payload);
            String accessToken = jwtTokenProvider.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("user", user);

            log.info("[GoogleLogin] 로그인 성공 - userId: {}", user.getUserId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[GoogleLogin] 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @Operation(summary = "애플 회원가입", description = "애플 계정으로 로그인 및 회원가입을 진행합니다.")
    @PostMapping("/apple")
    public ResponseEntity<?> appleLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        log.info("[AppleLogin] 요청 수신 - code: {}", code);

        try {
            // 1️⃣ code → access_token 교환
            AppleTokenResponse tokenResponse = appleClient.getToken(code);
            log.debug("[AppleLogin] 애플 토큰 응답: {}", tokenResponse);

            // 2️⃣ id_token 검증
            AppleUserInfo userInfo = appleJwtUtil.verifyIdentityToken(tokenResponse.getIdToken());
            log.debug("[AppleLogin] 사용자 정보: {}", userInfo);

            // 3️⃣ DB 유저 조회/생성
            User user = userService.findOrCreateUserFromApple(
                    userInfo.getEmail(),
                    userInfo.getSub(),
                    userInfo.getName(),
                    null
            );
            log.info("[AppleLogin] 유저 처리 완료 - userId: {}, email: {}", user.getUserId(), user.getEmail());

            // 4️⃣ JWT 발급
            String accessToken = jwtTokenProvider.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("user", user);

            log.info("[AppleLogin] 로그인 성공 ✅ - userId: {}", user.getUserId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[AppleLogin] 예외 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Apple login failed");
        }
    }
}
