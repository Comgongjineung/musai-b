package com.musai.musai.config;

import com.musai.musai.service.user.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )

                // ✅ OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // 로그인 페이지 경로
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOauth2UserService) // ✅ 커스텀 OAuth2 서비스 연결
                        )
                        .defaultSuccessUrl("/", true) // 로그인 성공 시 리다이렉트
                )

                // ✅ 로그아웃 설정
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 홈으로 이동
                )

                // ✅ CSRF 비활성화 (필요 시)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
