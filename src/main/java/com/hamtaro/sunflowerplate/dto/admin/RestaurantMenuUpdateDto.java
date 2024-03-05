package com.hamtaro.sunflowerplate.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantMenuUpdateDto {
    private Long restaurantMenuId;
    private String restaurantMenuName;
    private Integer restaurantMenuPrice;
}
