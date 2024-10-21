package com.richjun.campride.post.domain.repository.querydsl.impl;

import static com.richjun.campride.block.domain.QBlock.block;
import static com.richjun.campride.post.domain.QPost.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.repository.PostRepositoryCustom;
import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.user.domain.User;
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

    private BooleanExpression notBlockedByUser(User user) {
        if (user == null) {
            return null;
        }
        return post.user.id.notIn(
                queryFactory.select(block.targetUser.id)
                        .from(block)
                        .where(block.user.id.eq(user.getId()))
        ).and(post.comments.any().authorId.notIn(
                queryFactory.select(block.targetUser.id)
                        .from(block)
                        .where(block.user.id.eq(user.getId()))
        ));
    }


    private BooleanExpression notBlockedByCurrentUser(User currentUser) {
        if (currentUser == null) {
            return null;
        }
        List<Long> blockedUserIds = currentUser.getBlocks().stream()
                .map(block -> block.getTargetUser().getId())
                .collect(Collectors.toList());
        return post.user.id.notIn(blockedUserIds).and(post.comments.any().authorId.notIn(blockedUserIds));
    }


    @Override
    public Page<PostResponse> searchPostsPage(Pageable pageable) {

        List<Post> posts = queryFactory.selectFrom(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }

    @Override
    public Page<PostResponse> searchPostsPageByLikes(Pageable pageable) {
        List<Post> posts = queryFactory.selectFrom(post)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.likes.size().desc(), post.createdAt.desc()
                )
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }

    @Override
    public Page<PostResponse> searchPostsPageV2(Pageable pageable, User user) {

        List<Post> posts = queryFactory.selectFrom(post)
                .where(notBlockedByCurrentUser(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .where(notBlockedByCurrentUser(user))
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }

    @Override
    public Page<PostResponse> searchPostsPageByLikesV2(Pageable pageable, User user) {

        List<Post> posts = queryFactory.selectFrom(post)
                .where(notBlockedByCurrentUser(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.likes.size().desc(), post.createdAt.desc())
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .where(notBlockedByCurrentUser(user))
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }

    @Override
    public Page<PostResponse> searchPostsPageV3(Pageable pageable, User user) {

        List<Post> posts = queryFactory.selectFrom(post)
                .where(notBlockedByUser(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc())
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }

    @Override
    public Page<PostResponse> searchPostsPageByLikesV3(Pageable pageable, User user) {
        List<Post> posts = queryFactory.selectFrom(post)
                .where(notBlockedByUser(user))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.likes.size().desc(), post.createdAt.desc()
                )
                .fetch();

        List<PostResponse> postResponses = posts.stream()
                .map(PostResponse::of).collect(Collectors.toList());

        long total = queryFactory.selectFrom(post)
                .fetchCount();

        return new PageImpl<>(postResponses, pageable, total);
    }
}
