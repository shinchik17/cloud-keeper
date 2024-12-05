package com.shinchik.cloudkeeper.aop;


import com.shinchik.cloudkeeper.storage.exception.controller.DtoValidationException;
import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import com.shinchik.cloudkeeper.storage.exception.service.MinioServiceException;
import com.shinchik.cloudkeeper.storage.exception.service.NoSuchFolderException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException e, Model model){
        log.warn("Resource /{} not found.", e.getResourcePath());
        model.addAttribute("errorMessage",
                "How did you get here? Anyway, go back to home page. It's definitely better out there :)");
        return "error";
    }

    // TODO: check other exceptions
    @ExceptionHandler(NoSuchFolderException.class)
    public RedirectView handleNoSuchFolderException(NoSuchFolderException e, RedirectAttributes redirectAttributes) {
        log.debug(e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return new RedirectView("/", true);
    }

    @ExceptionHandler(DtoValidationException.class)
    @ResponseBody
    public ResponseEntity<String> handleDtoValidationException(DtoValidationException e) {
        log.debug(e.getMessage());
        return ResponseEntity.badRequest().body(exToJsonString(e));
    }


    @ExceptionHandler(MinioServiceException.class)
    @ResponseBody
    public ResponseEntity<String> handleMinioServiceException(MinioServiceException e) {
        log.debug(e.getMessage());
        return ResponseEntity.badRequest().body(exToJsonString(e));
    }

    @ExceptionHandler(MinioRepositoryException.class)
    @ResponseBody
    public ResponseEntity<String> handleMinioRepositoryException(MinioRepositoryException e) {
        log.debug(e.getMessage());
        return ResponseEntity.badRequest().body(exToJsonString(e));
    }

    @ExceptionHandler(Exception.class)
    public String handleOtherExceptions(Exception e, HttpServletRequest request){
        String errorStatusCode = (String) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String uri = request.getRequestURI();
        String errorMessage = e.getMessage();
        log.error("Attempted to access '{}', status code '{}', message '{}'", errorStatusCode, uri, errorMessage);
        return "redirect:error";
    }


    private static String exToJsonString(Exception e) {
        return "{ \"error\": \"%s\" }".formatted(e.getMessage());
    }

}
