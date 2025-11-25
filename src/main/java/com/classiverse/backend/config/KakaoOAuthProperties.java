package com.classiverse.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthProperties {

    // kakao.client-id
    private String clientId;

    // kakao.client-secret
    private String clientSecret;

    // kakao.redirect-uri
    private String redirectUri;

    // kakao.authorize-uri
    private String authorizeUri;

    // kakao.token-uri
    private String tokenUri;

    // kakao.user-info-uri
    private String userInfoUri;
}