package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.mapper.BreadcrumbMapper;
import com.shinchik.cloudkeeper.storage.model.BaseReqDto;
import com.shinchik.cloudkeeper.storage.model.Breadcrumb;
import com.shinchik.cloudkeeper.storage.model.UploadDto;
import com.shinchik.cloudkeeper.storage.service.MinioService;
import com.shinchik.cloudkeeper.user.model.User;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BaseController {

    private MinioService minioService;

    @Autowired
    public BaseController(MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/welcome")
    public String welcome(){

//        User user = userDetails.getUser();
//        UploadDto uploadDto = new UploadDto();
//        uploadDto.setUser(user);
//        FolderUploadDto folderUploadDto = new FolderUploadDto();
//        folderUploadDto.setUser(user);
//
//        model.addAttribute("uploadDto", uploadDto);
//        model.addAttribute("folderUploadDto", folderUploadDto);
//        model.addAttribute("user", user);
        return "welcome";
    }


    @GetMapping
    public String home(@RequestParam(value = "path", required = false, defaultValue = "") String path,
                       @AuthenticationPrincipal(expression = "getUser") User user,
                       @ModelAttribute("uploadDto") UploadDto uploadDto,
                       Model model) {

        Breadcrumb breadcrumb = BreadcrumbMapper.INSTANCE.mapToModel(path);


        BaseReqDto listReq = new BaseReqDto(user, path);
        List<BaseReqDto> userObjects = minioService.list(listReq);


        model.addAttribute("path", path);

        return "home";
    }


}
