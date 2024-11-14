package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.storage.exception.MinioRepositoryException;
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

@Service
@Slf4j
public class BucketService {

    private final MinioClient minioClient;
    private final String bucketName;

    @Autowired
    public BucketService(MinioClient minioClient,
                         @Value("${minio.bucket-name}") String bucketName) throws MinioException {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
//        createBucket(bucketName);
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
                                .build()
                );
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
