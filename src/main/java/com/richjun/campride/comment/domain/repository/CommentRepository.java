package com.richjun.campride.comment.domain.repository;

import com.richjun.campride.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository  extends JpaRepository<Comment,Long> {
    Boolean existsByIdAndAuthor(Long postId, String nickname);
}
