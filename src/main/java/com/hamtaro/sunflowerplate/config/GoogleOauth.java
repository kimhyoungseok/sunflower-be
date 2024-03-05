package com.hamtaro.sunflowerplate.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hamtaro.sunflowerplate.dto.member.google.GoogleDto;
import com.hamtaro.sunflowerplate.dto.member.google.GoogleTokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class GoogleOauth {
    private String googleTokenUrl = "https://oauth2.googleapis.com/token";
    private String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v1/userinfo";

//    @Value("${OAuth2.google.client-id}")
//    private String GOOGLE_CLIENT_ID;
//
//    @Value("${OAuth2.google.client-secret}")
//    private String GOOGLE_CLIENT_SECRET;
//
//    @Value("${OAuth2.google.redirect-url}")
//    private String GOOGLE_REDIRECT_URL;

    public ResponseEntity<String> requestAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("client_id", GOOGLE_CLIENT_ID);
//        params.add("client_secret", GOOGLE_CLIENT_SECRET);
//        params.add("code", code);
//        params.add("grant_type", "authorization_code");
//        params.add("redirect_uri", GOOGLE_REDIRECT_URL);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(googleTokenUrl, params, String.class);

        return responseEntity;
    }

    public GoogleTokenDto getAccessToken(ResponseEntity<String> accessToken) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GoogleTokenDto googleTokenDto = objectMapper.readValue(accessToken.getBody(), GoogleTokenDto.class);
        return googleTokenDto;
    }

    public ResponseEntity<String> requestUserInfo(GoogleTokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokenDto.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, request, String.class);
        System.out.println("response.getBody() = " + response.getBody());
        return response;

    }

    public GoogleDto getUserInfo(ResponseEntity<String> userInfo) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        GoogleDto getGoogleDto = objectMapper.readValue(userInfo.getBody(), GoogleDto.class);
        return getGoogleDto;
    }
}
