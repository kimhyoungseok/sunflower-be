package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<RequestEntity,Long>{



    List<RequestEntity> findByMemberEntity_MemberId(Long memberId);
}
