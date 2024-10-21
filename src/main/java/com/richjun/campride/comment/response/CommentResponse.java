package com.richjun.campride.comment.response;

import com.richjun.campride.block.domain.Block;
import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.like.response.LikeResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long authorId;
    private String nickname;
    private String content;
    private List<LikeResponse> likeResponses;
    private LocalDateTime createdAt;

    public static List<CommentResponse> from(List<Comment> comments, List<Block> blocks) {
        return comments.stream()
                .filter((comment) -> blocks.stream()
                        .noneMatch((block) -> block.getTargetUser().getId().equals(comment.getAuthorId())))
                .map((comment) -> new CommentResponse(comment.getId(), comment.getAuthorId(), comment.getAuthor(),
                        comment.getContent(), LikeResponse.from(comment.getLikes()), comment.getCreatedAt()))
                .collect(Collectors.toList());

//        return comments.stream()
//                .map((comment) -> new CommentResponse(comment.getId(), comment.getAuthorId(), comment.getAuthor(),
//                        comment.getContent(), LikeResponse.from(comment.getLikes()), comment.getCreatedAt()))
//                .collect(Collectors.toList());
    }

}
