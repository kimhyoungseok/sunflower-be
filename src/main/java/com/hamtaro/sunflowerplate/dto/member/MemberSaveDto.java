package com.hamtaro.sunflowerplate.dto.member;

import com.sun.istack.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSaveDto {
    @NotNull
    @Schema(description = "이메일",example = "회원가입할 이메일")
    private String email;
    @NotNull
    @Schema(description = "비밀번호",example = "회원가입할 비밀번호" , allowableValues = {"8~20글자 영어숫자포함"})
    private String password;
    @NotNull
    @Schema(description = "닉네임",example = "회원가입할 닉네임")
    private String nickName;
    @NotNull
    @Schema(description = "전화번호",example = "회원가입할 전화번호")
    private String phone;
}
