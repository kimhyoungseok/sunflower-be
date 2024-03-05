package com.hamtaro.sunflowerplate.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class MemberEditDto {
    @Schema(description = "비밀번호",example = "수정할 비밀번호" , allowableValues = {"8~20글자 영어숫자포함"})
    private String password;
    @Schema(description = "닉네임",example = "수정할 닉네임")
    private String nickName;
    @Schema(description = "전화번호",example = "수정할 전화번호")
    private String phone;


}
