package com.hamtaro.sunflowerplate.repository.member.kakao;

import com.hamtaro.sunflowerplate.entity.member.kakao.KakaoLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoRepository extends JpaRepository<KakaoLoginEntity,Long> {
    Optional<KakaoLoginEntity> findByKakaoLoginId(Long kakaoLoginId);
    void deleteByMemberEntityMemberId(Long memberId);
}
