package com.hamtaro.sunflowerplate.service.member;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplate.dto.member.MyPlaceDto;
import com.hamtaro.sunflowerplate.dto.member.MyReviewDto;
import com.hamtaro.sunflowerplate.dto.member.UpdateReviewDto;
import com.hamtaro.sunflowerplate.dto.member.UpdateReviewImageDto;
import com.hamtaro.sunflowerplate.dto.review.ReviewImageDto;
import com.hamtaro.sunflowerplate.dto.review.ReviewReturnDto;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.review.EmpathyEntity;
import com.hamtaro.sunflowerplate.entity.review.LikeCountEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.LikeCountRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantRepository;
import com.hamtaro.sunflowerplate.repository.review.EmpathyRepository;
import com.hamtaro.sunflowerplate.repository.review.ReviewImageRepository;
import com.hamtaro.sunflowerplate.repository.review.ReviewRepository;
import com.hamtaro.sunflowerplate.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewService reviewService;
    private final LikeCountRepository likeCountRepository;
    private final RestaurantRepository restaurantRepository;
    private final EmpathyRepository empathyRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.review-img}")
    private String bucketName;

    public ResponseEntity<?> getMyReview(String userId) {
        MemberEntity findId = memberRepository.findById(Long.valueOf(userId)).get();
        List<ReviewEntity> myReview = reviewRepository.findByMemberEntity_MemberId(findId.getMemberId());
        List<MyReviewDto> myReviewDtoList = new ArrayList<>();
        for (ReviewEntity reviewEntity : myReview) {
            List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();

            for (ReviewImageEntity reviewImageEntity : reviewEntity.getReviewImageEntityList()) {
                ReviewImageDto reviewImageDto =
                        ReviewImageDto.builder()
                                .reviewImageId(reviewImageEntity.getReviewImageId())
                                .reviewOriginName(reviewImageEntity.getReviewOriginName())
                                .reviewOriginUrl(reviewImageEntity.getReviewOriginUrl())
                                .reviewResizeUrl(reviewImageEntity.getReviewResizeUrl())
                                .reviewStoredName(reviewImageEntity.getReviewStoredName())
                                .build();
                reviewImageDtoList.add(reviewImageDto);
            }
            MyReviewDto myReviewDto =
                    MyReviewDto.builder()
                            .reviewId(reviewEntity.getReviewId())
                            .restaurantId(reviewEntity.getRestaurantEntity().getRestaurantId()) // 레스토랑ID
                            .restaurantName(reviewEntity.getRestaurantEntity().getRestaurantName()) //레스토랑이름
                            .reviewContent(reviewEntity.getReviewContent()) //리뷰내용
                            .reviewStarRating(reviewEntity.getReviewStarRating()) //리뷰별점
                            .reviewImageDto(reviewImageDtoList)
                            .reviewAt(reviewEntity.getReviewAt()) // 리뷰작성시간
                            .build();
            myReviewDtoList.add(myReviewDto);
        }
        return ResponseEntity.status(200).body(myReviewDtoList);
    }

    @Transactional
    public ResponseEntity<?> deleteMyReview(Long reviewId, String userId) {
        Optional<List<ReviewImageEntity>> reviewImageId = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId);
        ReviewEntity findReview = reviewRepository.findById(reviewId).get();
        if (findReview.getMemberEntity().getMemberId().equals(Long.valueOf(userId))) {

            if (reviewImageId.isPresent()) {
                for (ReviewImageEntity reviewImageEntity : reviewImageId.get()) {
                    reviewService.deleteS3Image(reviewImageEntity.getReviewStoredName());
                    reviewService.deleteS3Image(reviewImageEntity.getReviewResizeStoredName());
                }
                reviewRepository.deleteById(reviewId);
                return ResponseEntity.status(200).body("리뷰삭제");
            } else {
                reviewRepository.deleteById(reviewId);
                return ResponseEntity.status(200).body("리뷰삭제");
            }
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "권한없음");
            return ResponseEntity.status(403).body(response);
        }
    }

    private File resizeImage(MultipartFile originalImage) throws IOException {
        File resizeFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                .size(400, 400)
                .toFile(resizeFile);
        return resizeFile;
    }

    private String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + filename + ")입니다.");
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    @Transactional
    public Boolean updateMyReview(Long reviewId, UpdateReviewDto updateReviewDto, String userId, List<MultipartFile> imageFile) {
        ReviewEntity findReview = reviewRepository.findById(reviewId).get();
        if (findReview.getMemberEntity().getMemberId().equals(Long.valueOf(userId))) {
            for (UpdateReviewImageDto updateReviewImageDto : updateReviewDto.getImageDtoList()) {
                for (ReviewImageEntity reviewImageEntity : findReview.getReviewImageEntityList()) {
                    if (updateReviewImageDto.getImageId().equals(reviewImageEntity.getReviewImageId())) {
                        reviewService.deleteS3Image(reviewImageEntity.getReviewStoredName());
                        reviewService.deleteS3Image(reviewImageEntity.getReviewResizeStoredName());
                        reviewImageRepository.deleteById(reviewImageEntity.getReviewImageId());
                    }
                }
            }

            List<ReviewImageEntity> reviewImageEntityList = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId)
                    .get();
            findReview.setReviewImageEntityList(reviewImageEntityList);
            Long delSaveReviewId = reviewRepository.save(findReview).getReviewId();
            if (updateReviewDto.getImageDtoList().size() == 3) {
                for (ReviewImageEntity reviewImageEntity : findReview.getReviewImageEntityList()) {
                    reviewService.deleteS3Image(reviewImageEntity.getReviewStoredName());
                    reviewService.deleteS3Image(reviewImageEntity.getReviewResizeStoredName());
                    reviewImageRepository.deleteById(reviewImageEntity.getReviewImageId());
                }
                findReview.setReviewContent(updateReviewDto.getReviewContent());
                Long saveReviewId = reviewRepository.save(findReview).getReviewId();
                ReviewEntity updateReview = reviewRepository.findById(saveReviewId).get();
                if(imageFile.size()>1) {
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

                            //이미지 저장
                            reviewImageRepository.save(ReviewImageEntity.builder()
                                    .reviewOriginName(fileName)
                                    .reviewStoredName(storedName)
                                    .reviewResizeStoredName(resizeName)
                                    .reviewOriginUrl(accessUrl)
                                    .reviewResizeUrl(resizeUrl)
                                    .reviewEntity(updateReview)
                                    .build());

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
                }
            } else if (updateReviewDto.getImageDtoList().size() > 0) {
                ReviewEntity updateReviewEntity = reviewRepository.findById(delSaveReviewId).get();
                updateReviewEntity.setReviewContent(updateReviewDto.getReviewContent());
                Long saveReviewId = reviewRepository.save(updateReviewEntity).getReviewId();
                ReviewEntity updateReview = reviewRepository.findById(saveReviewId).get();
                if(imageFile.size()>1) {
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
                                    .reviewEntity(updateReview)
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
                }
            } else {
                ReviewEntity updateReviewEntity = reviewRepository.findById(delSaveReviewId).get();
                updateReviewEntity.setReviewContent(updateReviewDto.getReviewContent());
                reviewRepository.save(updateReviewEntity);
            }
            return true;
        } else {
            return false;
        }
    }

    public ResponseEntity<?> updateMyReview(Long reviewId) {
        ReviewEntity responseEntity = reviewRepository.findById(reviewId).get();
        List<ReviewImageDto> reviewImageDtoList = new ArrayList<>();

        for (ReviewImageEntity reviewImageEntity : reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId).get()) {
            ReviewImageDto reviewImageDto =
                    ReviewImageDto.builder()
                            .reviewImageId(reviewImageEntity.getReviewImageId())
                            .reviewOriginName(reviewImageEntity.getReviewOriginName())
                            .reviewOriginUrl(reviewImageEntity.getReviewOriginUrl())
                            .reviewResizeUrl(reviewImageEntity.getReviewResizeUrl())
                            .reviewStoredName(reviewImageEntity.getReviewStoredName())
                            .build();
            reviewImageDtoList.add(reviewImageDto);
        }
        boolean empathyReview = false;
        for (EmpathyEntity empathyEntity : responseEntity.getEmpathyEntityList()) {
            if(empathyEntity.getMemberEntity().getMemberId().equals(responseEntity.getMemberEntity().getMemberId())){
               empathyReview = empathyEntity.getEmpathyState();
               break;
            }
        }

        ReviewReturnDto reviewReturnDto = ReviewReturnDto.builder()
                .reviewId(responseEntity.getReviewId())
                .memberId(responseEntity.getMemberEntity().getMemberId())
                .memberNickname(responseEntity.getMemberEntity().getMemberNickname())
                .memberProfilePicture(responseEntity.getMemberEntity().getMemberProfilePicture())
                .reviewContent(responseEntity.getReviewContent())
                .reviewStarRating(responseEntity.getReviewStarRating())
                .reviewAt(responseEntity.getReviewAt())
                .reviewImageDtoList(reviewImageDtoList)
                .reviewEmpathyCount(responseEntity.getEmpathyEntityList().size())
                .empathyReview(empathyReview)
                .build();
//        MyReviewDto myReviewDto =
//                MyReviewDto.builder()
//                        .restaurantId(responseEntity.getRestaurantEntity().getRestaurantId()) // 레스토랑ID
//                        .restaurantName(responseEntity.getRestaurantEntity().getRestaurantName()) //레스토랑이름
//                        .reviewContent(responseEntity.getReviewContent()) //리뷰내용
//                        .reviewStarRating(responseEntity.getReviewStarRating()) //리뷰별점
//                        .reviewImageDto(reviewImageDtoList)
//                        .reviewAt(responseEntity.getReviewAt()) // 리뷰작성시간
//                        .build();
        return ResponseEntity.status(200).body(reviewReturnDto);

    }


    public ResponseEntity<?> getMyPlace(String userId) {
        MemberEntity findId = memberRepository.findById(Long.valueOf(userId)).get();
        List<LikeCountEntity> myPlace = likeCountRepository.findByMemberEntity_MemberId(findId.getMemberId());
        List<MyPlaceDto> myPlaceDtoList = new ArrayList<>();
        for (LikeCountEntity likeCountEntity : myPlace) {
            MyPlaceDto myPlaceDto = MyPlaceDto.builder()
                    .restaurantId(likeCountEntity.getRestaurantEntity()
                            .getRestaurantId())
                    .restaurantName(likeCountEntity.getRestaurantEntity()
                            .getRestaurantName())
                    .restaurantAddress(likeCountEntity.getRestaurantEntity()
                            .getRestaurantAddress())
                    .restaurantWebSite(likeCountEntity.getRestaurantEntity()
                            .getRestaurantWebSite())
                    .resizeImgUrl(likeCountEntity.getRestaurantEntity()
                            .getRestaurantImageEntity()
                            .get(0)
                            .getRestaurantResizeUrl())
                    .build();
            myPlaceDtoList.add(myPlaceDto);
        }
        return ResponseEntity.status(200).body(myPlaceDtoList);
    }

    public ResponseEntity<?> clickLikeButton(Long restaurantId, String userId) {
        Long memberId = Long.valueOf(userId);
        Map<String, Object> likeCountMap = new HashMap<>();
        // 좋아요가 있는지 체크
        Optional<LikeCountEntity> likeCountEntityOptional = likeCountRepository.findByMemberEntityAndRestaurantEntity(memberId, restaurantId);

        // 장소 저장이 되었는지 체크 후 장소 저장 카운트 리턴
        int likeCount;
        boolean likeButton;
        if (likeCountEntityOptional.isPresent()) { // 찜이 이미 존재하는 경우
            boolean likeStatus = likeCountEntityOptional.get().isLikeStatus();
            likeCountEntityOptional.get().setLikeStatus(!likeStatus);
            likeCountRepository.save(likeCountEntityOptional.get());
            likeButton = !likeStatus;
        } else { // 값이 없다면 생성하기
            LikeCountEntity likeCountEntity = LikeCountEntity
                    .builder()
                    .memberEntity(memberRepository.findById(memberId).get()) // 로그인한 아이디 값 가져오도록 수정 필요
                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
                    .likeStatus(true)
                    .build();
            likeCountRepository.save(likeCountEntity);
            likeButton = true;
        }
        likeCount = likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantId);

        likeCountMap.put("likeButtonClicked", likeButton);
        likeCountMap.put("likeCount", likeCount);

        return ResponseEntity.status(200).body(likeCountMap);
    }


}
