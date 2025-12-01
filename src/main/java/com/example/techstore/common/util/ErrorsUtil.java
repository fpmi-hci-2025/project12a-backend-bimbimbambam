package com.example.techstore.common.util;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class ErrorsUtil {
    public static String getErrorMsg(BindingResult bindingResult) {
        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMsg.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }
        return errorMsg.toString();
    }
}