package com.hamtaro.sunflowerplate.entity.member;

import com.hamtaro.sunflowerplate.entity.member.kakao.KakaoLoginEntity;
import com.hamtaro.sunflowerplate.entity.review.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "member_email", nullable = false, unique = true, length = 50)
    private String memberEmail;

    @Column(name = "member_password", nullable = false, length = 100)
    private String memberPassword;

    @Column(name = "member_nickname", nullable = false, unique = true, length = 20)
    private String memberNickname;

    @Column(name = "member_phone", unique = true, length = 13)
    private String memberPhone;

    @Column(name = "member_profile_picture", length = 100)
    private String memberProfilePicture;

    @Column(name = "member_join_date")
    private LocalDate memberJoinDate;

    @Builder.Default
    @Column(name = "member_state")
    private Boolean memberState = true;

    @Column(name = "member_role")
    private String memberRole;

    @OneToOne(mappedBy = "memberEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private KakaoLoginEntity kakaoLoginEntity;


    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ReportEntity> reportEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<ReviewEntity> reviewEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<RequestEntity> requestEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<EmpathyEntity> empathyEntityList;

    @OneToMany(mappedBy = "memberEntity",cascade = CascadeType.REMOVE,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<LikeCountEntity> likeCountEntityList;

    public void setMemberNickname(String memberNickname) {
        this.memberNickname = memberNickname;
    }

    public void setMemberPassword(String memberPassword) {
        this.memberPassword = memberPassword;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public void setMemberProfilePicture(String memberProfilePicture) {
        this.memberProfilePicture = memberProfilePicture;
    }

    public void setMemberState(Boolean memberState) {
        this.memberState = memberState;
    }
}
