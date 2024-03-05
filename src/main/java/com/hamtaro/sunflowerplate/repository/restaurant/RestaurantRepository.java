package com.hamtaro.sunflowerplate.repository.restaurant;

import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity,Long> {
    Optional<RestaurantEntity> findByRestaurantId(Long useId);

    // 리뷰 많은 순 정렬
    // 1. 키워드 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY r.reviewEntityList.size DESC ,COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantName(Pageable pageable, @Param("keyword") String keyword);

    // 2. 키워드, dong 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName = :dong " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY r.reviewEntityList.size DESC ,COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongName(Pageable pageable, @Param("keyword") String keyword, @Param("dong") String dong);

    // 3. 키워드, district 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName = :district " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY r.reviewEntityList.size DESC ,COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsName(Pageable pageable, @Param("keyword") String keyword, @Param("district")String district);

    // 4. 키워드, city 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY r.reviewEntityList.size DESC ,COALESCE(AVG(review.reviewStarRating),0) DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityName(Pageable pageable, @Param("keyword") String keyword, @Param("city") String city);

    // 좋아요 많은 순 정렬
    // 1. 키워드 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC ,COALESCE(AVG(review.reviewStarRating),0) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword);

    // 2. 키워드, dong 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName = :dong " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC, COALESCE(AVG(review.reviewStarRating),0) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, @Param("dong") String dong);

    // 3. 키워드, district 입력
    @Query ("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName = :district " +
            "AND r.restaurantStatus = 'OPEN'" +
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC, COALESCE(AVG(review.reviewStarRating),0) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, @Param("district") String district);

    // 4. 키워드, city 입력
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN LikeCountEntity l ON r.restaurantId = l.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city " +
            "AND r.restaurantStatus = 'OPEN'"+
            "GROUP BY r.restaurantId " +
            "ORDER BY SUM(CASE WHEN l.likeStatus = true THEN 1 ELSE 0 END) DESC, COALESCE(AVG(review.reviewStarRating),0) DESC")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndLikeCountEntity_likeStatus(Pageable pageable, @Param("keyword") String keyword, @Param("city")String city);

    // 1. 별점 순 정렬 + 키워드
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC, r.reviewEntityList.size DESC")
    Page<RestaurantEntity> findByRate(Pageable pageable, @Param("keyword") String keyword);

    // 2. 별점 순 정렬 + 키워드, dong 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.dongName =:dong " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC, r.reviewEntityList.size DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DongNameAndRate(Pageable pageable, @Param("keyword") String keyword, @Param("dong") String dong);

    // 3. 별점 순 정렬 + 키워드, district 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.districtsName =:district " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC, r.reviewEntityList.size DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_DistrictsNameAndRate(Pageable pageable, @Param("keyword") String keyword, @Param("district") String district);

    // 4. 별점 순 정렬 + 키워드, city 입력
    @Query("SELECT r " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantName LIKE %:keyword% " +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName =:city " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r.restaurantId " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC, r.reviewEntityList.size DESC ")
    Page<RestaurantEntity> findByRestaurantNameAndDongEntity_DistrictsEntity_CityEntity_CityNameAndRate(Pageable pageable, @Param("keyword") String keyword, @Param("city") String city);

    // 별점 계산
    @Query("SELECT ROUND(COALESCE(AVG(review.reviewStarRating),0),2) " +
            "FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantId = :restaurantId")
    BigDecimal findStarRateByRestaurantId(Long restaurantId);

    // 관리자용 정렬
    // 키워드 검색
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword%")
    Page<RestaurantEntity> findByRestaurantNameForAdmin(Pageable pageable, @Param("keyword") String keyword);
    // 동 검색 + 키워드 검색
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword%" +
            "AND r.dongEntity.dongName = :dong")
    Page<RestaurantEntity> findByRestaurantNameAndDongNameForAdmin(Pageable pageable, @Param("keyword") String keyword, @Param("dong") String dong);

    // 구 검색 + 키워드 검색
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword%" +
            "AND r.dongEntity.districtsEntity.districtsName = :distirct")
    Page<RestaurantEntity> findByRestaurantNameAndDistrictNameForAdmin(Pageable pageable, @Param("keyword") String keyword, @Param("distirct") String distirct);

    // 시 검색 + 키워드 검색
    @Query("SELECT r FROM RestaurantEntity r " +
            "WHERE r.restaurantName LIKE %:keyword%" +
            "AND r.dongEntity.districtsEntity.cityEntity.cityName = :city")
    Page<RestaurantEntity> findByRestaurantNameAndCityNameForAdmin(Pageable pageable, @Param("keyword") String keyword, @Param("city") String city);


    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 " +
            "AND (r.dongEntity.dongName = '서교동' " +
            "OR r.dongEntity.dongName = '동교동' " +
            "OR r.dongEntity.dongName = '연남동') " +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findRestaurantAtHongdae(Pageable pageable);

    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 " +
            "AND (r.dongEntity.dongName = '창천동' " +
            "OR r.dongEntity.dongName = '신촌동' " +
            "OR r.dongEntity.dongName = '노고산동' " +
            "OR r.dongEntity.dongName = '대현동' )" +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findRestaurantAtSinchon(Pageable pageable);

    // 메인 베스트 식당 조회
    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 " +
            "AND (r.dongEntity.dongName = '안국동' " +
            "OR r.dongEntity.dongName = '재동' " +
            "OR r.dongEntity.dongName = '소격동' " +
            "OR r.dongEntity.dongName = '송현동' " +
            "OR r.dongEntity.dongName = '계동' " +
            "OR r.dongEntity.dongName = '사간동') " +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findRestaurantAtAnguk(Pageable pageable);

    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN RestaurantMenuEntity menu ON r.restaurantId = menu.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 "+
            "AND menu.restaurantMenuName LIKE '%라멘%' " +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findBestRamenRestaurant(Pageable pageable);

    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN RestaurantMenuEntity menu ON r.restaurantId = menu.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 "+
            "AND (menu.restaurantMenuName LIKE '%피자%' OR menu.restaurantMenuName LIKE '%pizza%'" +
            "OR menu.restaurantMenuName LIKE '%마르게리따%' OR menu.restaurantMenuName LIKE '%마리나라%' )" +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findBestPizzaRestaurant(Pageable pageable);

    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN RestaurantMenuEntity menu ON r.restaurantId = menu.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.restaurantStatus = 'OPEN' " +
            "AND r.reviewEntityList.size > 5 "+
            "AND (menu.restaurantMenuName LIKE '%파스타%' OR menu.restaurantMenuName LIKE '%pasta%' " +
            "OR menu.restaurantMenuName LIKE '%알리오%' OR menu.restaurantMenuName LIKE '%뇨끼%' " +
            "OR menu.restaurantMenuName LIKE '%까르보나라%' OR menu.restaurantMenuName LIKE '%스파게티%' " +
            "OR menu.restaurantMenuName LIKE '%라자냐%' OR menu.restaurantMenuName LIKE '%링귀니%' " +
            "OR menu.restaurantMenuName LIKE '%나폴리탄%' )" +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findBestPastaRestaurant(Pageable pageable);

    @Query("SELECT r FROM RestaurantEntity r " +
            "LEFT JOIN RestaurantMenuEntity menu ON r.restaurantId = menu.restaurantEntity.restaurantId " +
            "LEFT JOIN ReviewEntity review ON r.restaurantId = review.restaurantEntity.restaurantId " +
            "WHERE r.reviewEntityList.size > 5 "+
            "AND menu.restaurantMenuName LIKE '%떡볶이%' " +
            "AND r.restaurantStatus = 'OPEN' " +
            "GROUP BY r " +
            "ORDER BY COALESCE(AVG(review.reviewStarRating),0) DESC ")
    List<RestaurantEntity> findBestTteokbokkiRestaurant(Pageable pageable);


}
