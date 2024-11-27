package com.maumai.glasses.kiosk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();

        // 허용할 헤더, 메서드, 도메인 설정
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        config.addAllowedOrigin("http://localhost:3000"); // 로컬 개발 환경 허용
        config.addAllowedOrigin("http://127.0.0.1:3000"); // IP 형식 로컬호스트 허용
        config.addAllowedOrigin("https://d9hbd26otiifc.cloudfront.net"); // 배포된 도메인 허용
        config.setAllowCredentials(true); // 쿠키 및 인증 정보 허용

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}