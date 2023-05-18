package com.hanghae.be_h010gram.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hanghae.be_h010gram.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static com.hanghae.be_h010gram.exception.ExceptionEnum.FILE_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    //파일을 s3에 업로드
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        validateFileExists(multipartFile);

        String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
        log.info(fileName);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());

        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public boolean delete(String fileUrl) {
        try {
            String[] temp = fileUrl.split("/");
            String fileKey = temp[temp.length - 1];
            amazonS3Client.deleteObject(bucket, fileKey);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //파일 유 / 무 확인 메서드
    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new CustomException(FILE_NOT_FOUND);
        }
    }
}
