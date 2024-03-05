package com.hamtaro.sunflowerplate.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hamtaro.sunflowerplate.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final MemberService memberService;

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<?> googleLogin(@RequestParam String code) throws JsonProcessingException {
        return memberService.googleLogin(code);
    }
}
