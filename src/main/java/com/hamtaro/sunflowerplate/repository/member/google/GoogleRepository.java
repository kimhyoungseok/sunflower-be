package com.hamtaro.sunflowerplate.repository.member.google;

import com.hamtaro.sunflowerplate.entity.member.google.GoogleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleRepository extends JpaRepository<GoogleEntity, Long> {
    Optional<GoogleEntity> findByGoogleId(String googleId);
}
