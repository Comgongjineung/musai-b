package com.musai.musai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Flutter 앱 주소로 제한 가능
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
