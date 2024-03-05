package com.hamtaro.sunflowerplate.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantMenuDto {
    private String restaurantMenuName;
    private Integer restaurantMenuPrice;
}
