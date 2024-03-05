package com.hamtaro.sunflowerplate.dto.member.kakao;

import com.hamtaro.sunflowerplate.entity.member.kakao.KakaoLoginEntity;
import lombok.Data;

@Data
public class LoginResponseDto {
    public boolean loginSuccess;
    public KakaoLoginEntity kakaoLoginEntity;
}
