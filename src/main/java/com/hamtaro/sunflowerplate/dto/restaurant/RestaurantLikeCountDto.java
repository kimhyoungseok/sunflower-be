package com.hamtaro.sunflowerplate.dto.restaurant;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RestaurantLikeCountDto {

    private int restaurantLikeCount;
    private boolean likedRestaurant;
}
