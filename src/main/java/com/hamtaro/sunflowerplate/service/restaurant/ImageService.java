package com.hamtaro.sunflowerplate.service.restaurant;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantEntity;
import com.hamtaro.sunflowerplate.entity.restaurant.RestaurantImageEntity;
import com.hamtaro.sunflowerplate.repository.restaurant.RestaurantImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;

@Service
@RequiredArgsConstructor
@Slf4j
@Component
public class ImageService {

    private final AmazonS3Client amazonS3Client;
    private final RestaurantImageRepository restaurantImageRepository;

    @Value("${cloud.aws.s3.restaurant-bucket}")
    private String RestaurantImageBucket;

    private String createFileName(String fileName) { //  파일 이름 생성
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String filename) { //
        try {
            return filename.substring(filename.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + filename + ")입니다.");
        }
    }

    // 이미지 리사이징
    private File resizeImage(MultipartFile originalImage) throws IOException {
        File resizeFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                .size(400, 400)
                .toFile(resizeFile);
        return resizeFile;
    }


    //s3 올릴 이미지 객체 url로 변환 , DB 에 url 저장
    public void upload(List<MultipartFile> multipartFileList, String dirName, RestaurantEntity restaurantEntity) {
        for (MultipartFile multipartFile : multipartFileList) {
            if (multipartFile != null) {

                try {
                    // 메타데이터 설정
                    ObjectMetadata objectMetadata = new ObjectMetadata();
                    objectMetadata.setContentType(multipartFile.getContentType());
                    objectMetadata.setContentLength(multipartFile.getSize());

                    // 원본 저장
                    String originFileName = multipartFile.getOriginalFilename();
                    String storedName = createFileName(originFileName);
                    String accessUrl = uploadMultiFileToS3(multipartFile, storedName ,dirName, objectMetadata);

                    // 리사이즈 저장
                    String resizeName = "resized_" + storedName;
                    File resizedImageFile = resizeImage(multipartFile);
                    String resizeUrl = uploadResizedFileToS3(resizedImageFile, resizeName, dirName);

                    RestaurantImageEntity restaurantImageEntity = RestaurantImageEntity
                            .builder()
                            .restaurantOriginName(originFileName)
                            .restaurantStoredName(storedName)
                            .restaurantOriginUrl(accessUrl)
                            .restaurantResizedStoredName(resizeName)
                            .restaurantResizeUrl(resizeUrl)
                            .restaurantEntity(restaurantEntity)
                            .build();

                    restaurantImageRepository.save(restaurantImageEntity);

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
    }

    // S3에 멀티파일 + 메타데이터 업로드
    private String uploadMultiFileToS3(MultipartFile uploadFile, String storedName, String dirName, ObjectMetadata objectMetadata) throws IOException {
        String fileName = dirName + "/" + storedName; // 파일 경로 설정 + S3에 저장된 파일 이름
        amazonS3Client.putObject(new PutObjectRequest(RestaurantImageBucket, fileName, uploadFile.getInputStream(), objectMetadata));
        return amazonS3Client.getUrl(RestaurantImageBucket, fileName).toString();
    }

    // S3에 리사이즈 파일 업로드
    private String uploadResizedFileToS3(File uploadFile, String storedName, String dirName) throws IOException {
        String fileName = dirName + "/" + storedName; // 파일 경로 설정 + S3에 저장된 파일 이름
        amazonS3Client.putObject(new PutObjectRequest(RestaurantImageBucket, fileName, uploadFile));
        return amazonS3Client.getUrl(RestaurantImageBucket, fileName).toString();
    }

    // s3이미지 삭제
    public void deleteS3File(String filename) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(RestaurantImageBucket, filename));
        System.out.printf("[%s] 삭제에 성공했습니다.\n", filename);
    }
}
