package com.classiverse.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 보호 비활성화 (Postman/프론트 개발 초기 테스트 시 403 방지)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 기본 로그인 방식(formLogin, httpBasic) 비활성화
                // 카카오 OAuth + JWT 기반 인증을 사용할 예정
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3. 모든 HTTP 요청에 대해 인증 없이 접근 허용 (개발용)
                // - JWT 도입 후에는 여기에서 /api/auth/**, /swagger 등만 permitAll,
                //   나머지는 authenticated()로 단계적으로 잠글 예정
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}