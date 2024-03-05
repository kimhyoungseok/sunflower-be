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
public class MemberProfileDto {
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "닉네임")
    private String nickName;
    @Schema(description = "전화번호")
    private String phone;
    @Schema(description = "프로필사진")
    private String memberProfilePicture;
}
