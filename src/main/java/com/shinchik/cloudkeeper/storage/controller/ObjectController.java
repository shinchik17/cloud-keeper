package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.exception.controller.DtoValidationException;
import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.BaseRespDto;
import com.shinchik.cloudkeeper.storage.model.RenameDto;
import com.shinchik.cloudkeeper.storage.model.UploadDto;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.storage.util.PathUtils;
import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.validation.ValidationUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@Controller
@RequestMapping("/files")
@Profile("web")
public class ObjectController {

    private final MinioService minioService;

    @Autowired
    public ObjectController(MinioService minioService) {
        this.minioService = minioService;
    }


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFiles(@ModelAttribute("uploadDto") UploadDto uploadDto, BindingResult bindingResult,
                              @AuthenticationPrincipal(expression = "getUser") User user) {
        uploadDto.setUser(user);

        if (bindingResult.hasErrors()){
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
            throw new DtoValidationException("Invalid upload request");
        }

        minioService.upload(uploadDto);

        return "redirect:/?path=%s".formatted(PathUtils.getEncodedPath(uploadDto));
    }


    @GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFiles(
            @ModelAttribute("downloadDto") @Valid BaseRespDto downloadDto, BindingResult bindingResult,
            @AuthenticationPrincipal(expression = "getUser") User user) {

        downloadDto.setUser(user);

        if (bindingResult.hasErrors()){
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
            throw new DtoValidationException("Invalid download request");
        }

        BaseReqDto reqDto = new BaseReqDto(downloadDto.getUser(), downloadDto.getPath(), downloadDto.getObjName());
        InputStreamResource resource = minioService.download(reqDto);

        String filename = minioService.isDir(reqDto)
                ? String.format("%s.zip", reqDto.getObjName())
                : reqDto.getObjName();

        String filenameEncoded = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''\"%s\"".formatted(filenameEncoded))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PatchMapping
    public String renameFile(@ModelAttribute("renameDto") @Valid RenameDto renameDto, BindingResult bindingResult,
                             @AuthenticationPrincipal(expression = "getUser") User user) {

        renameDto.setUser(user);

        if (bindingResult.hasErrors()){
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
            throw new DtoValidationException("Invalid rename request");
        }

        minioService.rename(renameDto);

        return "redirect:/?path=%s".formatted(PathUtils.getEncodedPath(renameDto));
    }

    @DeleteMapping
    public String deleteFile(@ModelAttribute("deleteDto") @Valid BaseReqDto deleteDto, BindingResult bindingResult,
                             @AuthenticationPrincipal(expression = "getUser") User user) {

        deleteDto.setUser(user);

        if (bindingResult.hasErrors()){
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
            throw new DtoValidationException("Invalid delete request");
        }

        minioService.delete(deleteDto);

        return "redirect:/?path=%s".formatted(PathUtils.getEncodedPath(deleteDto));
    }


    @PostMapping("/create")
    public String createFolder(@ModelAttribute("mkDirDto") @Valid BaseReqDto mkDirDto, BindingResult bindingResult,
                               @AuthenticationPrincipal(expression = "getUser") User user){

        mkDirDto.setUser(user);

        if (bindingResult.hasErrors()){
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
        }

        minioService.createFolder(mkDirDto);

        return "redirect:/?path=%s".formatted(PathUtils.getEncodedPath(mkDirDto));

    }




}
