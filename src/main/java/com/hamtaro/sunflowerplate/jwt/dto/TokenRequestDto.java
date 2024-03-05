package com.hamtaro.sunflowerplate.jwt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequestDto {

    @Schema(description = "회원 ID")
    private Long memberId;
    @Schema(description = "회원 닉네임")
    private String memberNickName;
    @Schema(description = "액세스 토큰")
    private String accessToken;
    @Schema(description = "액세스 토큰 생성 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Date issuedAt;
    @Schema(description = "액세스 토큰 만료 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private Date accessTokenExpireDate;
}
