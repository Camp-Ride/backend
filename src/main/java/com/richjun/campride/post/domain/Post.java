package com.richjun.campride.post.domain;


import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.global.common.BaseEntity;
import com.richjun.campride.image.response.ImagesResponse;
import com.richjun.campride.like.domain.Like;
import com.richjun.campride.post.request.PostRequest;
import com.richjun.campride.report.domain.Report;
import com.richjun.campride.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String nickname;
    private String title;
    private String contents;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes;

    @ElementCollection
    private List<String> images;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // 외래 키 설정
    private User user;


    public static Post of(final PostRequest postRequest, final String nickname, final ImagesResponse imagesResponse,
                          final User user) {
        return new Post(
                null,
                nickname,
                postRequest.getTitle(),
                postRequest.getContents(),
                new ArrayList<>(),
                new ArrayList<>(),
                imagesResponse.getImageNames(),
                new ArrayList<>(),
                user
        );
    }

    public void update(final String title, final String contents, final List<String> images) {
        this.title = title;
        this.contents = contents;
        this.images = images;
    }
}
