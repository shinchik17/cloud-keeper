package com.shinchik.cloudkeeper;

import io.minio.*;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("kek");
        String bucketName = "user-files";
        String objectName = "/path/to/";

        try {

            MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("root", "rootpass")
                    .build();

            boolean isFound = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());

            if (!isFound) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(objectName)
//                            .stream(new ByteArrayInputStream(new byte[] {1,2,3}), 0, -1)
//                            .build()
//            );
            var a = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix("path")
                            .recursive(true)
                            .maxKeys(10)
                            .build());

            List<Item> list = new ArrayList<>();
            for (Result<Item> result : results) {
                list.add(result.get());
            }
//
            StatObjectResponse objectStat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());

//            minioClient.copyObject(
//                    CopyObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object("/path/to2/")
//                            .source(
//                                    CopySource.builder()
//                                            .bucket(bucketName)
//                                            .object(objectName)
//                                            .build())
//                            .build());

            System.out.println();
        } catch (Exception e){
            System.out.println(e);
        }


    }
}
