package com.hamtaro.sunflowerplate.controller.member;

import com.hamtaro.sunflowerplate.dto.member.UpdateReviewDto;
import com.hamtaro.sunflowerplate.jwt.config.TokenProvider;
import com.hamtaro.sunflowerplate.service.member.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sunflowerPlate/mypage")
@Tag(name = "마이페이지", description = "마이페이지 관련 API")
public class MyPageController {

    private final MyPageService myPageService;
    private final TokenProvider tokenProvider;

    @Tag(name = "마이페이지", description = "내 리뷰 조회 API")
    @Operation(summary = "내 리뷰 조회", description = "내 리뷰 조회하기")
    @GetMapping("/myreview")
    public ResponseEntity<?> getMyReview(HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.getMyReview(userId);
    }

    @Tag(name = "마이페이지", description = "내 리뷰 삭제 API")
    @Operation(summary = "내 리뷰 삭제", description = "내 리뷰 삭제하기")
    @DeleteMapping("/myreview")
    public ResponseEntity<?> deleteMyReview(HttpServletRequest request, @RequestParam Long reviewId) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.deleteMyReview(reviewId, userId);
    }

    @Tag(name = "마이페이지", description = "내 리뷰 수정 API")
    @Operation(summary = "내 리뷰 수정", description = "내 리뷰 수정하기")
    @PutMapping("/myreview")
    public ResponseEntity<?> updateMyReview(HttpServletRequest request, @RequestParam Long reviewId, @RequestPart UpdateReviewDto updateReviewDto, @RequestPart List<MultipartFile> imageFile) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        Boolean check = myPageService.updateMyReview(reviewId, updateReviewDto, userId, imageFile);
        if (check) {
            return myPageService.updateMyReview(reviewId);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "권한없음");
            return ResponseEntity.status(403).body(response);

        }
    }

    @Tag(name = "마이페이지", description = "내 장소 조회 API")
    @Operation(summary = "내 장소 조회", description = "내 장소 조회하기")
    @GetMapping("/myplace")
    public ResponseEntity<?> getMyPlace(HttpServletRequest request) {
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.getMyPlace(userId);
    }

    @Tag(name = "식당", description = "식당 관련 API")
    @Operation(summary = "식당 좋아요 저장", description = "식당 관련 API")
    @GetMapping("/like")
    public ResponseEntity<?> clickLike(HttpServletRequest request,@RequestParam Long restaurantId){
        String header = request.getHeader(tokenProvider.loginAccessToken);
        String userId = tokenProvider.getUserPk(header);
        return myPageService.clickLikeButton(restaurantId,userId);
    }

}
