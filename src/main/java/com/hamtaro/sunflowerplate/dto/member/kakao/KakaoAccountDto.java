package com.hamtaro.sunflowerplate.dto.member.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class KakaoAccountDto {
    private Long id;
    private KakaoAccountProfileDto kakao_account;
    public KakaoAccountDto() {
    }

    public KakaoAccountDto(Long id, KakaoAccountProfileDto kakao_account) {
        this.id = id;
        this.kakao_account = kakao_account;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("kakao_account")
    public KakaoAccountProfileDto getKakaoAccount() {
        return kakao_account;
    }

    @JsonProperty("kakao_account")
    public void setKakaoAccount(KakaoAccountProfileDto kakao_account) {
        this.kakao_account = kakao_account;
    }
}
