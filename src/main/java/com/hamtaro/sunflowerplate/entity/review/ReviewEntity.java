package com.hamtaro.sunflowerplate.entity.review;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "review_content", nullable = false, length = 1000)
    private String reviewContent;

    @Column(name = "review_star_rating", nullable = false)
    private Integer reviewStarRating;

    @CreatedDate
    @Column(name = "review_at", nullable = false)
    private LocalDateTime reviewAt;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @OneToMany(mappedBy = "reviewEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ReportEntity> reportEntityList;

    @OneToMany(mappedBy = "reviewEntity", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EmpathyEntity> empathyEntityList;

    @OneToMany(mappedBy = "reviewEntity",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<ReviewImageEntity> reviewImageEntityList;
}
