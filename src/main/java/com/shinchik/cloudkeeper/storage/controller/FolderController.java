package com.shinchik.cloudkeeper.storage.controller;


import com.shinchik.cloudkeeper.storage.dto.folder.FolderUploadDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/folders")
public class FolderController {


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFiles(@ModelAttribute("folderUploadDto") FolderUploadDto folderUploadDto,
                              BindingResult bindingResult) {

        return "welcome";
    }



}
