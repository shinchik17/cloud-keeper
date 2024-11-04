package com.shinchik.cloudkeeper.aop;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException e, Model model){
        String errorMessage = "Status %d: resource /%s not found.".formatted(e.getBody().getStatus(), e.getResourcePath());
        logger.warn(errorMessage);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }


    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception e, Model model, HttpServletRequest request){
        request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("errorCode", e.getMessage());
        logger.error(e.getMessage());
        return "error";
    }

}
