package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    int id;
    @Past
    LocalDateTime start;
    @Past
    LocalDateTime end;
    Item item;
    User booker;
    BookingStatus status;

}
