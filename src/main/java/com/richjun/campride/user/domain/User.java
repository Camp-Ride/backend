package com.richjun.campride.user.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.richjun.campride.global.common.BaseEntity;
import com.richjun.campride.room.domain.Room;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "app_user")
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String socialLoginId;
    private String nickname;
    private String role;

    @ManyToMany(mappedBy = "participants")
    private List<Room> rooms;


    public User(String socialLoginId, String nickname, String role) {
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.role = role;
    }
}