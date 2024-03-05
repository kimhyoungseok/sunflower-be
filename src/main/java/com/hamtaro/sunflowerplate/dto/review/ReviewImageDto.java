package com.hamtaro.sunflowerplate.dto.review;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewImageDto {

    private Long reviewImageId;
//
//    private Long reviewId;
//
//    private Long restaurantId;
//
    private String reviewOriginName;

    private String reviewStoredName;

    private String reviewResizeStoredName;

    private String reviewOriginUrl;

    private String reviewResizeUrl;

}
