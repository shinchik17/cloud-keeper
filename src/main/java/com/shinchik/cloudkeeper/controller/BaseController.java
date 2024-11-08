package com.shinchik.cloudkeeper.controller;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.storage.BucketService;
import com.shinchik.cloudkeeper.storage.dto.file.FileUploadDto;
import com.shinchik.cloudkeeper.storage.dto.folder.FolderUploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class BaseController {

    private BucketService service;

    @Autowired
    public BaseController(BucketService service) {
        this.service = service;
    }

    @GetMapping("/welcome")
    public String welcome(Model model,
                          @ModelAttribute("fileUploadDto") FileUploadDto fileUploadDto,
                          @ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto
                          ){

//        User user = userDetails.getUser();
//        FileUploadDto fileUploadDto = new FileUploadDto();
//        fileUploadDto.setUser(user);
//        FolderUploadDto folderUploadDto = new FolderUploadDto();
//        folderUploadDto.setUser(user);
//
//        model.addAttribute("fileUploadDto", fileUploadDto);
//        model.addAttribute("folderUploadDto", folderUploadDto);
//        model.addAttribute("user", user);
        return "welcome";
    }

    @GetMapping()
    public String showDefault(){
        return "default";
    }

}
