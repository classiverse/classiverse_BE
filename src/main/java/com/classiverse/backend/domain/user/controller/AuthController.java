package com.classiverse.backend.domain.user.controller;

import com.classiverse.backend.domain.user.dto.UserAuthDto;
import com.classiverse.backend.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String KAKAO_STATE_SESSION_KEY = "KAKAO_OAUTH_STATE";

    @Value("${app.frontend-redirect-url}")
    private String frontendUrl;

    private final AuthService authService;

    // [개발용] 임시 로그인 API
    @GetMapping("/dev/login")
    public ResponseEntity<UserAuthDto.AuthResponse> devLogin() {
        return ResponseEntity.ok(authService.devLogin());
    }

    // [기존] GET 로그인 시작
    @GetMapping("/kakao/login")
    public ResponseEntity<Void> kakaoLogin(HttpServletRequest request) {
        String state = generateState();
        HttpSession session = request.getSession(true);
        session.setAttribute(KAKAO_STATE_SESSION_KEY, state);

        String redirectUrl = authService.buildKakaoAuthorizeUrl(state);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }

    // [기존] GET
    @GetMapping("/kakao/callback")
    public void kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("세션이 만료되었습니다. 다시 로그인해 주세요.");
        }

        String savedState = (String) session.getAttribute(KAKAO_STATE_SESSION_KEY);
        if (savedState == null || !savedState.equals(state)) {
            throw new IllegalStateException("유효하지 않은 state 값입니다.");
        }

        UserAuthDto.AuthResponse tokenResponse = authService.loginWithKakao(code);

        session.removeAttribute(KAKAO_STATE_SESSION_KEY);

        String redirectUrl = String.format(
                "%s?access=%s&refresh=%s",
                frontendUrl,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );
        response.sendRedirect(redirectUrl);
    }

    // 프론트엔드 요청 대응용 POST

    // 1. 요청 Body를 받을 DTO
    public record KakaoLoginRequest(String authorizationCode) {}

    // 2. 프론트엔드가 인가 코드를 직접 보내주면 토큰을 반환하는 POST 메서드
    @PostMapping("/kakao/callback")
    public ResponseEntity<UserAuthDto.AuthResponse> kakaoLoginPost(
            @RequestBody KakaoLoginRequest request
    ) {
        // 기존 서비스 로직 재사용
        UserAuthDto.AuthResponse response = authService.loginWithKakao(request.authorizationCode());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserAuthDto.AuthResponse> refreshToken(
            @RequestBody UserAuthDto.RefreshTokenRequest request
    ) {
        UserAuthDto.AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody UserAuthDto.RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    private String generateState() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}