package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.storage.MinioService;
import com.shinchik.cloudkeeper.storage.dto.UploadDto;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/files")
public class FileController {

    private final MinioService minioService;

    @Autowired
    public FileController(MinioService minioService) {
        this.minioService = minioService;
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFiles(@ModelAttribute("fileUploadDto") UploadDto uploadDto, BindingResult bindingResult,
                              @AuthenticationPrincipal SecurityUserDetails userDetails) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        // TODO: rethink uploadDto structure, maybe I don't need it, replace with @RequestParam?
        uploadDto.setUser(userDetails.getUser());

        minioService.upload(uploadDto);

        return "redirect:/welcome";
    }



}
