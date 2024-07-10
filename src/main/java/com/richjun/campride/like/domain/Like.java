package com.richjun.campride.like.domain;

import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.post.domain.Post;
import jakarta.persistence.Entity;
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
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;


    public Like(String username, Post post) {
        this.username = username;
        this.post = post;
    }

    public Like(String username, Comment comment) {
        this.username = username;
        this.comment = comment;
    }

}