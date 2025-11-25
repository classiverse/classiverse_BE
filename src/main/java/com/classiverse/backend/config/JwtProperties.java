package com.classiverse.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;              // 서명용 비밀키
    private long accessTokenValidity;   // 액세스 토큰 만료(ms)
    private long refreshTokenValidity;  // 리프레시 토큰 만료(ms)
}