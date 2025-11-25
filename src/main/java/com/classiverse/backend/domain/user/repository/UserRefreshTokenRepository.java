package com.classiverse.backend.domain.user.repository;

import com.classiverse.backend.domain.user.entity.User;
import com.classiverse.backend.domain.user.entity.UserRefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {

    Optional<UserRefreshToken> findByUser(User user);

    Optional<UserRefreshToken> findByRefreshToken(String refreshToken);

    void deleteByUser(User user);
}