package com.hamtaro.sunflowerplate.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantAdministrativeDistrict {
    private String cityName;
    private String districtsName;
    private String dongName;
}
