package com.richjun.campride.comment.response;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.like.response.LikeResponse;
import com.richjun.campride.room.response.ParticipantResponse;
import com.richjun.campride.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String nickname;
    private List<LikeResponse> likeResponses;


    public static List<CommentResponse> from(List<Comment> comments) {

        return comments.stream()
                .map((comment) -> new CommentResponse(comment.getId(), comment.getAuthor(),
                        LikeResponse.from(comment.getLikes()))).collect(Collectors.toList());
    }

}
