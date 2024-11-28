package com.shinchik.cloudkeeper.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(annotations = Controller.class)
public class ControllerPresetter {

    private final Environment env;

    @Autowired
    public ControllerPresetter(Environment env) {
        this.env = env;
    }

    @ModelAttribute("namePattern")
    public String nameValidatePattern(){
        return env.getProperty("frontend.validation.pattern.obj-name");
    }

    @ModelAttribute("searchPattern")
    public String searchValidatePattern(){
        return env.getProperty("frontend.validation.pattern.search");
    }

}
