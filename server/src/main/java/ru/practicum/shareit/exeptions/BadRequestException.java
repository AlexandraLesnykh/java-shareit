package ru.practicum.shareit.exeptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class BadRequestException extends RuntimeException {

    public BadRequestException() {
    }

    public BadRequestException(final String error) {
        super(error);
    }
}
