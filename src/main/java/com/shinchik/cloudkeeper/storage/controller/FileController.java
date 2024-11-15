package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.model.UploadDto;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequestMapping("/files")
public class FileController {

    private final MinioService minioService;

    @Autowired
    public FileController(MinioService minioService) {
        this.minioService = minioService;
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFiles(@ModelAttribute("uploadDto") UploadDto uploadDto, BindingResult bindingResult,
                              @AuthenticationPrincipal(expression = "getUser") User user,
                              @RequestParam(value = "path", required = false, defaultValue = "") String path) {
        uploadDto.setUser(user);
        uploadDto.setPath(path);

        if (bindingResult.hasErrors()){
            log.warn(bindingResult.getObjectName());
        }

        minioService.upload(uploadDto);

        return "redirect:/";
    }



}
