package com.hamtaro.sunflowerplate.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UpdateReviewImageDto {

    @Schema(description = "리뷰 이미지ID",example = "수정할 리뷰 이미지ID")
    private Long imageId;
}
