package com.hamtaro.sunflowerplate.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class testController {
    @GetMapping("/check")
    public String checkServerStatus(){
        return "check";
    }

    @GetMapping("/asd")
    public String sad(@RequestBody Map<String , String> asd) throws UnsupportedEncodingException {
        System.out.println(asd.get("asd"));
        String qwe = asd.get("asd");
        String cookieValue = URLDecoder.decode( qwe, StandardCharsets.UTF_8.toString());

        return cookieValue;
    }
}
