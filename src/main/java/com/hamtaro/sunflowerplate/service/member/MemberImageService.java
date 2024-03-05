package com.hamtaro.sunflowerplate.service.member;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberImageService {
    @Value("${cloud.aws.s3.user-bucket}")
    private String bucketName;
    private final AmazonS3Client amazonS3Client;

    @Transactional
    public String imageSave(MultipartFile profileImage) {
        String originalName = profileImage.getOriginalFilename();
        String filename = getFileName(originalName);
        try {
            File resizedImageFile = resizeImage(profileImage);
            amazonS3Client.putObject(bucketName, filename, resizedImageFile);
            String resizeUrl = amazonS3Client.getUrl(bucketName, filename).toString();

            if (resizedImageFile != null && resizedImageFile.exists()) {
                if (resizedImageFile.delete()) {
                    System.out.println("이미지 삭제됨");
                } else {
                    System.out.println("이미지 삭제 실패");
                }
            } else {
                System.out.println("이미지 파일이 없음");
            }
            return resizeUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractExtension(String originName) {
        int index = originName.lastIndexOf('.');
        return originName.substring(index, originName.length());
    }

    public String getFileName(String originName) {
        return UUID.randomUUID() + "." + extractExtension(originName);
    }

    private File
    resizeImage(MultipartFile originalImage) throws IOException {
        File resizedFile = new File("resized_" + originalImage.getOriginalFilename());
        Thumbnails.of(originalImage.getInputStream())
                .size(300, 300)
                .toFile(resizedFile);
        return resizedFile;
    }

    public void deleteImageFromS3(String memberProfilePicture) {
        String splitStr = ".com/";
        String fileName = memberProfilePicture.substring(memberProfilePicture.lastIndexOf(splitStr) + splitStr.length());
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }
}
