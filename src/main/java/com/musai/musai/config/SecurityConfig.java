package com.musai.musai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger UI와 API 문서 경로에 대한 인증 없이 접근 허용
                .anyRequest().authenticated()  // 다른 모든 요청은 인증 필요
                .and()
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/swagger-ui/**", "/v3/api-docs/**"));  // Swagger UI와 API 문서 경로에 대해서만 CSRF 비활성화

        return http.build();
    }
}
