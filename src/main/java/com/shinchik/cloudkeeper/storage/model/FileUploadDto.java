package com.shinchik.cloudkeeper.storage.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileUploadDto {
    private String username;
    private List<MultipartFile> documents;
}
