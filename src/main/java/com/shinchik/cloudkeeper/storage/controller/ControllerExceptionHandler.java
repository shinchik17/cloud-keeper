package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.exception.NoSuchFolderException;
import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(NoSuchFolderException.class)
    public String handleNoSuchFolder(NoSuchFolderException e, Model model){
        log.debug(e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "storage/home";
    }



    @ExceptionHandler(MinioRepositoryException.class)
    public String handleMinioRepositoryException(NoSuchFolderException e, Model model){
        log.debug(e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "storage/home";
    }



}
