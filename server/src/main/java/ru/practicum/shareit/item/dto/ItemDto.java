package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ItemDto {
    long id;
    String name;
    String description;
    boolean available;
    long owner;
    long requestId;
    Booking lastBooking;
    Booking nextBooking;
    List<CommentDto> comments;

}
