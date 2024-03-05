package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmpathyRepository extends JpaRepository<EmpathyEntity , Long> {



       Optional<EmpathyEntity> findByMemberEntityAndReviewEntity(MemberEntity memberEntity , ReviewEntity reviewEntity);
       @Query("SELECT COUNT(e) FROM EmpathyEntity e WHERE e.empathyState is true AND e.reviewEntity = :reviewEntity")
       int countByReviewEntity(@Param("reviewEntity") ReviewEntity reviewEntity);

}
