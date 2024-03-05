package com.hamtaro.sunflowerplate.entity.review;


import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "empathy")
public class EmpathyEntity {
    //좋아요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empathy_id")
    private Long empathyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

    @Column(name = "empathy_state")
    private Boolean empathyState;

    public static EmpathyEntity toEntity(MemberEntity memberEntity, ReviewEntity reviewEntity) {
        EmpathyEntity empathyEntity = new EmpathyEntity();
        empathyEntity.setMemberEntity(memberEntity);
        empathyEntity.setReviewEntity(reviewEntity);

        return empathyEntity;
    }

    //    좋아요 취소 false 좋아요 true
    public void recoverLike(EmpathyEntity empathyEntity) {
        this.empathyState = null;
    }
}
