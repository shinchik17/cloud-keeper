package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.storage.exception.NoSuchFolderException;
import com.shinchik.cloudkeeper.storage.exception.NoSuchObjectException;
import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMethod;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerExceptionHandler {

//    @ExceptionHandler(NoSuchFolderException.class)
//    public String handleNoSuchFolder(NoSuchFolderException e, Model model){
//        log.debug(e.getMessage());
//        model.addAttribute("errorMessage", e.getMessage());
//        return "storage/home";
//    }

    @ExceptionHandler(NoSuchObjectException.class)
    public ResponseEntity<?> handleNoSuchObject(NoSuchObjectException e){
        log.debug(e.getMessage());

        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
//        return ResponseEntity.badRequest()
//                .contentType(MediaType.TEXT_HTML)
//                .body(e.getMessage());

    }



    @ExceptionHandler(MinioRepositoryException.class)
    public String handleMinioRepositoryException(NoSuchFolderException e, Model model){
        log.debug(e.getMessage());
        model.addAttribute("errorMessage", e.getMessage());
        return "storage/home";
    }



}
