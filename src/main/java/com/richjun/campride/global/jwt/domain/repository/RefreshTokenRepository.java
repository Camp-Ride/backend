package com.richjun.campride.global.jwt.domain.repository;

import com.richjun.campride.global.jwt.domain.RefreshToken;
import com.richjun.campride.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    Long deleteByUser(User user);

    Optional <RefreshToken> findByUser(User user);
}
