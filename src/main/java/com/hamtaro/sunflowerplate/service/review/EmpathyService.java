package com.hamtaro.sunflowerplate.service.review;


import com.amazonaws.services.kms.model.NotFoundException;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.repository.review.EmpathyRepository;
import com.hamtaro.sunflowerplate.repository.review.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmpathyService {

    private final EmpathyRepository empathyRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public ResponseEntity<?> countPlus(Long reviewId, String userId) {

        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new NotFoundException("Could not found user id : "
                        + memberRepository.findById(Long.valueOf(userId))));

        ReviewEntity reviewEntity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Could not found review id : "
                        + reviewRepository.findById(reviewId)));

        HashMap<Object, Object> empathy = new HashMap<>();


        Optional<EmpathyEntity> findEmpathy = empathyRepository.findByMemberEntityAndReviewEntity(memberEntity, reviewEntity);

        // memberEntity가 reviewEntity를 이미 좋아요한 경우 좋아요를 다시 누르면 좋아요가 취소.
        int empathyCount;
        boolean empathyButton;

        if (findEmpathy.isPresent()) {
            Boolean empathyState = findEmpathy.get().getEmpathyState();
            findEmpathy.get().setEmpathyState(!empathyState);
            empathyRepository.save(findEmpathy.get());
            empathyButton = !empathyState;
        } else {
            EmpathyEntity empathyEntity = EmpathyEntity.builder()
                    .reviewEntity(reviewEntity)
                    .memberEntity(memberEntity)
                    .empathyState(true)
                    .build();
            empathyRepository.save(empathyEntity);
            empathyButton = true;


        }

        empathyCount = empathyRepository.countByReviewEntity(reviewEntity);

        empathy.put("좋아요", empathyButton);
        empathy.put("좋아요 개수", empathyCount);

        return ResponseEntity.status(200).body(empathy);
    }
}




