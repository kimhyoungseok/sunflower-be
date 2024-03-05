package com.hamtaro.sunflowerplate.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPlaceDto {

    @Schema(description = "레스토랑ID",example = "레스토랑ID")
    private Long restaurantId;

    @Schema(description = "레스토랑 이름",example = "레스토랑이름")
    private String restaurantName;

    @Schema(description = "레스토랑 주소",example = "레스토랑주소")
    private String restaurantAddress;

    @Schema(description = "레스토랑 웹사이트",example = "레스토랑 웹사이트")
    private String restaurantWebSite;

    @Schema(description = "레스토랑 썸네일",example = "레스토랑 썸네일")
    private String resizeImgUrl;
}
