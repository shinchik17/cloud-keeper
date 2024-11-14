package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.dto.UploadDto;
import com.shinchik.cloudkeeper.storage.service.BucketService;
import com.shinchik.cloudkeeper.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BaseController {

    private BucketService service;

    @Autowired
    public BaseController(BucketService service) {
        this.service = service;
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
//                       @AuthenticationPrincipal(expression = "getUser") User user,
                       @ModelAttribute("uploadDto") UploadDto uploadDto,
                       Model model) {

        model.addAttribute("path", path);

        return "home";
    }


}
