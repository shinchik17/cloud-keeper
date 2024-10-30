package com.shinchik.cloudkeeper.controller;

import com.shinchik.cloudkeeper.model.User;
import com.shinchik.cloudkeeper.service.OnboardingService;
import com.shinchik.cloudkeeper.service.SecurityUserDetailsService;
import com.shinchik.cloudkeeper.util.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
//@PropertySource("classpath:application.yaml")
public class AuthController {

    private final OnboardingService onboardingService;
    private final UserValidator userValidator;

    @Autowired
    public AuthController(OnboardingService onboardingService, UserValidator userValidator) {
        this.onboardingService = onboardingService;
        this.userValidator = userValidator;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "auth/welcome";
    }

    @GetMapping("/login")
    public String logIn(){
        return "auth/login";
    }

    @GetMapping("/register")
    public String register(@ModelAttribute("user") User user){
        return "auth/registration";
    }

    @PostMapping("/register")
    public String performRegistration(@ModelAttribute("user") @Valid User user,
                                      BindingResult bindingResult) {

        userValidator.validate(user, bindingResult);

        if (bindingResult.hasErrors()){
            return "/auth/registration";
        }

        onboardingService.register(user);

        return "redirect:/auth/login";
    }

}
