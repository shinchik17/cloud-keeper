package com.shinchik.cloudkeeper.storage.service;

import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class BucketService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public BucketService(MinioClient minioClient,
                         @Value("${minio.bucket-name}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }


    public void createDefaultBucket(){
        createBucket(bucketName);
    }

    public void createBucket(String bucketName) {

        try {

            if (!isBucketExisting(bucketName)) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build());
                log.info("Default bucket has been created successfully");
            } else {
                log.info("Default bucket (%s) exists".formatted(bucketName));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }
    }


    public boolean isBucketExisting(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );
    }


}
