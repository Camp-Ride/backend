package com.richjun.campride.room.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.richjun.campride.room.domain.type.RoomType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {


    @NotBlank(message = "방제목을 입력해 주세요")
    @Size(max = 60, message = "방제목은 60자를 초과할 수 없습니다.")
    private String title;

    @NotBlank(message = "출발지를 입력해 주세요.")
    private String departure;

    @NotBlank(message = "도착지를 입력해 주세요.")
    private String destination;

    @NotNull(message = "출발 날짜를 입력해 주세요.")
    @Temporal(TemporalType.TIMESTAMP)
//    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm")
    private LocalDateTime departureTime;

    @NotNull(message = "방 최대 참여 인원을 선택해 주세요.")
    @Min(value = 2, message = "방 최대 참여 인원이 2보다 작을 수 없습니다.")
    @Max(value = 10, message = "방 최대 참여 인원이 10보다 클 수 없습니다.")
    private int maxParticipants;

    @NotNull(message = "방타입을 선택해 주세요.")
    private RoomType roomType;


}
