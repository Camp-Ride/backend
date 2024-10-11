package com.richjun.campride.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.request.CommentRequest;
import com.richjun.campride.global.common.BaseEntity;
import com.richjun.campride.like.domain.Like;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.report.domain.Report;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorId;
    private String author;
    private String content;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    public static Comment of(CommentRequest commentRequest, Long authorId, String nickname, Post post) {
        return new Comment(
                null,
                authorId,
                nickname,
                commentRequest.getContent(),
                post,
                new ArrayList<>(),
                new ArrayList<>()
        );

    }

    public void update(CommentRequest commentRequest) {
        this.content = commentRequest.getContent();
    }
}