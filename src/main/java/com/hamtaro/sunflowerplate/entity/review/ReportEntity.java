package com.hamtaro.sunflowerplate.entity.review;

import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "report")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @Column(name = "report_category", nullable = false, length = 100)
    private String reportCategory;

    @Column(name = "report_content", nullable = false, length = 1000)
    private String reportContent;

    @Column(name = "report_at", nullable = false)
    private LocalDate reportAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity memberEntity;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;
}
