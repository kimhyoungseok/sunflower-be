package com.hamtaro.sunflowerplate.repository.restaurant;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImageEntity,Long> {
    List<RestaurantImageEntity> findAllByRestaurantEntity_RestaurantId(Long restaurantId);
}
