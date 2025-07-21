package com.musai.musai.config;

import com.musai.musai.jwt.JwtAuthenticationFilter;
import com.musai.musai.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/google", "/recog/ping", // ✅ 인증 없이 허용할 API들
                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html" // ✅ Swagger 경로 허용
                        ).permitAll() // 예외적으로 허용
                        .anyRequest().authenticated() // 나머지는 전부 jwt 인증 필요
                )
                .csrf(csrf -> csrf.disable()) // ✅ CSRF 비활성화 (필요한 경우에만)
                .formLogin(form -> form.disable()) // ✅ 로그인 폼 비활성화
                .httpBasic(basic -> basic.disable()) // ✅ HTTP Basic 인증도 비활성화
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}