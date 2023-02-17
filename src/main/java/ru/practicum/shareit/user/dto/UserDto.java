package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Setter
@Getter
public class UserDto {
    long id;

    String name;

    String email;

    public UserDto(String name, String email) {
    }
}
