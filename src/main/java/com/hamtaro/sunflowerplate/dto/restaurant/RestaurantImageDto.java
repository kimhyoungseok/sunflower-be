package com.hamtaro.sunflowerplate.dto.restaurant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantImageDto {
    private String restaurantOriginName;
    private String restaurantStoredName;
    private String restaurantResizedStoredName;
    private String restaurantOriginUrl;
    private String restaurantResizeUrl;
}
