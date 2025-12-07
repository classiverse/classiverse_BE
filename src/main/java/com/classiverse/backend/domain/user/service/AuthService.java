package com.classiverse.backend.domain.user.service;

import com.classiverse.backend.config.KakaoOAuthProperties;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.user.dto.UserAuthDto;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.entity.UserRefreshToken;
import com.classiverse.backend.domain.user.jwt.JwtProvider;
import com.classiverse.backend.domain.user.repository.UserRefreshTokenRepository;
import com.classiverse.backend.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // [추가] 트랜잭션 처리를 위해 필요
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthProperties kakaoProps;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final JwtProvider jwtProvider;

    private static final List<String> RANDOM_PROFILE_IMAGES = List.of(
            "https://classiverse-storage.s3.ap-northeast-2.amazonaws.com/profile_1.png",
            "https://classiverse-storage.s3.ap-northeast-2.amazonaws.com/profile_2.png",
            "https://classiverse-storage.s3.ap-northeast-2.amazonaws.com/profile_3.png",
            "https://classiverse-storage.s3.ap-northeast-2.amazonaws.com/profile_4.png"
    );

    private final Random random = new Random();

    // 카카오 로그인 시작 시 authorize URL 생성
    public String buildKakaoAuthorizeUrl(String state) {
        return kakaoProps.getAuthorizeUri()
                + "?response_type=code"
                + "&client_id=" + kakaoProps.getClientId()
                + "&redirect_uri=" + kakaoProps.getRedirectUri()
                + "&state=" + state;
    }

    // 카카오 콜백: code 로 토큰 요청 → 유저 정보 조회 → 우리 서비스 로그인/회원가입 처리
    // 최종적으로 JWT access/refresh 토큰과 닉네임을 반환
    public UserAuthDto.AuthResponse loginWithKakao(String code) {
        // 1) 인가코드로 카카오 토큰 발급
        KakaoTokenResponse tokenResponse = requestKakaoToken(code);

        // 2) 액세스 토큰으로 카카오 유저 정보 조회
        KakaoUserResponse userResponse = requestKakaoUser(tokenResponse.getAccessToken());

        Long kakaoUserId = userResponse.getId();

        // 3) 우리 서비스 User 조회 또는 생성
        User user = userRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> createUserFromKakao(userResponse));

        // 4) JWT access / refresh 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        LocalDateTime refreshExpiresAt = jwtProvider.getExpiry(refreshToken);

        // 5) 리프레시 토큰을 DB에 저장(있으면 업데이트, 없으면 생성)
        userRefreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        entity -> {
                            entity.updateToken(refreshToken, refreshExpiresAt);
                            userRefreshTokenRepository.save(entity);
                        },
                        () -> userRefreshTokenRepository.save(
                                new UserRefreshToken(user, refreshToken, refreshExpiresAt)
                        )
                );

        return new UserAuthDto.AuthResponse(accessToken, refreshToken, user.getNickname());
    }

    // 리프레시 토큰으로 액세스 토큰 재발급 - 토큰 서명/만료 검증, DB 에 저장된 리프레시 토큰인지 확인
    public UserAuthDto.AuthResponse refreshToken(String refreshToken) {
        // 1) 형식/서명/만료 검증
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2) 토큰 타입 확인
        String type = jwtProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(type)) {
            throw new IllegalArgumentException("리프레시 토큰이 아닙니다.");
        }

        // 3) 토큰에서 userId 추출
        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        // 4) DB 에 저장된 리프레시 토큰인지 확인
        UserRefreshToken saved = userRefreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("저장되지 않은 리프레시 토큰입니다."));

        if (!saved.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("리프레시 토큰의 사용자 정보가 일치하지 않습니다.");
        }

        // 5) DB 상 만료 시간도 체크
        if (saved.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("만료된 리프레시 토큰입니다.");
        }

        // 6) 새 access / refresh 토큰 발급 (리프레시 토큰 회전)
        String newAccessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        LocalDateTime newExpiresAt = jwtProvider.getExpiry(newRefreshToken);

        saved.updateToken(newRefreshToken, newExpiresAt);
        userRefreshTokenRepository.save(saved);

        return new UserAuthDto.AuthResponse(newAccessToken, newRefreshToken, user.getNickname());
    }

    // 로그아웃: 리프레시 토큰을 기반으로 해당 유저의 서버 측 세션(리프레시 토큰) 제거
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("리프레시 토큰이 비어 있습니다.");
        }

        // 1) 형식/서명/만료 검증
        if (!jwtProvider.isTokenValid(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2) 토큰 타입 확인
        String type = jwtProvider.getTokenType(refreshToken);
        if (!"REFRESH".equals(type)) {
            throw new IllegalArgumentException("리프레시 토큰이 아닙니다.");
        }

        // 3) 토큰에서 userId 추출 후 유저 확인
        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 사용자입니다."));

        // 4) 해당 유저의 리프레시 토큰 삭제
        userRefreshTokenRepository.deleteByUser(user);
    }

    // ★ [추가] 개발용 임시 로그인 (백도어)
    // 카카오 로그인 없이 강제로 토큰을 발급받기 위한 메서드입니다.
    @Transactional
    public UserAuthDto.AuthResponse devLogin() {
        Long devUserId = 123456789L; // 임의의 가짜 카카오 ID

        // 1. 테스트 유저 조회 (없으면 생성)
        User user = userRepository.findByKakaoUserId(devUserId)
                .orElseGet(() -> {
                    // 랜덤 프로필 이미지 선택
                    int randomIndex = random.nextInt(RANDOM_PROFILE_IMAGES.size());
                    String selectedImage = RANDOM_PROFILE_IMAGES.get(randomIndex);

                    User newUser = new User(
                            "멋사", // [요청 반영] 닉네임 "멋사"로 고정
                            YnType.Y,
                            YnType.N,
                            devUserId,
                            selectedImage
                    );
                    return userRepository.save(newUser);
                });

        // 2. 토큰 발급
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        LocalDateTime refreshExpiresAt = jwtProvider.getExpiry(refreshToken);

        // 3. 리프레시 토큰 저장
        userRefreshTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        entity -> entity.updateToken(refreshToken, refreshExpiresAt),
                        () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken, refreshExpiresAt))
                );

        return new UserAuthDto.AuthResponse(accessToken, refreshToken, user.getNickname());
    }

    private KakaoTokenResponse requestKakaoToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProps.getClientId());
        params.add("client_secret", kakaoProps.getClientSecret());
        params.add("redirect_uri", kakaoProps.getRedirectUri());
        params.add("code", code);

        KakaoTokenResponse response = restTemplate.postForObject(
                kakaoProps.getTokenUri(),
                params,
                KakaoTokenResponse.class
        );

        if (response == null || response.getAccessToken() == null) {
            throw new IllegalStateException("카카오 토큰 발급에 실패했습니다.");
        }
        return response;
    }

    private KakaoUserResponse requestKakaoUser(String accessToken) {
        String url = kakaoProps.getUserInfoUri() + "?secure_resource=true";

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        org.springframework.http.HttpEntity<Void> entity =
                new org.springframework.http.HttpEntity<>(headers);

        org.springframework.http.ResponseEntity<KakaoUserResponse> response =
                restTemplate.exchange(url,
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        KakaoUserResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalStateException("카카오 사용자 정보 조회에 실패했습니다.");
        }

        return response.getBody();
    }

    private User createUserFromKakao(KakaoUserResponse kakaoUser) {
        Long kakaoUserId = kakaoUser.getId();

        // 초기 가입 시에는 임시 닉네임을 하나 넣음 - 우리 서비스 닉네임과는 별도
        String tempNickname = "클라시버스 유저_" + kakaoUserId;

        // 랜덤으로 하나 뽑기 (0 ~ 3 사이의 난수 생성)
        int randomIndex = random.nextInt(RANDOM_PROFILE_IMAGES.size());
        String selectedImage = RANDOM_PROFILE_IMAGES.get(randomIndex);

        User user = new User(
                tempNickname,
                YnType.Y,      // kakaoUser = Y
                YnType.N,      // appleUser = N
                kakaoUserId,
                selectedImage // <-- 여기에 넣어서 저장!
        );
        return userRepository.save(user);
    }

    // 카카오 응답 DTO

    @lombok.Getter
    @lombok.NoArgsConstructor
    public static class KakaoTokenResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("access_token")
        private String accessToken;

        @com.fasterxml.jackson.annotation.JsonProperty("refresh_token")
        private String refreshToken;

        @com.fasterxml.jackson.annotation.JsonProperty("token_type")
        private String tokenType;

        @com.fasterxml.jackson.annotation.JsonProperty("expires_in")
        private Integer expiresIn;

        @com.fasterxml.jackson.annotation.JsonProperty("scope")
        private String scope;
    }

    @lombok.Getter
    @lombok.NoArgsConstructor
    public static class KakaoUserResponse {
        private Long id;

        @com.fasterxml.jackson.annotation.JsonProperty("kakao_account")
        private Map<String, Object> kakaoAccount;
    }
}