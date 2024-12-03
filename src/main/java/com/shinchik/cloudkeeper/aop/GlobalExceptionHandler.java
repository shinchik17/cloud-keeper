package com.shinchik.cloudkeeper.aop;


import com.shinchik.cloudkeeper.storage.exception.NoSuchFolderException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException e, Model model){
        log.warn("Status {}: resource /{} not found.", e.getBody().getStatus(), e.getResourcePath());
        model.addAttribute("errorMessage",
                "How did you get here? Anyway, go back to home page it's definitely better out there :)");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception e, Model model, HttpServletRequest request){
        request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", e.getMessage());
        log.error(e.getMessage());
        return "error";
    }

}
