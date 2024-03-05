package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByReviewId(Long reviewId);
    List<ReviewEntity> findByMemberEntity_MemberId(Long id);

    //최신순
    @Query( "SELECT r FROM ReviewEntity r" +
            " WHERE r.restaurantEntity.restaurantId = :restaurantId" +
            " AND r.memberEntity.memberState = true" +
            " ORDER BY r.reviewAt desc ")
    Page<ReviewEntity> findReviewEntityByReviewIdAndAndReviewAt(Pageable pageable,Long restaurantId);

    //공감순
    @Query("SELECT r FROM ReviewEntity r "+
            " LEFT JOIN EmpathyEntity e ON r.reviewId = e.reviewEntity.reviewId" +
            " WHERE r.restaurantEntity.restaurantId = :restaurantId" +
            " AND r.memberEntity.memberState = true" +
            " GROUP BY r.reviewId" +
            " ORDER BY SUM(CASE WHEN e.empathyState = true THEN 1 ELSE 0 END) DESC")
    Page<ReviewEntity> findReviewEntityByReviewIdAndEmpathyEntity(Pageable pageable, Long restaurantId);
}
