package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.storage.dto.file.FileDownloadDto;
import com.shinchik.cloudkeeper.storage.dto.file.FileRenameDto;
import com.shinchik.cloudkeeper.storage.dto.file.FileRequestDto;
import com.shinchik.cloudkeeper.storage.dto.file.FileUploadDto;
import com.shinchik.cloudkeeper.storage.exception.FileServiceException;
import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import io.minio.SnowballObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${spring.servlet.multipart.max-file-size}")
    private long maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private long maxRequestSize;

    @Autowired
    public FileService(MinioRepository minioRepository) {
        this.minioRepository = minioRepository;
    }


    public void upload(FileUploadDto uploadDto) {

        List<MultipartFile> files = uploadDto.getDocuments();
        User user = uploadDto.getUser();

        if (isTotalExceededSizeConstraints(files)) {
            throw new MaxUploadSizeExceededException(maxRequestSize);
        }

        try {
            if (files.size() == 1) {
                MultipartFile file = uploadDto.getDocuments().get(0);
                String fullPath = formFullPath(file.getOriginalFilename(), user);
                minioRepository.upload(fullPath, file.getInputStream(), file.getSize());
            } else {
                minioRepository.upload(multipartToSnowball(files, user));
            }
        } catch (IOException | FileServiceException e) {
            throw new FileServiceException(e);
        }

    }


    public InputStreamResource download(FileDownloadDto downloadDto) {
        // TODO: implement
        String fullPath = formFullPath(downloadDto.getObjName(), downloadDto.getUser());

        if (isDir(fullPath)) {
            return new InputStreamResource(getZippedFolder(fullPath));
        } else {
            return new InputStreamResource(minioRepository.get(fullPath));
        }

    }


    public void rename(FileRenameDto renameDto){
        String fullPath = formFullPath(renameDto);
//        String newFullPath = formFullPath(newObjPath, user);

        if (isDir(fullPath)) {

//            Map<Item, InputStream> objectsMap = minioRepository.getAfterPath(fullPath);
            List<Item> objectsMeta = minioRepository.findRecursively(fullPath);

            for (Item item : objectsMeta) {
//                minioRepository.rename(item.objectName(), );
            }


        } else {

//            newFullPath = handleFileExtension(objPath, newFullPath);
//            minioRepository.rename(fullPath, newFullPath);
        }




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

    private boolean isDir(String path) {
        return path.endsWith("/");
    }

    private static List<SnowballObject> multipartToSnowball(List<MultipartFile> files, User user) {
        return files.stream()
                .map(file -> {
                    try {
                        return new SnowballObject(
                                formFullPath(file.getOriginalFilename(), user),
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
