package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.storage.dto.file.*;
import com.shinchik.cloudkeeper.storage.exception.FileServiceException;
import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import io.minio.SnowballObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class FileService {

    private final MinioRepository minioRepository;
    private final long maxFileSize;
    private final long maxRequestSize;

    @Autowired
    public FileService(MinioRepository minioRepository,
                       @Value("${spring.servlet.multipart.max-file-size}") long maxFileSize,
                       @Value("${spring.servlet.multipart.max-request-size}") long maxRequestSize) {
        this.minioRepository = minioRepository;
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
    }


    public void upload(FileUploadDto uploadDto) {

        List<MultipartFile> files = uploadDto.getDocuments();
        User user = uploadDto.getUser();
        String path = uploadDto.getPath();

        if (isTotalExceededSizeConstraints(files)) {
            throw new MaxUploadSizeExceededException(maxRequestSize);
        }

        try {
            if (files.size() == 1) {
                MultipartFile file = files.get(0);
                String fullObjPath = formFullPath(uploadDto) + "/" + file.getOriginalFilename();
                minioRepository.upload(fullObjPath, file.getInputStream(), file.getSize());
            } else {
                minioRepository.upload(multipartToSnowball(files, path, user));
            }
        } catch (IOException | FileServiceException e) {
            throw new FileServiceException(e);
        }
    }


    public InputStreamResource download(FileDownloadDto downloadDto) {
        // TODO: implement
        String fullObjPath = formFullPath(downloadDto) + "/" + downloadDto.getObjName();

        if (isDir(fullObjPath)) {
            return new InputStreamResource(getZippedFolder(fullObjPath));
        } else {
            return new InputStreamResource(minioRepository.get(fullObjPath));
        }

    }


    public void rename(FileRenameDto renameDto){
        String fullPath = formFullPath(renameDto);
        String objName = renameDto.getObjName();
        String newObjName = renameDto.getNewObjName();


        if (isDir(fullPath + objName)) {

//            Map<Item, InputStream> objectsMap = minioRepository.getAfterPath(fullPath);
            List<Item> objectsMeta = minioRepository.findRecursively(fullPath + "/" + objName);

            for (Item item : objectsMeta) {
                String objPath = item.objectName();
                minioRepository.rename(objPath, objPath.replace(objName, newObjName));
            }

        } else {

            String fullObjPath = fullPath + "/" + handleFileExtension(objName, newObjName);
            String fullNewObjPath = fullPath + "/" + newObjName;
            minioRepository.rename(fullObjPath, fullNewObjPath);
        }

    }


    public void delete(FileDeleteDto deleteDto){
        String fullObjPath = formFullPath(deleteDto) + "/" + deleteDto.getObjName();

        minioRepository.delete(fullObjPath);

    }


    public boolean isObjectExist(FileCheckDto checkDto){
        String fullObjPath = formFullPath(checkDto) + "/" + checkDto.getObjName();
        return minioRepository.isObjectExist(fullObjPath);
    }


    public void copyFile(String objPath, User user) {

        String fullPath = formFullPath(objPath, user);

        // TODO: implement
        int copyCount = 0;
        String copyPath = fullPath;
        while (minioRepository.isObjectExist(copyPath)) {
            copyCount++;
            copyPath = fullPath + "_(%d)".formatted(copyCount);
        }

        minioRepository.copy(fullPath, copyPath);

    }


    private ByteArrayInputStream getZippedFolder(String fullPath){

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutStream = new ZipOutputStream(byteOutStream)) {

            Map<Item, InputStream> objectsMap = minioRepository.getAfterPath(fullPath);

            for (Map.Entry<Item, InputStream> entry : objectsMap.entrySet()) {
                Item metaInfo = entry.getKey();
                InputStream stream = entry.getValue();
                String name = extractOrigName(metaInfo.objectName());
//                long size = metaInfo.size();
                zipOutStream.putNextEntry(new ZipEntry(name));
                zipOutStream.write(stream.readAllBytes());
                zipOutStream.closeEntry();
            }

        } catch (IOException e){
            throw new FileServiceException(e);
        }

        return new ByteArrayInputStream(byteOutStream.toByteArray());
    }


    private static String handleFileExtension(String oldName, String newName){
        int lastDotIndex = oldName.lastIndexOf(".");
        if (lastDotIndex != -1){
            return newName + oldName.substring(lastDotIndex);
        }

        return newName;
    }

    private boolean isDir(String objPath) {
        return objPath.endsWith("/");
    }

    private static List<SnowballObject> multipartToSnowball(List<MultipartFile> files, String path, User user) {
        return files.stream()
                .map(file -> {
                    try {
                        return new SnowballObject(
                                formFullPath(path + "/" + file.getOriginalFilename(), user),
                                file.getInputStream(),
                                file.getSize(),
                                null);
                    } catch (IOException e) {
                        throw new FileServiceException(e);
                    }
                })
                .collect(Collectors.toList());

    }

    private boolean isSingleFileExceededSizeConstraints(List<MultipartFile> files) {
        return files.stream().anyMatch(file -> file.getSize() > maxFileSize);
    }

    private boolean isTotalExceededSizeConstraints(List<MultipartFile> files) {
        return files.stream().mapToLong(MultipartFile::getSize).sum() > maxRequestSize;
    }

    private static String formFullPath(String path, User user) {
        return "user-%d-files/%s".formatted(user.getId(), path);
    }

    private static String formFullPath(FileRequestDto requestDto) {
        return "user-%d-files/%s".formatted(requestDto.getUser().getId(), requestDto.getPath());
    }


    private static String extractOrigName(String prefixedPath) {
//        return "user-%d-files/%s".formatted(user.getId(), path);
        return prefixedPath.replaceFirst("user-[0-9]{1,18}-files/", "");
    }

}
