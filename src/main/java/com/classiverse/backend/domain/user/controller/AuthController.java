package com.classiverse.backend.domain.user.controller;

import com.classiverse.backend.domain.user.dto.UserAuthDto;
import com.classiverse.backend.domain.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; // 리다이렉트 처리를 위해 추가
import jakarta.servlet.http.HttpSession;
import java.io.IOException; // sendRedirect 예외 처리를 위해 추가
import java.security.SecureRandom;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private static final String KAKAO_STATE_SESSION_KEY = "KAKAO_OAUTH_STATE";

    private final AuthService authService;

    // 카카오 로그인 시작 - state 생성 → 세션에 저장 → 카카오 authorize URL로 리다이렉트
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

    // 카카오 콜백 - code, state 파라미터 수신 → state 검증 후 AuthService.loginWithKakao 호출
    // void + HttpServletResponse를 통해 리다이렉트 처리
    @GetMapping("/kakao/callback")
    public void kakaoCallback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request,
            HttpServletResponse response // 응답 객체를 통해 리다이렉트를 보냄
    ) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IllegalStateException("세션이 만료되었습니다. 다시 로그인해 주세요.");
        }

        String savedState = (String) session.getAttribute(KAKAO_STATE_SESSION_KEY);
        if (savedState == null || !savedState.equals(state)) {
            throw new IllegalStateException("유효하지 않은 state 값입니다.");
        }

        // 1. 서비스 로직 수행 (카카오 토큰 발급 -> 유저 조회/생성 -> JWT 발급)
        UserAuthDto.AuthResponse tokenResponse = authService.loginWithKakao(code);

        // 한 번 사용한 state는 세션에서 제거
        session.removeAttribute(KAKAO_STATE_SESSION_KEY);

        // 프론트엔드로 리다이렉트 (JWT 전달), 브라우저를 프론트엔드 로그인 성공 페이지로 이동
        // URL 쿼리 파라미터에 access token과 refresh token을 실어서 보냅니다.
        // URL은 프론트엔드 라우트 주소, 일단 하드코딩해놓음
        String frontendUrl = "http://localhost:3000/login-success";

        String redirectUrl = String.format(
                "%s?access=%s&refresh=%s",
                frontendUrl,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );
        response.sendRedirect(redirectUrl);
    }

    // 액세스 토큰 재발급 (리프레시 토큰 사용)
    @PostMapping("/refresh")
    public ResponseEntity<UserAuthDto.AuthResponse> refreshToken(
            @RequestBody UserAuthDto.RefreshTokenRequest request
    ) {
        UserAuthDto.AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    // 로그아웃 - 리프레시 토큰을 기반으로 서버 측 세션(리프레시 토큰) 제거
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody UserAuthDto.RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());
        // 클라이언트에서는 이 응답을 받으면
        // - 저장된 accessToken/refreshToken 삭제
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // state 생성
    private String generateState() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}