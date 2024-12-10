package com.shinchik.cloudkeeper.storage.controller;

import com.shinchik.cloudkeeper.security.SecurityUserDetails;
import com.shinchik.cloudkeeper.user.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.http.HttpResponse;

@Slf4j
@Controller
@RequestMapping("/error")
@Profile({"dev", "prod"})
public class CustomErrorController implements ErrorController {

    @RequestMapping
    public String handleErrors(HttpServletRequest request, Model model) {
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        int statusCode = (int) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // TODO: try-catch
        if (request.getUserPrincipal() != null){
            User user = ((SecurityUserDetails) ((UsernamePasswordAuthenticationToken) request.getUserPrincipal()).getPrincipal()).getUser();
            log.warn("Attempted to access '{}', status code '{}', username '{}'", requestUri, statusCode, user.getUsername());
        } else {
            log.warn("Attempted to access '{}', status code '{}', user is anonymous", requestUri, statusCode);
        }


        if (requestUri.contains("login") || requestUri.contains("register") && statusCode == HttpStatus.FORBIDDEN.value()){
            return "redirect:/";
        }

        String errorMessage = "How did you get here? Anyway, go back to home page it's definitely better out there :)";
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }

}
