package com.hamtaro.sunflowerplate.controller.review;

import com.hamtaro.sunflowerplate.dto.review.ReportDto;
import com.hamtaro.sunflowerplate.dto.review.RequestUpdateDto;
import com.hamtaro.sunflowerplate.dto.review.ReviewSaveDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;

import com.hamtaro.sunflowerplate.service.review.EmpathyService;
import com.hamtaro.sunflowerplate.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/sunflowerPlate/user")
@RequiredArgsConstructor
@Tag(name = "리뷰", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final EmpathyService empathyService;
    private final TokenProvider tokenProvider;


    //유저 레스토랑 정보 신고 및 수정
    @Tag(name = "식당", description = "식당 관련 API")
    @Operation(summary = "레스토랑 정보 신고 및 수정 요청", description = "식당 관련 API")
    @PostMapping("/restaurant/edit")
    public ResponseEntity<Map<String, String>> requestRestaurant(HttpServletRequest request,
                                                                 @RequestBody RequestUpdateDto requestUpdateDto) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return reviewService.requestRestaurant(requestUpdateDto, userId);
    }


    //리뷰 작성 후 저장
    @Tag(name = "리뷰", description = "리뷰 관련 API")
    @Operation(summary = "리뷰 작성 후 저장", description = "리뷰 관련 API")
    @PostMapping(consumes = {"multipart/form-data"}, value="/review/new")
    public ResponseEntity<?> createReview(@RequestPart("reviewSaveDto") ReviewSaveDto reviewSaveDto,
                                          @RequestPart(required = false) List<MultipartFile> imageFile,
                                          @RequestParam Long restaurantId, HttpServletRequest request) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return reviewService.saveUserReview(reviewSaveDto, imageFile, restaurantId, userId);
    }

    //리뷰 신고하기
    @Tag(name = "리뷰", description = "리뷰 관련 API")
    @Operation(summary = "리뷰 신고하기", description = "리뷰 관련 API")
    @PostMapping("/report")
    public ResponseEntity<?> alertReview(@RequestBody ReportDto reportDto, HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return reviewService.reportReview(reportDto, userId);
    }

    //좋아요기능
    @Tag(name = "리뷰", description = "리뷰 관련 API")
    @Operation(summary = "리뷰 좋아요", description = "리뷰 관련 API")
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<?> like(@PathVariable Long reviewId, HttpServletRequest request) {

        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);

        return empathyService.countPlus(reviewId,userId);

    }
}
