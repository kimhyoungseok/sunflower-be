package com.hamtaro.sunflowerplate.jwt.service;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.jwt.dto.CustomUserDetails;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService {
    private final MemberRepository memberRepository;

    @Transactional
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {

        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userPk)).get();
        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setID(memberEntity.getMemberEmail());
        customUserDetails.setPASSWORD(memberEntity.getMemberPassword());
        customUserDetails.setNAME(memberEntity.getMemberNickname());
        customUserDetails.setAUTHORITY(memberEntity.getMemberRole());
        return customUserDetails;
    }
}
