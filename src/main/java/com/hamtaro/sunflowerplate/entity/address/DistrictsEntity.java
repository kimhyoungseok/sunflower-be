package com.hamtaro.sunflowerplate.entity.address;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "districts")
public class DistrictsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "districts_id")
    private Long districtsId;

    @Column(name = "districts_name", length = 10)
    private String districtsName;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private CityEntity cityEntity;

    @OneToMany(mappedBy = "districtsEntity", cascade = CascadeType.ALL)
    private List<DongEntity> dongEntityList;
}
