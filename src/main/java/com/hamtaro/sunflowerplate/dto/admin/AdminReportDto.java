package com.hamtaro.sunflowerplate.dto.admin;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminReportDto {

    private Long reviewId;

    private Long memberId;

    private String nickName;

    private String reviewAuthor;

    private String reviewProfilePicture;

    private String memberProfilePicture;

    private String reportCategory;

    private String reportContent;

    private LocalDate reportAt;


}
