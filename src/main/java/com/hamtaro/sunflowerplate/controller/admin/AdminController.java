package com.hamtaro.sunflowerplate.controller.admin;


import com.hamtaro.sunflowerplate.dto.admin.RestaurantSaveDto;
import com.hamtaro.sunflowerplate.dto.admin.UpdateRestaurantInfoDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.admin.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/sunflowerPlate/admin")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "관리자 관련 API")
public class AdminController {

    private final AdminService adminService;
    private final TokenProvider tokenProvider;

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "리뷰 삭제", description = "관리자 관련 API")
    @DeleteMapping("/review/delete")
    public ResponseEntity<?> removeAdminReview(@RequestParam Long reviewId, HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.deleteAdminReview(reviewId, userId);
    }

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "신고 확인", description = "관리자 관련 API")
    //관리자 신고 확인
    @GetMapping("/review/")
    public ResponseEntity<?> reviewReport(HttpServletRequest request) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.adminReportCheck(userId);
    }

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "식당 정보 수정 요청 확인", description = "관리자 관련 API")
    //관리자 식당 정보 수정 요청 확인
    @GetMapping("/restaurant/edit/")
    public ResponseEntity<?> requestRestaurant(HttpServletRequest request) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.adminRestaurantModifyCheck(userId);
    }

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "식당 정보 등록", description = "관리자 관련 API")
    // 식당 정보 등록
    @PostMapping(consumes = {"multipart/form-data"}, value = "/restaurant/registration")
    public ResponseEntity<?> saveRestaurantInfo(HttpServletRequest request,
                                                @RequestPart(value = "data") RestaurantSaveDto restaurantSaveDto,
                                                @RequestPart(name = "file") List<MultipartFile> multipartFilelist) throws IOException {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return adminService.saveRestaurant(restaurantSaveDto, multipartFilelist);
    }

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "식당 정보 수정", description = "관리자 관련 API")
    // 식당 정보 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/restaurant/{restaurantId}")
    public ResponseEntity<?> updateRestaurantInfo(HttpServletRequest request,
                                                  @PathVariable Long restaurantId,
                                                  @RequestPart(value = "data") UpdateRestaurantInfoDto restaurantDto,
                                                  @RequestPart(name = "file", required = false) List<MultipartFile> multipartFilelist) throws IOException {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return adminService.updateRestaurantInfo(restaurantId, restaurantDto, multipartFilelist);
    }

    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "식당 정보 수정", description = "관리자 관련 API")
    // 식당 정보 수정
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<?> getRestaurantInfoForAdmin(HttpServletRequest request,
                                                  @PathVariable Long restaurantId ) throws IOException {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return adminService.findRestaurantInfoForAdmin(restaurantId);
    }


    @Tag(name = "관리자", description = "관리자 관련 API")
    @Operation(summary = "폐업 포함 식당 이름 검색, 지역 조회 및 정렬", description = "식당 관련 API")
    @GetMapping("/restaurant")
    public  ResponseEntity<?> findAllRestaurantIncludeClose(HttpServletRequest request,
                                                                              @RequestParam(defaultValue = "") String keyword,
                                                                              @RequestParam(defaultValue = "latest") String sort,
                                                                              @RequestParam(required = false) String city,
                                                                              @RequestParam(required = false) String district,
                                                                              @RequestParam(required = false) String dong,
                                                                              @RequestParam(defaultValue = "1") int page) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return adminService.findRestaurantForAdmin(page-1, sort, keyword, city, district, dong);
    }
}
