package com.hamtaro.sunflowerplate.entity.restaurant;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "restaurant_image")
public class RestaurantImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restaurant_image_id")
    private Long restaurantImageId;

    @Column(name = "restaurant_origin_name")
    private String restaurantOriginName; //이미지 원본 이름

    @Column(name = "restaurant_stored_name")
    private String restaurantStoredName; //S3 저장 이름

    @Column(name="restaurant_resized_stored_name")
    private String restaurantResizedStoredName; //S3 저장 이름

    @Column(name = "restaurant_origin_url")
    private String restaurantOriginUrl; //원본사진 URL

    @Column(name = "restaurant_resize_url")
    private String restaurantResizeUrl; //리사이징 된 사진 URL

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;

}
