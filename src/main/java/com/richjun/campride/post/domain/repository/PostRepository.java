package com.richjun.campride.post.domain.repository;

import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.response.PostResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    Optional<Boolean> existsByIdAndUserId(Long postId, Long id);
}
