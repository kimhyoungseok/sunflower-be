package com.hamtaro.sunflowerplate.entity.member.google;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "google_login")

public class GoogleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String googleId;

    @Column
    private String email;

    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;
}
