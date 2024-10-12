package com.richjun.campride.user.domain.repository;

import com.richjun.campride.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findBySocialLoginId(String socialLoginId);

    boolean existsBySocialLoginId(String socialLoginId);

    boolean existsByNicknameAndIdNot(String nickname, Long userId);
}
