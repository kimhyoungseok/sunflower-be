package com.hamtaro.sunflowerplate.dto.member;

import com.sun.istack.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberLoginDto {
    @NotNull
    @Schema(description = "이메일", example = "로그인 할 이메일")
    private String email;
    @NotNull
    @Schema(description = "비밀번호", example = "로그인 할 비밀번호")
    private String password;
}
