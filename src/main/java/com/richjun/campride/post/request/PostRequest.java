package com.richjun.campride.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
