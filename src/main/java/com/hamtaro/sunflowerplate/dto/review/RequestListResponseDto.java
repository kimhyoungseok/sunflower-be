package com.hamtaro.sunflowerplate.dto.review;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestListResponseDto {

    private Long requestId;

    private String requestContent; //요청 내용

    private LocalDate requestAt; // 요청 날짜


}
