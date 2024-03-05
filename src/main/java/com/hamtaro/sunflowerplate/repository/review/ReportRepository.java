package com.hamtaro.sunflowerplate.repository.review;

import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<ReportEntity,Long> {
    Optional<ReportEntity> findByReportId(Long useId);

    List<ReportEntity> findByMemberEntity_MemberId(Long memberId);


}
