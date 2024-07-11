package com.richjun.campride.post.domain.repository.querydsl.impl;

import static com.richjun.campride.post.domain.QPost.post;
import static com.richjun.campride.room.domain.QRoom.room;

import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.richjun.campride.comment.response.CommentResponse;
import com.richjun.campride.like.response.LikeResponse;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.QPost;
import com.richjun.campride.post.domain.repository.PostRepositoryCustom;
import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.room.domain.QRoom;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.response.RoomResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostResponse> searchPostsPage(Pageable pageable) {

//        private List<LikeResponse> likeResponses;
//        private List<CommentResponse> commentResponses;
//        private List<String> imageNames;

        List<Post> posts = queryFactory.selectFrom(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }
}
