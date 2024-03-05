package com.hamtaro.sunflowerplate.entity.address;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dong")
public class DongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dong_id")
    private Long dongId;

    @Column(name = "dong_name", length = 10)
    private String dongName;

    @ManyToOne
    @JoinColumn(name = "districts_id")
    private DistrictsEntity districtsEntity;

    @OneToMany(mappedBy = "dongEntity", cascade = CascadeType.ALL)
    private List<RestaurantEntity> restaurantEntityList;
}
