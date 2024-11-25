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
        String errorMessage = "Status %d: resource /%s not found.".formatted(e.getBody().getStatus(), e.getResourcePath());
        log.warn(errorMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    @ExceptionHandler(NoSuchFolderException.class)
    public String handleNoSuchFolder(NoSuchFolderException e, Model model){
        log.warn(e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
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
