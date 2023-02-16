package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    int id;
            @Size(max = 20)
    String name;
            @Size(max = 200)
    String description;
    boolean available;
    User owner;
    ItemRequest request;

    public Item(String name, String description, boolean available, Integer integer) {
    }
}
