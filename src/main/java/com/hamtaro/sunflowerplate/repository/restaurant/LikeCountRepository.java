package com.hamtaro.sunflowerplate.repository.restaurant;

import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeCountRepository extends JpaRepository<LikeCountEntity,Long> {
    @Query("SELECT l FROM LikeCountEntity l WHERE l.memberEntity.memberId = :memberId And l.restaurantEntity.restaurantId = :restaurantId")
    Optional<LikeCountEntity> findByMemberEntityAndRestaurantEntity(@Param("memberId") Long memberId, @Param("restaurantId") Long restaurantId);

    @Query("SELECT count(l) FROM LikeCountEntity l WHERE l.restaurantEntity.restaurantId = :restaurantId AND l.likeStatus = true")
    int countByRestaurantEntity_RestaurantId(Long restaurantId);
    List<LikeCountEntity> findByMemberEntity_MemberId(Long id);

}
