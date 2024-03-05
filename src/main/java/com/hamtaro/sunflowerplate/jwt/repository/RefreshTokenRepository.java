package com.hamtaro.sunflowerplate.jwt.repository;

import com.hamtaro.sunflowerplate.jwt.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity , Long> {
    Optional<RefreshTokenEntity> findByMemberEntityMemberId(Long memberId);
    RefreshTokenEntity findByRefreshToken(String refreshToken);
}
