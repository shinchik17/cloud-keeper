package com.shinchik.cloudkeeper.storage.service;

import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.RenameDto;
import com.shinchik.cloudkeeper.storage.model.UploadDto;
import com.shinchik.cloudkeeper.storage.exception.MinioServiceException;
import com.shinchik.cloudkeeper.storage.repository.MinioRepository;
import io.minio.SnowballObject;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class MinioService {

    private final MinioRepository minioRepository;
    private final long maxFileSize;
    private final long maxRequestSize;

    @Autowired
    public MinioService(MinioRepository minioRepository,
                        @Value("${spring.servlet.multipart.max-file-size}") DataSize maxFileSize,
                        @Value("${spring.servlet.multipart.max-request-size}") DataSize maxRequestSize) {
        this.minioRepository = minioRepository;
        this.maxFileSize = maxFileSize.toBytes();
        this.maxRequestSize = maxRequestSize.toBytes();
    }


    public void upload(UploadDto uploadDto) {
        List<MultipartFile> files = uploadDto.getDocuments();
        String fullPath = formFullPath(uploadDto);

        if (isTotalExceededSizeConstraints(files)) {
            throw new MaxUploadSizeExceededException(maxRequestSize);
        }

        try {
            if (files.size() == 1) {
                MultipartFile file = files.get(0);
                String fullObjPath = fullPath + file.getOriginalFilename();
                minioRepository.upload(fullObjPath, file.getInputStream(), file.getSize());
            } else {
                minioRepository.upload(multipartToSnowball(files, fullPath));
            }
        } catch (IOException | MinioServiceException e) {
            throw new MinioServiceException(e);
        }
    }


    public InputStreamResource download(BaseReqDto downloadDto) {
        String fullObjPath = formFullPath(downloadDto) + downloadDto.getObjName();
        if (minioRepository.isObjectDir(fullObjPath)) {
            return new InputStreamResource(getZippedFolder(fullObjPath + "/"));
        } else {
            return new InputStreamResource(minioRepository.get(fullObjPath));
        }

    }


    public void rename(RenameDto renameDto) {
        String fullPath = formFullPath(renameDto);
        String objName = renameDto.getObjName();
        String newObjName = renameDto.getNewObjName();


        if (isDir(fullPath + objName)) {

            List<Item> objectsMeta = minioRepository.listRecursively(fullPath + objName);

            for (Item item : objectsMeta) {
                String objPath = item.objectName();
                String newObjPath = objPath.replaceFirst(fullPath + objName, fullPath + newObjName);
                minioRepository.rename(objPath, newObjPath);
            }

        } else {

            String newObjPath = fullPath + handleFileExtension(objName, newObjName);
            minioRepository.rename(fullPath + objName, newObjPath);
        }

    }


    public void delete(BaseReqDto deleteDto) {
        String fullPath = formFullPath(deleteDto);
        String objName = deleteDto.getObjName();

        if (isDir(fullPath + objName)) {

            List<Item> objectsMeta = minioRepository.listRecursively(fullPath + objName + "/");
            for (Item item : objectsMeta) {
                minioRepository.delete(item.objectName());
            }

        } else {

            minioRepository.delete(fullPath + objName);
        }

    }


    public List<BaseReqDto> list(BaseReqDto reqDto) {
        String fullPath = formFullPath(reqDto);
        List<BaseReqDto> objects = new ArrayList<>();
        // TODO: stream api
        for (Item obj : minioRepository.list(fullPath)) {
            // TODO: replace with mapper?
            if (isDir(obj) && obj.objectName().equals(fullPath)){
                continue;
            }
            BaseReqDto objInfo = new BaseReqDto(reqDto.getUser(), reqDto.getPath(), obj.objectName());
            objects.add(objInfo);
        }

        return objects;
    }


    public List<BaseReqDto> listRecursively(BaseReqDto reqDto) {
        String fullPath = formFullPath(reqDto);
        List<BaseReqDto> objects = new ArrayList<>();
        for (Item obj : minioRepository.listRecursively(fullPath)) {

            if (isDir(obj) && obj.objectName().equals(fullPath)){
                continue;
            }
            BaseReqDto resCheckDto = new BaseReqDto(reqDto.getUser(), reqDto.getPath(), obj.objectName());
            objects.add(resCheckDto);
        }

        return objects;
    }


    public boolean isObjectExist(BaseReqDto checkDto) {
        String fullObjPath = formFullPath(checkDto) + checkDto.getObjName();
        return minioRepository.isObjectExist(fullObjPath);
    }


    public void createFolder(BaseReqDto reqDto) {
        String fullObjPath = formFullPath(reqDto) + reqDto.getObjName() + "/";
        minioRepository.upload(fullObjPath, InputStream.nullInputStream(), 0);
    }


    private ByteArrayInputStream getZippedFolder(String fullPath) {

        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutStream = new ZipOutputStream(byteOutStream)) {

            Map<Item, byte[]> objectsMap = minioRepository.getAfterPath(fullPath);

            for (Map.Entry<Item, byte[]> entry : objectsMap.entrySet()) {
                Item metaInfo = entry.getKey();
                byte[] buffer = entry.getValue();
                String name = extractOrigName(metaInfo.objectName());
                zipOutStream.putNextEntry(new ZipEntry(name));
                zipOutStream.write(buffer);
                zipOutStream.closeEntry();
            }

        } catch (IOException e) {
            throw new MinioServiceException(e);
        }

        return new ByteArrayInputStream(byteOutStream.toByteArray());
    }




    public boolean isDir(String fullObjPath) {
        return minioRepository.isObjectDir(fullObjPath);
    }

    public boolean isDir(BaseReqDto checkDto) {
        String fullObjPath = formFullPath(checkDto) + checkDto.getObjName();
        return minioRepository.isObjectDir(fullObjPath);
    }

    private boolean isDir(Item objMeta) {
        return objMeta.isDir() || objMeta.objectName().endsWith("/");
    }

    private static String handleFileExtension(String oldName, String newName) {
        int lastDotIndex = oldName.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return newName + oldName.substring(lastDotIndex);
        }

        return newName;
    }

    private static List<SnowballObject> multipartToSnowball(List<MultipartFile> files, String fullPath) {
        return files.stream()
                .map(file -> {
                    try {
                        return new SnowballObject(
                                fullPath + file.getOriginalFilename(),
                                file.getInputStream(),
                                file.getSize(),
                                null);
                    } catch (IOException e) {
                        throw new MinioServiceException(e);
                    }
                })
                .collect(Collectors.toList());

    }

    private boolean isTotalExceededSizeConstraints(List<MultipartFile> files) {
        return files.stream().mapToLong(MultipartFile::getSize).sum() > maxRequestSize;
    }

    private static String formFullPath(BaseReqDto reqDto) {
        return "user-%d-files/%s/".formatted(reqDto.getUser().getId(), reqDto.getPath()).replace("//", "/");
    }

    private static String extractOrigName(String prefixedPath) {
        return prefixedPath.replaceFirst("user-[0-9]{1,18}-files/", "");
    }

}