package com.richjun.campride.like.domain.repository;

import com.richjun.campride.like.domain.Like;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Boolean existsByIdAndNickname(Long likeId, String nickname);

    boolean existsByPostIdAndNickname(Long id, String nickname);

    boolean existsByCommentIdAndNickname(Long id, String nickname);

    Optional<Like> findByPostIdAndNickname(Long id, String nickname);

    Optional<Like> findByCommentIdAndNickname(Long id, String nickname);
}
