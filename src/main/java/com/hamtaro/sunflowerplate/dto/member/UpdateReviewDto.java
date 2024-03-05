package com.hamtaro.sunflowerplate.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateReviewDto {

    @Schema(description = "리뷰 내용",example = "수정할 리뷰 내용")
    private String reviewContent;

    @Schema(description = "리뷰 이미지 리스트",example = "수정할 리뷰 이미지 리스트")
    private List<UpdateReviewImageDto> imageDtoList;



}
