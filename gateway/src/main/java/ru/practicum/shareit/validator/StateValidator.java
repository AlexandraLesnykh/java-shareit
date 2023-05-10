package ru.practicum.shareit.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateValidator implements ConstraintValidator<StateValidation, String> {
    public boolean isValid(String stateName, ConstraintValidatorContext cxt) {
        List list = Arrays.asList("ALL","CURRENT","FUTURE", "PAST", "WAITING", "REJECTED", null, "");
        List list1 = Stream.of(new String[]{}).collect(Collectors.toList());
        list.addAll(list1);
        return list.contains(stateName);
    }
}
