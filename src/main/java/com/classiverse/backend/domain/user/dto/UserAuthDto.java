package com.classiverse.backend.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserAuthDto {

    @Getter
    @AllArgsConstructor
    public static class AuthResponse {
        private String accessToken;
        private String refreshToken;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;
    }
}