package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewImageRepository extends JpaRepository<ReviewImageEntity,Long> {
    Optional<List<ReviewImageEntity>> findReviewImageEntityByReviewEntity_ReviewId(Long reviewId);
}
