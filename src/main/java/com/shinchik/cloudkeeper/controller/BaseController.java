package com.shinchik.cloudkeeper.controller;

import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.storage.BucketService;
import com.shinchik.cloudkeeper.storage.model.FileUploadDto;
import com.shinchik.cloudkeeper.storage.model.FolderUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    private BucketService service;

    @Autowired
    public BaseController(BucketService service) {
        this.service = service;
    }

    @GetMapping("/welcome")
    public String welcome(Model model, @AuthenticationPrincipal SecurityUserDetails user){

        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setUsername(user.getUsername());
        FolderUploadDto folderUploadDto = new FolderUploadDto();
        folderUploadDto.setUsername(user.getUsername());

        model.addAttribute("fileUploadDto", fileUploadDto);
        model.addAttribute("folderUploadDto", folderUploadDto);
//        model.addAttribute("user", user);
        return "welcome";
    }

    @GetMapping()
    public String showDefault(){
        return "default";
    }

}
