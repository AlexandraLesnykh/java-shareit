package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;


@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class UserDto {
    long id;

    @NonNull
    String name;

    @NonNull
    @Email
    String email;

    public UserDto(String name, String email) {
    }
}
