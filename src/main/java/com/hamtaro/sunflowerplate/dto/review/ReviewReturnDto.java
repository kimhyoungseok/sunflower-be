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
public class ReviewReturnDto {
    private Long reviewId;

    private Long memberId;
    private String memberNickname;
    private String memberProfilePicture;

    private String reviewContent;

    private Integer reviewStarRating;

    private LocalDateTime reviewAt;

    private List<ReviewImageDto> reviewImageDtoList;
    private int reviewEmpathyCount;
    private boolean empathyReview;
}
