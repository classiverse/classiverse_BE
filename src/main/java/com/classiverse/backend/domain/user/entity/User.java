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

    @Enumerated(EnumType.STRING)
    @Column(name = "kakao_user", nullable = false)
    private YnType kakaoUser = YnType.N;

    @Enumerated(EnumType.STRING)
    @Column(name = "apple_user", nullable = false)
    private YnType appleUser = YnType.N;

    @Column(nullable = false, length = 20)
    private String nickname;

    public User(String nickname, YnType kakaoUser, YnType appleUser) {
        this.nickname = nickname;
        this.kakaoUser = kakaoUser;
        this.appleUser = appleUser;
    }
}