package com.shinchik.cloudkeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BaseController {

    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }

    @GetMapping()
    public String showDefault(){
        return "default";
    }

}
