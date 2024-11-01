package com.shinchik.cloudkeeper;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;

public class Main {

    public static void main(String[] args) {
        System.out.println("kek");

        try {

            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("accessKey", "secretKey")
                    .build();

            boolean isFound = minioClient.bucketExists(BucketExistsArgs.builder().bucket("user-1").build());

            if (!isFound) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("user-1").build());
            }

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("user-1")
                            .object("sample_file")
                            .filename("C:\\Users\\shinyaevam\\Documents\\JavaProjects\\cloud-keeper\\sql\\script.sql")
                            .build()
            );

        } catch (Exception e){
            System.out.println(e);
        }


    }
}
