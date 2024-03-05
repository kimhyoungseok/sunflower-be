package com.hamtaro.sunflowerplate.dto.member;

import com.hamtaro.sunflowerplate.dto.review.ReviewImageDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyReviewDto {

    @Schema(description = "리뷰ID",example = "리뷰ID")
    private Long reviewId;

    @Schema(description = "레스토랑ID",example = "레스토랑ID")
    private Long restaurantId;

    @Schema(description = "레스토랑 이름",example = "레스토랑이름")
    private String restaurantName;

    @Schema(description = "리뷰 내용",example = "리뷰 내용")
    private String reviewContent;

    @Schema(description = "리뷰 별점",example = "리뷰 별점")
    private Integer reviewStarRating;

    @Schema(description = "리뷰 이미지 리스트",example = "리뷰 이미지 리스트")
    private List<ReviewImageDto> reviewImageDto;

    @Schema(description = "리뷰 작성 시간",example = "리뷰 작성 시간")
    private LocalDateTime reviewAt;
}
