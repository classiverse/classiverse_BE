package com.classiverse.backend.domain.user.controller;

import com.classiverse.backend.domain.user.dto.UserAuthDto;
import com.classiverse.backend.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String KAKAO_STATE_SESSION_KEY = "KAKAO_OAUTH_STATE";

    private final AuthService authService;

    // 카카오 로그인 시작  - state 생성 → 세션에 저장 → 카카오 authorize URL로 리다이렉트
    @GetMapping("/kakao/login")
    public ResponseEntity<Void> kakaoLogin(HttpServletRequest request) {
        String state = generateState();
        HttpSession session = request.getSession(true);
        session.setAttribute(KAKAO_STATE_SESSION_KEY, state);

        String redirectUrl = authService.buildKakaoAuthorizeUrl(state);

        return ResponseEntity.status(302)
                .header("Location", redirectUrl)
                .build();
    }

    // 카카오 콜백-code, state 파라미터 수신-state 검증 후 AuthService.loginWithKakao 호출
    @GetMapping("/kakao/callback")
    public ResponseEntity<UserAuthDto.AuthResponse> kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("세션이 만료되었습니다. 다시 로그인해 주세요.");
        }

        String savedState = (String) session.getAttribute(KAKAO_STATE_SESSION_KEY);
        if (savedState == null || !savedState.equals(state)) {
            throw new IllegalStateException("유효하지 않은 state 값입니다.");
        }

        UserAuthDto.AuthResponse response = authService.loginWithKakao(code);

        // 한번 사용한 state는 세션에서 제거
        session.removeAttribute(KAKAO_STATE_SESSION_KEY);

        return ResponseEntity.ok(response);
    }

    // 액세스 토큰 재발급 (리프레시 토큰 사용) - JWT 도입 후 AuthService.refreshToken 구현 예정
    @PostMapping("/refresh")
    public ResponseEntity<UserAuthDto.AuthResponse> refreshToken(
            @RequestBody UserAuthDto.RefreshTokenRequest request
    ) {
        UserAuthDto.AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    // state 생성

    private String generateState() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}