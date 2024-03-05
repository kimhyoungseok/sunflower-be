package com.hamtaro.sunflowerplate.repository.member;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByMemberEmail(String email);

    MemberEntity findByMemberPhone(String telNumber);

    MemberEntity findByMemberNickname(String nickName);


}
