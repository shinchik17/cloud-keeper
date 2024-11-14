package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.BucketService;
import com.shinchik.cloudkeeper.storage.dto.UploadDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BaseController {

    private BucketService service;

    @Autowired
    public BaseController(BucketService service) {
        this.service = service;
    }

    @GetMapping("/welcome")
    public String welcome(Model model,
                          @ModelAttribute("fileUploadDto") UploadDto uploadDto
//                          @ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto
                          ){

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

//    @GetMapping()
//    public String showDefault(){
//        return "default";
//    }

    @GetMapping()
    public String index(@RequestParam(value = "path", required = false) String path){
        return "default";
    }


}
