package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.user.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {

    @RequestMapping
    public String handleErrors(HttpServletRequest request, Model model) {
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        int statusCode = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        User user = ((SecurityUserDetails) ((UsernamePasswordAuthenticationToken) request.getUserPrincipal()).getPrincipal()).getUser();
        log.warn("Attempted to access '{}', status code '{}', username '{}'", requestUri, statusCode, user.getUsername());

        String errorMessage = "How did you get here? Anyway, go back to home page it's definitely better out there :)";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}
