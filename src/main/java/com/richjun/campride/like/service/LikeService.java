package com.richjun.campride.like.service;

import static com.richjun.campride.global.exception.ExceptionCode.*;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_COMMENT_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_POST_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.comment.repository.CommentRepository;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.like.domain.type.ContentType;
import com.richjun.campride.like.domain.Like;
import com.richjun.campride.like.domain.repository.LikeRepository;
import com.richjun.campride.like.request.LikeRequest;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.repository.PostRepository;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public Long like(final CustomOAuth2User oAuth2User, Long id, final LikeRequest likeRequest) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        return likeByType(id, likeRequest, user);

    }

    private Long likeByType(Long id, LikeRequest likeRequest, User user) {

        Long likeId = null;

        if (likeRequest.getLikeType().equals(ContentType.POST)) {
            Post post = postRepository.findById(id).orElseThrow(() -> new BadRequestException(
                    NOT_FOUND_POST_ID));

            Like postLike = Like.postLike(user.getNickname(), post, likeRequest.getLikeType());
            likeRepository.save(postLike);
            likeId = postLike.getId();
        }

        if (likeRequest.getLikeType().equals(ContentType.COMMENT)) {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new BadRequestException(
                            NOT_FOUND_COMMENT_ID));

            Like commentLike = Like.commentLike(user.getNickname(), comment, likeRequest.getLikeType());
            likeRepository.save(commentLike);
            likeId = commentLike.getId();
        }

        return likeId;
    }

    public Long unLike(Long id) {

        Like like = likeRepository.findById(id).orElseThrow(() -> new BadRequestException(NOT_FOUND_LIKE_ID));
        likeRepository.delete(like);

        return like.getId();
    }


}
