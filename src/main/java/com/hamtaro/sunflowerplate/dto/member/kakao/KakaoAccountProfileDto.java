package com.hamtaro.sunflowerplate.dto.member.kakao;

import lombok.Data;

@Data
public class KakaoAccountProfileDto {
    private String email;

    public KakaoAccountProfileDto() {
    }

    public KakaoAccountProfileDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
