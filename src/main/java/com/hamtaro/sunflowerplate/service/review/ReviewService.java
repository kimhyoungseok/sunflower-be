package com.hamtaro.sunflowerplate.service.review;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplate.dto.review.*;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantRepository;
import com.hamtaro.sunflowerplate.repository.review.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Transactional
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RequestRepository requestRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final RestaurantRepository restaurantRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final EmpathyRepository empathyRepository;


    //식당 정보 수정 요청(Modify x , Post)
    public ResponseEntity<Map<String, String>> requestRestaurant(RequestUpdateDto requestUpdateDto, String userId) {

        MemberEntity findByMemberId = memberRepository.findById(Long.valueOf(userId)).get();

        RestaurantEntity restaurantEntity = restaurantRepository.findById(requestUpdateDto.getRestaurantId()).get();

        RequestEntity reportEntity = RequestEntity.builder()
                .requestAt(LocalDate.now())
                .requestContent(requestUpdateDto.getRequestContent())
                .memberEntity(findByMemberId)
                .restaurantEntity(restaurantEntity)
                .build();

        Map<String, String> map = new HashMap<>();

        requestRepository.save(reportEntity);

        map.put("message", "식당 정보 및 폐업 신고 요청이 되었습니다.");
        return ResponseEntity.status(200).body(map);
    }


    //이미지 파일
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.review-img}")
    private String bucketName;

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + filename + ")입니다.");
        }
    }

    //s3에 저장된 이미지 지우기
    public void deleteS3Image(String fileName) {
        amazonS3Client.deleteObject(bucketName, fileName);
    }

    private File resizeImage(MultipartFile originalImage) throws IOException {
        File resizeFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                .size(400, 400)
                .toFile(resizeFile);
        return resizeFile;
    }

    //리뷰 작성 후 저장
    @Transactional
    public ResponseEntity<?> saveUserReview(ReviewSaveDto reviewSaveDto, List<MultipartFile> imageFile, Long restaurantId, String userId) {

        RestaurantEntity restaurantEntity = restaurantRepository.findByRestaurantId(restaurantId).get();
        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(userId)).get();
        ReviewEntity reviewSaveEntity = ReviewEntity.builder()
                .reviewContent(reviewSaveDto.getReviewContent())
                .reviewStarRating(reviewSaveDto.getReviewStarRating())
                .reviewAt(LocalDateTime.now())
                .memberEntity(memberEntity)
                .restaurantEntity(restaurantEntity)
                .build();
        Long reviewId = reviewRepository.save(reviewSaveEntity).getReviewId();
        ReviewEntity reviewEntity = reviewRepository.findById(reviewId).get();

        if (imageFile != null && imageFile.get(0).getSize() != 0) {

            for (MultipartFile multipartFile : imageFile) {
                String fileName = multipartFile.getOriginalFilename(); //원본 파일
                try {
                    //이미지 객체 생성
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentType(multipartFile.getContentType());
                    objectMetadata.setContentLength(multipartFile.getSize());

                    //원본 파일 저장
                    String storedName = createFileName(fileName);
                    amazonS3Client.putObject(new PutObjectRequest(bucketName, storedName, multipartFile.getInputStream(), objectMetadata));
                    String accessUrl = amazonS3Client.getUrl(bucketName, storedName).toString();
                    System.out.println("Original Image URL:" + accessUrl);

                    //리사이징 파일 저장
                    String resizeName = "resized_" + storedName;
                    File resizedImageFile = resizeImage(multipartFile);
                    amazonS3Client.putObject(bucketName, resizeName, resizedImageFile);
                    String resizeUrl = amazonS3Client.getUrl(bucketName, resizeName).toString();

                    ReviewImageEntity reviewImageEntity = ReviewImageEntity.builder()
                            .reviewOriginName(fileName)
                            .reviewStoredName(storedName)
                            .reviewResizeStoredName(resizeName)
                            .reviewOriginUrl(accessUrl)
                            .reviewResizeUrl(resizeUrl)
                            .reviewEntity(reviewEntity)
                            .build();

                    //이미지 저장
                    reviewImageRepository.save(reviewImageEntity);

                    if (resizedImageFile != null && resizedImageFile.exists()) {
                        if (resizedImageFile.delete()) {
                            log.info("이미지 삭제됨");
                        } else {
                            log.info("이미지 삭제");
                        }
                    } else {
                        log.info("이미지파일 없음");
                    }
                } catch (IOException e) {
                    throw new RuntimeException("이미지 업로드에 실패했습니다.");
                }
            }

            List<ReviewImageEntity> reviewImageEntity = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId).get();
            List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
            for (ReviewImageEntity reviewImage : reviewImageEntity) {
                ReviewImageDto reviewImageDto = ReviewImageDto.builder()
                        .reviewImageId(reviewImage.getReviewImageId())
                        .reviewOriginName(reviewImage.getReviewOriginName())
                        .reviewStoredName(reviewImage.getReviewStoredName())
                        .reviewResizeStoredName(reviewImage.getReviewResizeStoredName())
                        .reviewOriginUrl(reviewImage.getReviewOriginUrl())
                        .reviewResizeUrl(reviewImage.getReviewResizeUrl())
                        .build();
                reviewImageDtoList.add(reviewImageDto);
            }

            ReviewReturnDto reviewReturnDto = ReviewReturnDto.builder()
                    .reviewId(reviewId)
                    .memberProfilePicture(reviewEntity.getMemberEntity().getMemberProfilePicture())
                    .memberNickname(reviewEntity.getMemberEntity().getMemberNickname())
                    .reviewContent(reviewEntity.getReviewContent())
                    .reviewStarRating(reviewEntity.getReviewStarRating())
                    .reviewEmpathyCount(0)
                    .empathyReview(false)
                    .reviewAt(reviewEntity.getReviewAt())
                    .memberId(reviewEntity.getMemberEntity().getMemberId())
                    .reviewImageDtoList(reviewImageDtoList)
                    .build();
            return ResponseEntity.status(200).body(reviewReturnDto);
        } else {
            ReviewReturnDto reviewReturnDto = ReviewReturnDto.builder()
                    .reviewId(reviewId)
                    .memberProfilePicture(reviewEntity.getMemberEntity().getMemberProfilePicture())
                    .memberNickname(reviewEntity.getMemberEntity().getMemberNickname())
                    .reviewContent(reviewEntity.getReviewContent())
                    .reviewStarRating(reviewEntity.getReviewStarRating())
                    .reviewEmpathyCount(0)
                    .empathyReview(false)
                    .reviewAt(reviewEntity.getReviewAt())
                    .memberId(reviewEntity.getMemberEntity().getMemberId())
                    .build();
            return ResponseEntity.status(200).body(reviewReturnDto);
        }
    }

    //리뷰 신고
    public ResponseEntity<?> reportReview(ReportDto reportDto, String useId) {
        MemberEntity memberEntity = memberRepository.findById(Long.valueOf(useId)).get();
        ReviewEntity reviewEntity = reviewRepository.findByReviewId(reportDto.getReviewId()).get();

        Map<String, String> map = new HashMap<>();

        if (reviewEntity.getMemberEntity().getMemberId() != Long.valueOf(useId)){
            ReportEntity reportSaveEntity = ReportEntity.builder()
                    .reportCategory(reportDto.getReportCategory())
                    .reportContent(reportDto.getReportContent())
                    .reportAt(LocalDate.now())
                    .reviewEntity(reviewEntity)
                    .memberEntity(memberEntity)
                    .build();
            Long result = reportRepository.save(reportSaveEntity).getReportId();
            Optional<ReportEntity> findByReportId = reportRepository.findByReportId(result);

            if (findByReportId.isPresent()) {
                map.put("message", "신고가 접수되었습니다.");
                return ResponseEntity.status(200).body(map);

            } else {
                map.put("message", "신고가 접수되지 않았습니다.");
                return ResponseEntity.status(400).body(map);
            }
        } else {
            map.put("message", "신고할 수 없는 아이디입니다");
            return ResponseEntity.status(400).body(map);
        }
    }

    // restaurantId 리뷰 리턴
    public Page<ReviewReturnDto> findReviewPageByRestaurant(Long restaurantId, int page, String sort, String userId) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<ReviewEntity> reviewEntityPage;
        if (sort.equals("empathy")){
            reviewEntityPage = reviewRepository.findReviewEntityByReviewIdAndEmpathyEntity(pageable, restaurantId);
        }
        else {
            reviewEntityPage = reviewRepository.findReviewEntityByReviewIdAndAndReviewAt(pageable,restaurantId);
        }
        return reviewEntityPage.map((ReviewEntity reviewEntity) -> reviewEntityToReviewReturnDto(reviewEntity, userId));
    }

    // entity -> dto
    private ReviewReturnDto reviewEntityToReviewReturnDto (ReviewEntity reviewEntity, String userId) {
            List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();
            List<ReviewImageEntity> reviewImageEntityList = reviewEntity.getReviewImageEntityList();
            for (ReviewImageEntity reviewImage : reviewImageEntityList) {
                ReviewImageDto reviewImageDto = ReviewImageDto.builder()
                        .reviewImageId(reviewImage.getReviewImageId())
                        .reviewOriginName(reviewImage.getReviewOriginName())
                        .reviewStoredName(reviewImage.getReviewStoredName())
                        .reviewResizeStoredName(reviewImage.getReviewResizeStoredName())
                        .reviewOriginUrl(reviewImage.getReviewOriginUrl())
                        .reviewResizeUrl(reviewImage.getReviewResizeUrl())
                        .build();
                reviewImageDtoList.add(reviewImageDto);
            }

        boolean empathyButton;
        if(userId.equals("notLogin")) {
            empathyButton = false;
        } else {
            Long memberId = Long.valueOf(userId);
            if(empathyRepository.findByMemberEntityAndReviewEntity(memberRepository.findById(memberId).get(), reviewEntity).isPresent()){
                empathyButton = empathyRepository.findByMemberEntityAndReviewEntity(memberRepository.findById(memberId).get(), reviewEntity).get().getEmpathyState();
            } else {
                empathyButton = false;
            }
        }

        return ReviewReturnDto.builder()
                .reviewId(reviewEntity.getReviewId())
                .reviewAt(reviewEntity.getReviewAt())
                .reviewStarRating(reviewEntity.getReviewStarRating())
                .memberId(reviewEntity.getMemberEntity().getMemberId())
                .memberNickname(reviewEntity.getMemberEntity().getMemberNickname())
                .memberProfilePicture(reviewEntity.getMemberEntity().getMemberProfilePicture())
                .reviewContent(reviewEntity.getReviewContent())
                .reviewImageDtoList(reviewImageDtoList)
                .empathyReview(empathyButton)
                .reviewEmpathyCount(empathyRepository.countByReviewEntity(reviewEntity))
                .build();
    }
}