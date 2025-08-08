package com.musai.musai.service.ticket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public String uploadImage(MultipartFile file) throws IOException {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".png";
            String filename = "tickets/" + UUID.randomUUID().toString() + fileExtension;

            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            if (response.sdkHttpResponse().isSuccessful()) {
                String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                        bucketName, region, filename);
                log.info("이미지 업로드 성공: {}", s3Url);
                return s3Url;
            } else {
                throw new IOException("S3 업로드 실패: " + response.sdkHttpResponse().statusText());
            }

        } catch (Exception e) {
            log.error("S3 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw new IOException("이미지 업로드 실패: " + e.getMessage());
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            
            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
                    .build());
            
            log.info("이미지 삭제 성공: {}", key);
        } catch (Exception e) {
            log.error("이미지 삭제 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private String extractKeyFromUrl(String imageUrl) {
        String prefix = "https://" + bucketName + ".s3." + region + ".amazonaws.com/";
        return imageUrl.replace(prefix, "");
    }
}
