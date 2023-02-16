package ru.practicum.shareit.item.dto;

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

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    int id;

    @Size(max = 20)
    String name;

    @Size(max = 200)
    String description;
    boolean available;
    int owner;
    ItemRequest request;


    public ItemDto(String name, String description, boolean available, Integer owner) {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getOwner() {
        return owner;
    }

    public ItemRequest getRequest() {
        return request;
    }
}
