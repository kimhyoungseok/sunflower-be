package com.hamtaro.sunflowerplate.service.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hamtaro.sunflowerplate.config.GoogleOauth;
import com.hamtaro.sunflowerplate.dto.member.MemberEditDto;
import com.hamtaro.sunflowerplate.dto.member.MemberLoginDto;
import com.hamtaro.sunflowerplate.dto.member.MemberProfileDto;
import com.hamtaro.sunflowerplate.dto.member.MemberSaveDto;
import com.hamtaro.sunflowerplate.dto.member.google.GoogleDto;
import com.hamtaro.sunflowerplate.dto.member.google.GoogleTokenDto;
import com.hamtaro.sunflowerplate.dto.member.kakao.KakaoTokenDto;
import com.hamtaro.sunflowerplate.dto.member.kakao.LoginResponseDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.dto.member.kakao.KakaoAccountDto;
import com.hamtaro.sunflowerplate.entity.member.google.GoogleEntity;
import com.hamtaro.sunflowerplate.entity.member.kakao.KakaoLoginEntity;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.jwt.dto.LoginTokenSaveDto;
import com.hamtaro.sunflowerplate.jwt.dto.TokenDto;
import com.hamtaro.sunflowerplate.jwt.dto.TokenRequestDto;
import com.hamtaro.sunflowerplate.jwt.entity.RefreshTokenEntity;
import com.hamtaro.sunflowerplate.jwt.exception.CRefreshTokenException;
import com.hamtaro.sunflowerplate.jwt.exception.CUserNotFoundException;
import com.hamtaro.sunflowerplate.jwt.repository.RefreshTokenRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.repository.member.google.GoogleRepository;
import com.hamtaro.sunflowerplate.repository.member.kakao.KakaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoRepository kakaoRepository;
    private final GoogleRepository googleRepository;
    private final TokenProvider tokenProvider;
    private final MemberImageService memberImageService;
    private final GoogleOauth googleOauth;

//    @Value("${kakao.auth.clientId}")
//    private String clientId;
//    @Value("${kakao.auth.clientSecret}")
//    private String clientSecret;
//    @Value("${kakao.auth.redirectUrl}")
//    private String redirectUrl;

    private final String defaultMemberImage = "https://plate-user-img.s3.ap-northeast-2.amazonaws.com/BasicImage.png";

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ResponseEntity<?> memberSignUp(MemberSaveDto memberSaveDto) {
        Map<String, String> result = new HashMap<>();
        String passwordPatten = "^[A-Za-z0-9]{8,20}$";
        if (findByEmail(memberSaveDto.getEmail())) {
            if (findByPhoneNumber(memberSaveDto.getPhone())) {
                if (Pattern.matches(passwordPatten, memberSaveDto.getPassword())) {
                    String encPassword = bCryptPasswordEncoder.encode(memberSaveDto.getPassword());
                    MemberEntity memberEntity = MemberEntity.builder()
                            .memberEmail(memberSaveDto.getEmail())
                            .memberPassword(encPassword)
                            .memberNickname(memberSaveDto.getNickName())
                            .memberProfilePicture(defaultMemberImage)
                            .memberPhone(memberSaveDto.getPhone())
                            .memberJoinDate(LocalDate.now())
                            .memberRole("USER")
                            .build();
                    try {
                        memberRepository.save(memberEntity);
                        result.put("message", "회원가입이 완료되었습니다.");
                        return ResponseEntity.status(200).body(result);
                    } catch (Exception e) {
                        result.put("message", "회원가입에 실패하였습니다.");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
                    }
                } else {
                    result.put("message", "형식이 잘못된 비밀번호 입니다.");
                    return ResponseEntity.badRequest().body(result);
                }
            } else {
                result.put("message", "중복된 전화번호 입니다.");
                return ResponseEntity.badRequest().body(result);
            }
        } else {
            result.put("message", "중복된 아이디 입니다.");
            return ResponseEntity.badRequest().body(result);
        }
    }

    public boolean findByEmail(String email) {
        Optional<MemberEntity> memberEntity = memberRepository.findByMemberEmail(email);
        if (memberEntity.isPresent()) {
            return false; // 중복된 아이디
        } else {
            return true; // 새로운 아이디
        }
    }

    public boolean findByPhoneNumber(String telNumber) {
        MemberEntity memberEntity = memberRepository.findByMemberPhone(telNumber);
        if (memberEntity != null) {
            return false; // 중복된 전화번호
        } else {
            return true; // 새로운 전화번호
        }
    }

    public boolean findByNickName(String nickName) {
        MemberEntity memberEntity = memberRepository.findByMemberNickname(nickName);
        if (memberEntity != null) {
            return false; // 중복된 닉네임
        } else {
            return true; // 새로운 닉네임
        }
    }

    public ResponseEntity<?> memberLogin(MemberLoginDto memberLoginDto) {
        Map<String, Object> result = new HashMap<>();
        if (!findByEmail(memberLoginDto.getEmail())) {
            if (findByPasswordCheck(memberLoginDto.getEmail(), memberLoginDto.getPassword())) {
                MemberEntity memberEntity = memberRepository.findByMemberEmail(memberLoginDto.getEmail()).get();
                if (memberEntity.getMemberState()) {
                    LoginTokenSaveDto loginTokenSaveDto = LoginTokenSaveDto.builder()
                            .id(memberEntity.getMemberId())
                            .email(memberEntity.getMemberEmail())
                            .memberNickName(memberEntity.getMemberNickname())
                            .memberRole(memberEntity.getMemberRole())
                            .build();
                    TokenDto tokenDto = tokenProvider.createToken(loginTokenSaveDto.getId(), loginTokenSaveDto);
                    Optional<RefreshTokenEntity> optionalRefreshToken = refreshTokenRepository.findByMemberEntityMemberId(memberEntity.getMemberId());
                    if (optionalRefreshToken.isEmpty()) {
                        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                                .refreshToken(tokenDto.getRefreshToken())
                                .memberEntity(memberEntity)
                                .build();
                        refreshTokenRepository.save(refreshTokenEntity);
                    } else {
                        RefreshTokenEntity refreshTokenEntity = optionalRefreshToken.get();
                        refreshTokenEntity.updateToken(tokenDto.getRefreshToken());
                        refreshTokenRepository.save(refreshTokenEntity);
                    }
                    TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                            .memberId(memberEntity.getMemberId())
                            .memberNickName(memberEntity.getMemberNickname())
                            .accessToken(tokenDto.getAccessToken())
                            .accessTokenExpireDate(tokenDto.getAccessTokenExpireDate())
                            .issuedAt(tokenDto.getIssuedAt())
                            .build();

                    ResponseCookie cookie = ResponseCookie.from("refreshToken", tokenDto.getRefreshToken())
                            .maxAge(7 * 24 * 60 * 60)
                            .path("/")
                            .secure(true)
                            .sameSite("None")
                            .httpOnly(true)
                            .build();
                    return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);
                } else {
                    result.put("access", false);
                    result.put("message", "탈퇴한 회원입니다.");
                    return ResponseEntity.status(401).body(result);
                }
            } else {
                result.put("access", false);
                result.put("message", "아이디 또는 비밀번호가 틀립니다.");
                return ResponseEntity.status(401).body(result);
            }
        } else {
            result.put("access", false);
            result.put("message", "아이디 또는 비밀번호가 틀립니다.");
            return ResponseEntity.status(401).body(result);
        }
    }

    public boolean findByPasswordCheck(String email, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String entityPassword = memberRepository.findByMemberEmail(email).get().getMemberPassword();
        return bCryptPasswordEncoder.matches(password, entityPassword);
    }

    @Transactional
    public ResponseEntity<?> reissue(String refreshToken) {

        if (!tokenProvider.validationToken(refreshToken)) {
            throw new CRefreshTokenException("리플레쉬 토큰이 유효하지 않음");
        }

        MemberEntity memberEntity = memberRepository.findById(refreshTokenRepository.findByRefreshToken(refreshToken).getMemberEntity().getMemberId())
                .orElseThrow(CUserNotFoundException::new);

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByMemberEntityMemberId(memberEntity.getMemberId())
                .orElseThrow(CRefreshTokenException::new);

        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new CRefreshTokenException();
        }

        LoginTokenSaveDto tokenSaveDto = LoginTokenSaveDto.builder()
                .id(memberEntity.getMemberId())
                .email(memberEntity.getMemberEmail())
                .memberNickName(memberEntity.getMemberNickname())
                .memberRole(memberEntity.getMemberRole())
                .build();

        TokenDto newCreatedToken = tokenProvider.createToken(memberEntity.getMemberId(), tokenSaveDto);
        RefreshTokenEntity updateRefreshToken = refreshTokenEntity.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .memberNickName(memberEntity.getMemberNickname())
                .accessToken(newCreatedToken.getAccessToken())
                .accessTokenExpireDate(newCreatedToken.getAccessTokenExpireDate())
                .issuedAt(newCreatedToken.getIssuedAt())
                .build();

        Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuthentication != null) {
            Authentication expiredAuthentication = new UsernamePasswordAuthenticationToken(null, null, null);
            SecurityContextHolder.getContext().setAuthentication(expiredAuthentication);
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", newCreatedToken.getRefreshToken())
                .maxAge(7 * 24 * 60 * 60)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);

    }

    public ResponseEntity<?> logout(String userId) {
        Optional<RefreshTokenEntity> tokenOptional = refreshTokenRepository.findByMemberEntityMemberId(Long.valueOf(userId));
        if (tokenOptional.isPresent()) {
            refreshTokenRepository.deleteById(tokenOptional.get().getId());
        }
        return ResponseEntity.status(200).build();
    }

//    @Transactional
//    public KakaoTokenDto getKakaoAccessToken(String code) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("redirect_url", redirectUrl);
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> accessTokenResponse = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        KakaoTokenDto kakaoTokenDto = null;
//        try {
//            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return kakaoTokenDto;
//    }

    public ResponseEntity<?> kakaoLogin(KakaoTokenDto kakaoAccessToken) {
        KakaoLoginEntity kakaoLoginEntity = getKakaoInfo(kakaoAccessToken.getAccess_token());
        // 로그인 응답 데이터 생성
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setLoginSuccess(true);
        loginResponseDto.setKakaoLoginEntity(kakaoLoginEntity);
        Optional<KakaoLoginEntity> existOwner = kakaoRepository.findByKakaoLoginId(kakaoLoginEntity.getKakaoLoginId());
        try {
            if (existOwner.isEmpty()) {
                KakaoLoginEntity loginEntity = kakaoRepository.save(kakaoLoginEntity);

                ResponseCookie cookie = ResponseCookie.from("kakaoLogin", String.valueOf(loginEntity.getKakaoId()))
                        .maxAge(7 * 24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();
                return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
            } else {
                MemberEntity memberEntity = memberRepository.findById(existOwner.get().getMemberEntity().getMemberId()).get();
                LoginTokenSaveDto tokenSaveDto = LoginTokenSaveDto.builder()
                        .id(memberEntity.getMemberId())
                        .email(memberEntity.getMemberEmail())
                        .memberNickName(memberEntity.getMemberNickname())
                        .memberRole(memberEntity.getMemberRole())
                        .build();
                TokenDto newCreatedToken = tokenProvider.createToken(memberEntity.getMemberId(), tokenSaveDto);
                RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                        .refreshToken(newCreatedToken.getRefreshToken())
                        .memberEntity(memberEntity)
                        .build();
                refreshTokenRepository.save(refreshTokenEntity);
                TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                        .memberNickName(memberEntity.getMemberNickname())
                        .accessToken(newCreatedToken.getAccessToken())
                        .accessTokenExpireDate(newCreatedToken.getAccessTokenExpireDate())
                        .issuedAt(newCreatedToken.getIssuedAt())
                        .build();
                ResponseCookie cookie = ResponseCookie.from("refreshToken", newCreatedToken.getRefreshToken())
                        .maxAge(7 * 24 * 60 * 60)
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(true)
                        .build();
                return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public KakaoLoginEntity getKakaoInfo(String kakaoAccessToken) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        KakaoAccountDto kakaoAccountDto = null;
        try {

            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Long kakaoId = kakaoAccountDto.getId();
        KakaoLoginEntity existOwner = kakaoRepository.findByKakaoLoginId(kakaoId).orElse(null);

        if (existOwner != null) {
            return KakaoLoginEntity.builder()
                    .kakaoId(existOwner.getKakaoId())
                    .kakaoLoginId(existOwner.getKakaoLoginId())
                    .kakaoEmail(existOwner.getKakaoEmail())
                    .memberEntity(existOwner.getMemberEntity())
                    .build();
        } else {
            return KakaoLoginEntity.builder()
                    .kakaoLoginId(kakaoAccountDto.getId())
                    .kakaoEmail(kakaoAccountDto.getKakao_account().getEmail())
                    .build();
        }
    }

    //로컬 테스트용
//    @Transactional
//    public KakaoTokenDto getKakaoAccessToken1(String code) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        params.add("client_secret", clientSecret);
//        params.add("redirect_url", "http://localhost:8080/login/oauth2/code/kakao");
//        params.add("code", code);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//
//        RestTemplate rt = new RestTemplate();
//        ResponseEntity<String> accessTokenResponse = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                String.class
//        );
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        KakaoTokenDto kakaoTokenDto = null;
//        try {
//            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return kakaoTokenDto;
//    }
//
//    public ResponseEntity<MemberProfileDto> userProfile(String userId) {
//        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId)).get();
//        MemberProfileDto memberProfileDto = MemberProfileDto.builder()
//                .email(memberEntity.getMemberEmail())
//                .nickName(memberEntity.getMemberNickname())
//                .phone(memberEntity.getMemberPhone())
//                .memberProfilePicture(memberEntity.getMemberProfilePicture())
//                .build();
//        return ResponseEntity.status(200).body(memberProfileDto);
//    }


    public void editMember(String userId, MemberEditDto memberEditDto) {
        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId)).get();
        if (!memberEditDto.getNickName().equals("") && !memberEntity.getMemberNickname().equals(memberEditDto.getNickName())) {
            memberEntity.setMemberNickname(memberEditDto.getNickName());
        }
        if (!memberEditDto.getPassword().equals("") && !findByPasswordCheck(memberEntity.getMemberEmail(), memberEditDto.getPassword())) {
            String encPassword = bCryptPasswordEncoder.encode(memberEditDto.getPassword());
            memberEntity.setMemberPassword(encPassword);
        }
        if (!memberEditDto.getPhone().equals("") && !memberEntity.getMemberPhone().equals(memberEditDto.getPhone())) {
            memberEntity.setMemberPhone(memberEditDto.getPhone());
        }
        memberRepository.save(memberEntity);
    }

    public void editMemberProfileImage(String userId, MemberEditDto memberEditDto, MultipartFile profileImage) {
        editMember(userId, memberEditDto);
        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId)).get();
        String saveUrl = memberImageService.imageSave(profileImage);
        if (!memberEntity.getMemberProfilePicture().equals(defaultMemberImage)) {
            memberImageService.deleteImageFromS3(memberEntity.getMemberProfilePicture());
            memberEntity.setMemberProfilePicture(saveUrl);
        } else {
            memberEntity.setMemberProfilePicture(saveUrl);
        }
        memberRepository.save(memberEntity);
    }

    @Transactional
    public void memberWithdrawal(String userId) {
        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId)).get();
        memberEntity.setMemberState(false);
        memberEntity.setMemberPhone(null);
        kakaoRepository.deleteByMemberEntityMemberId(memberEntity.getMemberId());
        if (!memberEntity.getMemberProfilePicture().equals(defaultMemberImage)) {
            memberImageService.deleteImageFromS3(memberEntity.getMemberProfilePicture());
            memberEntity.setMemberProfilePicture(defaultMemberImage);
        }
        memberRepository.save(memberEntity);
    }

    private GoogleDto getUserInfo(String code) throws JsonProcessingException {
        ResponseEntity<String> accessToken = googleOauth.requestAccessToken(code);
        GoogleTokenDto tokenDto = googleOauth.getAccessToken(accessToken);
        ResponseEntity<String> userInfo = googleOauth.requestUserInfo(tokenDto);
        GoogleDto googleDto = googleOauth.getUserInfo(userInfo);
        return googleDto;
    }


    public ResponseEntity<?> googleLogin(String code) throws JsonProcessingException {
        GoogleDto googleDto = getUserInfo(code);
        Optional<GoogleEntity> googleEntity = googleRepository.findByGoogleId(googleDto.getId());
        if (googleEntity.isEmpty()){
            GoogleEntity googleSaveEntity = GoogleEntity.builder()
                    .googleId(googleDto.getId())
                    .email(googleDto.getEmail())
                    .build();
            Long id = googleRepository.save(googleSaveEntity).getId();
            ResponseCookie cookie = ResponseCookie.from("kakaoLogin", String.valueOf(id))
                    .maxAge(7 * 24 * 60 * 60)
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .httpOnly(true)
                    .build();
            return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
        }else{
            GoogleEntity googleEntity1 = googleEntity.get();
            MemberEntity memberEntity = memberRepository.findById(googleEntity1.getMemberEntity().getMemberId()).get();
            LoginTokenSaveDto tokenSaveDto = LoginTokenSaveDto.builder()
                    .id(memberEntity.getMemberId())
                    .email(memberEntity.getMemberEmail())
                    .memberNickName(memberEntity.getMemberNickname())
                    .memberRole(memberEntity.getMemberRole())
                    .build();
            TokenDto newCreatedToken = tokenProvider.createToken(memberEntity.getMemberId(), tokenSaveDto);
            RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                    .refreshToken(newCreatedToken.getRefreshToken())
                    .memberEntity(memberEntity)
                    .build();
            refreshTokenRepository.save(refreshTokenEntity);
            TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                    .memberNickName(memberEntity.getMemberNickname())
                    .accessToken(newCreatedToken.getAccessToken())
                    .accessTokenExpireDate(newCreatedToken.getAccessTokenExpireDate())
                    .issuedAt(newCreatedToken.getIssuedAt())
                    .build();
            ResponseCookie cookie = ResponseCookie.from("refreshToken", newCreatedToken.getRefreshToken())
                    .maxAge(7 * 24 * 60 * 60)
                    .path("/")
                    .secure(true)
                    .sameSite("None")
                    .httpOnly(true)
                    .build();
            return ResponseEntity.status(200).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(tokenRequestDto);
        }

    }
}
