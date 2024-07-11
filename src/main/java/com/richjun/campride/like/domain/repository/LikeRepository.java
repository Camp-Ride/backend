package com.richjun.campride.like.domain.repository;

import com.richjun.campride.like.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
