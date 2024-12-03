package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import com.shinchik.cloudkeeper.storage.exception.NoSuchFolderException;
import com.shinchik.cloudkeeper.storage.mapper.BreadcrumbMapper;
import com.shinchik.cloudkeeper.storage.model.*;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.storage.util.PathUtils;
import com.shinchik.cloudkeeper.user.model.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
public class HomeController {

    private MinioService minioService;

    @Autowired
    public HomeController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    @GetMapping
    public String home(@RequestParam(value = "path", required = false, defaultValue = "") String path,
                       @AuthenticationPrincipal(expression = "getUser") User user,
                       Model model) {

        path = PathUtils.normalize(path);

        boolean isDir;
        try {
            isDir = minioService.isDir(new BaseReqDto(user, "", path));
            if (!isDir && !path.isEmpty()) {
                log.info("Requested path '{}' does not exist", path);
                throw new NoSuchFolderException("Folder '%s' not found".formatted(path));
            }
        } catch (MinioRepositoryException e) {
            log.info("Requested path '{}' is not valid. Caught exception: {}", path, e.getMessage());
            throw new NoSuchFolderException("Folder '%s' not found".formatted(path));
        }


        Breadcrumb breadcrumb = BreadcrumbMapper.INSTANCE.mapToModel(path);
        List<BaseRespDto> userObjects = minioService.list(new BaseReqDto(user, path));

        model.addAttribute("path", path);
        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("userObjects", userObjects);
        model.addAttribute("user", user);
        model.addAttribute("uploadDto", new UploadDto());
        model.addAttribute("downloadDto", new BaseRespDto());
        model.addAttribute("renameDto", new RenameDto());
        model.addAttribute("deleteDto", new BaseReqDto());
        model.addAttribute("mkDirDto", new BaseReqDto());

        return "storage/home";
    }





}
