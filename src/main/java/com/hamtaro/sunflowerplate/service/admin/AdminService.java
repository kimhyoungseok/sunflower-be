package com.hamtaro.sunflowerplate.service.admin;

import com.hamtaro.sunflowerplate.dto.admin.*;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantDto;
import com.hamtaro.sunflowerplate.dto.restaurant.RestaurantImageDto;
import com.hamtaro.sunflowerplate.dto.review.RequestUpdateDto;
import com.hamtaro.sunflowerplate.entity.address.DongEntity;
import com.hamtaro.sunflowerplate.entity.member.MemberEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantMenuEntity;
import com.hamtaro.sunflowerplate.entity.review.ReportEntity;
import com.hamtaro.sunflowerplate.entity.review.RequestEntity;
import com.hamtaro.sunflowerplate.entity.review.ReviewImageEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.*;
import com.hamtaro.sunflowerplate.repository.review.ReportRepository;
import com.hamtaro.sunflowerplate.repository.review.RequestRepository;
import com.hamtaro.sunflowerplate.repository.review.ReviewImageRepository;
import com.hamtaro.sunflowerplate.repository.review.ReviewRepository;
import com.hamtaro.sunflowerplate.repository.member.MemberRepository;
import com.hamtaro.sunflowerplate.service.restaurant.ImageService;
import com.hamtaro.sunflowerplate.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class AdminService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final RequestRepository requestRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewService reviewService;
    private final RestaurantRepository restaurantRepository;
    private final DongRepository dongRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final LikeCountRepository likeCountRepository;
    private final ImageService imageService;

    @Transactional
    public ResponseEntity<?> saveRestaurant(RestaurantSaveDto restaurantSaveDto, List<MultipartFile> multipartFileList) throws IOException {

        // 동엔티티 설정
        DongEntity dong = dongRepository.findByDongName(restaurantSaveDto.getRestaurantAdministrativeDistrict().getDongName()).get();

        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .restaurantName(restaurantSaveDto.getRestaurantName())
                .restaurantTelNum(restaurantSaveDto.getRestaurantTelNum())
                .restaurantAddress(restaurantSaveDto.getRestaurantAddress())
                .restaurantOpenTime(restaurantSaveDto.getRestaurantOpenTime())
                .restaurantWebSite(restaurantSaveDto.getRestaurantWebSite())
                .restaurantStatus("OPEN")
                .dongEntity(dong)
                .build();

        Long restaurantId = restaurantRepository.save(restaurantEntity).getRestaurantId();

        List<RestaurantMenuDto> restaurantMenuDtoList = restaurantSaveDto.getRestaurantMenuDtoList();
        List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();

        for (RestaurantMenuDto restaurantMenuDto : restaurantMenuDtoList) {
            RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
                    .builder()
                    .restaurantMenuName(restaurantMenuDto.getRestaurantMenuName())
                    .restaurantMenuPrice(restaurantMenuDto.getRestaurantMenuPrice())
                    .restaurantEntity(restaurantRepository.findByRestaurantId(restaurantId).get())
                    .build();
            restaurantMenuEntityList.add(restaurantMenuEntity);
        }

        restaurantMenuRepository.saveAll(restaurantMenuEntityList);

        if (multipartFileList != null) {
            String dirName = "restaurant" + restaurantId;
            imageService.upload(multipartFileList, dirName, restaurantEntity);
        }

        if (restaurantRepository.findById(restaurantId).isEmpty()) {
            return ResponseEntity.status(400).body("식당 등록에 실패하였습니다.");
        } else {
            return ResponseEntity.status(200).body("식당 등록에 성공하였습니다.\nrestaurantId : " + restaurantId);
        }
    }

     // 식당 정보 수정용 get
    public ResponseEntity<?> findRestaurantInfoForAdmin(Long restaurantId) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findByRestaurantId(restaurantId);

        if (restaurantEntityOptional.isEmpty()){
            return ResponseEntity.status(400).body("restaurantId가 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();
            // 메뉴 리스트 불러오기
            List<RestaurantMenuUpdateDto> restaurantMenuDtoList = new ArrayList<>();
            for (RestaurantMenuEntity restaurantMenuEntity : restaurantEntity.getRestaurantMenuEntity()) {
                RestaurantMenuUpdateDto restaurantMenuDto = RestaurantMenuUpdateDto
                        .builder()
                        .restaurantMenuId(restaurantMenuEntity.getMenuId())
                        .restaurantMenuName(restaurantMenuEntity.getRestaurantMenuName())
                        .restaurantMenuPrice(restaurantMenuEntity.getRestaurantMenuPrice())
                        .build();
                restaurantMenuDtoList.add(restaurantMenuDto);
            }

            // 이미지 불러오기
            List<RestaurantImageDto> restaurantImageDtoList = new ArrayList<>();
            for(RestaurantImageEntity restaurantImageEntity : restaurantEntity.getRestaurantImageEntity()){
                RestaurantImageDto restaurantImageDto = RestaurantImageDto
                        .builder()
                        .restaurantOriginName(restaurantImageEntity.getRestaurantOriginName())
                        .restaurantStoredName(restaurantImageEntity.getRestaurantStoredName())
                        .restaurantOriginUrl(restaurantImageEntity.getRestaurantOriginUrl())
                        .restaurantResizedStoredName(restaurantImageEntity.getRestaurantResizedStoredName())
                        .restaurantResizeUrl(restaurantImageEntity.getRestaurantResizeUrl())
                        .build();
                restaurantImageDtoList.add(restaurantImageDto);
            }
            RestaurantAdministrativeDistrict restaurantAdministrativeDistrict = RestaurantAdministrativeDistrict.builder()
                    .dongName(restaurantEntity.getDongEntity().getDongName())
                    .districtsName(restaurantEntity.getDongEntity().getDistrictsEntity().getDistrictsName())
                    .cityName(restaurantEntity.getDongEntity().getDistrictsEntity().getCityEntity().getCityName())
                    .build();


            UpdateRestaurantInfoDto updateRestaurantInfoDto = UpdateRestaurantInfoDto.builder()
                    .restaurantId(restaurantId)
                    .restaurantName(restaurantEntity.getRestaurantName())
                    .restaurantAddress(restaurantEntity.getRestaurantAddress())
                    .restaurantStatus(restaurantEntity.getRestaurantStatus())
                    .restaurantAdministrativeDistrict(restaurantAdministrativeDistrict)
                    .restaurantImageDtoList(restaurantImageDtoList)
                    .restaurantMenuDtoList(restaurantMenuDtoList)
                    .restaurantOpenTime(restaurantEntity.getRestaurantOpenTime())
                    .restaurantTelNum(restaurantEntity.getRestaurantTelNum())
                    .restaurantWebSite(restaurantEntity.getRestaurantWebSite())
                    .build();
            return ResponseEntity.status(200).body(updateRestaurantInfoDto);
        }

    }
    // 식당 정보 수정
    @Transactional
    public ResponseEntity<?> updateRestaurantInfo(Long restaurantId, UpdateRestaurantInfoDto restaurantDto, List<MultipartFile> multipartFilelist) {
        Optional<RestaurantEntity> restaurantEntityOptional = restaurantRepository.findById(restaurantId);
        if (restaurantEntityOptional.isEmpty()) {
            return ResponseEntity.status(400).body("restaurantId가 존재하지 않습니다.");
        } else {
            RestaurantEntity restaurantEntity = restaurantEntityOptional.get();

            // 동엔티티 수정
            DongEntity dong = dongRepository.findByDongName(restaurantDto.getRestaurantAdministrativeDistrict().getDongName()).get();

            // 식당 정보 수정
            restaurantEntity.setRestaurantName(restaurantDto.getRestaurantName());
            restaurantEntity.setRestaurantTelNum(restaurantDto.getRestaurantTelNum());
            restaurantEntity.setRestaurantAddress(restaurantDto.getRestaurantAddress());
            restaurantEntity.setRestaurantOpenTime(restaurantDto.getRestaurantOpenTime());
            restaurantEntity.setRestaurantWebSite(restaurantDto.getRestaurantWebSite());
            restaurantEntity.setRestaurantStatus(restaurantDto.getRestaurantStatus());
            restaurantEntity.setDongEntity(dong);

            // 기존 메뉴 엔티티 삭제
            List<RestaurantMenuEntity> existingMenuEntityList = restaurantEntity.getRestaurantMenuEntity();
            restaurantMenuRepository.deleteAll(existingMenuEntityList);//  기존 메뉴 엔티티 리스트 삭제
            restaurantEntity.getRestaurantMenuEntity().clear();

            // 수정할 메뉴 dto -> entity 변환
            List<RestaurantMenuEntity> restaurantMenuEntityList = new ArrayList<>();
            List<RestaurantMenuUpdateDto> restaurantMenuDtoList = restaurantDto.getRestaurantMenuDtoList();

            for (RestaurantMenuUpdateDto restaurantMenuUpdateDto : restaurantMenuDtoList) {
                RestaurantMenuEntity restaurantMenuEntity = RestaurantMenuEntity
                        .builder()
                        .restaurantMenuName(restaurantMenuUpdateDto.getRestaurantMenuName())
                        .restaurantMenuPrice(restaurantMenuUpdateDto.getRestaurantMenuPrice())
                        .restaurantEntity(restaurantEntity)
                        .build();
                restaurantMenuEntityList.add(restaurantMenuEntity);
            }

            // 수정 후 메뉴 엔티티 리스트 저장 후 레스토랑에 연결
            restaurantMenuRepository.saveAll(restaurantMenuEntityList);
            restaurantEntity.setRestaurantMenuEntity(restaurantMenuEntityList);

            // 대표 이미지 수정
            if (multipartFilelist != null) {
                List<RestaurantImageEntity> restaurantImageEntityList = restaurantImageRepository.findAllByRestaurantEntity_RestaurantId(restaurantId);
                for (RestaurantImageEntity restaurantImageEntity : restaurantImageEntityList) {
                    String originImagePath = restaurantImageEntity.getRestaurantOriginUrl();
                    String resizedImagePath = restaurantImageEntity.getRestaurantResizeUrl();

                    int originStartIndex = originImagePath.indexOf("restaurant" + restaurantId + "/");
                    String originFileName = originImagePath.substring(originStartIndex);
                    imageService.deleteS3File(originFileName);

                    int resizedStartIndex = resizedImagePath.indexOf("restaurant" + restaurantId + "/");
                    String resizedFileName = resizedImagePath.substring(resizedStartIndex);
                    imageService.deleteS3File(resizedFileName);
                }
                restaurantImageRepository.deleteAll(restaurantImageEntityList);

                String dirName = "restaurant" + restaurantId;
                imageService.upload(multipartFilelist, dirName, restaurantEntity);
            }

            // 변경된 내용을 저장
            restaurantRepository.save(restaurantEntity);

            return ResponseEntity.status(200).body("식당 정보 및 메뉴 엔티티가 업데이트되었습니다.\nrestaurantId:" +  restaurantId);

        }
    }


    public ResponseEntity<?> deleteAdminReview(Long reviewId, String userId) {
        Map<String, String> result = new HashMap<>();
        Optional<List<ReviewImageEntity>> deleteId = reviewImageRepository.findReviewImageEntityByReviewEntity_ReviewId(reviewId);
        if (deleteId.isPresent()) {
            // 리뷰와 연결된 이미지를 삭제
            for (ReviewImageEntity imageEntity : deleteId.get()) {
                // S3에서 이미지를 삭제
                reviewService.deleteS3Image(imageEntity.getReviewStoredName());
                reviewService.deleteS3Image(imageEntity.getReviewResizeStoredName());
            }
            // 리뷰 삭제
            reviewRepository.deleteById(reviewId);
            result.put("message", "리뷰가 삭제되었습니다.");

            return ResponseEntity.status(200).body(result);
        } else {
            result.put("message", "권한이 없습니다.");
            return ResponseEntity.status(403).body(result);
        }

    }


    //관리자 신고내역 조회
    public ResponseEntity<?> adminReportCheck(String userId) {

        List<ReportEntity> reportEntityList = reportRepository.findAll();

        List<AdminReportDto> reportDtoList = new ArrayList<>();

        for (ReportEntity reportEntity : reportEntityList) {

            AdminReportDto reportDos = AdminReportDto.builder()
                    .reviewId(reportEntity.getReportId())
                    .reviewAuthor(reportEntity.getReviewEntity().getMemberEntity().getMemberNickname())
                    .reviewProfilePicture(reportEntity.getReviewEntity().getMemberEntity().getMemberProfilePicture())
                    .reportContent(reportEntity.getReportContent())
                    .reportCategory(reportEntity.getReportCategory())
                    .reportAt(reportEntity.getReportAt())
                    .nickName(reportEntity.getMemberEntity().getMemberNickname())
                    .memberId(reportEntity.getMemberEntity().getMemberId())
                    .memberProfilePicture(reportEntity.getMemberEntity().getMemberProfilePicture())
                    .build();

            reportDtoList.add(reportDos);
        }

        return ResponseEntity.status(200).body(reportDtoList);
    }

    public ResponseEntity<?> adminRestaurantModifyCheck(String userId) {
        MemberEntity findByMemberId = memberRepository.findById(Long.valueOf(userId)).get();

        List<RequestEntity> byRequestId = requestRepository.findAll();

        List<RequestUpdateDto> requestUpdateDtoList = new ArrayList<>();

        for (RequestEntity requestEntity : byRequestId) {

            RequestUpdateDto requestUpdateDto = RequestUpdateDto.builder()
                    .requestId(requestEntity.getRequestId())
                    .requestAt(requestEntity.getRequestAt())
                    .memberId(findByMemberId.getMemberId())
                    .restaurantId(requestEntity.getRestaurantEntity().getRestaurantId())
                    .requestContent(requestEntity.getRequestContent())
                    .build();

            requestUpdateDtoList.add(requestUpdateDto);
        }
        return ResponseEntity.status(200).body(requestUpdateDtoList);
    }

    public ResponseEntity<?> findRestaurantForAdmin(int page, String sort, String keyword, String city, String district, String dong) {
        // 관리자 식당 리스트 조회
        Sort sortBy = getSortByCriterion(sort);
        if (sortBy == null) {
            return ResponseEntity.status(404).body("잘못된 접근입니다.");
        }

        Pageable pageable = PageRequest.of(page, 10, sortBy);
        Page<RestaurantDto> restaurantDtoPage;
        Page<RestaurantEntity> restaurantEntityPage = searchRestaurantForAdmin(pageable, keyword, dong, district, city);
        restaurantDtoPage = restaurantEntityPage // dto -> entity
                .map(this::restaurantEntityToRestaurantDto);
        return ResponseEntity.status(200).body(restaurantDtoPage);
    }

    private Page<RestaurantEntity> searchRestaurantForAdmin(Pageable pageable, String keyword, String dong, String district, String city) {
        // 폐업 식당 포함 검색
        if (dong != null) { // 동 이름 + 키워드 검색
            return restaurantRepository.findByRestaurantNameAndDongNameForAdmin(pageable, keyword, dong);
        } else if (district != null) { // 구 이름 + 키워드 검색
            return restaurantRepository.findByRestaurantNameAndDistrictNameForAdmin(pageable, keyword, district);
        } else if (city != null) { // 시 이름 + 키워드 검색
            return restaurantRepository.findByRestaurantNameAndCityNameForAdmin(pageable, keyword, city);
        } else { // 키워드 검색
            return restaurantRepository.findByRestaurantNameForAdmin(pageable, keyword);
        }
    }

    private Sort getSortByCriterion(String sort) { // 정렬 기준 비교
        if ("rateDesc".equals(sort)) {
            return Sort.by(Sort.Direction.DESC, "reviewEntity.size");
        } else if (("latest").equals(sort)) {
            return Sort.by(Sort.Direction.DESC,"restaurantId");
        } else {
            return null;
        }
    }

    private RestaurantDto restaurantEntityToRestaurantDto(RestaurantEntity restaurantEntity) {
        return RestaurantDto
                .builder()
                .restaurantId(restaurantEntity.getRestaurantId())
                .restaurantName(restaurantEntity.getRestaurantName())
                .restaurantStatus(restaurantEntity.getRestaurantStatus())
                .restaurantAddress(restaurantEntity.getRestaurantAddress())
                .restaurantWebSite(restaurantEntity.getRestaurantWebSite())
                .resizedImageUrl(restaurantEntity.getRestaurantImageEntity()
                        .stream()
                        .findFirst()
                        .map(RestaurantImageEntity::getRestaurantResizeUrl)
                        .orElse("null"))
                .likeCount(likeCountRepository.countByRestaurantEntity_RestaurantId(restaurantEntity.getRestaurantId()))
                .reviewCount(restaurantEntity.getReviewEntityList().size())
                .avgStarRate(restaurantRepository.findStarRateByRestaurantId(restaurantEntity.getRestaurantId()))
                .build();
    }

}
