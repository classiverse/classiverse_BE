package com.classiverse.backend.domain.user.entity;

import com.classiverse.backend.domain.common.BaseTimeEntity;
import com.classiverse.backend.domain.common.YnType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    // 카카오 고유 유저 ID (카카오 /v2/user/me 에서 내려주는 id)
    @Column(name = "kakao_user_id", unique = true)
    private Long kakaoUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "kakao_user", nullable = false)
    private YnType kakaoUser = YnType.N;

    @Enumerated(EnumType.STRING)
    @Column(name = "apple_user", nullable = false)
    private YnType appleUser = YnType.N;

    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    // 유저 프로필 이미지 URL
    @Column(name = "profile_image", length = 500)
    private String profileImage;


    public User(String nickname, YnType kakaoUser, YnType appleUser) {
        this.nickname = nickname;
        this.kakaoUser = kakaoUser;
        this.appleUser = appleUser;
    }

    public User(String nickname, YnType kakaoUser, YnType appleUser, Long kakaoUserId) {
        this.nickname = nickname;
        this.kakaoUser = kakaoUser;
        this.appleUser = appleUser;
        this.kakaoUserId = kakaoUserId;
    }
}