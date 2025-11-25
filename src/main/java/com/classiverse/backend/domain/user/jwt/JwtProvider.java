package com.classiverse.backend.domain.user.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.classiverse.backend.config.JwtProperties;
import com.classiverse.backend.domain.user.entity.User;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    void init() {
        // jwt.secret 을 기반으로 HMAC 서명 알고리즘 초기화
        this.algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
        this.verifier = JWT.require(algorithm).build();
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getAccessTokenValidity());

        return JWT.create()
                .withSubject(String.valueOf(user.getUserId()))
                .withClaim("type", "ACCESS")
                .withClaim("kakaoUserId", user.getKakaoUserId())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .sign(algorithm);
    }

    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtProperties.getRefreshTokenValidity());

        return JWT.create()
                .withSubject(String.valueOf(user.getUserId()))
                .withClaim("type", "REFRESH")
                .withClaim("kakaoUserId", user.getKakaoUserId())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiry))
                .sign(algorithm);
    }

    public boolean isTokenValid(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return Long.valueOf(jwt.getSubject());
    }

    public String getTokenType(String token) {
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getClaim("type").asString();
    }

    public LocalDateTime getExpiry(String token) {
        DecodedJWT jwt = verifier.verify(token);
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}