package com.shinchik.cloudkeeper.user.controller;

import com.shinchik.cloudkeeper.user.exception.InvalidUserCredentialsException;
import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.service.AuthService;
import com.shinchik.cloudkeeper.user.util.UserValidator;
import com.shinchik.cloudkeeper.validation.ValidationUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/auth")
@Profile({"web"})
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;

    @Autowired
    public AuthController(AuthService authService, UserValidator userValidator) {
        this.authService = authService;
        this.userValidator = userValidator;
    }


    @GetMapping("/login")
    public String logIn(@ModelAttribute("user") User user) {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(@ModelAttribute("user") User user) {
        return "auth/registration";
    }

    @PostMapping("/register")
    public String performRegistration(@ModelAttribute("user") @Valid User user,
                                            BindingResult bindingResult,
                                            @RequestParam(value = "pass-repeat") String passConfirmation,
                                            Model model) {

        userValidator.validate(user, bindingResult);
        userValidator.validatePasswordsMatch(user, bindingResult, passConfirmation);

        if (bindingResult.hasErrors()) {
            ValidationUtil.extractErrorsInfo(bindingResult).forEach(log::warn);
            model.addAttribute("errorMessage", ValidationUtil.chooseMainErrorMessage(bindingResult));
            return "/auth/registration";
        }

        authService.register(user);
        return "redirect:/auth/login";
    }

}
