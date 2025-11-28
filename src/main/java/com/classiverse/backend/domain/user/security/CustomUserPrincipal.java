package com.classiverse.backend.domain.user.security;

import com.classiverse.backend.domain.user.entity.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserPrincipal implements UserDetails {

    private final Long userId;
    private final Long kakaoUserId;
    private final String nickname;

    public CustomUserPrincipal(User user) {
        this.userId = user.getUserId();           // User 엔티티에 getUserId() 있어야 합니다.
        this.kakaoUserId = user.getKakaoUserId(); // getKakaoUserId()
        this.nickname = user.getNickname();       // getNickname()
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 일단 전부 ROLE_USER 로 고정
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 카카오 OAuth + JWT라 비밀번호 로그인을 안 씁니다.
        return "";
    }

    @Override
    public String getUsername() {
        // username 기준은 userId 로 두겠습니다.
        return String.valueOf(userId);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}