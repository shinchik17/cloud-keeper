package com.shinchik.cloudkeeper.storage.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/error")
public class ErrorHandleController implements ErrorController  {


    // do not clearly understand the way it works, but when this controller presents
    // NoSuchResourceFoundException is routed to GlobalExceptionHandler
    // otherwise not and spring shows error page without any custom message
    // TODO: try to replace with th manipulations on error.html
    @RequestMapping("/error")
    public String handleErrors(Model model) {
        model.addAttribute("errorMessage",
                "How did you get here? Anyway, go back to home page it's definitely better out there :)");
        return "error"; // Return error view
    }

}
