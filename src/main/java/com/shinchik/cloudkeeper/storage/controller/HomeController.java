package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.config.handler.BaseRequest;
import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import com.shinchik.cloudkeeper.storage.exception.service.NoSuchFolderException;
import com.shinchik.cloudkeeper.storage.mapper.BreadcrumbMapper;
import com.shinchik.cloudkeeper.storage.model.*;
import com.shinchik.cloudkeeper.storage.model.dto.*;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
@Profile({"dev", "prod"})
public class HomeController {

    private final MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    @GetMapping
    public String home(@BaseRequest BaseReqDto reqDto,
                       @AuthenticationPrincipal(expression = "getUser") User user,
                       Model model) {

        String path = reqDto.getPath();

        boolean isDir;
        try {
            isDir = minioService.isDir(reqDto);
            if (!isDir && !path.isEmpty()) {
                log.info("Requested path '{}' does not exist", path);
                throw new NoSuchFolderException(path);
            }
        } catch (MinioRepositoryException e) {
            log.info("Requested path '{}' is not valid. Caught exception: {}", path, e.getMessage());
            throw new NoSuchFolderException(path);
        }

        Breadcrumb breadcrumb = BreadcrumbMapper.INSTANCE.mapToModel(path);
        List<BaseRespDto> userObjects = minioService.list(reqDto);
        StorageInfo storageInfo = minioService.getStorageInfo(new BaseReqDto(reqDto.getUserId(), ""));

        model.addAttribute("path", path);
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("userObjects", userObjects);
        model.addAttribute("storageInfo", storageInfo);
        model.addAttribute("user", user);


        return "storage/home";
    }

}
