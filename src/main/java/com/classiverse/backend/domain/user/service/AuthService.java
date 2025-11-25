package com.classiverse.backend.domain.user.service;

import com.classiverse.backend.config.KakaoOAuthProperties;
import com.classiverse.backend.domain.common.YnType;
import com.classiverse.backend.domain.user.dto.UserAuthDto;
import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoOAuthProperties kakaoProps;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    //  로그인 요청 시 카카오 authorize URL 생성-state는 컨트롤러에서 생성 후 넘겨받아 URL에 포함
    public String buildKakaoAuthorizeUrl(String state) {
        // response_type
        return kakaoProps.getAuthorizeUri()
                + "?response_type=code"
                + "&client_id=" + kakaoProps.getClientId()
                + "&redirect_uri=" + kakaoProps.getRedirectUri()
                + "&state=" + state;
    }

    // 콜백에서 받은 code로 카카오 토큰 요청 → 유저 정보 요청 → 우리 서비스 유저 생성/조회, 최종적으로 우리 서비스용 access/refresh 토큰과 닉네임을 리턴
    public UserAuthDto.AuthResponse loginWithKakao(String code) {
        // 1) 인가코드로 토큰 받기
        KakaoTokenResponse tokenResponse = requestKakaoToken(code);

        // 2) 액세스 토큰으로 유저 정보 조회
        KakaoUserResponse userResponse = requestKakaoUser(tokenResponse.getAccessToken());

        Long kakaoUserId = userResponse.getId();

        // 3) 우리 서비스 User 조회 또는 생성
        User user = userRepository.findByKakaoUserId(kakaoUserId)
                .orElseGet(() -> createUserFromKakao(userResponse));

        // 4) (임시) JWT 대신 UUID 토큰 생성
        //    - 나중에 JwtProvider를 도입해서 실제 JWT로 교체 예정
        return generateTokens(user);
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    public UserAuthDto.AuthResponse refreshToken(String refreshToken) {
        // TODO: JWT 도입 후, refreshToken 검증 → 새로운 accessToken 발급 로직 구현
        throw new UnsupportedOperationException("refreshToken 로직은 JWT 도입 후 구현 예정입니다.");
    }

    private KakaoTokenResponse requestKakaoToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProps.getClientId());
        params.add("client_secret", kakaoProps.getClientSecret());
        params.add("redirect_uri", kakaoProps.getRedirectUri());
        params.add("code", code);

        // application/x-www-form-urlencoded 로 POST
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

        // Authorization: Bearer {accessToken}
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

        // 서비스 닉네임 정책
        String tempNickname = "클라시버스 유저_" + kakaoUserId;

        User user = new User(
                tempNickname,
                YnType.Y,      // kakaoUser = Y
                YnType.N,      // appleUser = N
                kakaoUserId
        );
        return userRepository.save(user);
    }

    private UserAuthDto.AuthResponse generateTokens(User user) {
        // [임시 구현]
        // - 현재는 JWT 라이브러리를 붙이지 않았으므로 UUID 기반 토큰 문자열을 사용
        // - 실제 서비스 단계에서는 JwtProvider 등을 도입해 access/refresh를 JWT로 발급
        String accessToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        return new UserAuthDto.AuthResponse(accessToken, refreshToken, user.getNickname());
    }

    // ====== 카카오 응답 DTO (필요 최소 필드만 정의) ======

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