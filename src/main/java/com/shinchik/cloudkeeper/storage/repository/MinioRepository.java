package com.shinchik.cloudkeeper.storage.repository;

import com.shinchik.cloudkeeper.storage.exception.MinioRepositoryException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MinioRepository {

    private final String bucketName;
    private final int maxFindKeys;
    private final MinioClient minioClient;

    public MinioRepository(MinioClient minioClient,
                           @Value("${minio.bucket-name}") String bucketName,
                           @Value("${minio.max-listed-objects}") int maxFindKeys) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.maxFindKeys = maxFindKeys;
    }

    public void upload(String objPath, InputStream fileStream, long size) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objPath)
                            .stream(fileStream, size, -1)
                            .build());
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }
    }

    public void upload(List<SnowballObject> objects) {
        try {
            minioClient.uploadSnowballObjects(
                    UploadSnowballObjectsArgs.builder()
                            .bucket(bucketName)
                            .objects(objects)
                            .build());
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }
    }


    public InputStream get(String objPath) {

        if (!isObjectExist(objPath)) {
            throw new MinioRepositoryException("File %s not found".formatted(objPath));
        }

        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objPath)
                        .build())) {

            return stream;

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }

    }

    public Map<Item, InputStream> getAfterPath(String path){
        List<Item> objects = findRecursively(path);
        Map<Item, InputStream> resultMap = new HashMap<>();
        for (Item object : objects) {
            resultMap.put(object, get(object.objectName()));
        }
        return resultMap;
    }

    public List<InputStream> getByFolder(String folderPath){
        List<Item> objects = find(folderPath);
        List<InputStream> resultList = new ArrayList<>();
        for (Item object : objects) {
            resultList.add(get(object.objectName()));
        }
        return resultList;
    }

    public void delete(String objPath) {

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objPath)
                            .build());

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }
    }


    public void copy(String objPath, String copyPath) {
        try {
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(copyPath)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucketName)
                                            .object(objPath)
                                            .build())
                            .build());

        } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }
    }

    public void rename(String objPath, String newFilepath){
        copy(objPath, newFilepath);
        delete(objPath);
    }

    public List<Item> find(String prefix) {
        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> find(String prefix, String startsAfter) {

        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .startAfter(startsAfter)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> findRecursively(String prefix) {
        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .prefix(prefix)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> findRecursively(String prefix, String startsAfter) {
        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .prefix(prefix)
                        .startAfter(startsAfter)
                        .maxKeys(maxFindKeys)
                        .build()));
    }


    private List<Item> extractItems(Iterable<Result<Item>> resultIterable) {
        List<Item> items = new ArrayList<>();

        try {

            for (Result<Item> result : resultIterable) {
                items.add(result.get());
            }

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }

        return items;
    }


    private boolean isObjectDir(Item item){
        return item.isDir() || item.objectName().endsWith("/");
    }

    public boolean isObjectExist(String objPath) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objPath)
                            .build());
            return true;
        } catch (ErrorResponseException e) {
            log.warn(e.getMessage());
            return false;
        } catch (InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }

    }

}
