package com.hamtaro.sunflowerplate.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginTokenSaveDto {
    private Long id;
    private String email;
    private String memberNickName;
    private String memberRole;
}
