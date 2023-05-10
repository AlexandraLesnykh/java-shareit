package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class StateValidator implements ConstraintValidator<StateValidation, String> {
    public boolean isValid(String stateName, ConstraintValidatorContext cxt) {
        //    List list = Arrays.asList("ALL", "CURRENT", "FUTURE", "PAST", "WAITING", "REJECTED");
        List list = Arrays.asList(BookingState.from(stateName));
        if (!list.contains(stateName)) {
            throw new IllegalArgumentException("Unknown state" + stateName);
        }
        return list.contains(stateName);
    }
}
