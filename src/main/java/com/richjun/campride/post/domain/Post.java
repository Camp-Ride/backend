package com.richjun.campride.post.domain;


import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.image.response.ImagesResponse;
import com.richjun.campride.like.domain.Like;
import com.richjun.campride.post.request.PostRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
public class Post {

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


    public static Post of(final PostRequest postRequest, final String nickname, final ImagesResponse imagesResponse) {
        return new Post(
                null,
                nickname,
                postRequest.getTitle(),
                postRequest.getContents(),
                new ArrayList<>(),
                new ArrayList<>(),
                imagesResponse.getImageNames()
        );
    }

    public void update(final String title, final String contents, final List<String> images) {
        this.title = title;
        this.contents = contents;
        this.images = images;
    }
}
