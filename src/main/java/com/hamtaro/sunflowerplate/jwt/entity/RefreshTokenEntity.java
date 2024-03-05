package com.hamtaro.sunflowerplate.jwt.entity;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_token")
public class RefreshTokenEntity extends RefreshTokenTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @Column(nullable = false)
    private String refreshToken;

    public RefreshTokenEntity updateToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public RefreshTokenEntity(MemberEntity memberEntity, String refreshToken) {
        this.memberEntity = memberEntity;
        this.refreshToken = refreshToken;
    }
}
