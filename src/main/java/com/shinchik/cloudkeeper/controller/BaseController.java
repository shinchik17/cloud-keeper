package com.shinchik.cloudkeeper.controller;

import com.shinchik.cloudkeeper.storage.BucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    BucketService service;

    @Autowired
    public BaseController(BucketService service) {
        this.service = service;
    }

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping()
    public String showDefault(){
        return "default";
    }

}
