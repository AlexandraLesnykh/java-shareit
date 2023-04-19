package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.dto.CommentDto;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "items", schema = "public")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Size(max = 20)
    String name;

    @Size(max = 200)
    String description;

    boolean available;
    Long owner;

    @Column(name = "request_id")
    Long requestId;
    @Transient
    Booking lastBooking;

    @Transient
    Booking nextBooking;

    @Transient
    List<CommentDto> comments;

    @Transient
    Set<Booking> bookings = new HashSet<>();

}
