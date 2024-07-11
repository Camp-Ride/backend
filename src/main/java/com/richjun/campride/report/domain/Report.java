package com.richjun.campride.report.domain;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.request.PostRequest;
import com.richjun.campride.report.request.ReportRequest;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String content;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Comment comment;


    public static Report of(final User user, final ReportRequest reportRequest, final Post post) {
        return new Report(
                null,
                user,
                reportRequest.getContent(),
                post,
                null
        );
    }

    public static Report of(final User user, final ReportRequest reportRequest, final Comment comment) {
        return new Report(
                null,
                user,
                reportRequest.getContent(),
                null,
                comment
        );
    }


}
