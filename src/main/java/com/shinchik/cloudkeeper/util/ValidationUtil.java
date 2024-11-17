package com.shinchik.cloudkeeper.util;

import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ValidationUtil {

    public static List<String> extractErrorMessages(BindingResult bindingResult){
        List<ObjectError> errors = bindingResult.getAllErrors();
        List<String> messages = new ArrayList<>();
        for (ObjectError e : errors){
            String message;
            if (e instanceof FieldError){
                message = "Object <%s>, field <%s>: %s".formatted(e.getObjectName(), ((FieldError) e).getField(), e.getDefaultMessage());
            } else {
                message = "Object <%s>: %s".formatted(e.getObjectName(), e.getDefaultMessage());
            }
            messages.add(message);
        }

        return messages;
    }



}
