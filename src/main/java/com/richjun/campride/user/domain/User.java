package com.richjun.campride.user.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.richjun.campride.room.domain.Room;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "app_user")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String socialLoginId;
    private String nickname;
    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;


    public User(String socialLoginId, String nickname, String role) {
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.role = role;
    }
}