package ru.practicum.shareit.exeptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "The validation was fallen")
public class ValidationException extends Exception {

    public ValidationException() {
    }

    public ValidationException(final String error) {
        super(error);
    }
}
