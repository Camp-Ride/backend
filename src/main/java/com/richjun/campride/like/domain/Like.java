package com.richjun.campride.like.domain;

import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.global.common.BaseEntity;
import com.richjun.campride.like.domain.type.ContentType;
import com.richjun.campride.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Table(name = "likeEmotion")
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private ContentType likeType;

    public Like(String username, Post post, ContentType likeType) {
        this.nickname = username;
        this.post = post;
        this.likeType = likeType;
    }

    public Like(String username, Comment comment, ContentType likeType) {
        this.nickname = username;
        this.comment = comment;
        this.likeType = likeType;
    }

    public static Like postLike(String username, Post post, ContentType likeType) {
        return new Like(username, post, likeType);
    }

    public static Like commentLike(String username, Comment comment, ContentType likeType) {
        return new Like(username, comment, likeType);
    }


}