package com.hamtaro.sunflowerplate.dto.review;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateDto {

    private Long requestId;

    private String requestContent; //요청 내용

    private LocalDate requestAt; // 요청 날짜

    private Long memberId;

    private Long restaurantId;


}
