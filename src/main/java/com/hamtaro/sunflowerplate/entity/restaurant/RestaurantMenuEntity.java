package com.hamtaro.sunflowerplate.entity.restaurant;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "restaurant_menu")
public class RestaurantMenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "restaurant_menu_name", nullable = false, length = 100)
    private String restaurantMenuName;

    @Column(name = "restaurant_menu_price")
    private Integer restaurantMenuPrice;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurantEntity;
}
