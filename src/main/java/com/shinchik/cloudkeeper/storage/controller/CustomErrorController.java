package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.security.SecurityConfig;
import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.user.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/error")
@Profile({"dev", "prod"})
public class CustomErrorController implements ErrorController {

    private final SecurityConfig securityConfig;

    @Autowired
    public CustomErrorController(SecurityConfig securityConfig) {
        this.securityConfig = securityConfig;
    }

    @RequestMapping
    public String handleErrors(HttpServletRequest request, Model model) {
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        int statusCode = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (request.getUserPrincipal() != null) {
            User user = ((SecurityUserDetails) ((UsernamePasswordAuthenticationToken) request.getUserPrincipal()).getPrincipal()).getUser();
            log.warn("Attempted to access '{}', status code '{}', username '{}'", requestUri, statusCode, user.getUsername());
        } else {
            log.warn("Attempted to access '{}', status code '{}', user is anonymous", requestUri, statusCode);
        }

        if (isAuthUri(requestUri) && statusCode == HttpStatus.FORBIDDEN.value()) {
            return "redirect:/";
        }

        String errorMessage = "How did you get here? Anyway, go back to home page it's definitely better out there :)";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

    private boolean isAuthUri(String requestUri){
        return requestUri.contains(securityConfig.getRegisterUrl()) || requestUri.contains(securityConfig.getLoginUrl());
    }

}
