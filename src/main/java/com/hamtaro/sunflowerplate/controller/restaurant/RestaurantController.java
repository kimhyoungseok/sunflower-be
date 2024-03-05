package com.hamtaro.sunflowerplate.controller.restaurant;

import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.restaurant.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/restaurant")
@Tag(name = "식당", description = "식당 관련 API")
public class RestaurantController {
    private final RestaurantService restaurantService;
    private final TokenProvider tokenProvider;

    @Tag(name = "식당", description = "식당 관련 API")
    @Operation(summary = "식당 정보 상세 조회 및 리뷰 조회", description = "식당 관련 API")
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> findRestaurantInfo(HttpServletRequest request,
                                                @PathVariable Long restaurantId,
                                                @RequestParam(defaultValue = "1") int reviewPage,
                                                @RequestParam(defaultValue = "reviewAt") String reviewSort){
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId;
        if(header == null) {
            userId = "notLogin";
        } else {
            userId = tokenProvider.getUserPk(header);
        }
        return restaurantService.findRestaurantDetailsById(restaurantId, reviewPage-1, reviewSort, userId);
    }

    @Tag(name = "식당", description = "식당 관련 API")
    @Operation(summary = "식당 이름 검색, 지역 조회 및 정렬", description = "식당 관련 API")
    @GetMapping("/search")
    public ResponseEntity<?> findRestaurantList (
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "rateDesc") String sort,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String dong,
            @RequestParam(defaultValue = "1") int page ){
        return restaurantService.findRestaurantByKeyword(page-1, sort, keyword, city, district, dong);
    }

    @Tag(name = "식당", description = "식당 관련 API")
    @Operation(summary = "식당 베스트 조회", description = "식당 관련 API")
    @GetMapping("/best")
    public ResponseEntity<?> bestRestaurantList (
            @RequestParam(defaultValue = "") String address,
            @RequestParam(required = false) String menu){
        return restaurantService.findBestRestaurantList(address,menu);
    }


}