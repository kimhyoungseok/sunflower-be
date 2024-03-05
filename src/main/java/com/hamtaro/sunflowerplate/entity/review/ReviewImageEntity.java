package com.hamtaro.sunflowerplate.entity.review;


import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "review_image")
public class ReviewImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id")
    private Long reviewImageId;

    @Column(name = "review_origin_name")
    private String reviewOriginName; //원본 파일

    @Column(name = "review_stored_name")
    private String reviewStoredName; //s3저장 이름

    @Column(name = "review_resize_stored_name")
    private String reviewResizeStoredName; //리사이즈 된 파일 s3저장 이름

    @Column(name = "review_origin_url")
    private String reviewOriginUrl;

    @Column(name = "review_resize_url")
    private String reviewResizeUrl;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

}
