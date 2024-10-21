package com.richjun.campride.post.domain.repository;

import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostResponse> searchPostsPage(Pageable pageable);

    Page<PostResponse> searchPostsPageByLikes(Pageable pageable);

    Page<PostResponse> searchPostsPageV2(Pageable pageable, User user);

    Page<PostResponse> searchPostsPageByLikesV2(Pageable pageable, User user);

    Page<PostResponse> searchPostsPageV3(Pageable pageable, User user);

    Page<PostResponse> searchPostsPageByLikesV3(Pageable pageable, User user);
}
