package com.hamtaro.sunflowerplate.controller.member;

import com.hamtaro.sunflowerplate.dto.member.kakao.KakaoTokenDto;
import com.hamtaro.sunflowerplate.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@Tag(name = "카카오로그인 테스트용", description = "백엔드 카카오로그인 테스트 API")
//public class KakaoTestController {
//    private final MemberService memberService;
//    @Tag(name = "카카오로그인 테스트용", description = "백엔드 카카오로그인 테스트 API")
//    @Operation(summary = "카카오 로그인 백엔드 테스트용", description = "카카오로그인 API")
//    @GetMapping("/login/oauth2/code/kakao")
//    public ResponseEntity<?> kakaoLogin(HttpServletRequest request) {
//        String code = request.getParameter("code");
//        KakaoTokenDto kakaoAccessToken = memberService.getKakaoAccessToken1(code);
//        return memberService.kakaoLogin(kakaoAccessToken);
//    }
//}
