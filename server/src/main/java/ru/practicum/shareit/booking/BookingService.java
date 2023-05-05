package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ValidationException;

import java.util.List;

public interface BookingService {
    BookingDto create(Booking booking, long ownerId) throws ValidationException;

    BookingDto update(long bookingId, boolean approved, long ownerId) throws ValidationException;

    BookingDto getBookingById(long bookingId, long ownerId);

    List<BookingDto> findAll(String state, long ownerId, PageRequest of) throws ValidationException;

    List<BookingDto> findAllWithOwner(String state, long ownerId, PageRequest of) throws ValidationException;
}
