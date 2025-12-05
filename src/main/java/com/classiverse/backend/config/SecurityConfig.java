package com.classiverse.backend.config;

import com.classiverse.backend.domain.user.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // 추가
import org.springframework.web.cors.CorsConfigurationSource; // 추가
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // 추가

import java.util.List; // 추가

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // 생성자 주입을 위해 추가 (JwtAuthenticationFilter 등)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {

        http
                // ★ [추가] CORS 설정 적용 (아래 corsConfigurationSource 메서드 사용)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/health",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ★ [추가] CORS 설정 빈 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 허용할 프론트엔드 도메인 (로컬 + 배포 주소)
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",       // 로컬 React
                "http://localhost:5175",       // 로컬 Vite
                "https://classiverse.site",    // 배포된 프론트 (HTTPS)
                "http://classiverse.site"      // 배포된 프론트 (HTTP)
        ));

        // 2. 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 3. 허용할 헤더
        config.setAllowedHeaders(List.of("*"));

        // 4. 노출할 헤더 (프론트에서 읽을 수 있게 허용)
        config.setExposedHeaders(List.of("Authorization", "Location", "X-Total-Count"));

        // 5. 자격 증명 허용 (쿠키, 인증 헤더 등)
        config.setAllowCredentials(true);

        // 6. Preflight 요청 캐시 시간 (초 단위)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}