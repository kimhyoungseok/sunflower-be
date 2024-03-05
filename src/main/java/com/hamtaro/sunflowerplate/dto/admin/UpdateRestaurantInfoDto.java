package com.hamtaro.sunflowerplate.dto.admin;

import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantImageDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateRestaurantInfoDto {
    private Long restaurantId;
    private String restaurantName;
    private String restaurantTelNum;
    private String restaurantAddress;
    private String restaurantOpenTime;
    private String restaurantWebSite;
    private String restaurantStatus;
    private List<RestaurantMenuUpdateDto> restaurantMenuDtoList;
    private List<RestaurantImageDto> restaurantImageDtoList;
    private RestaurantAdministrativeDistrict restaurantAdministrativeDistrict;
}
