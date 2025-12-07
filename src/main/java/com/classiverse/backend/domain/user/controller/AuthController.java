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

    // [수정] 설정 파일(application.yaml)에서 주소 가져오기
    @Value("${app.frontend-redirect-url}")
    private String frontendUrl;

    private final AuthService authService;

    // ★ [추가] 개발용 임시 로그인 API
    // GET /api/auth/dev/login
    // 카카오 로그인 없이 "멋사" 유저로 로그인된 토큰을 즉시 반환합니다.
    @GetMapping("/dev/login")
    public ResponseEntity<UserAuthDto.AuthResponse> devLogin() {
        return ResponseEntity.ok(authService.devLogin());
    }

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

        // [수정] 하드코딩 제거하고 변수(frontendUrl) 사용
        String redirectUrl = String.format(
                "%s?access=%s&refresh=%s",
                frontendUrl,
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken()
        );
        response.sendRedirect(redirectUrl);
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