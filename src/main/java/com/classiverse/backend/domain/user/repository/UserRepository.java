package com.classiverse.backend.domain.user.repository;

import com.classiverse.backend.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoUserId(Long kakaoUserId);
    boolean existsByNickname(String nickname);
}