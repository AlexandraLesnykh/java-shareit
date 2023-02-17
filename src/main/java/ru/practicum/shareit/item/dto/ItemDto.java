package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
public class ItemDto {
    long id;

    @Size(max = 20)
    String name;

    @Size(max = 200)
    String description;
    boolean available;
    int owner;
    ItemRequest request;

    public ItemDto(String name, String description, boolean available, Long owner) {
    }


}
