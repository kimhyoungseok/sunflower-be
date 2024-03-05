package com.hamtaro.sunflowerplate.dto.review;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSaveDto {
    private Long restaurantId;

    private Long memberId;

    private String reviewContent;

    private Integer reviewStarRating;

    private LocalDateTime reviewAt;

}
