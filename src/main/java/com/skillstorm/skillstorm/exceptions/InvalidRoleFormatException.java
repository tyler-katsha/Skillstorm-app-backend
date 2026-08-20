package com.skillstorm.skillstorm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT,reason = "Unable to process role in it's current format")
public class InvalidRoleFormatException extends RuntimeException {
    public InvalidRoleFormatException(String message) {
        super(message);
    }
}
