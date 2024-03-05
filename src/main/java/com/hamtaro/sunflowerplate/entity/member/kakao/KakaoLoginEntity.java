package com.hamtaro.sunflowerplate.entity.member.kakao;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
@Getter
@Entity
@Table(name = "kakao_login")
public class KakaoLoginEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kakao_id")
    private Long kakaoId;
    @Column(name = "kakao_login_id", nullable = false)
    private Long kakaoLoginId;
    @Column(name = "kakao_email", unique = true, length = 50, nullable = false)
    private String kakaoEmail;
    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    public KakaoLoginEntity() {
    }
    public KakaoLoginEntity(String kakaoEmail , Long kakaoLoginId) {
        this.kakaoEmail = kakaoEmail;
        this.kakaoLoginId = kakaoLoginId;
    }

    public KakaoLoginEntity(Long kakaoId, Long kakaoLoginId, String kakaoEmail, MemberEntity memberEntity) {
        this.kakaoId = kakaoId;
        this.kakaoLoginId = kakaoLoginId;
        this.kakaoEmail = kakaoEmail;
        this.memberEntity = memberEntity;
    }

    public void setKakaoEmail(String kakaoEmail) {
        this.kakaoEmail = kakaoEmail;
    }

    public void setKakaoLoginId(Long kakaoLoginId) {
        this.kakaoLoginId = kakaoLoginId;
    }

    public void setMemberEntity(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    @Builder
    public KakaoLoginEntity(Long kakaoId, String kakaoEmail , Long kakaoLoginId , MemberEntity memberEntity) {
        this.kakaoId=kakaoId;
        this.kakaoEmail = kakaoEmail;
        this.kakaoLoginId = kakaoLoginId;
        this.memberEntity = memberEntity;
    }
}
