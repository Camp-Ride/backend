package com.richjun.campride.post.request;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.global.location.domain.Location;
import com.richjun.campride.image.domain.Image;
import com.richjun.campride.like.domain.Like;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.room.domain.type.RoomType;
import com.richjun.campride.room.request.RoomRequest;
import com.richjun.campride.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    @NotBlank(message = "글제목을 입력해 주세요.")
    @Size(max = 60, message = "방제목은 60자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "내용을 입력해 주세요.")
    private String contents;








}
