package ru.practicum.shareit.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.validator.ValidationErrorResponse;
import ru.practicum.shareit.validator.Violation;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String list = e.getBindingResult().getFieldError().toString();
        return new ValidationErrorResponse(list);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse onConstraintViolationException(ConstraintViolationException e) {
        String list = e.getConstraintViolations().stream()
                .map(
                        message -> message.getMessage()
                ).toString();
        return new ValidationErrorResponse(list);
    }

}
