package com.richjun.campride.post.domain.repository;

import com.richjun.campride.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
