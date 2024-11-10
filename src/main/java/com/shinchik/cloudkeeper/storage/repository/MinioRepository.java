package com.shinchik.cloudkeeper.storage.repository;

import com.shinchik.cloudkeeper.storage.exception.MinioRepositoryException;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objPath)
                            .build());

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }

    }

    public Map<Item, byte[]> getAfterPath(String path) {
        List<Item> objects = listRecursively(path);
        Map<Item, byte[]> resultMap = new HashMap<>();
        for (Item obj : objects) {
            try (InputStream stream = get(obj.objectName())) {

                resultMap.put(obj, stream.readAllBytes());

            } catch (IOException e) {
                String message = "Error downloading object %s".formatted(obj.objectName());
                log.error(message);
                throw new MinioRepositoryException(message);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new MinioRepositoryException(e.getMessage());
            }

        }
        return resultMap;
    }

    public List<InputStream> getByFolder(String folderPath) {
        List<Item> objects = list(folderPath);
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

    public void rename(String objPath, String newFilepath) {
        copy(objPath, newFilepath);
        delete(objPath);
    }

    public List<Item> list(String prefix) {
        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> list(String prefix, String startsAfter) {

        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .startAfter(startsAfter)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> listRecursively(String prefix) {
        return extractItems(minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .recursive(true)
                        .prefix(prefix)
                        .maxKeys(maxFindKeys)
                        .build()));
    }

    public List<Item> listRecursively(String prefix, String startsAfter) {
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


    public boolean isObjectDir(String objPath) {
        if (objPath.endsWith("/")){
            objPath = objPath.substring(0, objPath.length() - 1);
        }
        if (!isObjectExist(objPath)) {
            for (Item item : list(objPath)) {
                if (item.objectName().equals(objPath + "/")) {
                    return true;
                }
            }
        }

        return false;
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
            if (e.errorResponse().code().equals("NoSuchKey")) {
                log.warn("No such object (%s) in storage".formatted(objPath));
            } else {
                log.warn(e.getMessage());
            }
            return false;
        } catch (InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error(e.getMessage());
            throw new MinioRepositoryException(e.getMessage());
        }

    }

}
