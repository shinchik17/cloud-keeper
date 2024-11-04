package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class BucketService {

    private final MinioClient minioClient;

    @Autowired
    public BucketService(MinioClient minioClient,
                         @Value("minio.bucket-name") String defaultBucketName) throws MinioException {
        this.minioClient = minioClient;
        createBucket(defaultBucketName);
    }


    public void createDefaultUserFolder(User user) throws MinioException {
        String bucketName = "user-%d-files".formatted(user.getId());
        createBucket(bucketName);
    }


    public void createBucket(String bucketName) throws MinioException {

        try {
            if (!isBucketExisting(bucketName)) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            }

        } catch (Exception ex) {
            throw new MinioException(ex.getMessage());
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
