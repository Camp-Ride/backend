package com.richjun.campride.user.domain;

import static jakarta.persistence.GenerationType.IDENTITY;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.richjun.campride.global.common.BaseEntity;
import com.richjun.campride.global.jwt.domain.RefreshToken;
import com.richjun.campride.room.domain.Participant;
import com.richjun.campride.room.domain.Room;
import com.richjun.campride.user.request.UserRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
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
    private String deviceToken;
    private Boolean isNicknameUpdated;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private RefreshToken refreshToken;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    public User(String socialLoginId, String nickname, String role, String deviceToken) {
        this.socialLoginId = socialLoginId;
        this.nickname = nickname;
        this.role = role;
        this.deviceToken = deviceToken;
        this.isNicknameUpdated = false;
    }

    public void update(UserRequest userRequest) {
        this.nickname = userRequest.getNickname();
        this.isNicknameUpdated = true;
    }

    public boolean getIsNicknameUpdated() {
        return isNicknameUpdated;
    }

}