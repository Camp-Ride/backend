package com.richjun.campride.post.response;

import com.richjun.campride.comment.response.CommentResponse;
import com.richjun.campride.like.response.LikeResponse;
import com.richjun.campride.post.domain.Post;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String name;
    private String title;
    private List<LikeResponse> likeResponses;
    private List<CommentResponse> commentResponses;
    private List<String> imageNames;


    public static PostResponse of(Post post) {
        return new PostResponse(
                post.getId(),
                post.getName(),
                post.getTitle(),
                LikeResponse.from(post.getLikes()),
                CommentResponse.from(post.getComments()),
                post.getImages()

        );
    }

}
