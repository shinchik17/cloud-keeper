package com.shinchik.cloudkeeper.user.controller;

import com.shinchik.cloudkeeper.user.model.User;
import com.shinchik.cloudkeeper.user.service.AuthService;
import com.shinchik.cloudkeeper.user.util.UserValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserValidator userValidator;

    @Autowired
    public AuthController(AuthService authService, UserValidator userValidator) {
        this.authService = authService;
        this.userValidator = userValidator;
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

        authService.register(user);
        return "redirect:/auth/login";
    }

}
