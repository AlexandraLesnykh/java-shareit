package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {
    long id;

    @Size(max = 20)
    String name;

    @Size(max = 200)
    String description;

    @NotNull
    boolean available;
    Long requestId;

}
