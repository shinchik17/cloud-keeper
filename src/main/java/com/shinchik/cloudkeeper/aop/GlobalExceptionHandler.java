package com.shinchik.cloudkeeper.aop;


import com.shinchik.cloudkeeper.storage.exception.controller.DtoValidationException;
import com.shinchik.cloudkeeper.storage.exception.repository.MinioRepositoryException;
import com.shinchik.cloudkeeper.storage.exception.service.MinioServiceException;
import com.shinchik.cloudkeeper.storage.exception.service.NoSuchFolderException;
import com.shinchik.cloudkeeper.storage.exception.service.SuchFolderAlreadyExistsException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
    public RedirectView handleNoResourceFound(NoResourceFoundException e, RedirectAttributes redirectAttributes) {
        log.warn("Resource '{}' not found. Redirecting to /error", e.getResourcePath());
        redirectAttributes.addFlashAttribute("errorMessage",
                "How did you get here? Anyway, go back to home page. It's definitely better out there :)");
        redirectAttributes.addFlashAttribute("resourcePath", e.getResourcePath());
        return new RedirectView("/error", true);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e, Model model) {
        log.debug("DataIntegrityViolationException: {}", e.getMessage());
        model.addAttribute("errorMessage", "Unexpected service error. Please try again later");
        return "/auth/registration";
    }

    @ExceptionHandler(NoSuchFolderException.class)
    public RedirectView handleNoSuchFolderException(NoSuchFolderException e, RedirectAttributes redirectAttributes) {
        log.debug(e.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        return new RedirectView("/", true);
    }

    @ExceptionHandler(SuchFolderAlreadyExistsException.class)
    public ResponseEntity<String> handleSuchFolderAlreadyExistsException(SuchFolderAlreadyExistsException e, RedirectAttributes redirectAttributes) {
        log.debug(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exToJsonString(e));
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
    public String handleOtherExceptions(Exception e, HttpServletRequest request, Model model) {
        String errorStatusCode = (String) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String uri = request.getRequestURI();
        String errorMessage = e.getMessage();
        log.error("Attempted to access '{}', status code '{}', message '{}'", uri, errorStatusCode,errorMessage);
        model.addAttribute("errorMessage", "Service is unavailable. Please try again later");
        return "redirect:error";
    }


    private static String exToJsonString(Exception e) {
        return "{ \"message\": \"%s\" }".formatted(e.getMessage());
    }

}
