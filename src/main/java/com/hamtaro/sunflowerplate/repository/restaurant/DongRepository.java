package com.hamtaro.sunflowerplate.repository.restaurant;

import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DongRepository extends JpaRepository<DongEntity, Long> {
    Optional<DongEntity> findByDongName(String dongName);
}
