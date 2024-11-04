package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.model.FileUploadDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/files")
public class FileController {


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadFiles(@ModelAttribute("fileUploadDto") FileUploadDto fileUploadDto, BindingResult bindingResult) {

        return "welcome";
    }



}
