package com.richjun.campride.post.domain.repository;

import com.richjun.campride.post.response.PostResponse;
import com.richjun.campride.room.response.RoomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    Page<PostResponse> searchPostsPage(Pageable pageable);

    Page<PostResponse> searchPostsPageByLikes(Pageable pageable);
}
